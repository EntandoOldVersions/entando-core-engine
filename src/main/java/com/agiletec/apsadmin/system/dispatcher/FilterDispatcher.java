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
package com.agiletec.apsadmin.system.dispatcher;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterConfig;

import org.apache.struts2.dispatcher.Dispatcher;

import com.agiletec.apsadmin.system.ApsAdminSystemConstants;

/**
 * Estensione del Filtro base di Struts2.
 * L'estensione permettere di locale la configurazione base di Struts2 (la definizione dei file di configurazione) 
 * al di fuori della configurazione del filtro ma sempre all'interno del Deployment Descriptor (web.xml).
 * Il nome del parametro di configurazione (init-param del web.xml) viene definito nelle costanti di sistema 
 * nell'interfaccia {@link ApsAdminSystemConstants}.
 * @author E.Santoboni
 * @deprecated use {@link StrutsPrepareAndExecuteFilter}
 */
public class FilterDispatcher extends org.apache.struts2.dispatcher.FilterDispatcher {
	
	@Override
	protected Dispatcher createDispatcher(FilterConfig filterConfig) {
		Map<String, String> params = new HashMap<String, String>();
		for (Enumeration e = filterConfig.getInitParameterNames(); e.hasMoreElements();) {
			String name = (String) e.nextElement();
			String value = filterConfig.getInitParameter(name);
			params.put(name, value);
		}
		String struts2Config = this.getServletContext().getInitParameter(ApsAdminSystemConstants.STRUTS2_CONFIG_INIT_PARAM_NAME);
		if (null != struts2Config) {
			params.put("config", struts2Config);
		}
		return new Dispatcher(filterConfig.getServletContext(), params);
	}
	
}
