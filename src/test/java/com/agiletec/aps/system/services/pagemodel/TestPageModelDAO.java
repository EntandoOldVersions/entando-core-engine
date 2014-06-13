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
package com.agiletec.aps.system.services.pagemodel;

import java.util.Map;

import javax.sql.DataSource;

import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.util.ApsProperties;

/**
 * @author M.Diana - E.Santoboni
 */
public class TestPageModelDAO extends BaseTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.init();
	}
	
	public void testLoadModels() throws Throwable {
		Map<String, PageModel> models = this._pageModelDAO.loadModels();
		assertTrue(models.containsKey("home"));
		assertTrue(models.containsKey("service"));
	}
	
	public void testAddRemoveModel() throws Throwable {
		Map<String, PageModel> models = this._pageModelDAO.loadModels();
		String testPageModelCode = "test_pagemodel";
		try {
			assertTrue(!models.containsKey(testPageModelCode));
			PageModel mockModel = this.createMockPageModel(testPageModelCode);
			this._pageModelDAO.addModel(mockModel);
			models = this._pageModelDAO.loadModels();
			PageModel extractedMockModel = models.get(testPageModelCode);
			assertNotNull(extractedMockModel);
			assertEquals(testPageModelCode, extractedMockModel.getCode());
			assertTrue(extractedMockModel.getDescr().contains(testPageModelCode));
			assertEquals(3, extractedMockModel.getFrames().length);
			Widget[] defaultWidgets = extractedMockModel.getDefaultWidget();
			assertEquals(3, defaultWidgets.length);
			Widget defWidg0 = defaultWidgets[0];
			assertNull(defWidg0);
			Widget defWidg1 = defaultWidgets[1];
			assertNotNull(defWidg1);
			assertEquals("content_viewer_list", defWidg1.getType().getCode());
			assertEquals(1, defWidg1.getConfig().size());
			assertEquals("ART", defWidg1.getConfig().get("contentType"));
			Widget defWidg2 = defaultWidgets[2];
			assertNotNull(defWidg2);
			assertEquals("login_form", defWidg2.getType().getCode());
			assertNull(defWidg2.getConfig());
			assertEquals("<strong>Freemarker template content</strong>", extractedMockModel.getTemplate());
		} catch (Exception e) {
			throw e;
		} finally {
			this._pageModelDAO.deleteModel(testPageModelCode);
			models = this._pageModelDAO.loadModels();
			assertTrue(!models.containsKey(testPageModelCode));
		}
	}
	
	public void testUpdateModel() throws Throwable {
		Map<String, PageModel> models = this._pageModelDAO.loadModels();
		String testPageModelCode = "test_pagemodel";
		try {
			assertTrue(!models.containsKey(testPageModelCode));
			PageModel mockModel = this.createMockPageModel(testPageModelCode);
			this._pageModelDAO.addModel(mockModel);
			models = this._pageModelDAO.loadModels();
			PageModel extractedMockModel = models.get(testPageModelCode);
			extractedMockModel.setDescription("Modified Description");
			String[] frames = {"Freme 0", "Frame 1", "Frame 2", "Frame 3"};
			extractedMockModel.setFrames(frames);
			Widget[] extractedDefaultWidgets = new Widget[4];
			extractedDefaultWidgets[0] = extractedMockModel.getDefaultWidget()[1];
			extractedDefaultWidgets[1] = extractedMockModel.getDefaultWidget()[2];
			extractedDefaultWidgets[2] = extractedMockModel.getDefaultWidget()[0];
			Widget defWidg3ToSet = new Widget();
			defWidg3ToSet.setType(this._widgetTypeManager.getWidgetType("content_viewer"));
			ApsProperties props3 = new ApsProperties();
			props3.setProperty("contentId", "ART187");
			defWidg3ToSet.setConfig(props3);
			extractedDefaultWidgets[3] = defWidg3ToSet;
			extractedMockModel.setDefaultWidget(extractedDefaultWidgets);
			extractedMockModel.setTemplate("<strong>Modified Freemarker template content</strong>");
			this._pageModelDAO.updateModel(extractedMockModel);
			
			models = this._pageModelDAO.loadModels();
			extractedMockModel = models.get(testPageModelCode);
			assertNotNull(extractedMockModel);
			assertEquals(testPageModelCode, extractedMockModel.getCode());
			assertEquals("Modified Description", extractedMockModel.getDescr());
			assertEquals(4, extractedMockModel.getFrames().length);
			Widget[] defaultWidgets = extractedMockModel.getDefaultWidget();
			assertEquals(4, defaultWidgets.length);
			
			Widget defWidg0 = defaultWidgets[0];
			assertNotNull(defWidg0);
			assertEquals("content_viewer_list", defWidg0.getType().getCode());
			assertEquals(1, defWidg0.getConfig().size());
			assertEquals("ART", defWidg0.getConfig().get("contentType"));
			
			Widget defWidg1 = defaultWidgets[1];
			assertNotNull(defWidg1);
			assertEquals("login_form", defWidg1.getType().getCode());
			assertNull(defWidg1.getConfig());
			
			Widget defWidg2 = defaultWidgets[2];
			assertNull(defWidg2);
			
			Widget defWidg3 = defaultWidgets[3];
			assertNotNull(defWidg3);
			assertEquals("content_viewer", defWidg3.getType().getCode());
			assertEquals(1, defWidg3.getConfig().size());
			assertEquals("ART187", defWidg3.getConfig().get("contentId"));
			
			assertEquals("<strong>Modified Freemarker template content</strong>", extractedMockModel.getTemplate());
			
		} catch (Exception e) {
			throw e;
		} finally {
			this._pageModelDAO.deleteModel(testPageModelCode);
			models = this._pageModelDAO.loadModels();
			assertTrue(!models.containsKey(testPageModelCode));
		}
	}
	
	private PageModel createMockPageModel(String code) {
		PageModel model = new PageModel();
		model.setCode(code);
		model.setDescription("Description of model " + code);
		String[] frames = {"Freme 0", "Frame 1", "Frame 2"};
		model.setFrames(frames);
		Widget[] defaultWidgets = new Widget[3];
		Widget defWidg1 = new Widget();
		defWidg1.setType(this._widgetTypeManager.getWidgetType("content_viewer_list"));
		ApsProperties props1 = new ApsProperties();
		props1.setProperty("contentType", "ART");
		defWidg1.setConfig(props1);
		defaultWidgets[1] = defWidg1;
		Widget defWidg2 = new Widget();
		defWidg2.setType(this._widgetTypeManager.getWidgetType("login_form"));
		defaultWidgets[2] = defWidg2;
		model.setDefaultWidget(defaultWidgets);
		model.setTemplate("<strong>Freemarker template content</strong>");
		return model;
	}
	
	private void init() throws Exception {
		try {
			this._widgetTypeManager = (IWidgetTypeManager) this.getService(SystemConstants.WIDGET_TYPE_MANAGER);
			DataSource dataSource = (DataSource) this.getApplicationContext().getBean("portDataSource");
			this._pageModelDAO = new PageModelDAO();
			this._pageModelDAO.setDataSource(dataSource);
			this._pageModelDAO.setWidgetTypeManager(this._widgetTypeManager);
		} catch (Throwable t) {
			throw new Exception(t);
		}
	}

	private PageModelDAO _pageModelDAO;
	private IWidgetTypeManager _widgetTypeManager;
	
}
