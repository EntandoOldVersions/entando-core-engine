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

import java.util.List;
import java.util.Map;

import com.agiletec.apsadmin.ApsAdminBaseTestCase;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.util.SelectItem;
import com.agiletec.apsadmin.portal.AbstractPortalAction;
import com.agiletec.apsadmin.portal.ShowletsViewerAction;
import com.opensymphony.xwork2.Action;

/**
 * @author E.Santoboni
 */
public class TestShowletsViewerAction extends ApsAdminBaseTestCase {
	
	public void testViewShowlets() throws Throwable {
		String result = this.executeViewShowlets("admin");
		assertEquals(Action.SUCCESS, result);
		
		result = this.executeViewShowlets("pageManagerCustomers");
		assertEquals(Action.SUCCESS, result);
		
		result = this.executeViewShowlets("editorCustomers");
		assertEquals("userNotAllowed", result);
		
		result = this.executeViewShowlets(null);
		assertEquals("apslogin", result);
	}
	
	public void testGetShowletFlavours() throws Throwable {
		String result = this.executeViewShowlets("admin");
		assertEquals(Action.SUCCESS, result);
		AbstractPortalAction action = (AbstractPortalAction) this.getAction();
		List<List<SelectItem>> showletFlavours = action.getShowletFlavours();
		assertNotNull(showletFlavours);
		assertTrue(showletFlavours.size() >= 3);
		Lang currentLang = action.getCurrentLang();
		
		List<SelectItem> userShowlets = showletFlavours.get(0);
		assertEquals(2, userShowlets.size());
		SelectItem userType = userShowlets.get(1);
		assertEquals(AbstractPortalAction.USER_SHOWLETS_CODE, userType.getOptgroup());
		if (currentLang.getCode().equals("it")) {
			assertEquals("logic_type", userType.getKey());
			assertEquals("Tipo logico per test", userType.getValue());
		} else {
			assertEquals("logic_type", userType.getKey());
			assertEquals("Logic type for test", userType.getValue());
		}
		
		List<SelectItem> customShowlets = showletFlavours.get(1);
		assertEquals(1, customShowlets.size());
		SelectItem customType = customShowlets.get(0);
		assertEquals(AbstractPortalAction.CUSTOM_SHOWLETS_CODE, customType.getOptgroup());
		if (currentLang.getCode().equals("it")) {
			assertEquals("leftmenu", customType.getKey());
			assertEquals("Menu di navigazione verticale", customType.getValue());
		} else {
			assertEquals("leftmenu", customType.getKey());
			assertEquals("Vertical Navigation Menu", customType.getValue());
		}
		
		List<SelectItem> jacmsShowlets = showletFlavours.get(2);
		assertEquals(3, jacmsShowlets.size());
		SelectItem jacmsShowletsType = jacmsShowlets.get(1);
		assertEquals("jacms", jacmsShowletsType.getOptgroup());
		if (currentLang.getCode().equals("it")) {
			assertEquals("content_viewer_list", jacmsShowletsType.getKey());
			assertEquals("Contenuti - Pubblica una Lista di Contenuti", jacmsShowletsType.getValue());
		}
		
		List<SelectItem> stockShowlets = showletFlavours.get(showletFlavours.size()-1);
		assertEquals(4, stockShowlets.size());
		SelectItem stockType = stockShowlets.get(3);
		assertEquals(AbstractPortalAction.STOCK_SHOWLETS_CODE, stockType.getOptgroup());
		if (currentLang.getCode().equals("it")) {
			assertEquals("login_form", stockType.getKey());
			assertEquals("Widget di Login", stockType.getValue());
		} else {
			assertEquals("messages_system", stockType.getKey());
			assertEquals("System Messages", stockType.getValue());
		}
	}
	
	public void testGetShowletUtilizers_1() throws Throwable {
		String result = this.executeViewShowletUtilizers("admin", null);
		assertEquals(Action.INPUT, result);
		Map<String, List<String>> fieldErrors = this.getAction().getFieldErrors();
		assertEquals(1, fieldErrors.size());
		assertEquals(1, fieldErrors.get("showletTypeCode").size());

		result = this.executeViewShowletUtilizers("admin", "");
		assertEquals(Action.INPUT, result);
		fieldErrors = this.getAction().getFieldErrors();
		assertEquals(1, fieldErrors.size());
		assertEquals(1, fieldErrors.get("showletTypeCode").size());

		result = this.executeViewShowletUtilizers("admin", "invalidShowletCode");
		assertEquals(Action.INPUT, result);
		fieldErrors = this.getAction().getFieldErrors();
		assertEquals(1, fieldErrors.size());
		assertEquals(1, fieldErrors.get("showletTypeCode").size());
	}
	
	public void testGetShowletUtilizers_2() throws Throwable {
		String result = this.executeViewShowletUtilizers("admin", "logic_type");
		assertEquals(Action.SUCCESS, result);
		ShowletsViewerAction action = (ShowletsViewerAction) this.getAction();
		List<IPage> pageUtilizers = action.getShowletUtilizers();
		assertEquals(0, pageUtilizers.size());
		
		result = this.executeViewShowletUtilizers("admin", "leftmenu");
		assertEquals(Action.SUCCESS, result);
		action = (ShowletsViewerAction) this.getAction();
		pageUtilizers = action.getShowletUtilizers();
		assertEquals(1, pageUtilizers.size());
		assertEquals("pagina_1", pageUtilizers.get(0).getCode());
		
		result = this.executeViewShowletUtilizers("admin", "content_viewer");
		assertEquals(Action.SUCCESS, result);
		action = (ShowletsViewerAction) this.getAction();
		pageUtilizers = action.getShowletUtilizers();
		assertEquals(7, pageUtilizers.size());
		assertEquals("homepage", pageUtilizers.get(0).getCode());
		assertEquals("pagina_2", pageUtilizers.get(3).getCode());
		assertEquals("customer_subpage_2", pageUtilizers.get(6).getCode());
	}
	
	private String executeViewShowlets(String username) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/Portal/ShowletType", "viewShowlets");
		String result = this.executeAction();
		return result;
	}
	
	private String executeViewShowletUtilizers(String username, String showletTypeCode) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/Portal/ShowletType", "viewShowletUtilizers");
		if (null != showletTypeCode) {
			this.addParameter("showletTypeCode", showletTypeCode);
		}
		String result = this.executeAction();
		return result;
	}
	
}