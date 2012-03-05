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
package com.agiletec.apsadmin.user;

import java.util.List;

import com.agiletec.apsadmin.ApsAdminBaseTestCase;

import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.apsadmin.user.IUserFinderAction;
import com.opensymphony.xwork2.Action;

/**
 * @version 1.0
 * @author E.Santoboni
 */
public class TestUserFinderAction extends ApsAdminBaseTestCase {
	
	public void testListWithUserNotAllowed() throws Throwable {
		String result = this.executeList("developersConf");
		assertEquals("apslogin", result);
	}
	
	public void testList() throws Throwable {
		String result = this.executeList("admin");
		assertEquals(Action.SUCCESS, result);
		IUserFinderAction userFinderAction = (IUserFinderAction) this.getAction();
		List<UserDetails> users = userFinderAction.getUsers();
		assertFalse(users.isEmpty());
		assertTrue(users.size()>=8);
	}
	
    public void testSearchUsers() throws Throwable {
    	String result = this.executeSearch("admin", "ustomer");
    	assertEquals(Action.SUCCESS, result);
    	IUserFinderAction userFinderAction = (IUserFinderAction) this.getAction();
    	List<UserDetails> users = userFinderAction.getUsers();
		assertEquals(3, users.size());
		
		result = this.executeSearch("admin", "anager");
    	assertEquals(Action.SUCCESS, result);
    	userFinderAction = (IUserFinderAction) this.getAction();
    	users = userFinderAction.getUsers();
		assertEquals(2, users.size());
		
		result = this.executeSearch("admin", "");
		assertEquals(Action.SUCCESS, result);
    	userFinderAction = (IUserFinderAction) this.getAction();
    	users = userFinderAction.getUsers();
		assertTrue(users.size()>=8);
		
		result = this.executeSearch("admin", null);
		assertEquals(Action.SUCCESS, result);
    	userFinderAction = (IUserFinderAction) this.getAction();
    	users = userFinderAction.getUsers();
		assertTrue(users.size()>=8);
    }
	
	private String executeList(String currentUser) throws Throwable {
		this.setUserOnSession(currentUser);
		this.initAction("/do/User", "list");
		return this.executeAction();
	}
	
	private String executeSearch(String currentUser, String text) throws Throwable {
		this.setUserOnSession(currentUser);
		this.initAction("/do/User", "search");
		if (null != text) this.addParameter("text", text);
		return this.executeAction();
	}
	
}