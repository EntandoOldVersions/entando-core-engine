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

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * Interceptor gestore della verifica delle autorizzazioni dell'utente corrente.
 * Verifica che l'utente corrente sia abilitato all'esecuzione dell'azione richiesta.
 * @author E.Santoboni
 */
public abstract class BaseInterceptorMadMax extends AbstractInterceptor {
	
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		boolean isAuthorized = false;
		try {
			HttpSession session = ServletActionContext.getRequest().getSession();
			UserDetails currentUser = (UserDetails) session.getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER);
			IAuthorizationManager authManager = (IAuthorizationManager) ApsWebApplicationUtils.getBean(SystemConstants.AUTHORIZATION_SERVICE, ServletActionContext.getRequest());
			if (currentUser != null) {
				String requiredPermission = this.getRequiredPermission();
				isAuthorized = (requiredPermission == null || authManager.isAuthOnPermission(currentUser, Permission.SUPERUSER) || 
						authManager.isAuthOnPermission(currentUser, requiredPermission));
				if (!isAuthorized) return this.getErrorResultName();
			}
			if (isAuthorized) {
				return this.invoke(invocation);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "intercept", "Error occurred verifying authority of current user");
			return BaseAction.FAILURE;
		}
		return this.getErrorResultName();
	}
	
	/**
	 * Restituisce il permesso specifico.
	 * @return Il permesso specifico.
	 */
	public abstract String getRequiredPermission();
	
	public abstract String getErrorResultName();
	
	/**
	 * Invokes the next step in processing this ActionInvocation. 
	 * @see com.opensymphony.xwork2.ActionInvocation#invoke()
	 * @return The code of the execution result.
	 */
	protected String invoke(ActionInvocation invocation) throws Exception {
		return invocation.invoke();
	}
	
}