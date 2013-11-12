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

import com.agiletec.aps.services.mock.MockWidgetTypeDAO;
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
public class TestWidgetTypeAction extends ApsAdminBaseTestCase {
	
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
    	String widgetTypeCode = "test_showletType";
    	assertNull(this._widgetTypeManager.getWidgetType(widgetTypeCode));
    	try {
			WidgetType type = this.createNewShowletType(widgetTypeCode);
			this._widgetTypeManager.addWidgetType(type);
			String result = this.executeUpdate(widgetTypeCode, "", "english title", "admin");
			assertEquals(Action.INPUT, result);
			ActionSupport action = this.getAction();
			assertEquals(1, action.getFieldErrors().size());
			result = this.executeUpdate(widgetTypeCode, "Titolo modificato", "Modified title", "admin");
			assertEquals(Action.SUCCESS, result);
			WidgetType extracted = this._widgetTypeManager.getWidgetType(widgetTypeCode);
			assertNotNull(extracted);
			assertEquals("Titolo modificato", extracted.getTitles().get("it"));
			assertEquals("Modified title", extracted.getTitles().get("en"));
		} catch (Throwable t) {
			throw t;
		} finally {
			if (null != this._widgetTypeManager.getWidgetType(widgetTypeCode)) {
				this._widgetTypeManager.deleteWidgetType(widgetTypeCode);
			}
			assertNull(this._widgetTypeManager.getWidgetType(widgetTypeCode));
		}
    }
	
	public void testUpdate() throws Throwable {
    	String widgetTypeCode = "test_showletType";
    	assertNull(this._widgetTypeManager.getWidgetType(widgetTypeCode));
    	try {
			WidgetType type = this.createNewShowletType(widgetTypeCode);
			this._widgetTypeManager.addWidgetType(type);
			ApsProperties newProperties = new ApsProperties();
			newProperties.put("contentId", "EVN191");
			String result = this.executeUpdate(widgetTypeCode, "Titolo modificato", "Modified title", "admin", newProperties);
			assertEquals(Action.SUCCESS, result);
			WidgetType extracted = this._widgetTypeManager.getWidgetType(widgetTypeCode);
			assertNotNull(extracted);
			assertEquals("Titolo modificato", extracted.getTitles().get("it"));
			assertEquals("Modified title", extracted.getTitles().get("en"));
			assertEquals("EVN191", extracted.getConfig().getProperty("contentId"));
			
			newProperties.put("contentId", "EVN194");
			result = this.executeUpdate(widgetTypeCode, "Titolo modificato 2", "Modified title 2", "pageManagerCoach", newProperties);
			assertEquals(Action.SUCCESS, result);
			extracted = this._widgetTypeManager.getWidgetType(widgetTypeCode);
			assertNotNull(extracted);
			assertEquals("Titolo modificato 2", extracted.getTitles().get("it"));
			assertEquals("Modified title 2", extracted.getTitles().get("en"));
			assertEquals("EVN191", extracted.getConfig().getProperty("contentId"));
		} catch (Throwable t) {
			throw t;
		} finally {
			if (null != this._widgetTypeManager.getWidgetType(widgetTypeCode)) {
				this._widgetTypeManager.deleteWidgetType(widgetTypeCode);
			}
			assertNull(this._widgetTypeManager.getWidgetType(widgetTypeCode));
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
    	String widgetTypeCode = "test_showletType";
    	assertNull(this._widgetTypeManager.getWidgetType(widgetTypeCode));
    	try {
			WidgetType type = this.createNewShowletType(widgetTypeCode);
			type.setLocked(true);
			this._widgetTypeManager.addWidgetType(type);
			assertNotNull(this._widgetTypeManager.getWidgetType(widgetTypeCode));
			String result = this.executeTrash(widgetTypeCode, "admin");
			assertEquals("inputShowletTypes", result);
			ActionSupport action = this.getAction();
			assertEquals(1, action.getActionErrors().size());
			assertNotNull(this._widgetTypeManager.getWidgetType(widgetTypeCode));
		} catch (Throwable t) {
			throw t;
		} finally {
			if (null != this._widgetTypeManager.getWidgetType(widgetTypeCode)) {
				this._mockWidgetTypeDAO.deleteShowletType(widgetTypeCode);
			}
			((IManager) this._widgetTypeManager).refresh();
			assertNull(this._widgetTypeManager.getWidgetType(widgetTypeCode));
		}
    }
	
	public void testTrashType() throws Throwable {
		String pageCode = "pagina_1";
		int frame = 1;
		String widgetTypeCode = "test_showletType";
    	assertNull(this._widgetTypeManager.getWidgetType(widgetTypeCode));
    	try {
			WidgetType type = this.createNewShowletType(widgetTypeCode);
			this._widgetTypeManager.addWidgetType(type);
			assertNotNull(this._widgetTypeManager.getWidgetType(widgetTypeCode));
			
			IPage pagina_1 = this._pageManager.getPage(pageCode);
			assertNull(pagina_1.getWidgets()[frame]);
			String result = this.executeJoinShowlet(pageCode, frame, widgetTypeCode, "admin");
			assertEquals(Action.SUCCESS, result);
			pagina_1 = this._pageManager.getPage(pageCode);
			assertNotNull(pagina_1.getWidgets()[frame]);
			
			result = this.executeTrash(widgetTypeCode, "admin");
			assertEquals("inputShowletTypes", result);
			ActionSupport action = this.getAction();
			assertEquals(1, action.getActionErrors().size());
			
			result = this.executeDeleteShowletFromPage(pageCode, frame, "admin");
			assertEquals(Action.SUCCESS, result);
			
			result = this.executeTrash(widgetTypeCode, "admin");
			assertEquals(Action.SUCCESS, result);
			
			result = this.executeDelete(widgetTypeCode, "admin");
			assertEquals(Action.SUCCESS, result);
			
			assertNull(this._widgetTypeManager.getWidgetType(widgetTypeCode));
		} catch (Throwable t) {
			IPage pagina_1 = this._pageManager.getPage(pageCode);
			pagina_1.getWidgets()[frame] = null;
			this._pageManager.updatePage(pagina_1);
			if (null != this._widgetTypeManager.getWidgetType(widgetTypeCode)) {
				this._mockWidgetTypeDAO.deleteShowletType(widgetTypeCode);
			}
			((IManager) this._widgetTypeManager).refresh();
			assertNull(this._widgetTypeManager.getWidgetType(widgetTypeCode));
			throw t;
		}
	}
	
	private String executeJoinShowlet(String pageCode, int frame, String showletTypeCode, String username) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/Page", "joinWidget");
		this.addParameter("pageCode", pageCode);
		this.addParameter("widgetTypeCode", showletTypeCode);
		this.addParameter("frame", String.valueOf(frame));
		return this.executeAction();
	}
	
	private String executeDeleteShowletFromPage(String pageCode, int frame, String username) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/Page", "deleteWidgetFromPage");
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
		this.initAction("/do/Portal/WidgetType", "save");
		this.addParameter("widgetTypeCode", showletTypeCode);
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
		this.initAction("/do/Portal/WidgetType", "trash");
		this.addParameter("widgetTypeCode", showletTypeCode);
		return this.executeAction();
	}

	private String executeDelete(String showletTypeCode, String username) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/Portal/WidgetType", "delete");
		this.addParameter("widgetTypeCode", showletTypeCode);
		return this.executeAction();
	}
	
	private WidgetType createNewShowletType(String code) {
    	WidgetType type = new WidgetType();
    	type.setCode(code);
    	ApsProperties titles = new ApsProperties();
    	titles.put("it", "Titolo");
    	titles.put("en", "Title");
    	type.setTitles(titles);
    	WidgetType parent = this._widgetTypeManager.getWidgetType("content_viewer");
    	assertNotNull(parent);
    	type.setParentType(parent);
    	type.setPluginCode("jacms");
    	ApsProperties config = new ApsProperties();
    	config.put("contentId", "ART112");
    	type.setConfig(config);
    	return type;
    }
	
	public void testCopyWidgetType() throws Throwable {
		String result = this.executeCopyWidgetType("editorCustomers", "customers_page", "2");
		assertEquals("userNotAllowed", result);
		
		result = this.executeCopyWidgetType("admin", "customers_page", "12");
		assertEquals("inputShowletTypes", result);
		
		result = this.executeCopyWidgetType("admin", "invalidPage", "2");
		assertEquals("inputShowletTypes", result);
		
		result = this.executeCopyWidgetType("admin", "customers_page", null);
		assertEquals("inputShowletTypes", result);
		
		result = this.executeCopyWidgetType("admin", "customers_page", "3");
		assertEquals("inputShowletTypes", result);
		
		result = this.executeCopyWidgetType("admin", "customers_page", "2");
		assertEquals(Action.SUCCESS, result);
		
		result = this.executeCopyWidgetType("admin", "customers_page", "2");
		assertEquals(Action.SUCCESS, result);
	}
    
	private String executeCopyWidgetType(String username, String pageCode, String framePos) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/Portal/WidgetType", "copy");
		this.addParameter("pageCode", pageCode);
		this.addParameter("framePos", framePos);
		this.addParameter("strutsAction", ApsAdminSystemConstants.PASTE);
		return this.executeAction();
	}
	
	public void testNewWidgetType() throws Throwable {
		String result = this.executeNewWidgetType("editorCustomers", "content_viewer_list");
		assertEquals("userNotAllowed", result);
		
		result = this.executeNewWidgetType("admin", "messages_system");
		assertEquals("inputShowletTypes", result);
		
		result = this.executeNewWidgetType("admin", "logic_type");
		assertEquals("inputShowletTypes", result);
		
		result = this.executeNewWidgetType("admin", "content_viewer_list");
		assertEquals(Action.SUCCESS, result);
		
		result = this.executeNewWidgetType("admin", "content_viewer_list");
		assertEquals(Action.SUCCESS, result);
	}
    
	private String executeNewWidgetType(String username, String parentShowletTypeCode) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/Portal/WidgetType", "new");
		this.addParameter("parentShowletTypeCode", parentShowletTypeCode);
		this.addParameter("strutsAction", ApsAdminSystemConstants.ADD);
		return this.executeAction();
	}

	public void testFailurePasteWidgetType() throws Throwable {
		String widgetTypeCode = "randomShowletCode";
		try {
			String result = this.executePasteWidgetType("editorCustomers", widgetTypeCode, "en", "it", "customers_page", "2");
			assertEquals("userNotAllowed", result);
			
			result = this.executePasteWidgetType("admin", widgetTypeCode, "en", "it", "customers_page", "12");
			assertEquals(Action.INPUT, result);
			
			result = this.executePasteWidgetType("admin", widgetTypeCode, "en", "it", "invalidPage", "2");
			assertEquals(Action.INPUT, result);
			
			result = this.executePasteWidgetType("admin", widgetTypeCode, "en", "it", "customers_page", null);
			assertEquals(Action.INPUT, result);
		} catch (Throwable t) {
			this._widgetTypeManager.deleteWidgetType(widgetTypeCode);
			throw t;
		}
	}
    
	public void testFailureAddNewWidgetType() throws Throwable {
		String widgetTypeCode = "randomShowletCode";
		try {
			String result = this.executeAddWidgetType("editorCustomers", widgetTypeCode, "en", "it", "content_viewer_list");
			assertEquals("userNotAllowed", result);
			
			result = this.executeAddWidgetType("admin", widgetTypeCode, "en", "it", "messages_system");
			assertEquals(Action.INPUT, result);
			
			result = this.executeAddWidgetType("admin", widgetTypeCode, "en", "it", "logic_type");
			assertEquals(Action.INPUT, result);
		} catch (Throwable t) {
			this._widgetTypeManager.deleteWidgetType(widgetTypeCode);
			throw t;
		}
	}
    
	public void testValidatePasteWidgetType() throws Throwable {
		String widgetTypeCode = "randomShowletCode";
		try {
			String result = this.executePasteWidgetType("admin", widgetTypeCode, "en", null, "customers_page", "2");
			assertEquals(Action.INPUT, result);
			Map<String, List<String>> fieldErrors = this.getAction().getFieldErrors();
			assertEquals(1, fieldErrors.size());
			assertEquals(1, fieldErrors.get("italianTitle").size());
			
			result = this.executePasteWidgetType("admin", "ty &", "en", null, "customers_page", "2");
			assertEquals(Action.INPUT, result);
			fieldErrors = this.getAction().getFieldErrors();
			assertEquals(2, fieldErrors.size());
			assertEquals(1, fieldErrors.get("showletTypeCode").size());
			assertEquals(1, fieldErrors.get("italianTitle").size());
			
			result = this.executePasteWidgetType("admin", "content_viewer_list", null, "it", "customers_page", "2");
			assertEquals(Action.INPUT, result);
			fieldErrors = this.getAction().getFieldErrors();
			assertEquals(2, fieldErrors.size());
			assertEquals(1, fieldErrors.get("showletTypeCode").size());
			assertEquals(1, fieldErrors.get("englishTitle").size());
		} catch (Throwable t) {
			this._widgetTypeManager.deleteWidgetType(widgetTypeCode);
			throw t;
		}
	}
	
	public void testValidateAddNewWidgetType() throws Throwable {
		String widgetTypeCode = "randomShowletCode";
		try {
			String result = this.executeAddWidgetType("admin", widgetTypeCode, "enTitle", "", "content_viewer_list");
			assertEquals(Action.INPUT, result);
			Map<String, List<String>> fieldErrors = this.getAction().getFieldErrors();
			assertEquals(1, fieldErrors.size());
			assertEquals(1, fieldErrors.get("italianTitle").size());
			
			result = this.executeAddWidgetType("admin", "ht$", "enTitle", "", "content_viewer_list");
			assertEquals(Action.INPUT, result);
			fieldErrors = this.getAction().getFieldErrors();
			assertEquals(2, fieldErrors.size());
			assertEquals(1, fieldErrors.get("showletTypeCode").size());
			assertEquals(1, fieldErrors.get("italianTitle").size());
			
			result = this.executeAddWidgetType("admin", "messages_system", null, "it", "content_viewer_list");
			assertEquals(Action.INPUT, result);
			fieldErrors = this.getAction().getFieldErrors();
			assertEquals(2, fieldErrors.size());
			assertEquals(1, fieldErrors.get("showletTypeCode").size());
			assertEquals(1, fieldErrors.get("englishTitle").size());
		} catch (Throwable t) {
			throw t;
		} finally {
			this._widgetTypeManager.deleteWidgetType(widgetTypeCode);
		}
	}
    
	public void testPasteNewWidgetType_1() throws Throwable {
		String widgetTypeCode = "randomShowletCode_1";
		try {
			assertNull(this._widgetTypeManager.getWidgetType(widgetTypeCode));
			String result = this.executePasteWidgetType("admin", widgetTypeCode, "en", "it", "customers_page", "2");
			assertEquals(Action.SUCCESS, result);
			
			Widget copiedShowlet = this._pageManager.getPage("customers_page").getWidgets()[2];
			assertNotNull(copiedShowlet);
			assertNotNull(copiedShowlet.getConfig());
			WidgetType addedType = this._widgetTypeManager.getWidgetType(widgetTypeCode);
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
			this._widgetTypeManager.deleteWidgetType(widgetTypeCode);
		}
	}
	
	public void testPasteNewWidgetType_2() throws Throwable {
		String widgetTypeCode = "randomShowletCode-2";
		String pageDest = "pagina_1";
		int frameDest = 1;
		Widget temp = this._pageManager.getPage("pagina_11").getWidgets()[2];
		assertNotNull(temp);
		assertEquals("content_viewer", temp.getType().getCode());
		IPage page = this._pageManager.getPage(pageDest);
		try {
			assertNull(page.getWidgets()[frameDest]);
			page.getWidgets()[frameDest] = temp;
			this._pageManager.updatePage(page);
			
			this.setUserOnSession("admin");
			this.initAction("/do/Portal/WidgetType", "save");
			this.addParameter("widgetTypeCode", widgetTypeCode);
			this.addParameter("englishTitle", "en");
			this.addParameter("italianTitle", "it");
			this.addParameter("pageCode", pageDest);
			this.addParameter("framePos", frameDest);
			this.addParameter("strutsAction", ApsAdminSystemConstants.PASTE);
			this.addParameter("replaceOnPage", "true");
			String result = this.executeAction();
			assertEquals("replaceOnPage", result);
			
			Widget newWidget = this._pageManager.getPage(pageDest).getWidgets()[frameDest];
			assertNotNull(newWidget);
			assertNotNull(newWidget.getConfig());
			WidgetType addedType = this._widgetTypeManager.getWidgetType(widgetTypeCode);
			assertNotNull(addedType);
			assertEquals(newWidget.getType().getCode(), addedType.getCode());
			ApsProperties config = addedType.getConfig();
			Iterator<Object> keysIter = config.keySet().iterator();
			while (keysIter.hasNext()) {
				String key = (String) keysIter.next();
				assertEquals(newWidget.getConfig().getProperty(key), config.getProperty(key));
			}
		} catch (Throwable t) {
			throw t;
		} finally {
			page.getWidgets()[frameDest] = null;
			this._pageManager.updatePage(page);
			this._widgetTypeManager.deleteWidgetType(widgetTypeCode);
		}
	}
    
	public void testAddNewWidgetType() throws Throwable {
		String widgetTypeCode = "randomShowletCode_3";
		try {
			this.setUserOnSession("admin");
			this.initAction("/do/Portal/WidgetType", "save");
			this.addParameter("widgetTypeCode", widgetTypeCode);
			this.addParameter("englishTitle", "en");
			this.addParameter("italianTitle", "it");
			this.addParameter("parentShowletTypeCode", "content_viewer_list");
			this.addParameter("strutsAction", ApsAdminSystemConstants.ADD);
			this.addParameter("maxElemForItem", "5");
			this.addParameter("contentType", "EVN");
			String result = this.executeAction();
			assertEquals(Action.SUCCESS, result);
			
			WidgetType addedType = this._widgetTypeManager.getWidgetType(widgetTypeCode);
			assertNotNull(addedType);
			ApsProperties config = addedType.getConfig();
			assertEquals(2, config.size());
			assertEquals("EVN", config.getProperty("contentType"));
			assertEquals("5", config.getProperty("maxElemForItem"));
		} catch (Throwable t) {
			throw t;
		} finally {
			this._widgetTypeManager.deleteWidgetType(widgetTypeCode);
		}
	}
	
	private String executePasteWidgetType(String username, String code, String englishTitle, String italianTitle, String pageCode, String framePos) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/Portal/WidgetType", "save");
		this.addParameter("widgetTypeCode", code);
		this.addParameter("englishTitle", englishTitle);
		this.addParameter("italianTitle", italianTitle);
		this.addParameter("pageCode", pageCode);
		this.addParameter("framePos", framePos);
		this.addParameter("strutsAction", ApsAdminSystemConstants.PASTE);
		return this.executeAction();
	}
	
	private String executeAddWidgetType(String username, String code, String englishTitle, String italianTitle, String parentShowletTypeCode) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/Portal/WidgetType", "save");
		this.addParameter("widgetTypeCode", code);
		this.addParameter("englishTitle", englishTitle);
		this.addParameter("italianTitle", italianTitle);
		this.addParameter("parentShowletTypeCode", parentShowletTypeCode);
		this.addParameter("strutsAction", ApsAdminSystemConstants.ADD);
		return this.executeAction();
	}
	
	private void init() throws Exception {
		try {
			this._pageManager = (IPageManager) this.getService(SystemConstants.PAGE_MANAGER);
			this._widgetTypeManager = (IWidgetTypeManager) this.getService(SystemConstants.WIDGET_TYPE_MANAGER);
			DataSource dataSource = (DataSource) this.getApplicationContext().getBean("portDataSource");
			this._mockWidgetTypeDAO = new MockWidgetTypeDAO();
			this._mockWidgetTypeDAO.setDataSource(dataSource);
		} catch (Throwable e) {
			throw new Exception(e);
		}
	}
    
	private IPageManager _pageManager = null;
    private IWidgetTypeManager _widgetTypeManager = null;
    private MockWidgetTypeDAO _mockWidgetTypeDAO;
	
}