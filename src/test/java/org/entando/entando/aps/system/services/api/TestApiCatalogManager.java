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
 * @author E.Santoboni
 */
public class TestApiCatalogManager extends ApiBaseTestCase {
	
    public void testGetMethod() throws Throwable {
    	ApiMethod method = this.getApiCatalogManager().getMethod("getService");
    	assertNotNull(method);
    	assertTrue(method.isActive());
    }
    
    public void testGetMethods() throws Throwable {
    	Map<String, ApiMethod> methods = this.getApiCatalogManager().getMethods();
    	assertNotNull(methods);
    	assertTrue(methods.size() > 0);
    }
    
    public void testUpdateMethodStatus() throws Throwable {
    	ApiMethod method = this.getApiCatalogManager().getMethod("getService");
    	method.setActive(false);
    	this.getApiCatalogManager().updateApiStatus(method);
    	method = this.getApiCatalogManager().getMethod("getService");
    	assertFalse(method.isActive());
    }
    
    public void testGetServices() throws Throwable {
    	Map<String, ApiService> services = this.getApiCatalogManager().getApiServices();
    	assertNotNull(services);
    	assertTrue(services.size() == 0);
    }
    
}