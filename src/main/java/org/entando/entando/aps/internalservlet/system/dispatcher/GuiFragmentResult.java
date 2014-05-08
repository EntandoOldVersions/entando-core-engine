/*
 *
 * Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
 *
 * This file is part of Entando Enterprise Edition software.
 * You can redistribute it and/or modify it
 * under the terms of the Entando's EULA
 * 
 * See the file License for the specific language governing permissions   
 * and limitations under the License
 * 
 * 
 * 
 * Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
 *
 */
package org.entando.entando.aps.internalservlet.system.dispatcher;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.StrutsResultSupport;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

import org.entando.entando.aps.system.services.controller.executor.ExecutorBeanContainer;
import org.entando.entando.aps.system.services.guifragment.GuiFragment;
import org.entando.entando.aps.system.services.guifragment.IGuiFragmentManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Renders a view using a GuiFragment (Entando object) builded by Freemarker.
 *
 * <b>This result type takes the following parameters:</b>
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *
 * <li><b>code (default)</b> - the code of the fragment to process.</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 * <b>Example:</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;result name="success" type="guiFragment"&gt;fooCode&lt;/result&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 */
public class GuiFragmentResult extends StrutsResultSupport {

	private static final Logger _logger = LoggerFactory.getLogger(GuiFragmentResult.class);
	private Writer _writer;
	
	public GuiFragmentResult() {
		super();
	}

	public GuiFragmentResult(String code) {
		super(code);
	}
	
	/**
	 * Execute this result, using the specified fragment. The fragment code has
	 * already been interoplated for any variable substitutions
	 * @param code The code of the fragment
	 * @param invocation The invocation
	 */
	@Override
	public void doExecute(String code, ActionInvocation invocation) throws IOException, TemplateException {
		ActionContext ctx = invocation.getInvocationContext();
		HttpServletRequest req = (HttpServletRequest) ctx.get(ServletActionContext.HTTP_REQUEST);
		IGuiFragmentManager guiFragmentManager =
				(IGuiFragmentManager) ApsWebApplicationUtils.getBean(SystemConstants.GUI_FRAGMENT_MANAGER, req);
		try {
			String output;
			GuiFragment guiFragment = guiFragmentManager.getGuiFragment(code);
			if (null != guiFragment) {
				output = guiFragment.getGui();
			} else {
				_logger.info("The fragment '{}' is unavailable - Action '{}' - Namespace '{}'", 
						code, invocation.getProxy().getActionName(), invocation.getProxy().getNamespace());
				output = "The fragment '" + code + "' is unavailable";
			}
			RequestContext reqCtx = (RequestContext) req.getAttribute(RequestContext.REQCTX);
			ExecutorBeanContainer ebc = (ExecutorBeanContainer) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_EXECUTOR_BEAN_CONTAINER);
			Writer writer = this.getWriter();
			Template template = new Template(code, new StringReader(output), ebc.getConfiguration());
			template.process(ebc.getTemplateModel(), writer);
		} catch (Throwable t) {
			_logger.error("Error processing GuiFragment result!", t);
			throw new RuntimeException("Error processing GuiFragment result!", t);
		} finally {
			
		}
	}
	
	public void setWriter(Writer writer) {
		this._writer = writer;
	}
	
	protected Writer getWriter() throws IOException {
		if (_writer != null) {
			return _writer;
		}
		return ServletActionContext.getResponse().getWriter();
	}
	
}
