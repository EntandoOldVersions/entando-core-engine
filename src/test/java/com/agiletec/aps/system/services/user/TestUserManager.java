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
package com.agiletec.aps.system.services.user;

import java.util.Date;
import java.util.List;

import com.agiletec.aps.BaseTestCase;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.User;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.DateConverter;

/**
 * @version 1.0
 * @author M.Casari
 */
public class TestUserManager extends BaseTestCase {
	
    protected void setUp() throws Exception {
        super.setUp();
        this.init();
    }
    
    public void testGetUsers() throws Throwable {
		List<UserDetails> users = this._userManager.getUsers();
		assertTrue(users.size()>=8);
    }
    
    public void testSearchUsers() throws Throwable {
		List<UserDetails> users = this._userManager.searchUsers("ustomer");
		assertEquals(3, users.size());
		
		users = this._userManager.searchUsers("anager");
		assertEquals(2, users.size());
		
		users = this._userManager.searchUsers("");
		assertTrue(users.size()>=8);
		
		users = this._userManager.searchUsers(null);
		assertTrue(users.size()>=8);
    }
    
    public void testGetUser_1() throws Throwable {
    	UserDetails adminUser = this._userManager.getUser("admin");
    	assertEquals("admin", adminUser.getUsername());
    	assertEquals("admin", adminUser.getPassword());
    	assertEquals(0, adminUser.getAuthorities().length);
    	
    	UserDetails nullUser = this._userManager.getUser("wrongUserName");
    	assertNull(nullUser);
    	
    	UserDetails guest = this._userManager.getGuestUser();
    	assertEquals(SystemConstants.GUEST_USER_NAME, guest.getUsername());
    	assertNull(guest.getPassword());
    	assertEquals(0, guest.getAuthorities().length);
    }
    
    public void testGetUser_2() throws Throwable {
    	UserDetails adminUser = this._userManager.getUser("admin", "admin");
    	assertEquals("admin", adminUser.getUsername());
    	assertEquals("admin", adminUser.getPassword());
    	assertEquals(0, adminUser.getAuthorities().length);
    	
    	adminUser = this._userManager.getUser("admin", "wrongPassword");
    	assertNull(adminUser);
    	
    	UserDetails user = this._userManager.getUser("guest", "guest");
    	assertNull(user);
    }
    
    public void testAddDeleteUser() throws Throwable {
    	String username = "UserForTest1";
		String todayDateString = DateConverter.getFormattedDate(new Date(), "dd/MM/yyyy");
    	MockUser user = this.createUserForTest(username);
		try {
			UserDetails extractedUser = this._userManager.getUser(username, user.getPassword());
			assertNull(extractedUser);
			this._userManager.addUser(user);
			
			extractedUser = this._userManager.getUser(username, user.getPassword());
			assertNotNull(extractedUser);
			assertTrue(extractedUser.isJapsUser());
			assertEquals(user.getUsername(), extractedUser.getUsername());
			Date creationDate = ((User) extractedUser).getCreationDate();
			assertEquals(todayDateString, DateConverter.getFormattedDate(creationDate, "dd/MM/yyyy"));
		} catch (Throwable t) {
			throw t;
		} finally {
			this._userManager.removeUser(user);
			UserDetails extractedUser = this._userManager.getUser(username);
			assertNull(extractedUser);
		}
	}
    
    public void testUpdateUser() throws Throwable {
    	String username = "UserForTest2";
		MockUser user = this.createUserForTest(username);
		try {
			UserDetails extractedUser = this._userManager.getUser(username);
			assertNull(extractedUser);
			this._userManager.addUser(user);
			
			extractedUser = this._userManager.getUser(username, user.getPassword());
			assertNotNull(extractedUser);
			assertEquals(user.getUsername(), extractedUser.getUsername());
			assertEquals(0, extractedUser.getAuthorities().length);
			
			user.setPassword("changedPassword");
			this._userManager.updateUser(user);
			// l'aggiornamento non comporta la criptazione della password
			extractedUser = this._userManager.getUser(username);
			assertEquals(user.getUsername(), extractedUser.getUsername());
			assertEquals("changedPassword", extractedUser.getPassword());
			extractedUser = this._userManager.getUser(username);
			assertNotNull(extractedUser);
		} catch (Throwable t) {
			throw t;
		} finally {
			this._userManager.removeUser(user);
			UserDetails extractedUser = _userManager.getUser(username);
			assertNull(extractedUser);
		}
	}
    
    public void testChangePassword() throws Throwable {
    	String username = "UserForTest3";
		String todayDateString = DateConverter.getFormattedDate(new Date(), "dd/MM/yyyy");
    	MockUser user = this.createUserForTest(username);
		try {
			UserDetails extractedUser = this._userManager.getUser(username, user.getPassword());
			assertNull(extractedUser);
			this._userManager.addUser(user);
			
			extractedUser = this._userManager.getUser(username, user.getPassword());
			Date creationDate = ((User) extractedUser).getCreationDate();
			assertEquals(todayDateString, DateConverter.getFormattedDate(creationDate, "dd/MM/yyyy"));
			assertNull(((User) extractedUser).getLastPasswordChange());
			
			String newPassword = "newPassword";
			this._userManager.changePassword(username, newPassword);
			extractedUser = this._userManager.getUser(username, newPassword);
			assertNotNull(extractedUser);
			Date lastPasswordChange = ((User) extractedUser).getLastPasswordChange();
			assertEquals(todayDateString, DateConverter.getFormattedDate(lastPasswordChange, "dd/MM/yyyy"));
		} catch (Throwable t) {
			throw t;
		} finally {
			this._userManager.removeUser(user);
			UserDetails extractedUser = this._userManager.getUser(username);
			assertNull(extractedUser);
		}
	}
    
    //TODO FARE TEST PER OPERAZIONI SPECIALI SU UTENTE (VERIFICA DATE ACCESSI E CAMBIO PASSWORD)
    
    private void init() throws Exception {
    	try {
    		this._userManager = (IUserManager) this.getService(SystemConstants.USER_MANAGER);
		} catch (Throwable e) {
			throw new Exception(e);
		}
    }
    
    protected MockUser createUserForTest(String username) {
    	MockUser user = new MockUser();
		user.setUsername(username);
        user.setPassword("temp");
        return user;
	}
	
	private IUserManager _userManager = null;
	
}
