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
package org.entando.entando.aps.servlet;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.controller.ControllerManager;
import com.agiletec.aps.util.ApsWebApplicationUtils;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModel;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.entando.entando.aps.system.services.controller.executor.ExecutorServiceInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet di controllo, punto di ingresso per le richieste di pagine del portale.
 * Predispone il contesto di richiesta, invoca il controller e ne gestisce lo stato di uscita.
 * @author  
 */
public class ControllerServlet extends freemarker.ext.servlet.FreemarkerServlet {
	
	private static final Logger _logger = LoggerFactory.getLogger(ControllerServlet.class);
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.service(request, response);
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.service(request, response);
	}
    
	@Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        RequestContext reqCtx = new RequestContext();
        _logger.debug("Request:" + request.getServletPath());
        request.setAttribute(RequestContext.REQCTX, reqCtx);
        reqCtx.setRequest(request);
        reqCtx.setResponse(response);
		ControllerManager controller = 
				(ControllerManager) ApsWebApplicationUtils.getBean(SystemConstants.CONTROLLER_MANAGER, request);
        int status = controller.service(reqCtx);
        if (status == ControllerManager.REDIRECT) {
        	_logger.debug("Redirection");
            this.redirect(reqCtx, response);
        } else if (status == ControllerManager.OUTPUT) {
        	_logger.debug("Output");
			try {
				Configuration config = new Configuration();
				DefaultObjectWrapper wrapper = new DefaultObjectWrapper();
				config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
				config.setObjectWrapper(wrapper);
				config.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
				TemplateModel templateModel = super.createModel(wrapper, this.getServletContext(), request, response);
				List<ExecutorServiceInterface> executors = 
						(List<ExecutorServiceInterface>) ApsWebApplicationUtils.getBean("ExecutorServices", request);
				for (int i = 0; i < executors.size(); i++) {
					ExecutorServiceInterface executor = executors.get(i);
					executor.service(config, templateModel, reqCtx);
				}
			} catch (Throwable t) {
				_logger.error("Error building response", t);
				throw new ServletException("Error building response", t);
			}
        } else if (status == ControllerManager.ERROR) {
			_logger.debug("Error");
            this.outputError(reqCtx, response);
        } else {
        	_logger.error("Error: final status = {} - request: {}", ControllerManager.getStatusDescription(status),request.getServletPath());
            throw new ServletException("Service not available");
        }
        return;
    }
    
    private void redirect(RequestContext reqCtx, HttpServletResponse response) throws ServletException {
        try {
            String url = (String) reqCtx.getExtraParam(RequestContext.EXTRAPAR_REDIRECT_URL);
            response.sendRedirect(url);
        } catch (Exception e) {
            throw new ServletException("Service not available", e);
        }
    }
    
    private void outputError(RequestContext reqCtx, HttpServletResponse response)
            throws ServletException {
        try {
            if (!response.isCommitted()) {
                Integer httpErrorCode = (Integer) reqCtx.getExtraParam("errorCode");
                if (httpErrorCode == null) {
                    httpErrorCode = new Integer(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                }
                response.sendError(httpErrorCode.intValue());
            }
        } catch (IOException e) {
        	_logger.error("outputError", e);
            //ApsSystemUtils.logThrowable(e, this, "outputError");
            throw new ServletException("Service not available");
        }
    }
    
}
