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
package com.agiletec.apsadmin.user.group;

import java.util.List;

import com.agiletec.apsadmin.ApsAdminBaseTestCase;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.apsadmin.user.group.IGroupFinderAction;
import com.opensymphony.xwork2.Action;

/**
 * @version 1.0
 * @author E.Mezzano
 */
public class TestGroupFinderAction extends ApsAdminBaseTestCase {
	
	public void testListWithUserNotAllowed() throws Throwable {
		String result = this.executeList("developersConf");
		assertEquals("apslogin", result);
	}
	
	public void testList() throws Throwable {
		String result = this.executeList("admin");
		assertEquals(Action.SUCCESS, result);
		IGroupFinderAction groupFinderAction = (IGroupFinderAction) this.getAction();
		List<Group> groups = groupFinderAction.getGroups();
		assertFalse(groups.isEmpty());
	}
	
	private String executeList(String currentUser) throws Throwable {
		this.setUserOnSession(currentUser);
		this.initAction("/do/Group", "list");
		return this.executeAction();
	}
	
}