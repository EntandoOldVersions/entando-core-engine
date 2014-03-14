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

import com.agiletec.aps.system.services.pagemodel.PageModel;
import com.opensymphony.xwork2.Action;

import java.util.List;
import java.util.Map;

/**
 * @author E.Santoboni
 */
public class TestPageModelAction extends AbstractTestPageModelAction {
	
	public void testTrashPageModels_1() throws Throwable {
		String result = this.executeAction("admin", "trash", null);
		assertEquals("pageModelList", result);
		result = this.executeAction("admin", "trash", "invalidCode");
		assertEquals("pageModelList", result);
		result = this.executeAction("admin", "trash", "home");
		assertEquals("references", result);
		PageModelAction pageModelAction = (PageModelAction) this.getAction();
		Map<String, List<Object>> references = pageModelAction.getReferences();
		assertFalse(references.isEmpty());
		assertEquals(1, references.size());
		assertEquals(11, references.get("PageManagerUtilizers").size());
	}
	
	public void testTrashPageModels_2() throws Throwable {
		String testPageModelCode = "test_pagemodel";
		assertNull(this._pageModelManager.getPageModel(testPageModelCode));
		try {
			PageModel mockModel = this.createMockPageModel(testPageModelCode);
			this._pageModelManager.addPageModel(mockModel);
			String result = this.executeAction("admin", "trash", testPageModelCode);
			assertEquals(Action.SUCCESS, result);
			PageModelAction pageModelAction = (PageModelAction) this.getAction();
			Map<String, List<Object>> references = pageModelAction.getReferences();
			assertTrue(null == references || references.isEmpty());
		} catch (Exception e) {
			throw e;
		} finally {
			this._pageModelManager.deletePageModel(testPageModelCode);
			assertNull(this._pageModelManager.getPageModel(testPageModelCode));
		}
	}
	
	public void testDeletePageModels_1() throws Throwable {
		String result = this.executeAction("admin", "delete", null);
		assertEquals("pageModelList", result);
		result = this.executeAction("admin", "delete", "invalidCode");
		assertEquals("pageModelList", result);
		result = this.executeAction("admin", "delete", "home");
		assertEquals("references", result);
		PageModelAction pageModelAction = (PageModelAction) this.getAction();
		Map<String, List<Object>> references = pageModelAction.getReferences();
		assertFalse(references.isEmpty());
		assertEquals(1, references.size());
		assertEquals(11, references.get("PageManagerUtilizers").size());
	}
	
	public void testDeletePageModels_2() throws Throwable {
		String testPageModelCode = "test_pagemodel";
		assertNull(this._pageModelManager.getPageModel(testPageModelCode));
		try {
			PageModel mockModel = this.createMockPageModel(testPageModelCode);
			this._pageModelManager.addPageModel(mockModel);
			String result = this.executeAction("admin", "delete", testPageModelCode);
			assertEquals(Action.SUCCESS, result);
			PageModelAction pageModelAction = (PageModelAction) this.getAction();
			Map<String, List<Object>> references = pageModelAction.getReferences();
			assertTrue(null == references || references.isEmpty());
		} catch (Exception e) {
			this._pageModelManager.deletePageModel(testPageModelCode);
			throw e;
		} finally {
			assertNull(this._pageModelManager.getPageModel(testPageModelCode));
		}
	}
	
	private String executeAction(String currentUser, String actionName, String modelCode) throws Throwable {
		this.setUserOnSession(currentUser);
		this.initAction("/do/PageModel", actionName);
		super.addParameter("code", modelCode);
		return this.executeAction();
	}
	
}