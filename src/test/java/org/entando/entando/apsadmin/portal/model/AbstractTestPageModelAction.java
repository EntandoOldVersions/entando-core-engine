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
package org.entando.entando.apsadmin.portal.model;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.system.services.pagemodel.Frame;
import com.agiletec.aps.system.services.pagemodel.IPageModelManager;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.apsadmin.ApsAdminBaseTestCase;

import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;

/**
 * @author E.Santoboni
 */
public abstract class AbstractTestPageModelAction extends ApsAdminBaseTestCase {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.init();
	}
	/*
	public void testGetPageModels_1() throws Throwable {
		String result = this.executeList("admin");
		assertEquals(Action.SUCCESS, result);
		PageModelFinderAction pageModelFinderAction = (PageModelFinderAction) this.getAction();
		List<PageModel> models = pageModelFinderAction.getModels();
		assertEquals(3, models.size());
		assertEquals("internal", models.get(0).getCode());
		assertEquals("home", models.get(1).getCode());
		assertEquals("service", models.get(2).getCode());
	}
	
	public void testGetPageModels_2() throws Throwable {
		String testPageModelCode = "test_pagemodel";
		assertNull(this._pageModelManager.getPageModel(testPageModelCode));
		try {
			PageModel mockModel = this.createMockPageModel(testPageModelCode);
			this._pageModelManager.addPageModel(mockModel);
			
			String result = this.executeList("admin");
			assertEquals(Action.SUCCESS, result);
			PageModelFinderAction pageModelFinderAction = (PageModelFinderAction) this.getAction();
			List<PageModel> models = pageModelFinderAction.getModels();
			assertEquals(4, models.size());
			assertEquals(testPageModelCode, models.get(0).getCode());
		} catch (Exception e) {
			throw e;
		} finally {
			this._pageModelManager.deletePageModel(testPageModelCode);
			assertNull(this._pageModelManager.getPageModel(testPageModelCode));
		}
	}
	
	private String executeList(String currentUser) throws Throwable {
		this.setUserOnSession(currentUser);
		this.initAction("/do/PageModel", "list");
		return this.executeAction();
	}
	*/
	protected PageModel createMockPageModel(String code) {
		PageModel model = new PageModel();
		model.setCode(code);
		model.setDescription("Description of model " + code);
		Frame frame0 = new Frame();
		frame0.setPos(0);
		frame0.setDescription("Freme 0");
		frame0.setMainFrame(true);
		Frame frame1 = new Frame();
		frame1.setPos(1);
		frame1.setDescription("Freme 1");
		Widget defWidg1 = new Widget();
		defWidg1.setType(this._widgetTypeManager.getWidgetType("content_viewer_list"));
		ApsProperties props1 = new ApsProperties();
		props1.setProperty("contentType", "ART");
		defWidg1.setConfig(props1);
		frame1.setDefaultWidget(defWidg1);
		Frame frame2 = new Frame();
		frame2.setPos(1);
		frame2.setDescription("Freme 2");
		Widget defWidg2 = new Widget();
		defWidg2.setType(this._widgetTypeManager.getWidgetType("login_form"));
		frame2.setDefaultWidget(defWidg2);
		Frame[] configuration = {frame0, frame1, frame2};
		model.setConfiguration(configuration);
		model.setTemplate("<strong>Freemarker template content</strong>");
		return model;
	}
	
	private void init() throws Exception {
    	try {
			this._widgetTypeManager = (IWidgetTypeManager) this.getService(SystemConstants.WIDGET_TYPE_MANAGER);
    		this._pageModelManager = (IPageModelManager) this.getService(SystemConstants.PAGE_MODEL_MANAGER);
    	} catch (Throwable t) {
    		throw new Exception(t);
        }
    }
    
	protected IWidgetTypeManager _widgetTypeManager;
    protected IPageModelManager _pageModelManager = null;
	
}