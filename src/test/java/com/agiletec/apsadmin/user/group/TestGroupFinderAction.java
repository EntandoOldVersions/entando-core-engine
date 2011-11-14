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