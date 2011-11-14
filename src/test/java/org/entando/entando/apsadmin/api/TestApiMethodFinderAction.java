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
package org.entando.entando.apsadmin.api;

import javax.sql.DataSource;

import org.entando.entando.aps.system.services.api.ApiTestHelperDAO;
import org.entando.entando.aps.system.services.api.IApiCatalogManager;
import org.entando.entando.aps.system.services.api.model.ApiMethod;

import com.agiletec.aps.system.SystemConstants;
import com.opensymphony.xwork2.Action;

import com.agiletec.apsadmin.ApsAdminBaseTestCase;

/**
 * @author E.Santoboni
 */
public class TestApiMethodFinderAction extends ApsAdminBaseTestCase {
	
	@Override
	protected void setUp() throws Exception {
        super.setUp();
        this.init();
    }
	
	public void testMethodList() throws Throwable {
		String result = this.executeListMethods("admin");
		assertEquals(Action.SUCCESS, result);
		
		result = this.executeListMethods(null);
		assertEquals("apslogin", result);
	}
	
	public void testUpdateAllStatus_1() throws Throwable {
		try {
			ApiMethod method = this._apiCatalogManager.getMethod("getServices");
			assertTrue(method.isActive());
			this.setUserOnSession("admin");
			this.initAction("/do/Api/Method", "updateAllStatus");
			String result = this.executeAction();
			assertEquals(Action.SUCCESS, result);
			assertFalse(this.getAction().hasActionMessages());
			method = this._apiCatalogManager.getMethod("getServices");
			assertTrue(method.isActive());
		} catch (Throwable t) {
			throw t;
		}
	}
	
	public void testUpdateAllStatus_2() throws Throwable {
		try {
			ApiMethod method = this._apiCatalogManager.getMethod("getServices");
			assertTrue(method.isActive());
			this.setUserOnSession("admin");
			this.initAction("/do/Api/Method", "updateAllStatus");
			this.addParameter("getServices_checkField", "true");
			String result = this.executeAction();
			assertEquals(Action.SUCCESS, result);
			assertTrue(this.getAction().hasActionMessages());
			assertEquals(1, this.getAction().getActionMessages().size());
			method = this._apiCatalogManager.getMethod("getServices");
			assertFalse(method.isActive());
		} catch (Throwable t) {
			throw t;
		}
	}
	
	public void testUpdateAllStatus_3() throws Throwable {
		try {
			ApiMethod method = this._apiCatalogManager.getMethod("getServices");
			assertTrue(method.isActive());
			this.setUserOnSession("admin");
			this.initAction("/do/Api/Method", "updateAllStatus");
			this.addParameter("getServices_checkField", "true");
			String result = this.executeAction();
			assertEquals(Action.SUCCESS, result);
			assertTrue(this.getAction().hasActionMessages());
			assertEquals(1, this.getAction().getActionMessages().size());
			method = this._apiCatalogManager.getMethod("getServices");
			assertFalse(method.isActive());
			
			this.setUserOnSession("admin");
			this.initAction("/do/Api/Method", "updateAllStatus");
			this.addParameter("getServices_active", "true");
			this.addParameter("getServices_checkField", "true");
			result = this.executeAction();
			assertEquals(Action.SUCCESS, result);
			assertTrue(this.getAction().hasActionMessages());
			assertEquals(1, this.getAction().getActionMessages().size());
			method = this._apiCatalogManager.getMethod("getServices");
			assertTrue(method.isActive());
		} catch (Throwable t) {
			throw t;
		}
	}
	
	private String executeListMethods(String username) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/Api/Method", "list");
		return this.executeAction();
	}
	
	private void init() throws Exception {
    	try {
    		this._apiCatalogManager = (IApiCatalogManager) this.getService(SystemConstants.API_CATALOG_MANAGER);
    	} catch (Throwable t) {
            throw new Exception(t);
        }
    }
	
    @Override
	protected void tearDown() throws Exception {
    	try {
    		ApiTestHelperDAO helperDao = new ApiTestHelperDAO();
    		DataSource dataSource = (DataSource) this.getApplicationContext().getBean("servDataSource");
    		helperDao.setDataSource(dataSource);
    		helperDao.cleanApiStatus();
    		//helperDao.cleanServices();
    		super.tearDown();
    	} catch (Throwable t) {
    		throw new Exception(t);
        }
	}
    
    private IApiCatalogManager _apiCatalogManager = null;
	
}