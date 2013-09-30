/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software. 
* Entando is a free software; 
* You can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.aps.tags;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.TagSupport;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.tags.util.HeadInfoContainer;
import com.agiletec.aps.tags.util.IFrameDecoratorContainer;
import com.agiletec.aps.util.ApsWebApplicationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanComparator;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.springframework.web.context.WebApplicationContext;

/**
 * Triggers the preliminary execution of the widget, it must be used
 * <b>uniquely</b> in the main.jsp. It does not produce any output since the
 * goal is to insert in the RequestContext the partial output of each widget
 * and the informations to include in the page head
 *
 * @author M.Diana - E.Santoboni
 */
@SuppressWarnings("serial")
public class ExecWidgetTag extends TagSupport {

	/**
	 * Invoke the widget configured in the page.
	 *
	 * @throws JspException In case of errors in this method or in the included
	 * JSPs
	 */
	@Override
	public int doEndTag() throws JspException {
		ServletRequest req = this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) req.getAttribute(RequestContext.REQCTX);
		try {
			reqCtx.addExtraParam(SystemConstants.EXTRAPAR_HEAD_INFO_CONTAINER, new HeadInfoContainer());
			IPage page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
			String[] widgetOutput = new String[page.getWidgets().length];
			reqCtx.addExtraParam("ShowletOutput", widgetOutput);
			this.buildWidgetOutput(page, widgetOutput);
			String redirect = (String) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_EXTERNAL_REDIRECT);
			if (null != redirect) {
				HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();
				response.sendRedirect(redirect);
				return SKIP_PAGE;
			}
			this.pageContext.popBody();
		} catch (Throwable t) {
			String msg = "Error detected during widget preprocessing";
			ApsSystemUtils.logThrowable(t, this, "doEndTag", msg);
			throw new JspException(msg, t);
		}
		return super.doEndTag();
	}

	/**
	 * @deprecated Use {@link #buildWidgetOutput(IPage,String[])} instead
	 */
	protected void buildShowletOutput(IPage page, String[] showletOutput) throws JspException {
		buildWidgetOutput(page, showletOutput);
	}

	protected void buildWidgetOutput(IPage page, String[] widgetOutput) throws JspException {
		ServletRequest req = this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) req.getAttribute(RequestContext.REQCTX);
		try {
			List<IFrameDecoratorContainer> decorators = this.extractDecorators();
			BodyContent body = this.pageContext.pushBody();
			Widget[] widgets = page.getWidgets();
			for (int frame = 0; frame < widgets.length; frame++) {
				reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME, new Integer(frame));
				Widget widget = widgets[frame];
				body.clearBody();
				this.includeWidget(reqCtx, widget, decorators);
				widgetOutput[frame] = body.getString();
			}
		} catch (Throwable t) {
			String msg = "Error detected during widget preprocessing";
			throw new JspException(msg, t);
		}
	}
	
	/**
	 * @deprecated Use {@link #includeWidget(RequestContext,Widget,List<IFrameDecoratorContainer>)} instead
	 */
	protected void includeShowlet(RequestContext reqCtx, Widget widget, List<IFrameDecoratorContainer> decorators) throws Throwable {
		includeWidget(reqCtx, widget, decorators);
	}

	protected void includeWidget(RequestContext reqCtx, Widget widget, List<IFrameDecoratorContainer> decorators) throws Throwable {
		if (null != widget && this.isUserAllowed(reqCtx, widget)) {
			reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_WIDGET, widget);
		} else {
			reqCtx.removeExtraParam(SystemConstants.EXTRAPAR_CURRENT_WIDGET);
		}
		this.includeDecorators(widget, decorators, false, true);
		if (null != widget && this.isUserAllowed(reqCtx, widget)) {
			WidgetType showletType = widget.getType();
			if (showletType.isLogic()) {
				showletType = showletType.getParentType();
			}
			String pluginCode = showletType.getPluginCode();
			boolean isPluginWidget = (null != pluginCode && pluginCode.trim().length() > 0);
			StringBuilder jspPath = new StringBuilder("/WEB-INF/");
			if (isPluginWidget) {
				jspPath.append("plugins/").append(pluginCode.trim()).append("/");
			}
			jspPath.append(WIDGET_LOCATION).append(showletType.getCode()).append(".jsp");
			this.includeDecorators(widget, decorators, true, true);
			this.pageContext.include(jspPath.toString());
			this.includeDecorators(widget, decorators, true, false);
		}
		this.includeDecorators(widget, decorators, false, false);
	}

	protected List<IFrameDecoratorContainer> extractDecorators() throws ApsSystemException {
		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
		WebApplicationContext wac = ApsWebApplicationUtils.getWebApplicationContext(request);
		List<IFrameDecoratorContainer> containters = new ArrayList<IFrameDecoratorContainer>();
		try {
			String[] beanNames = wac.getBeanNamesForType(IFrameDecoratorContainer.class);
			for (int i = 0; i < beanNames.length; i++) {
				IFrameDecoratorContainer container = (IFrameDecoratorContainer) wac.getBean(beanNames[i]);
				containters.add(container);
			}
			BeanComparator comparator = new BeanComparator("order");
			Collections.sort(containters, comparator);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "extractDecorators", "Error extracting widget decorators");
			throw new ApsSystemException("Error extracting widget decorators", t);
		}
		return containters;
	}
	
	protected void includeDecorators(Widget widget, 
			List<IFrameDecoratorContainer> decorators, boolean isWidgetDecorator, boolean includeHeader) throws Throwable {
		if (null == decorators || decorators.isEmpty()) {
			return;
		}
		RequestContext reqCtx = (RequestContext) this.pageContext.getRequest().getAttribute(RequestContext.REQCTX);
		for (int i = 0; i < decorators.size(); i++) {
			IFrameDecoratorContainer decoratorContainer = (includeHeader)
					? decorators.get(i)
					: decorators.get(decorators.size() - i - 1);
			if ((isWidgetDecorator != decoratorContainer.isShowletDecorator()) 
					|| !decoratorContainer.needsDecoration(widget, reqCtx)) {
				continue;
			}
			String path = (includeHeader) ? decoratorContainer.getHeaderPath() : decoratorContainer.getFooterPath();
			if (null != path && path.trim().length() > 0) {
				this.pageContext.include(path);
			}
		}
	}
	
	protected boolean isUserAllowed(RequestContext reqCtx, Widget widget) throws Throwable {
		if (null == widget) {
			return false;
		}
		String showletTypeGroup = widget.getType().getMainGroup();
		try {
			if (null == showletTypeGroup || showletTypeGroup.equals(Group.FREE_GROUP_NAME)) {
				return true;
			}
			IAuthorizationManager authorizationManager = (IAuthorizationManager) ApsWebApplicationUtils.getBean(SystemConstants.AUTHORIZATION_SERVICE, pageContext);
			UserDetails currentUser = (UserDetails) reqCtx.getRequest().getSession().getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER);
			return authorizationManager.isAuthOnGroup(currentUser, showletTypeGroup);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "isUserAllowed", "Error checking user authorities");
		}
		return false;
	}
	
	protected final String JSP_FOLDER = "/WEB-INF/aps/jsp/";
	public final static String WIDGET_LOCATION = "aps/jsp/widgets/";
}