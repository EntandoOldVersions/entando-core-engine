/*
 *
 * Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
 *
 * This file is part of Entando software.
 * Entando is a free software; 
 * you can redistribute it and/or modify it
 * under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
 * 
 * See the file License for the specific language governing permissions   
 * and limitations under the License
 * 
 * 
 * 
 * Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
 *
 */
package org.entando.entando.plugins.jacms.apsadmin.content;

import com.agiletec.aps.system.common.entity.model.AttributeTracer;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.CompositeAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.ListAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.MonoListAttribute;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.opensymphony.xwork2.Action;

/**
 * @author E.Santoboni
 */
public class TestValidateDateAttribute extends AbstractTestValidateAttribute {
	
	public void testValidate_Single_1() throws Throwable {
		try {
			Content content = this.executeCreateNewContent();
			AttributeTracer tracer = this.getTracer();
			AttributeInterface dateAttribute = (AttributeInterface) content.getAttribute("Date");
			String formFieldName = tracer.getFormFieldName(dateAttribute);

			this.initSaveContentAction();
			this.addParameter(formFieldName, "dateValue");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, formFieldName);

			this.initSaveContentAction();
			this.addParameter(formFieldName, "25/07/2012");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, formFieldName);
		} catch (Throwable t) {
			this.deleteTestContent();
			throw t;
		}
	}
	
	public void testValidate_MonoListElement() throws Throwable {
		try {
			Content content = this.executeCreateNewContent();
			AttributeTracer tracer = this.getTracer();
			MonoListAttribute monolist = (MonoListAttribute) content.getAttribute("MonoLDate");
			AttributeInterface attribute = monolist.addAttribute();
			
			tracer.setLang(this.getLangManager().getDefaultLang());
			tracer.setListIndex(monolist.getAttributes().size() - 1);
			tracer.setListLang(this.getLangManager().getDefaultLang());
			tracer.setMonoListElement(true);
			//tracer.setParentAttribute(monolist);
			
			String formFieldName = tracer.getFormFieldName(attribute);
			assertEquals("MonoLDate_0", formFieldName);
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, formFieldName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldName, "dateValue");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, formFieldName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldName, "11/07/1982");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, formFieldName);
			
			AttributeInterface attribute2 = monolist.addAttribute();
			tracer.setListIndex(monolist.getAttributes().size() - 1);
			String formFieldName2 = tracer.getFormFieldName(attribute2);
			assertEquals("MonoLDate_1", formFieldName2);
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, formFieldName2);
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.addParameter(formFieldName2, "18/04/1971");
			this.checkFieldErrors(1, formFieldName2);
			
		} catch (Throwable t) {
			this.deleteTestContent();
			throw t;
		}
	}
	
	public void testValidate_ListElement() throws Throwable {
		try {
			Content content = this.executeCreateNewContent();
			AttributeTracer tracerIT = this.getTracer();
			ListAttribute list = (ListAttribute) content.getAttribute("ListDate");
			AttributeInterface attributeIT = list.addAttribute("it");
			assertEquals(0, list.getAttributeList("en").size());
			assertEquals(1, list.getAttributeList("it").size());
			
			tracerIT.setListIndex(list.getAttributeList("it").size() - 1);
			tracerIT.setListLang(this.getLangManager().getLang("it"));
			tracerIT.setListElement(true);
			//tracerIT.setParentAttribute(list);
			
			String formFieldItName = tracerIT.getFormFieldName(attributeIT);
			assertEquals("it_ListDate_0", formFieldItName);
			
			AttributeTracer tracerEN = tracerIT.clone();
			tracerEN.setLang(this.getLangManager().getLang("en"));
			tracerEN.setListLang(this.getLangManager().getLang("en"));
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, formFieldItName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldItName, "ListDateElement0Value");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, formFieldItName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldItName, "26/11/2007");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, formFieldItName);
			
			AttributeInterface attribute2 = list.addAttribute("it");
			tracerIT.setListIndex(list.getAttributes().size() - 1);
			formFieldItName = tracerIT.getFormFieldName(attribute2);
			assertEquals("it_ListDate_1", formFieldItName);
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, formFieldItName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldItName, "26/11/2007");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, formFieldItName);
			
			AttributeInterface attributeEN = list.addAttribute("en");
			String formFieldEnName = tracerEN.getFormFieldName(attributeEN);
			assertEquals("en_ListDate_0", formFieldEnName);
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, formFieldEnName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldEnName, "06/07/1987");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, formFieldEnName);
			
		} catch (Throwable t) {
			this.deleteTestContent();
			throw t;
		}
	}
	
	public void testValidate_CompositeElement() throws Throwable {
		try {
			Content content = this.executeCreateNewContent();
			AttributeTracer tracer = this.getTracer();
			CompositeAttribute compositeAttribute = (CompositeAttribute) content.getAttribute("Composite");
			AttributeInterface attribute = compositeAttribute.getAttribute("Date");
			
			tracer.setCompositeElement(true);
			tracer.setParentAttribute(compositeAttribute);
			
			String formFieldName = tracer.getFormFieldName(attribute);
			assertEquals("Composite_Date", formFieldName);
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, formFieldName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldName, "wrongDateValue");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, formFieldName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldName, "10/10/2011");//validation: end range 10/10/2010
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, formFieldName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldName, "10/10/2008");//validation: end range 10/10/2010
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, formFieldName);
			
			this.initSaveContentAction();
			this.addParameter("Date", "10/10/2009");//validation: start range attribute "Date"
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, formFieldName);
		} catch (Throwable t) {
			this.deleteTestContent();
			throw t;
		}
	}
	
	public void testValidate_ListCompositeElement() throws Throwable {
		try {
			Content content = this.executeCreateNewContent();
			AttributeTracer tracer = this.getTracer();
			MonoListAttribute monolist = (MonoListAttribute) content.getAttribute("MonoLCom");
			CompositeAttribute compositeElement = (CompositeAttribute) monolist.addAttribute();
			AttributeInterface attribute = compositeElement.getAttribute("Date");
			
			tracer.setListIndex(monolist.getAttributes().size() - 1);
			tracer.setListLang(this.getLangManager().getDefaultLang());
			tracer.setMonoListElement(true);
			tracer.setCompositeElement(true);
			tracer.setParentAttribute(compositeElement);
			
			String formFieldName = tracer.getFormFieldName(attribute);
			assertEquals("MonoLCom_Date_0", formFieldName);
			
			String monolistElementName = tracer.getMonolistElementFieldName(compositeElement);
			assertEquals("MonoLCom_0", monolistElementName);
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, monolistElementName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldName, "wrongDateValue");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, formFieldName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldName, "24/10/1961");//validation: start range 10/10/1971
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, formFieldName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldName, "10/10/2008");//validation: start range 10/10/1971
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, formFieldName);
			
			this.initSaveContentAction();
			this.addParameter("Date", "10/10/1999");//validation: end range attribute "Date"
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, formFieldName);
			
		} catch (Throwable t) {
			this.deleteTestContent();
			throw t;
		}
	}
	
}