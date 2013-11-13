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
package com.agiletec.apsadmin.system;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * Interceptor per la gestione degli "Aps Action Params", 
 * parametri inseriti nei nomi delle action invocate.
 * L'interceptor intercetta i parametri il cui nome è strutturato secondo la sintassi:<br />
 * &#60;ACTION_NAME&#62;?&#60;PARAM_NAME_1&#62;=&#60;PARAM_VALUE_1&#62;;&#60;PARAM_NAME_2&#62;=&#60;PARAM_VALUE_2&#62;;....;&#60;PARAM_NAME_N&#62;=&#60;PARAM_VALUE_N&#62;
 * <br />
 * L'interceptor effettua il parsing della stringa inserendo i parametri estratti nella richiesta corrente 
 * in maniera tale che vengano intercettati dal successivo "Parameters Interceptor" di default del sistema.
 * @author E.Santoboni
 */
public class ApsActionParamsInterceptor extends AbstractInterceptor {
	
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		String entandoActionName = null;
		Enumeration params = request.getParameterNames();
        while (params.hasMoreElements()) {
        	String pname = (String) params.nextElement();
        	if (pname.startsWith("action:")) {
        		entandoActionName = pname.substring("action:".length());
        		break;
        	}
        	if (pname.startsWith("entandoaction:")) {
        		entandoActionName = pname.substring("entandoaction:".length());
        		break;
        	}
        }
        if (null != entandoActionName) {
        	if (entandoActionName.endsWith(".x") || entandoActionName.endsWith(".y")) {
            	entandoActionName = entandoActionName.substring(0, entandoActionName.length()-2);
            }
        	if (entandoActionName.indexOf("?") >= 0 ) {
        		this.createApsActionParam(entandoActionName, invocation);
        	}
        }
        return invocation.invoke();
	}
	
	private void createApsActionParam(String entandoActionName, ActionInvocation invocation) {
		String[] blocks = entandoActionName.split("[?]");
		if (blocks.length == 2) {
			String paramBlock = blocks[1];
			String[] params = paramBlock.split(";");
			for (int i=0; i<params.length; i++) {
				Map parameters = invocation.getInvocationContext().getParameters();
				HttpServletRequest request = ServletActionContext.getRequest();
				String[] parameter = params[i].split("=");
				if (parameter.length == 2) {
					request.setAttribute(parameter[0], parameter[1]);
					parameters.put(parameter[0], parameter[1]);
				}
			}
		}
	}
	
}
