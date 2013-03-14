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

import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.MonoListAttribute;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.SymbolicLink;
import com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute.LinkAttribute;
import com.agiletec.plugins.jacms.apsadmin.content.attribute.action.link.helper.ILinkAttributeActionHelper;
import com.opensymphony.xwork2.Action;

/**
 * @author E.Santoboni
 */
public class TestLinkAttributeAction extends AbstractBaseTestContentAction {
	
	public void testChooseLink_1() throws Throwable {
		this.executeEdit("ART1", "admin");
		this.initAction("/do/jacms/Content", "chooseLink");
		this.addParameter("attributeName", "VediAnche");
		this.addParameter("langCode", "it");
		String result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		
		HttpSession session = this.getRequest().getSession();
		assertEquals("VediAnche", session.getAttribute(ILinkAttributeActionHelper.ATTRIBUTE_NAME_SESSION_PARAM));
		assertEquals("it", session.getAttribute(ILinkAttributeActionHelper.LINK_LANG_CODE_SESSION_PARAM));
		assertNull(session.getAttribute(ILinkAttributeActionHelper.LIST_ELEMENT_INDEX_SESSION_PARAM));
		Content currentContent = this.getContentOnEdit();
		assertNotNull(currentContent);
		assertEquals("ART1", currentContent.getId());
		assertEquals("Articolo", currentContent.getDescr());
	}
	
	public void testChooseLink_2() throws Throwable {
		this.executeEdit("EVN191", "admin");
		this.initAction("/do/jacms/Content", "chooseLink");
		this.addParameter("attributeName", "LinkCorrelati");
		this.addParameter("elementIndex", "0");
		this.addParameter("langCode", "it");
		String result = this.executeAction();
		assertEquals(BaseAction.FAILURE, result);//FALLIMENTO PER LISTA VUOTA
		
		Content currentContent = this.getContentOnEdit();
		MonoListAttribute monoListAttribute = (MonoListAttribute) currentContent.getAttribute("LinkCorrelati");
		List<AttributeInterface> attributes = monoListAttribute.getAttributes();
		assertEquals(0, attributes.size());
		
		this.initAction("/do/jacms/Content", "addListElement");
		this.addParameter("attributeName", "LinkCorrelati");
		this.addParameter("listLangCode", "it");
		result = this.executeAction();
		assertEquals("chooseLink", result);
		
		currentContent = this.getContentOnEdit();
		monoListAttribute = (MonoListAttribute) currentContent.getAttribute("LinkCorrelati");
		attributes = monoListAttribute.getAttributes();
		assertEquals(1, attributes.size());
		
		this.initAction("/do/jacms/Content", "chooseLink");
		this.addParameter("attributeName", "LinkCorrelati");
		this.addParameter("elementIndex", "0");
		this.addParameter("langCode", "it");
		result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		
		HttpSession session = this.getRequest().getSession();
		assertEquals("LinkCorrelati", session.getAttribute(ILinkAttributeActionHelper.ATTRIBUTE_NAME_SESSION_PARAM));
		assertEquals("it", session.getAttribute(ILinkAttributeActionHelper.LINK_LANG_CODE_SESSION_PARAM));
		assertNotNull(session.getAttribute(ILinkAttributeActionHelper.LIST_ELEMENT_INDEX_SESSION_PARAM));
		assertEquals(new Integer(0), session.getAttribute(ILinkAttributeActionHelper.LIST_ELEMENT_INDEX_SESSION_PARAM));
		currentContent = this.getContentOnEdit();
		assertNotNull(currentContent);
		assertEquals("EVN191", currentContent.getId());
		assertEquals("Evento 1", currentContent.getDescr());
	}
	
	public void testRemoveLink() throws Throwable {
		this.executeEdit("ART102", "admin");
		
		Content currentContent = this.getContentOnEdit();
		assertNotNull(currentContent);
		LinkAttribute linkAttribute = (LinkAttribute) currentContent.getAttribute("VediAnche");
		assertNotNull(linkAttribute);
		SymbolicLink symbolicLink = linkAttribute.getSymbolicLink();
		assertNotNull(symbolicLink);
		assertEquals("ART111", symbolicLink.getContentDest());
		
		this.initAction("/do/jacms/Content", "removeLink");
		this.addParameter("attributeName", "VediAnche");
		this.addParameter("langCode", "it");
		String result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		
		currentContent = this.getContentOnEdit();
		assertNotNull(currentContent);
		linkAttribute = (LinkAttribute) currentContent.getAttribute("VediAnche");
		assertNotNull(linkAttribute);
		symbolicLink = linkAttribute.getSymbolicLink();
		assertNull(symbolicLink);
	}
	
	public void testFailureChooseLinkType_1() throws Throwable {
		this.executeEdit("ART1", "admin");
		
		//iniziazione parametri sessione
		HttpSession session = this.getRequest().getSession();
		session.setAttribute(ILinkAttributeActionHelper.ATTRIBUTE_NAME_SESSION_PARAM, "VediAnche");
		session.setAttribute(ILinkAttributeActionHelper.LINK_LANG_CODE_SESSION_PARAM, "it");
		
		this.initAction("/do/jacms/Content/Link", "configLink");
		String result = this.executeAction();
		assertEquals(Action.INPUT, result);
		Map<String, List<String>> fieldErrors = this.getAction().getFieldErrors();
		assertEquals(1, fieldErrors.size());
		List<String> typeFieldErrors = fieldErrors.get("linkType");
		assertEquals(1, typeFieldErrors.size());
		
		Content currentContent = this.getContentOnEdit();
		assertNotNull(currentContent);
		assertEquals("ART1", currentContent.getId());
		assertEquals("Articolo", currentContent.getDescr());
	}
	
	public void testFailureChooseLinkType_2() throws Throwable {
		this.executeEdit("ART1", "admin");
		
		//iniziazione parametri sessione
		HttpSession session = this.getRequest().getSession();
		session.setAttribute(ILinkAttributeActionHelper.ATTRIBUTE_NAME_SESSION_PARAM, "VediAnche");
		session.setAttribute(ILinkAttributeActionHelper.LINK_LANG_CODE_SESSION_PARAM, "it");
		
		this.initAction("/do/jacms/Content/Link", "configLink");
		this.addParameter("linkType", "0");
		String result = this.executeAction();
		assertEquals(Action.INPUT, result);
		Map<String, List<String>> fieldErrors = this.getAction().getFieldErrors();
		assertEquals(1, fieldErrors.size());
		List<String> typeFieldErrors = fieldErrors.get("linkType");
		assertEquals(1, typeFieldErrors.size());
		
		this.initAction("/do/jacms/Content/Link", "configLink");
		this.addParameter("linkType", "4");
		result = this.executeAction();
		assertEquals(Action.INPUT, result);
		fieldErrors = this.getAction().getFieldErrors();
		assertEquals(1, fieldErrors.size());
		typeFieldErrors = fieldErrors.get("linkType");
		assertEquals(1, typeFieldErrors.size());
		
		Content currentContent = this.getContentOnEdit();
		assertNotNull(currentContent);
		assertEquals("ART1", currentContent.getId());
		assertEquals("Articolo", currentContent.getDescr());
	}
	
	public void testChooseLinkType() throws Throwable {
		this.executeEdit("ART1", "admin");
		
		//iniziazione parametri sessione
		HttpSession session = this.getRequest().getSession();
		session.setAttribute(ILinkAttributeActionHelper.ATTRIBUTE_NAME_SESSION_PARAM, "VediAnche");
		session.setAttribute(ILinkAttributeActionHelper.LINK_LANG_CODE_SESSION_PARAM, "it");
		
		this.initAction("/do/jacms/Content/Link", "configLink");
		this.addParameter("linkType", "1");
		String result = this.executeAction();
		assertEquals("configUrlLink", result);
		
		this.initAction("/do/jacms/Content/Link", "configLink");
		this.addParameter("linkType", "2");
		result = this.executeAction();
		assertEquals("configPageLink", result);
		
		this.initAction("/do/jacms/Content/Link", "configLink");
		this.addParameter("linkType", "3");
		result = this.executeAction();
		assertEquals("configContentLink", result);
		
		Content currentContent = this.getContentOnEdit();
		assertNotNull(currentContent);
		assertEquals("ART1", currentContent.getId());
		assertEquals("Articolo", currentContent.getDescr());
	}
	
	
}