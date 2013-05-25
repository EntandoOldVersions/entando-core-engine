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
package com.agiletec.plugins.jacms.apsadmin.portal.specialshowlet.viewer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.agiletec.apsadmin.ApsAdminBaseTestCase;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.plugins.jacms.apsadmin.portal.specialshowlet.viewer.IContentViewerShowletAction;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author E.Santoboni
 */
public class TestContentViewerShowletAction extends ApsAdminBaseTestCase {
	
	@Override
	protected void setUp() throws Exception {
        super.setUp();
        this.init();
    }
	
	public void testInitConfigViewerwithNoShowletCode() throws Throwable {
		String result = this.executeConfigViewer("admin", "homepage", "1", null);
		assertEquals("pageTree", result);
		assertEquals(1, this.getAction().getActionErrors().size());
	}
	
	public void testInitConfigViewer_1() throws Throwable {
		String result = this.executeConfigViewer("admin", "homepage", "1", "content_viewer");
		assertEquals(Action.SUCCESS, result);
		IContentViewerShowletAction action = (IContentViewerShowletAction) this.getAction();
		Showlet showlet = action.getShowlet();
		assertNotNull(showlet);
		assertEquals(0, showlet.getConfig().size());
	}
	
	public void testInitConfigViewer_2() throws Throwable {
		String result = this.executeConfigViewer("admin", "homepage", "2", null);
		assertEquals(Action.SUCCESS, result);
		IContentViewerShowletAction action = (IContentViewerShowletAction) this.getAction();
		Showlet showlet = action.getShowlet();
		assertNotNull(showlet);
		ApsProperties props = showlet.getConfig();
		assertEquals(2, props.size());
		assertEquals("ART1", props.getProperty("contentId"));
		assertEquals("2", props.getProperty("modelId"));
	}
	
	private String executeConfigViewer(String userName, 
			String pageCode, String frame, String showletTypeCode) throws Throwable {
		this.setUserOnSession(userName);
		this.initAction("/do/Page/SpecialShowlet", "viewerConfig");
		this.addParameter("pageCode", pageCode);
		this.addParameter("frame", frame);
		if (null != showletTypeCode && showletTypeCode.trim().length()>0) {
			this.addParameter("showletTypeCode", showletTypeCode);
		}
		return this.executeAction();
	}
	
	public void testFailureJoinContent_1() throws Throwable {
		String result = this.executeJoinContent("admin", "pagina_11", "1", null);//ID Nullo
		assertEquals(Action.INPUT, result);
		
		ActionSupport action = this.getAction();
		Map<String, List<String>> fieldErrors = action.getFieldErrors();
		assertEquals(1, fieldErrors.size());
		List<String> contentIdFieldErrors = fieldErrors.get("contentId");
		assertEquals(1, contentIdFieldErrors.size());
	}
	
	public void testFailureJoinContent_2() throws Throwable {
		String result = this.executeJoinContent("admin", "pagina_11", "1", "ART179");//ID di contenuto non pubblico
		assertEquals(Action.INPUT, result);
		
		ActionSupport action = this.getAction();
		Map<String, List<String>> fieldErrors = action.getFieldErrors();
		assertEquals(1, fieldErrors.size());
		List<String> contentIdFieldErrors = (List<String>) fieldErrors.get("contentId");
		assertEquals(1, contentIdFieldErrors.size());
	}
	
	public void testFailureJoinContent_3() throws Throwable {
		String result = this.executeJoinContent("admin", "pagina_11", "1", "ART122");//ID di contenuto non autorizzato
		assertEquals(Action.INPUT, result);
		
		ActionSupport action = this.getAction();
		Map<String, List<String>> fieldErrors = action.getFieldErrors();
		assertEquals(1, fieldErrors.size());
		List<String> contentIdFieldErrors = (List<String>) fieldErrors.get("contentId");
		assertEquals(1, contentIdFieldErrors.size());
	}
	
	public void testJoinContent_1() throws Throwable {
		String result = this.executeJoinContent("admin", "pagina_11", "1", "EVN24");//Contenuto Free
		assertEquals(Action.SUCCESS, result);
		IContentViewerShowletAction action = (IContentViewerShowletAction) this.getAction();
		Showlet newShowlet = action.getShowlet();
		assertNotNull(newShowlet);
		assertEquals("EVN24", newShowlet.getConfig().getProperty("contentId"));
		assertNull(newShowlet.getConfig().getProperty("modelId"));
		
		result = this.executeJoinContent("admin", "pagina_11", "1", "ART121");//Contenuto del gruppo "administrators" ma autorizzato ai free
		assertEquals(Action.SUCCESS, result);
		action = (IContentViewerShowletAction) this.getAction();
		newShowlet = action.getShowlet();
		assertNotNull(newShowlet);
		assertEquals("ART121", newShowlet.getConfig().getProperty("contentId"));
		assertNull(newShowlet.getConfig().getProperty("modelId"));
	}
	
	public void testJoinContent_2() throws Throwable {
		String result = this.executeJoinContent("admin", "customers_page", "1", "EVN191");//Contenuto Free su pagina non free
		assertEquals(Action.SUCCESS, result);
		IContentViewerShowletAction action = (IContentViewerShowletAction) this.getAction();
		Showlet newShowlet = action.getShowlet();
		assertNotNull(newShowlet);
		assertEquals("EVN191", newShowlet.getConfig().getProperty("contentId"));
		assertNull(newShowlet.getConfig().getProperty("modelId"));
		
		result = this.executeJoinContent("admin", "customers_page", "1", "EVN25");//Contenuto del gruppo "non free" su pagina di gruppo diverso ma autorizzato ai free
		assertEquals(Action.SUCCESS, result);
		action = (IContentViewerShowletAction) this.getAction();
		newShowlet = action.getShowlet();
		assertNotNull(newShowlet);
		assertEquals("EVN25", newShowlet.getConfig().getProperty("contentId"));
		assertNull(newShowlet.getConfig().getProperty("modelId"));
	}
	
	private String executeJoinContent(String currentUserName, String pageCode, String frame, String contentId) throws Throwable {
		this.setUserOnSession(currentUserName);
		this.initAction("/do/jacms/Page/SpecialShowlet/Viewer", "executeJoinContent");
		this.addParameter("pageCode", pageCode);
		this.addParameter("frame", frame);
		this.addParameter("showletTypeCode", "content_viewer");
		if (null != contentId) {
			this.addParameter("contentId", contentId);
		}
		return this.executeAction();
	}
	
	public void testSave_1() throws Throwable {
		String pageCode = "pagina_2";
		int frame = 0;
		IPage page = this._pageManager.getPage(pageCode);
		Showlet showlet = page.getShowlets()[frame];
		assertNull(showlet);
		try {
			this.setUserOnSession("admin");
			this.initAction("/do/jacms/Page/SpecialShowlet/Viewer", "saveViewerConfig");
			this.addParameter("pageCode", pageCode);
			this.addParameter("frame", String.valueOf(frame));
			this.addParameter("showletTypeCode", "content_viewer");
			this.addParameter("contentId", "ART187");
			this.addParameter("modelId", "1");
			String result = this.executeAction();
			assertEquals("configure", result);
			page = this._pageManager.getPage(pageCode);
			showlet = page.getShowlets()[frame];
			assertNotNull(showlet);
			assertEquals("content_viewer", showlet.getType().getCode());
			assertEquals(2, showlet.getConfig().size());
			assertEquals("ART187", showlet.getConfig().getProperty("contentId"));
			assertEquals("1", showlet.getConfig().getProperty("modelId"));
		} catch (Throwable t) {
			throw t;
		} finally {
			page = this._pageManager.getPage(pageCode);
			page.getShowlets()[frame] = null;
			this._pageManager.updatePage(page);
		}
	}
	
	public void testSave_2() throws Throwable {
		this.testSave_2("ART102", "customer_subpage_1", 0, Action.INPUT);
		this.testSave_2("ART104", "customer_subpage_1", 0, Action.INPUT);
		this.testSave_2("ART111", "customer_subpage_1", 0, "configure");
		this.testSave_2("ART122", "customer_subpage_1", 0, Action.INPUT);
		this.testSave_2("ART121", "customer_subpage_1", 0, "configure");
	}
	
	private void testSave_2(String contentId, String pageCode, int frame, String expectedResult) throws Throwable {
		try {
			this.intSaveViewerConfig(contentId, pageCode, frame);
			String result = this.executeAction();
			assertEquals(expectedResult, result);
			if (expectedResult.equals(Action.INPUT)) {
				ActionSupport action = this.getAction();
				assertEquals(1, action.getFieldErrors().size());
				assertEquals(1, action.getFieldErrors().get("contentId").size());
			}
		} catch (Throwable t) {
			throw t;
		} finally {
			IPage page = this._pageManager.getPage(pageCode);
			page.getShowlets()[frame] = null;
			this._pageManager.updatePage(page);
		}
	}
	
	private void intSaveViewerConfig(String contentId, String pageCode, int frame) throws Throwable {
		IPage page = this._pageManager.getPage(pageCode);
		Showlet showlet = page.getShowlets()[frame];
		assertNull(showlet);
		this.setUserOnSession("admin");
		this.initAction("/do/jacms/Page/SpecialShowlet/Viewer", "saveViewerConfig");
		Map<String, String> params = new HashMap<String, String>();
		params.put("pageCode", pageCode);
		params.put("frame", String.valueOf(frame));
		params.put("showletTypeCode", "content_viewer");
		params.put("contentId", contentId);
		this.addParameters(params);
	}
	
	private void init() throws Exception {
    	try {
    		this._pageManager = (IPageManager) this.getService(SystemConstants.PAGE_MANAGER);
    	} catch (Throwable t) {
            throw new Exception(t);
        }
    }
    
    private IPageManager _pageManager = null;
	
}
