/*
*
* Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
* This file is part of jAPS software.
* jAPS is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
*/
package com.agiletec.plugins.jacms.apsadmin.content.attribute;

import java.util.List;

import com.agiletec.plugins.jacms.apsadmin.content.util.AbstractBaseTestContentAction;

import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.ITextAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.MonoListAttribute;
import com.agiletec.apsadmin.system.entity.attribute.action.list.IListAttributeAction;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.opensymphony.xwork2.Action;

/**
 * @author E.Santoboni
 */
public class TestListAttributeAction extends AbstractBaseTestContentAction {
	
	public void testAddListElement() throws Throwable {
		this.initContent();
		this.initAction("/do/jacms/Content", "addListElement");
		this.addParameter("attributeName", "Autori");
		this.addParameter("listLangCode", "it");
		String result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		Content currentContent = this.getContentOnEdit();
		MonoListAttribute monoListAttribute = (MonoListAttribute) currentContent.getAttribute("Autori");
		List<AttributeInterface> attributes = monoListAttribute.getAttributes();
		String[] expected = {"Pippo", "Paperino", "Pluto", ""};
		this.verifyText(attributes, expected);
	}
	
	public void testMoveListElement() throws Throwable {
		this.initContent();
		this.initAction("/do/jacms/Content", "moveListElement");
		this.addParameter("attributeName", "Autori");
		this.addParameter("elementIndex", "1");
		this.addParameter("listLangCode", "it");
		this.addParameter("movement", IListAttributeAction.MOVEMENT_UP_CODE);
		String result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		Content currentContent = this.getContentOnEdit();
		MonoListAttribute monoListAttribute = (MonoListAttribute) currentContent.getAttribute("Autori");
		List<AttributeInterface> attributes = monoListAttribute.getAttributes();
		String[] expected = {"Paperino", "Pippo", "Pluto"};
		this.verifyText(attributes, expected);
		
		this.initAction("/do/jacms/Content", "moveListElement");
		this.addParameter("attributeName", "Autori");
		this.addParameter("elementIndex", "1");
		this.addParameter("listLangCode", "it");
		this.addParameter("movement", IListAttributeAction.MOVEMENT_DOWN_CODE);
		result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		currentContent = this.getContentOnEdit();
		monoListAttribute = (MonoListAttribute) currentContent.getAttribute("Autori");
		attributes = monoListAttribute.getAttributes();
		String[] expected2 = {"Paperino", "Pluto", "Pippo"};
		this.verifyText(attributes, expected2);
	}
	
	public void testRemoveListElement() throws Throwable {
		this.initContent();
		this.initAction("/do/jacms/Content", "removeListElement");
		this.addParameter("attributeName", "Autori");
		this.addParameter("elementIndex", "1");
		this.addParameter("listLangCode", "it");
		String result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		Content currentContent = this.getContentOnEdit();
		MonoListAttribute monoListAttribute = (MonoListAttribute) currentContent.getAttribute("Autori");
		List<AttributeInterface> attributes = monoListAttribute.getAttributes();
		String[] expected = {"Pippo", "Pluto"};
		this.verifyText(attributes, expected);
	}
	
	private void initContent() throws Throwable {
		this.executeEdit("ART1", "admin");
		Content currentContent = this.getContentOnEdit();
		MonoListAttribute monoListAttribute = (MonoListAttribute) currentContent.getAttribute("Autori");
		List<AttributeInterface> attributes = monoListAttribute.getAttributes();
		String[] expected = {"Pippo", "Paperino", "Pluto"};
		this.verifyText(attributes, expected);
	}
	
	private void verifyText(List<AttributeInterface> attributes, String[] expected) {
		assertEquals(expected.length, attributes.size());
		for (int i=0; i<attributes.size(); i++) {
			ITextAttribute textAttribute = (ITextAttribute) attributes.get(i);
			assertEquals(expected[i], textAttribute.getText());
		}
	}
	
	//TODO FARE IL RESTO
	
}
