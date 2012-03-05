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
package org.entando.entando.apsadmin.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.logging.Level;
import org.entando.entando.aps.system.services.api.model.ApiMethod;

import com.agiletec.aps.system.ApsSystemUtils;

/**
 * @author E.Santoboni
 * @deprecated 
 */
public class ApiMethodFinderAction extends AbstractApiFinderAction implements IApiMethodFinderAction {
	
	@Override
	public String updateAllStatus() {
		try {
			Map<String, ApiMethod> methodMap = this.getApiCatalogManager().getMethods();
			List<ApiMethod> methods = new ArrayList<ApiMethod>(methodMap.values());
			for (int i = 0; i < methods.size(); i++) {
				ApiMethod apiMethod = methods.get(i);
				boolean activeMethod = (this.getRequest().getParameter(apiMethod.getMethodName() + "_active") != null);
				boolean checkParameter = (this.getRequest().getParameter(apiMethod.getMethodName() + "_checkField") != null);
				if (checkParameter && (activeMethod != apiMethod.isActive())) {
					apiMethod.setStatus(activeMethod);
					this.getApiCatalogManager().updateApiStatus(apiMethod);
					this.addActionMessage(this.getText("message.method.status.updated", new String[]{apiMethod.getMethodName()}));
					ApsSystemUtils.getLogger().log(Level.INFO, "Updated api method status - Method ''{0}''", apiMethod.getMethodName());
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateAllStatus");
			return FAILURE;
		}
		return SUCCESS;
	}
	
}