/*
*
* Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
* This file is part of jAPS software.
* jAPS is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
*/
package com.agiletec.apsadmin.system;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * 
 * @author E.Santoboni
 */
public class BackendGuiClientInterceptor extends AbstractInterceptor {
	
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		Map<String, Object> params = invocation.getInvocationContext().getParameters();
		boolean storeInSession = false;
		HttpSession session = ServletActionContext.getRequest().getSession();
		String currentBackendGUiClient = (String) session.getAttribute(ApsAdminSystemConstants.SESSION_PARAM_BACKEND_GUI_CLIENT);
		Object backendGuiClient = this.findParameter(params, DEFAULT_PARAMETER);
		if (backendGuiClient != null) {
			storeInSession = true;
		} else if (null == currentBackendGUiClient) {
			backendGuiClient = ApsAdminSystemConstants.BACKEND_GUI_CLIENT_NORMAL;
			storeInSession = true;
		}
		if (storeInSession) {
			session.setAttribute(ApsAdminSystemConstants.SESSION_PARAM_BACKEND_GUI_CLIENT, backendGuiClient);
		}
		return invocation.invoke();
	}
	
	private Object findParameter(Map<String, Object> params, String parameterName ) {
		Object param = params.remove(parameterName);
		if (param != null && param.getClass().isArray() && ((Object[]) param).length == 1) {
			param = ((Object[]) param)[0];
		}
		return param;
	}
	
	public static final String DEFAULT_PARAMETER = "backend_client_gui";
	
}
