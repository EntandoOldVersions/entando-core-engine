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
package org.entando.entando.aps.system.services.controller.executor;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateModel;

import java.io.IOException;
import java.io.StringReader;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author E.Santoboni
 */
public class PageExecutorService implements ExecutorServiceInterface {
	
	private static final Logger _logger = LoggerFactory.getLogger(PageExecutorService.class);
	
	@Override
	public void afterPropertiesSet() throws Exception {
		//nothing to do
	}
	
	@Override
	public void service(Configuration freemarkerConfig, TemplateModel templateModel, RequestContext reqCtx) {
		HttpServletRequest request = reqCtx.getRequest();
		HttpServletResponse response = reqCtx.getResponse();
		try {
			IPage page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
			PageModel model = page.getModel();
			if (StringUtils.isBlank(model.getTemplate())) {
				String jspPath = this.getPageModelJspPath(page);
				RequestDispatcher dispatcher = request.getSession().getServletContext().getRequestDispatcher(jspPath);
				dispatcher.forward(request, response);
			} else {
				Template template = new Template(page.getCode(), new StringReader(model.getTemplate()), freemarkerConfig);
				try {
					template.process(templateModel, response.getWriter());
				} catch (Throwable t) {
					String msg = "Error detected while including a page model " + model.getCode();
					_logger.error(msg, t);
					throw new RuntimeException(msg, t);
				}
			}
		} catch (ServletException e) {
			String msg = "Error detected while including a page model";
			_logger.error(msg, e);
			//ApsSystemUtils.logThrowable(e, this, "doEndTag", msg);
			throw new RuntimeException(msg, e);
		} catch (IOException e) {
			String msg = "IO error detected while including the page model";
			_logger.error(msg, e);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * Return the jsp path of current page model.
	 * @param page The current page.
	 * @return The jsp path of current page model.
	 */
	protected String getPageModelJspPath(IPage page) {
		String pluginCode = page.getModel().getPluginCode();
		boolean isPluginPageModel = (null != pluginCode && pluginCode.trim().length()>0);
		StringBuilder jspPath = new StringBuilder("/WEB-INF/");
		if (isPluginPageModel) {
			jspPath.append("plugins/").append(pluginCode.trim()).append("/");
		}
		jspPath.append("aps/jsp/models/").append(page.getModel().getCode()).append(".jsp");
		return jspPath.toString();
	}
	
}