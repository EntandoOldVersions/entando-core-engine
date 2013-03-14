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
package com.agiletec.plugins.jacms.apsadmin.content.attribute;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.agiletec.plugins.jacms.apsadmin.content.util.AbstractBaseTestContentAction;

import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.SymbolicLink;
import com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute.LinkAttribute;
import com.agiletec.plugins.jacms.apsadmin.content.attribute.action.link.helper.ILinkAttributeActionHelper;
import com.opensymphony.xwork2.Action;

/**
 * @author E.Santoboni
 */
public class TestUrlLinkAction extends AbstractBaseTestContentAction {
	
	public void testFailureJoinContentLink_1() throws Throwable {
		this.initJoinLinkTest("ART1", "VediAnche", "it");
		
		this.initAction("/do/jacms/Content/Link", "joinUrlLink");
		String result = this.executeAction();
		assertEquals(Action.INPUT, result);
		Map<String, List<String>> fieldErrors = this.getAction().getFieldErrors();
		assertEquals(1, fieldErrors.size());
		List<String> typeFieldErrors = fieldErrors.get("url");
		assertEquals(1, typeFieldErrors.size());
	}
	
	public void testJoinContentLink_1() throws Throwable {
		this.initJoinLinkTest("ART1", "VediAnche", "it");
		
		this.initAction("/do/jacms/Content/Link", "joinUrlLink");
		this.addParameter("url", "http://www.japsportal.org");
		String result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		
		Content content = this.getContentOnEdit();
		LinkAttribute attribute = (LinkAttribute) content.getAttribute("VediAnche");
		SymbolicLink symbolicLink = attribute.getSymbolicLink();
		assertNotNull(symbolicLink);
		assertNull(symbolicLink.getPageDest());
		assertNull(symbolicLink.getContentDest());
		assertEquals("http://www.japsportal.org", symbolicLink.getUrlDest());
	}
	
	private void initJoinLinkTest(String contentId, String simpleLinkAttributeName, String langCode) throws Throwable {
		this.executeEdit(contentId, "admin");
		//iniziazione parametri sessione
		HttpSession session = this.getRequest().getSession();
		session.setAttribute(ILinkAttributeActionHelper.ATTRIBUTE_NAME_SESSION_PARAM, simpleLinkAttributeName);
		session.setAttribute(ILinkAttributeActionHelper.LINK_LANG_CODE_SESSION_PARAM, langCode);
	}
	
}