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
package com.agiletec.apsadmin.admin;

import java.util.Map;

import com.agiletec.apsadmin.ApsAdminBaseTestCase;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.apsadmin.admin.BaseAdminAction;
import com.agiletec.apsadmin.admin.IBaseAdminAction;
import com.opensymphony.xwork2.Action;

/**
 * @author E.Santoboni
 */
public class TestBaseAdminAction extends ApsAdminBaseTestCase {
	
	public void testReloadConfig() throws Throwable {
		this.setUserOnSession("supervisorCoach");
		this.initAction("/do/BaseAdmin", "reloadConfig");
		String result = this.executeAction();
		assertEquals("userNotAllowed", result);
		
		this.setUserOnSession("admin");
		this.initAction("/do/BaseAdmin", "reloadConfig");
		result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		assertEquals(BaseAdminAction.SUCCESS_RELOADING_RESULT_CODE, ((BaseAdminAction) this.getAction()).getReloadingResult());
	}
	
	public void testReloadEntitiesReferences() throws Throwable {
		this.setUserOnSession("supervisorCoach");
		this.initAction("/do/BaseAdmin", "reloadEntitiesReferences");
		String result = this.executeAction();
		assertEquals("userNotAllowed", result);
		
		this.setUserOnSession("admin");
		this.initAction("/do/BaseAdmin", "reloadEntitiesReferences");
		result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
	}
	
	public void testConfigSystemParams() throws Throwable {
		this.setUserOnSession("admin");
		this.initAction("/do/BaseAdmin", "configSystemParams");
		String result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		
		IBaseAdminAction action = (IBaseAdminAction) this.getAction();
		Map<String, String> params = action.getSystemParams();
		assertTrue(params.size()>=6);
		assertEquals("homepage", params.get(SystemConstants.CONFIG_PARAM_HOMEPAGE_PAGE_CODE));
	}
	
}
