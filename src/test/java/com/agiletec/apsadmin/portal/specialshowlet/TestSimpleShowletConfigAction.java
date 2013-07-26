/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software. 
* Entando is a free software;
* You can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.apsadmin.portal.specialshowlet;

import org.entando.entando.aps.system.services.page.IPage;
import org.entando.entando.aps.system.services.page.IPageManager;
import org.entando.entando.aps.system.services.page.Showlet;

import com.agiletec.apsadmin.ApsAdminBaseTestCase;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.apsadmin.portal.specialshowlet.ISimpleShowletConfigAction;
import com.opensymphony.xwork2.Action;

/**
 * @version 1.0
 * @author E.Santoboni
 */
public class TestSimpleShowletConfigAction extends ApsAdminBaseTestCase {
	
	protected void setUp() throws Exception {
        super.setUp();
        this.init();
    }
	
	public void testInitConfigSimpleParameter_1() throws Throwable {
		String result = this.executeConfigSimpleParameter("admin", "homepage", "1", "formAction");
		assertEquals(Action.SUCCESS, result);
		ISimpleShowletConfigAction action = (ISimpleShowletConfigAction) this.getAction();
		Showlet showlet = action.getShowlet();
		assertNotNull(showlet);
		assertEquals(0, showlet.getConfig().size());
	}
	
	public void testInitConfigSimpleParameter_withNoShowletCode() throws Throwable {
		String result = this.executeConfigSimpleParameter("admin", "homepage", "1", null);
		assertEquals("pageTree", result);
		assertEquals(1, this.getAction().getActionErrors().size());
	}
	
	public void testInitConfigSimpleParameter_2() throws Throwable {
		String result = this.executeConfigSimpleParameter("admin", "pagina_2", "2", null);
		assertEquals(Action.SUCCESS, result);
		ISimpleShowletConfigAction action = (ISimpleShowletConfigAction) this.getAction();
		Showlet showlet = action.getShowlet();
		assertNotNull(showlet);
		ApsProperties props = showlet.getConfig();
		assertEquals(1, props.size());
		String value = props.getProperty("actionPath");
		assertEquals("/do/login", value);
	}
	
	private String executeConfigSimpleParameter(String userName, 
			String pageCode, String frame, String showletTypeCode) throws Throwable {
		this.setUserOnSession(userName);
		this.initAction("/do/Page/SpecialShowlet", "configSimpleParameter");
		this.addParameter("pageCode", pageCode);
		this.addParameter("frame", frame);
		if (null != showletTypeCode && showletTypeCode.trim().length()>0) {
			this.addParameter("showletTypeCode", showletTypeCode);
		}
		return this.executeAction();
	}
	
	public void testSave() throws Throwable {
		String pageCode = "pagina_2";
		int frame = 0;
		IPage page = this._pageManager.getPage(pageCode);
		Showlet showlet = page.getShowlets()[frame];
		assertNull(showlet);
		try {
			this.setUserOnSession("admin");
			this.initAction("/do/Page/SpecialShowlet", "saveConfigSimpleParameter");
			this.addParameter("pageCode", pageCode);
			this.addParameter("frame", String.valueOf(frame));
			this.addParameter("showletTypeCode", "formAction");
			this.addParameter("actionPath", "/WEB-INF/pippo.jsp");
			String result = this.executeAction();
			assertEquals("configure", result);
			page = this._pageManager.getPage(pageCode);
			showlet = page.getShowlets()[frame];
			assertNotNull(showlet);
			assertEquals("formAction", showlet.getType().getCode());
			assertEquals(1, showlet.getConfig().size());
			assertEquals("/WEB-INF/pippo.jsp", showlet.getConfig().getProperty("actionPath"));
		} catch (Throwable t) {
			throw t;
		} finally {
			page = this._pageManager.getPage(pageCode);
			page.getShowlets()[frame] = null;
			this._pageManager.updatePage(page);
		}
	}
	
	private void init() throws Exception {
    	try {
    		_pageManager = (IPageManager) this.getService(SystemConstants.PAGE_MANAGER);
    	} catch (Throwable t) {
            throw new Exception(t);
        }
    }
    
    private IPageManager _pageManager = null;

}
