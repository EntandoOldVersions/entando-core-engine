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
package org.entando.entando.aps.system.services.api;

import java.util.Map;

import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.ApiService;


/**
 * Interfrace for service Objects
 * @author E.Santoboni
 */
public interface IApiCatalogDAO {
	
	public void loadApiStatus(Map<String, ApiMethod> methods);
	
	public void saveApiStatus(ApiMethod method);
	
	public Map<String, ApiService> loadServices(Map<String, ApiMethod> methods);
	
	public void addService(ApiService service);
	
	public void updateService(ApiService service);
	
	public void deleteService(String key);
	
}