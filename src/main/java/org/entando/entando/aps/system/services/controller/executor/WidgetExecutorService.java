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
package org.entando.entando.aps.system.services.controller.executor;

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

import freemarker.template.Configuration;
import freemarker.template.TemplateModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanComparator;
import org.entando.entando.aps.system.services.widgettype.WidgetType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.context.WebApplicationContext;

/**
 * @author E.Santoboni
 */
public class WidgetExecutorService implements ExecutorServiceInterface {
	
	private static final Logger _logger = LoggerFactory.getLogger(WidgetExecutorService.class);
	
	@Override
	public void afterPropertiesSet() throws Exception {
		//nothing to do
	}
	
	@Override
	public void service(Configuration freemarkerConfig, TemplateModel templateModel, RequestContext reqCtx) {
		try {
			reqCtx.addExtraParam(SystemConstants.EXTRAPAR_HEAD_INFO_CONTAINER, new HeadInfoContainer());
			IPage page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
			String[] widgetOutput = new String[page.getWidgets().length];
			reqCtx.addExtraParam("ShowletOutput", widgetOutput);
			this.buildWidgetsOutput(reqCtx, page, widgetOutput);
			String redirect = (String) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_EXTERNAL_REDIRECT);
			if (null != redirect) {
				HttpServletResponse response = (HttpServletResponse) reqCtx.getResponse();
				response.sendRedirect(redirect);
				return;
			}
		} catch (Throwable t) {
			String msg = "Error detected during widget preprocessing";
			_logger.error(msg, t);
			throw new RuntimeException(msg, t);
		}
		return;
	}
	
	protected void buildWidgetsOutput(RequestContext reqCtx, IPage page, String[] widgetOutput) throws ApsSystemException {
		try {
			List<IFrameDecoratorContainer> decorators = this.extractDecorators(reqCtx);
			Widget[] widgets = page.getWidgets();
			for (int frame = 0; frame < widgets.length; frame++) {
				reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME, new Integer(frame));
				Widget widget = widgets[frame];
				widgetOutput[frame] = this.buildWidgetOutput(reqCtx, widget, decorators);
			}
		} catch (Throwable t) {
			String msg = "Error detected during widget preprocessing";
			throw new ApsSystemException(msg, t);
		}
	}
	
	protected String buildWidgetOutput(RequestContext reqCtx, 
			Widget widget, List<IFrameDecoratorContainer> decorators) throws ApsSystemException {
		StringBuilder buffer = new StringBuilder();
		try {
			if (null != widget && this.isUserAllowed(reqCtx, widget)) {
				reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_WIDGET, widget);
			} else {
				reqCtx.removeExtraParam(SystemConstants.EXTRAPAR_CURRENT_WIDGET);
			}
			buffer.append(this.extractDecoratorsOutput(reqCtx, widget, decorators, false, true));
			if (null != widget && this.isUserAllowed(reqCtx, widget)) {
				WidgetType showletType = widget.getType();
				if (showletType.isLogic()) {
					showletType = showletType.getParentType();
				}
				String pluginCode = showletType.getPluginCode();
				boolean isWidgetPlugin = (null != pluginCode && pluginCode.trim().length() > 0);
				StringBuilder jspPath = new StringBuilder("/WEB-INF/");
				if (isWidgetPlugin) {
					jspPath.append("plugins/").append(pluginCode.trim()).append("/");
				}
				jspPath.append(WIDGET_LOCATION).append(showletType.getCode()).append(".jsp");
				buffer.append(this.extractDecoratorsOutput(reqCtx, widget, decorators, true, true));
				buffer.append(this.extractJspOutput(reqCtx, jspPath.toString()));
				buffer.append(this.extractDecoratorsOutput(reqCtx, widget, decorators, true, false));
			}
			buffer.append(this.extractDecoratorsOutput(reqCtx, widget, decorators, false, false));
		} catch (Throwable t) {
			String msg = "Error creating widget output";
			_logger.error(msg, t);
			throw new RuntimeException(msg, t);
		}
		return buffer.toString();
	}
	
	protected List<IFrameDecoratorContainer> extractDecorators(RequestContext reqCtx) throws ApsSystemException {
		HttpServletRequest request = reqCtx.getRequest();
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
			_logger.error("Error extracting widget decorators", t);
			throw new ApsSystemException("Error extracting widget decorators", t);
		}
		return containters;
	}
	
	protected String extractDecoratorsOutput(RequestContext reqCtx, Widget widget, 
			List<IFrameDecoratorContainer> decorators, boolean isWidgetDecorator, boolean includeHeader) throws Throwable {
		if (null == decorators || decorators.isEmpty()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
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
				String output = this.extractJspOutput(reqCtx, path);
				builder.append(output);
			}
		}
		return builder.toString();
	}
	
	protected boolean isUserAllowed(RequestContext reqCtx, Widget widget) /*throws Throwable */{
		if (null == widget) {
			return false;
		}
		String showletTypeGroup = widget.getType().getMainGroup();
		try {
			if (null == showletTypeGroup || showletTypeGroup.equals(Group.FREE_GROUP_NAME)) {
				return true;
			}
			IAuthorizationManager authorizationManager = (IAuthorizationManager) ApsWebApplicationUtils.getBean(SystemConstants.AUTHORIZATION_SERVICE, reqCtx.getRequest());
			UserDetails currentUser = (UserDetails) reqCtx.getRequest().getSession().getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER);
			return authorizationManager.isAuthOnGroup(currentUser, showletTypeGroup);
		} catch (Throwable t) {
			_logger.error("Error checking user authorities", t);
		}
		return false;
	}
	
	protected String extractJspOutput(RequestContext reqCtx, String jspPath) throws ServletException, IOException {
		HttpServletRequest request = reqCtx.getRequest();
		HttpServletResponse response = reqCtx.getResponse();
		BufferedHttpResponseWrapper wrapper = new BufferedHttpResponseWrapper(response);
		ServletContext context = request.getSession().getServletContext();
		String url = response.encodeRedirectURL(jspPath);
		RequestDispatcher dispatcher = context.getRequestDispatcher(url);
		dispatcher.include(request, wrapper);
		return wrapper.getOutput();
	}
	
	protected final String JSP_FOLDER = "/WEB-INF/aps/jsp/";
	public final static String WIDGET_LOCATION = "aps/jsp/widgets/";
	
}
