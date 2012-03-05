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
package org.entando.entando.aps.system.services.api;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.entando.entando.aps.system.services.api.model.ApiError;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.api.model.ApiMethod;


/**
 * @author E.Santoboni
 */
public class TestResponseBuilder extends ApiBaseTestCase {
	
    public void testInvoke() throws Throwable {
    	Properties properties = new Properties();
    	Object services = this.getResponseBuilder().invoke("getServices", properties);
    	assertNotNull(services);
    	assertTrue(services instanceof Collection);
    }

    public void testInvokeWithErrors_1() throws Throwable {
    	Properties properties = new Properties();
    	this.testInvokeWithErrors("unknowApiMethod", properties, IApiErrorCodes.API_INVALID);
    }

    public void testInvokeWithErrors_2() throws Throwable {
    	Properties properties = new Properties();
    	this.testInvokeWithErrors("getService", properties, IApiErrorCodes.API_PARAMETER_REQUIRED);
    }
    
    public void testInvokeWithErrors_3() throws Throwable {
    	Properties properties = new Properties();
    	properties.put("key", "unknowServices");
    	this.testInvokeWithErrors("getService", properties, IApiErrorCodes.API_SERVICE_INVALID);
    }
    
    public void testInvokeWithErrors_4() throws Throwable {
    	ApiMethod method = this.getApiCatalogManager().getMethod("getServices");
    	method.setStatus(false);
    	this.getApiCatalogManager().updateApiStatus(method);
    	Properties properties = new Properties();
    	this.testInvokeWithErrors("getServices", properties, IApiErrorCodes.API_ACTIVE_FALSE);
    }
    
    private void testInvokeWithErrors(String methodName, Properties properties, String expectedErrorCode) throws Throwable {
    	Object response = null;
		try {
			response = this.getResponseBuilder().invoke(methodName, properties);
			fail();
		} catch (ApiException e) {
			List<ApiError> errors = e.getErrors();
			assertEquals(1, errors.size());
			ApiError error = errors.get(0);
			assertEquals(expectedErrorCode, error.getCode());
		} catch (Throwable t) {
			fail();
		}
    	assertNull(response);
    }
    
}
