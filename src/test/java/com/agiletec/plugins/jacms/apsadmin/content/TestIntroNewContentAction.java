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
package com.agiletec.plugins.jacms.apsadmin.content;

import java.util.List;
import java.util.Map;

import com.agiletec.apsadmin.ApsAdminBaseTestCase;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.apsadmin.content.ContentActionConstants;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author E.Santoboni
 */
public class TestIntroNewContentAction extends ApsAdminBaseTestCase {
	
	public void testOpenNew() throws Throwable {
		String result = this.executeOpenNew("admin");
		assertEquals(Action.SUCCESS, result);
		assertNull(this.getRequest().getSession().getAttribute(ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT));
		
		result = this.executeOpenNew("pageManagerCoach");
		assertEquals("userNotAllowed", result);
	}
	
	private String executeOpenNew(String currentUserName) throws Throwable {
		this.initAction("/do/jacms/Content", "new");
		this.setUserOnSession(currentUserName);
		return this.executeAction();
	}
	
	public void testCreateNewVoid() throws Throwable {
		this.initAction("/do/jacms/Content", "createNewVoid");
		this.setUserOnSession("admin");
		this.addParameter("contentTypeCode", "ART");
		String result = this.executeAction();
		assertEquals(Action.INPUT, result);
		Map<String, List<String>> fieldErrors = this.getAction().getFieldErrors();
		assertEquals(2, fieldErrors.size());
		assertEquals(1, fieldErrors.get("contentDescription").size());
		assertEquals(1, fieldErrors.get("contentMainGroup").size());
		
		Content content = (Content) this.getRequest().getSession().getAttribute(ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT);
		assertNull(content);
		
		this.initAction("/do/jacms/Content", "createNewVoid");
		this.setUserOnSession("admin");
		this.addParameter("contentTypeCode", "ART");
		this.addParameter("contentDescription", "Descrizione di prova");
		this.addParameter("contentMainGroup", Group.FREE_GROUP_NAME);
		result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		
		content = (Content) this.getRequest().getSession().getAttribute(ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT);
		assertNotNull(content);
		assertEquals("ART", content.getTypeCode());
	}
	
	public void testCreateNewVoid_2() throws Throwable {
		this.initAction("/do/jacms/Content", "createNewVoid");
		this.setUserOnSession("admin");
		String result = this.executeAction();
		assertEquals(Action.INPUT, result);
		ActionSupport action = this.getAction();
		Map<String, List<String>> fieldErros = action.getFieldErrors();
		assertEquals(3, fieldErros.size());
	}
	
	public void testCreateNewVoid_3() throws Throwable {
		this.initAction("/do/jacms/Content", "createNewVoid");
		this.setUserOnSession("admin");
		this.addParameter("contentTypeCode", "ART");
		this.addParameter("contentDescription", "");
		String result = this.executeAction();
		assertEquals(Action.INPUT, result);
		ActionSupport action = this.getAction();
		Map<String, List<String>> fieldErros = action.getFieldErrors();
		assertEquals(2, fieldErros.size());
		assertEquals(1, fieldErros.get("contentDescription").size());
		assertEquals(1, fieldErros.get("contentMainGroup").size());
	}
	
	public void testCreateNewVoid_4() throws Throwable {
		this.initAction("/do/jacms/Content", "createNewVoid");
		this.setUserOnSession("admin");
		this.addParameter("contentTypeCode", "ART");
		this.addParameter("contentDescription", "Description");
		this.addParameter("contentMainGroup", "invalidGroupCode");
		String result = this.executeAction();
		assertEquals(Action.INPUT, result);
		ActionSupport action = this.getAction();
		Map<String, List<String>> fieldErros = action.getFieldErrors();
		assertEquals(1, fieldErros.size());
		assertEquals(1, fieldErros.get("contentMainGroup").size());
	}
	
	public void testCreateNewVoid_5() throws Throwable {
		this.setUserOnSession("editorCustomers");
		this.initAction("/do/jacms/Content", "createNewVoid");
		this.addParameter("contentTypeCode", "ART");
		this.addParameter("contentDescription", "Description");
		this.addParameter("contentMainGroup", Group.FREE_GROUP_NAME);
		String result = this.executeAction();
		assertEquals(Action.INPUT, result);
		ActionSupport action = this.getAction();
		Map<String, List<String>> fieldErros = action.getFieldErrors();
		assertEquals(1, fieldErros.size());
		assertEquals(1, fieldErros.get("contentMainGroup").size());
		
		this.initAction("/do/jacms/Content", "createNewVoid");
		this.addParameter("contentTypeCode", "ART");
		this.addParameter("contentDescription", "Description");
		this.addParameter("contentMainGroup", "customers");
		result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
	}
	
}