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
package com.agiletec.apsadmin.portal;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.entando.entando.aps.system.services.widgettype.WidgetType;

import com.agiletec.aps.services.mock.MockShowletTypeDAO;
import com.agiletec.apsadmin.ApsAdminBaseTestCase;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.IManager;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.apsadmin.system.ApsAdminSystemConstants;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author E.Santoboni
 */
public class TestShowletTypeAction extends ApsAdminBaseTestCase {
	
	@Override
	protected void setUp() throws Exception {
        super.setUp();
        this.init();
    }
	
	public void testFailureUpdateTitles() throws Throwable {
		String result = this.executeUpdate("content_viewer", "italian title", "english title", "editorCustomers");
		assertEquals("userNotAllowed", result);
		
		result = this.executeUpdate("content_viewer", "italian title", "", "admin");
		assertEquals(Action.INPUT, result);
		ActionSupport action = this.getAction();
		assertEquals(1, action.getFieldErrors().size());
		
		result = this.executeUpdate("invalidShowletTitles", "italian title", "english title", "admin");
		assertEquals("inputShowletTypes", result);
		action = this.getAction();
		assertEquals(1, action.getActionErrors().size());
	}

	public void testUpdateTitles() throws Throwable {
    	String showletTypeCode = "test_showletType";
    	assertNull(this._showletTypeManager.getShowletType(showletTypeCode));
    	try {
			WidgetType type = this.createNewShowletType(showletTypeCode);
			this._showletTypeManager.addShowletType(type);
			String result = this.executeUpdate(showletTypeCode, "", "english title", "admin");
			assertEquals(Action.INPUT, result);
			ActionSupport action = this.getAction();
			assertEquals(1, action.getFieldErrors().size());
			result = this.executeUpdate(showletTypeCode, "Titolo modificato", "Modified title", "admin");
			assertEquals(Action.SUCCESS, result);
			WidgetType extracted = this._showletTypeManager.getShowletType(showletTypeCode);
			assertNotNull(extracted);
			assertEquals("Titolo modificato", extracted.getTitles().get("it"));
			assertEquals("Modified title", extracted.getTitles().get("en"));
		} catch (Throwable t) {
			throw t;
		} finally {
			if (null != this._showletTypeManager.getShowletType(showletTypeCode)) {
				this._showletTypeManager.deleteShowletType(showletTypeCode);
			}
			assertNull(this._showletTypeManager.getShowletType(showletTypeCode));
		}
    }
	
	public void testUpdate() throws Throwable {
    	String showletTypeCode = "test_showletType";
    	assertNull(this._showletTypeManager.getShowletType(showletTypeCode));
    	try {
			WidgetType type = this.createNewShowletType(showletTypeCode);
			this._showletTypeManager.addShowletType(type);
			ApsProperties newProperties = new ApsProperties();
			newProperties.put("contentId", "EVN191");
			String result = this.executeUpdate(showletTypeCode, "Titolo modificato", "Modified title", "admin", newProperties);
			assertEquals(Action.SUCCESS, result);
			WidgetType extracted = this._showletTypeManager.getShowletType(showletTypeCode);
			assertNotNull(extracted);
			assertEquals("Titolo modificato", extracted.getTitles().get("it"));
			assertEquals("Modified title", extracted.getTitles().get("en"));
			assertEquals("EVN191", extracted.getConfig().getProperty("contentId"));
			
			newProperties.put("contentId", "EVN194");
			result = this.executeUpdate(showletTypeCode, "Titolo modificato 2", "Modified title 2", "pageManagerCoach", newProperties);
			assertEquals(Action.SUCCESS, result);
			extracted = this._showletTypeManager.getShowletType(showletTypeCode);
			assertNotNull(extracted);
			assertEquals("Titolo modificato 2", extracted.getTitles().get("it"));
			assertEquals("Modified title 2", extracted.getTitles().get("en"));
			assertEquals("EVN191", extracted.getConfig().getProperty("contentId"));
		} catch (Throwable t) {
			throw t;
		} finally {
			if (null != this._showletTypeManager.getShowletType(showletTypeCode)) {
				this._showletTypeManager.deleteShowletType(showletTypeCode);
			}
			assertNull(this._showletTypeManager.getShowletType(showletTypeCode));
		}
    }
	
	public void testFailureTrashType_1() throws Throwable {
		String result = this.executeTrash("content_viewer", "editorCustomers");
		assertEquals("userNotAllowed", result);
		
		result = this.executeTrash("content_viewer", "admin");
		assertEquals("inputShowletTypes", result);
		ActionSupport action = this.getAction();
		assertEquals(1, action.getActionErrors().size());
		
		result = this.executeUpdate("invalidShowletTitles", "italian title", "english title", "admin");
		assertEquals("inputShowletTypes", result);
		action = this.getAction();
		assertEquals(1, action.getActionErrors().size());
	}
	
	public void testFailureTrashType_2() throws Throwable {
    	String showletTypeCode = "test_showletType";
    	assertNull(this._showletTypeManager.getShowletType(showletTypeCode));
    	try {
			WidgetType type = this.createNewShowletType(showletTypeCode);
			type.setLocked(true);
			this._showletTypeManager.addShowletType(type);
			assertNotNull(this._showletTypeManager.getShowletType(showletTypeCode));
			String result = this.executeTrash(showletTypeCode, "admin");
			assertEquals("inputShowletTypes", result);
			ActionSupport action = this.getAction();
			assertEquals(1, action.getActionErrors().size());
			assertNotNull(this._showletTypeManager.getShowletType(showletTypeCode));
		} catch (Throwable t) {
			throw t;
		} finally {
			if (null != this._showletTypeManager.getShowletType(showletTypeCode)) {
				this._mockShowletTypeDAO.deleteShowletType(showletTypeCode);
			}
			((IManager) this._showletTypeManager).refresh();
			assertNull(this._showletTypeManager.getShowletType(showletTypeCode));
		}
    }
	
	public void testTrashType() throws Throwable {
		String pageCode = "pagina_1";
		int frame = 1;
		String showletTypeCode = "test_showletType";
    	assertNull(this._showletTypeManager.getShowletType(showletTypeCode));
    	try {
			WidgetType type = this.createNewShowletType(showletTypeCode);
			this._showletTypeManager.addShowletType(type);
			assertNotNull(this._showletTypeManager.getShowletType(showletTypeCode));
			
			IPage pagina_1 = this._pageManager.getPage(pageCode);
			assertNull(pagina_1.getShowlets()[frame]);
			String result = this.executeJoinShowlet(pageCode, frame, showletTypeCode, "admin");
			assertEquals(Action.SUCCESS, result);
			pagina_1 = this._pageManager.getPage(pageCode);
			assertNotNull(pagina_1.getShowlets()[frame]);
			
			result = this.executeTrash(showletTypeCode, "admin");
			assertEquals("inputShowletTypes", result);
			ActionSupport action = this.getAction();
			assertEquals(1, action.getActionErrors().size());
			
			result = this.executeDeleteShowletFromPage(pageCode, frame, "admin");
			assertEquals(Action.SUCCESS, result);
			
			result = this.executeTrash(showletTypeCode, "admin");
			assertEquals(Action.SUCCESS, result);
			
			result = this.executeDelete(showletTypeCode, "admin");
			assertEquals(Action.SUCCESS, result);
			
			assertNull(this._showletTypeManager.getShowletType(showletTypeCode));
		} catch (Throwable t) {
			IPage pagina_1 = this._pageManager.getPage(pageCode);
			pagina_1.getShowlets()[frame] = null;
			this._pageManager.updatePage(pagina_1);
			if (null != this._showletTypeManager.getShowletType(showletTypeCode)) {
				this._mockShowletTypeDAO.deleteShowletType(showletTypeCode);
			}
			((IManager) this._showletTypeManager).refresh();
			assertNull(this._showletTypeManager.getShowletType(showletTypeCode));
			throw t;
		}
	}
	
	private String executeJoinShowlet(String pageCode, int frame, String showletTypeCode, String username) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/Page", "joinShowlet");
		this.addParameter("pageCode", pageCode);
		this.addParameter("showletTypeCode", showletTypeCode);
		this.addParameter("frame", String.valueOf(frame));
		return this.executeAction();
	}
	
	private String executeDeleteShowletFromPage(String pageCode, int frame, String username) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/Page", "deleteShowletFromPage");
		this.addParameter("pageCode", pageCode);
		this.addParameter("frame", String.valueOf(frame));
		return this.executeAction();
	}
	
	private String executeUpdate(String showletTypeCode, String italianTitle, String englishTitle, String username) throws Throwable {
		return this.executeUpdate(showletTypeCode, italianTitle, englishTitle, username, null);
	}
	
	private String executeUpdate(String showletTypeCode, String italianTitle, 
			String englishTitle, String username, ApsProperties properties) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/Portal/ShowletType", "save");
		this.addParameter("showletTypeCode", showletTypeCode);
		this.addParameter("italianTitle", italianTitle);
		this.addParameter("englishTitle", englishTitle);
		this.addParameter("strutsAction", ApsAdminSystemConstants.EDIT);
		if (null != properties) {
			this.addParameters(properties);
		}
		return this.executeAction();
	}

	private String executeTrash(String showletTypeCode, String username) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/Portal/ShowletType", "trash");
		this.addParameter("showletTypeCode", showletTypeCode);
		return this.executeAction();
	}

	private String executeDelete(String showletTypeCode, String username) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/Portal/ShowletType", "delete");
		this.addParameter("showletTypeCode", showletTypeCode);
		return this.executeAction();
	}
	
	private WidgetType createNewShowletType(String code) {
    	WidgetType type = new WidgetType();
    	type.setCode(code);
    	ApsProperties titles = new ApsProperties();
    	titles.put("it", "Titolo");
    	titles.put("en", "Title");
    	type.setTitles(titles);
    	WidgetType parent = this._showletTypeManager.getShowletType("content_viewer");
    	assertNotNull(parent);
    	type.setParentType(parent);
    	type.setPluginCode("jacms");
    	ApsProperties config = new ApsProperties();
    	config.put("contentId", "ART112");
    	type.setConfig(config);
    	return type;
    }
	
	public void testCopyShowletType() throws Throwable {
		String result = this.executeCopyShowletType("editorCustomers", "customers_page", "2");
		assertEquals("userNotAllowed", result);
		
		result = this.executeCopyShowletType("admin", "customers_page", "12");
		assertEquals("inputShowletTypes", result);
		
		result = this.executeCopyShowletType("admin", "invalidPage", "2");
		assertEquals("inputShowletTypes", result);
		
		result = this.executeCopyShowletType("admin", "customers_page", null);
		assertEquals("inputShowletTypes", result);
		
		result = this.executeCopyShowletType("admin", "customers_page", "3");
		assertEquals("inputShowletTypes", result);
		
		result = this.executeCopyShowletType("admin", "customers_page", "2");
		assertEquals(Action.SUCCESS, result);
		
		result = this.executeCopyShowletType("admin", "customers_page", "2");
		assertEquals(Action.SUCCESS, result);
	}
    
	private String executeCopyShowletType(String username, String pageCode, String framePos) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/Portal/ShowletType", "copy");
		this.addParameter("pageCode", pageCode);
		this.addParameter("framePos", framePos);
		this.addParameter("strutsAction", ApsAdminSystemConstants.PASTE);
		return this.executeAction();
	}
	
	public void testNewShowletType() throws Throwable {
		String result = this.executeNewShowletType("editorCustomers", "content_viewer_list");
		assertEquals("userNotAllowed", result);
		
		result = this.executeNewShowletType("admin", "messages_system");
		assertEquals("inputShowletTypes", result);
		
		result = this.executeNewShowletType("admin", "logic_type");
		assertEquals("inputShowletTypes", result);
		
		result = this.executeNewShowletType("admin", "content_viewer_list");
		assertEquals(Action.SUCCESS, result);
		
		result = this.executeNewShowletType("admin", "content_viewer_list");
		assertEquals(Action.SUCCESS, result);
	}
    
	private String executeNewShowletType(String username, String parentShowletTypeCode) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/Portal/ShowletType", "new");
		this.addParameter("parentShowletTypeCode", parentShowletTypeCode);
		this.addParameter("strutsAction", ApsAdminSystemConstants.ADD);
		return this.executeAction();
	}

	public void testFailurePasteShowletType() throws Throwable {
		String showletTypeCode = "randomShowletCode";
		try {
			String result = this.executePasteShowletType("editorCustomers", showletTypeCode, "en", "it", "customers_page", "2");
			assertEquals("userNotAllowed", result);
			
			result = this.executePasteShowletType("admin", showletTypeCode, "en", "it", "customers_page", "12");
			assertEquals(Action.INPUT, result);
			
			result = this.executePasteShowletType("admin", showletTypeCode, "en", "it", "invalidPage", "2");
			assertEquals(Action.INPUT, result);
			
			result = this.executePasteShowletType("admin", showletTypeCode, "en", "it", "customers_page", null);
			assertEquals(Action.INPUT, result);
		} catch (Throwable t) {
			this._showletTypeManager.deleteShowletType(showletTypeCode);
			throw t;
		}
	}
    
	public void testFailureAddNewShowletType() throws Throwable {
		String showletTypeCode = "randomShowletCode";
		try {
			String result = this.executeAddShowletType("editorCustomers", showletTypeCode, "en", "it", "content_viewer_list");
			assertEquals("userNotAllowed", result);
			
			result = this.executeAddShowletType("admin", showletTypeCode, "en", "it", "messages_system");
			assertEquals(Action.INPUT, result);
			
			result = this.executeAddShowletType("admin", showletTypeCode, "en", "it", "logic_type");
			assertEquals(Action.INPUT, result);
		} catch (Throwable t) {
			this._showletTypeManager.deleteShowletType(showletTypeCode);
			throw t;
		}
	}
    
	public void testValidatePasteShowletType() throws Throwable {
		String showletTypeCode = "randomShowletCode";
		try {
			String result = this.executePasteShowletType("admin", showletTypeCode, "en", null, "customers_page", "2");
			assertEquals(Action.INPUT, result);
			Map<String, List<String>> fieldErrors = this.getAction().getFieldErrors();
			assertEquals(1, fieldErrors.size());
			assertEquals(1, fieldErrors.get("italianTitle").size());
			
			result = this.executePasteShowletType("admin", "ty &", "en", null, "customers_page", "2");
			assertEquals(Action.INPUT, result);
			fieldErrors = this.getAction().getFieldErrors();
			assertEquals(2, fieldErrors.size());
			assertEquals(1, fieldErrors.get("showletTypeCode").size());
			assertEquals(1, fieldErrors.get("italianTitle").size());
			
			result = this.executePasteShowletType("admin", "content_viewer_list", null, "it", "customers_page", "2");
			assertEquals(Action.INPUT, result);
			fieldErrors = this.getAction().getFieldErrors();
			assertEquals(2, fieldErrors.size());
			assertEquals(1, fieldErrors.get("showletTypeCode").size());
			assertEquals(1, fieldErrors.get("englishTitle").size());
		} catch (Throwable t) {
			this._showletTypeManager.deleteShowletType(showletTypeCode);
			throw t;
		}
	}
	
	public void testValidateAddNewShowletType() throws Throwable {
		String showletTypeCode = "randomShowletCode";
		try {
			String result = this.executeAddShowletType("admin", showletTypeCode, "enTitle", "", "content_viewer_list");
			assertEquals(Action.INPUT, result);
			Map<String, List<String>> fieldErrors = this.getAction().getFieldErrors();
			assertEquals(1, fieldErrors.size());
			assertEquals(1, fieldErrors.get("italianTitle").size());
			
			result = this.executeAddShowletType("admin", "ht$", "enTitle", "", "content_viewer_list");
			assertEquals(Action.INPUT, result);
			fieldErrors = this.getAction().getFieldErrors();
			assertEquals(2, fieldErrors.size());
			assertEquals(1, fieldErrors.get("showletTypeCode").size());
			assertEquals(1, fieldErrors.get("italianTitle").size());
			
			result = this.executeAddShowletType("admin", "messages_system", null, "it", "content_viewer_list");
			assertEquals(Action.INPUT, result);
			fieldErrors = this.getAction().getFieldErrors();
			assertEquals(2, fieldErrors.size());
			assertEquals(1, fieldErrors.get("showletTypeCode").size());
			assertEquals(1, fieldErrors.get("englishTitle").size());
		} catch (Throwable t) {
			throw t;
		} finally {
			this._showletTypeManager.deleteShowletType(showletTypeCode);
		}
	}
    
	public void testPasteNewShowletType_1() throws Throwable {
		String showletTypeCode = "randomShowletCode_1";
		try {
			assertNull(this._showletTypeManager.getShowletType(showletTypeCode));
			String result = this.executePasteShowletType("admin", showletTypeCode, "en", "it", "customers_page", "2");
			assertEquals(Action.SUCCESS, result);
			
			Widget copiedShowlet = this._pageManager.getPage("customers_page").getShowlets()[2];
			assertNotNull(copiedShowlet);
			assertNotNull(copiedShowlet.getConfig());
			WidgetType addedType = this._showletTypeManager.getShowletType(showletTypeCode);
			assertNotNull(addedType);
			ApsProperties config = addedType.getConfig();
			Iterator<Object> keysIter = config.keySet().iterator();
			while (keysIter.hasNext()) {
				String key = (String) keysIter.next();
				assertEquals(copiedShowlet.getConfig().getProperty(key), config.getProperty(key));
			}
		} catch (Throwable t) {
			throw t;
		} finally {
			this._showletTypeManager.deleteShowletType(showletTypeCode);
		}
	}
	
	public void testPasteNewShowletType_2() throws Throwable {
		String showletTypeCode = "randomShowletCode_2";
		String pageDest = "pagina_1";
		int frameDest = 1;
		Widget temp = this._pageManager.getPage("pagina_11").getShowlets()[2];
		assertNotNull(temp);
		assertEquals("content_viewer", temp.getType().getCode());
		IPage page = this._pageManager.getPage(pageDest);
		try {
			assertNull(page.getShowlets()[frameDest]);
			page.getShowlets()[frameDest] = temp;
			this._pageManager.updatePage(page);
			
			this.setUserOnSession("admin");
			this.initAction("/do/Portal/ShowletType", "save");
			this.addParameter("showletTypeCode", showletTypeCode);
			this.addParameter("englishTitle", "en");
			this.addParameter("italianTitle", "it");
			this.addParameter("pageCode", pageDest);
			this.addParameter("framePos", frameDest);
			this.addParameter("strutsAction", ApsAdminSystemConstants.PASTE);
			this.addParameter("replaceOnPage", "true");
			String result = this.executeAction();
			assertEquals("replaceOnPage", result);
			
			Widget newShowlet = this._pageManager.getPage(pageDest).getShowlets()[frameDest];
			assertNotNull(newShowlet);
			assertNotNull(newShowlet.getConfig());
			WidgetType addedType = this._showletTypeManager.getShowletType(showletTypeCode);
			assertNotNull(addedType);
			assertEquals(newShowlet.getType().getCode(), addedType.getCode());
			ApsProperties config = addedType.getConfig();
			Iterator<Object> keysIter = config.keySet().iterator();
			while (keysIter.hasNext()) {
				String key = (String) keysIter.next();
				assertEquals(newShowlet.getConfig().getProperty(key), config.getProperty(key));
			}
		} catch (Throwable t) {
			throw t;
		} finally {
			page.getShowlets()[frameDest] = null;
			this._pageManager.updatePage(page);
			this._showletTypeManager.deleteShowletType(showletTypeCode);
		}
	}
    
	public void testAddNewShowletType() throws Throwable {
		String showletTypeCode = "randomShowletCode_3";
		try {
			this.setUserOnSession("admin");
			this.initAction("/do/Portal/ShowletType", "save");
			this.addParameter("showletTypeCode", showletTypeCode);
			this.addParameter("englishTitle", "en");
			this.addParameter("italianTitle", "it");
			this.addParameter("parentShowletTypeCode", "content_viewer_list");
			this.addParameter("strutsAction", ApsAdminSystemConstants.ADD);
			this.addParameter("maxElemForItem", "5");
			this.addParameter("contentType", "EVN");
			String result = this.executeAction();
			assertEquals(Action.SUCCESS, result);
			
			WidgetType addedType = this._showletTypeManager.getShowletType(showletTypeCode);
			assertNotNull(addedType);
			ApsProperties config = addedType.getConfig();
			assertEquals(2, config.size());
			assertEquals("EVN", config.getProperty("contentType"));
			assertEquals("5", config.getProperty("maxElemForItem"));
		} catch (Throwable t) {
			throw t;
		} finally {
			this._showletTypeManager.deleteShowletType(showletTypeCode);
		}
	}
	
	private String executePasteShowletType(String username, String code, String englishTitle, String italianTitle, String pageCode, String framePos) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/Portal/ShowletType", "save");
		this.addParameter("showletTypeCode", code);
		this.addParameter("englishTitle", englishTitle);
		this.addParameter("italianTitle", italianTitle);
		this.addParameter("pageCode", pageCode);
		this.addParameter("framePos", framePos);
		this.addParameter("strutsAction", ApsAdminSystemConstants.PASTE);
		return this.executeAction();
	}
	
	private String executeAddShowletType(String username, String code, String englishTitle, String italianTitle, String parentShowletTypeCode) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/Portal/ShowletType", "save");
		this.addParameter("showletTypeCode", code);
		this.addParameter("englishTitle", englishTitle);
		this.addParameter("italianTitle", italianTitle);
		this.addParameter("parentShowletTypeCode", parentShowletTypeCode);
		this.addParameter("strutsAction", ApsAdminSystemConstants.ADD);
		return this.executeAction();
	}
	
	private void init() throws Exception {
		try {
			this._pageManager = (IPageManager) this.getService(SystemConstants.PAGE_MANAGER);
			this._showletTypeManager = (IWidgetTypeManager) this.getService(SystemConstants.WIDGET_TYPE_MANAGER);
			DataSource dataSource = (DataSource) this.getApplicationContext().getBean("portDataSource");
			this._mockShowletTypeDAO = new MockShowletTypeDAO();
			this._mockShowletTypeDAO.setDataSource(dataSource);
		} catch (Throwable e) {
			throw new Exception(e);
		}
	}
    
	private IPageManager _pageManager = null;
    private IWidgetTypeManager _showletTypeManager = null;
    private MockShowletTypeDAO _mockShowletTypeDAO;
	
}