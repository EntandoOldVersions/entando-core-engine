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
package com.agiletec.aps.system.services.controller.control;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.services.controller.ControllerManager;

/**
 * Implementazione del sottoservizio di controllo che 
 * genera l'output destinato al client. Questa implementazione 
 * invoca la jsp "/WEB-INF/aps/jsp/system/main.jsp"
 * @author M.Diana
 */
public class Executor implements ControlServiceInterface {

	private static final Logger _logger = LoggerFactory.getLogger(Executor.class);
	
	@Override
	public void afterPropertiesSet() throws Exception {
		_logger.debug("{} : initialized", this.getClass().getName());
	}
	
	@Override
	public int service(RequestContext reqCtx, int status) {
		int retStatus = ControllerManager.INVALID_STATUS;
		if (status == ControllerManager.ERROR) {
			return status;
		}
		try {
			HttpServletResponse resp = reqCtx.getResponse();
			HttpServletRequest req = reqCtx.getRequest();
			String jspPath = "/WEB-INF/aps/jsp/system/main.jsp";
			req.setCharacterEncoding("UTF-8");
			RequestDispatcher dispatcher = req.getRequestDispatcher(jspPath);
			dispatcher.forward(req, resp);
			_logger.debug("Executed forward to {}", jspPath);
			retStatus = ControllerManager.OUTPUT;
		} catch (ServletException t) {
			_logger.error("Error while building page portal", t);
			//ApsSystemUtils.logThrowable(t, this, "service", "Error while building page portal");
			retStatus = ControllerManager.ERROR;
			reqCtx.setHTTPError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (Throwable t) {
			_logger.error("Error while forwarding to main.jsp", t);
			//ApsSystemUtils.logThrowable(t, this, "service", "Error while forwarding to main.jsp");
			retStatus = ControllerManager.SYS_ERROR;
			reqCtx.setHTTPError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		return retStatus;
	}

}
