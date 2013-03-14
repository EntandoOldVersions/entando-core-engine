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
package com.agiletec.apsadmin.common;

import com.agiletec.apsadmin.ApsAdminBaseTestCase;

import com.opensymphony.xwork2.Action;

/**
 * @version 1.0
 * @author E.Santoboni
 */
public class TestDispatchForward extends ApsAdminBaseTestCase {
	
	public void testGoOnMainPage() throws Throwable {
    	this.initAction("/do", "main");
    	this.setUserOnSession("admin");
    	String result = super.executeAction();
		assertEquals(Action.SUCCESS, result);
	}
	
    public void testGoOnMainPageWithUserNotAbilitated() throws Throwable {
    	this.initAction("/do", "main");
    	this.setUserOnSession("guest");
    	String result = super.executeAction();
		assertEquals("apslogin", result);
	}
    
    public void testGoOnMainPageWithNullUser() throws Throwable {
    	this.initAction("/do", "main");
    	this.removeUserOnSession();
    	String result = super.executeAction();
		assertEquals("apslogin", result);
	}
    
}
