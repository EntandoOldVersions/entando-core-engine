/*
*
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* Entando is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.aps.system.services.controller.control;

import java.util.logging.Level;

import javax.servlet.http.HttpServletResponse;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.controller.ControllerManager;
import com.agiletec.aps.system.services.url.PageURL;

/**
 * Implementazione del sottoservizio di controllo che gestisce gli errori
 * @author 
 */
public class ErrorManager extends AbstractControlService {
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this._errorPageCode = this.getConfigManager().getParam(SystemConstants.CONFIG_PARAM_ERROR_PAGE_CODE);
		this._log.config(this.getClass().getName() + ": initialized");
	}
	
	@Override
	public int service(RequestContext reqCtx, int status) {
		int retStatus = ControllerManager.INVALID_STATUS;
		this._log.finer("Intervention of the error service");
		try {
			PageURL url = this.getUrlManager().createURL(reqCtx);
			url.setPageCode(this._errorPageCode);
			String redirUrl = url.getURL();
			if(_log.isLoggable(Level.FINEST)) {
				_log.finest("Redirecting to " + redirUrl);
			}
			reqCtx.clearError();
			reqCtx.addExtraParam(RequestContext.EXTRAPAR_REDIRECT_URL, redirUrl);
			retStatus = ControllerManager.REDIRECT;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "service", "Error detected while processing the request");
			retStatus = ControllerManager.SYS_ERROR;
			reqCtx.setHTTPError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}
		return retStatus;
	}
	
	protected ConfigInterface getConfigManager() {
		return _configManager;
	}
	public void setConfigManager(ConfigInterface configService) {
		this._configManager = configService;
	}
	
	private  String _errorPageCode ;
	
	private ConfigInterface _configManager;
	
}