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
import com.agiletec.aps.system.common.entity.model.attribute.MonoListAttribute;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.opensymphony.xwork2.Action;

/**
 * @author E.Santoboni
 */
public class TestValidateTextAttribute extends AbstractTestContentAttribute {
	
	public void testValidate_Single_1() throws Throwable {
		try {
			Content content = this.executeCreateNewContent();
			AttributeTracer tracer = this.getTracer();
			AttributeInterface monotext = (AttributeInterface) content.getAttribute("Text");
			String formFieldName = tracer.getFormFieldName(monotext);
			
			this.initSaveContentAction();
			this.addParameter(formFieldName, "monotextValue");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, formFieldName);
		} catch (Throwable t) {
			this.deleteTestContent();
			throw t;
		}
	}
	
	public void testValidate_Single_2() throws Throwable {
		try {
			Content content = this.executeCreateNewContent();
			AttributeTracer tracer = this.getTracer();
			AttributeInterface textAttribute = (AttributeInterface) content.getAttribute("Text2");
			//Mail attribute (required, min=15, max=30, regex=**mailFormat**)
			String formFieldName = tracer.getFormFieldName(textAttribute);
			assertEquals("it_Text2", formFieldName);
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, formFieldName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldName, "invalidText2Value");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, formFieldName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldName, "ii@22.it");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, formFieldName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldName, "aabbccddeeffgghh112233@iillmmnnooppqq334455.it");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, formFieldName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldName, "aabbccdd@eeffgghhii.com");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, formFieldName);
			
		} catch (Throwable t) {
			this.deleteTestContent();
			throw t;
		}
	}
	
	public void testValidate_Single_3() throws Throwable {
		try {
			Content content = this.executeCreateNewContent();
			AttributeTracer tracer = this.getTracer();
			tracer.setLang(this.getLangManager().getLang("en"));//NO DEFAULT LANG
			AttributeInterface textAttribute = (AttributeInterface) content.getAttribute("Text2");
			//Mail attribute (required, min=15, max=50, regex=**mailFormat**)
			String formFieldName = tracer.getFormFieldName(textAttribute);
			assertEquals("en_Text2", formFieldName);
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, formFieldName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldName, "invalidText2Value");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, formFieldName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldName, "ii@22.it");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, formFieldName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldName, "aabbccddeeffgghh112233@iillmmnnooppqq334455.it");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, formFieldName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldName, "aabbccdd@eeffgghhii.com");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, formFieldName);
			
		} catch (Throwable t) {
			this.deleteTestContent();
			throw t;
		}
	}
	
	public void testValidate_MonoListElement_1() throws Throwable {
		try {
			Content content = this.executeCreateNewContent();
			AttributeTracer tracer = this.getTracer();
			MonoListAttribute monolistAttribute = (MonoListAttribute) content.getAttribute("MonoLText");
			AttributeInterface textAttribute = monolistAttribute.addAttribute();
			
			tracer.setListIndex(monolistAttribute.getAttributes().size() - 1);
			tracer.setListLang(this.getLangManager().getDefaultLang());
			tracer.setMonoListElement(true);
			tracer.setParentAttribute(monolistAttribute);
			
			String monolistElementName = tracer.getMonolistElementFieldName(textAttribute);
			assertEquals("MonoLText_0", monolistElementName);
			
			String formFieldName = tracer.getFormFieldName(textAttribute);
			assertEquals("it_MonoLText_0", formFieldName);
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, monolistElementName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldName, "MonoLMonotElement0Value");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, formFieldName);
			
			AttributeInterface attribute2 = monolistAttribute.addAttribute();
			tracer.setListIndex(monolistAttribute.getAttributes().size() - 1);
			String formFieldName2 = tracer.getFormFieldName(attribute2);
			assertEquals("it_MonoLText_1", formFieldName2);
			String monolistElementName2 = tracer.getMonolistElementFieldName(attribute2);
			assertEquals("MonoLText_1", monolistElementName2);
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, monolistElementName2);
		} catch (Throwable t) {
			this.deleteTestContent();
			throw t;
		}
	}
	
	public void testValidate_MonoListElement_2() throws Throwable {
		try {
			Content content = this.executeCreateNewContent();
			AttributeTracer tracerIT = this.getTracer();
			MonoListAttribute monolistAttribute = (MonoListAttribute) content.getAttribute("MonoLText");
			AttributeInterface textAttribute = monolistAttribute.addAttribute();
			
			tracerIT.setListIndex(monolistAttribute.getAttributes().size() - 1);
			tracerIT.setListLang(this.getLangManager().getDefaultLang());
			tracerIT.setMonoListElement(true);
			tracerIT.setParentAttribute(monolistAttribute);
			
			AttributeTracer tracerEN = tracerIT.clone();
			tracerEN.setLang(this.getLangManager().getLang("en"));
			
			String monolistElementName = tracerIT.getMonolistElementFieldName(textAttribute);
			assertEquals("MonoLText_0", monolistElementName);
			
			String formITFieldName = tracerIT.getFormFieldName(textAttribute);
			assertEquals("it_MonoLText_0", formITFieldName);
			
			String formENFieldName = tracerEN.getFormFieldName(textAttribute);
			assertEquals("en_MonoLText_0", formENFieldName);
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, monolistElementName);
			
			this.initSaveContentAction();
			this.addParameter(formENFieldName, "MonoLMonotElement0ValueEN");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, monolistElementName);
			
			this.initSaveContentAction();
			this.addParameter(formENFieldName, "MonoLMonotElement0ValueEN");
			this.addParameter(formITFieldName, "MonoLMonotElement0ValueIT");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, monolistElementName);
			
			AttributeInterface attribute2 = monolistAttribute.addAttribute();
			tracerIT.setListIndex(monolistAttribute.getAttributes().size() - 1);
			tracerEN.setListIndex(monolistAttribute.getAttributes().size() - 1);
			String formITFieldName2 = tracerIT.getFormFieldName(attribute2);
			assertEquals("it_MonoLText_1", formITFieldName2);
			String formENFieldName2 = tracerEN.getFormFieldName(attribute2);
			assertEquals("en_MonoLText_1", formENFieldName2);
			String monolistElementName2 = tracerIT.getMonolistElementFieldName(attribute2);
			assertEquals("MonoLText_1", monolistElementName2);
			
			this.initSaveContentAction();
			this.addParameter(formENFieldName2, "MonoLMonotElement1ValueEN");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, monolistElementName2);
			
			this.initSaveContentAction();
			this.addParameter(formENFieldName2, "MonoLMonotElement1ValueEN");
			this.addParameter(formITFieldName2, "MonoLMonotElement1ValueIT");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, monolistElementName2);
			
		} catch (Throwable t) {
			this.deleteTestContent();
			throw t;
		}
	}
	
	public void testValidate_CompositeElement() throws Throwable {
		try {
			Content content = this.executeCreateNewContent();
			AttributeTracer tracerIT = this.getTracer();
			CompositeAttribute compositeAttribute = (CompositeAttribute) content.getAttribute("Composite");
			AttributeInterface attribute = compositeAttribute.getAttribute("Text");
			
			tracerIT.setCompositeElement(true);
			tracerIT.setParentAttribute(compositeAttribute);
			
			String formITFieldName = tracerIT.getFormFieldName(attribute);
			assertEquals("it_Composite_Text", formITFieldName);
			
			AttributeTracer tracerEN = tracerIT.clone();
			tracerEN.setLang(this.getLangManager().getLang("en"));
			String formENFieldName = tracerEN.getFormFieldName(attribute);
			assertEquals("en_Composite_Text", formENFieldName);
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, formITFieldName);
			
			this.initSaveContentAction();
			this.addParameter(formITFieldName, "itValue");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, formITFieldName);
			
			this.initSaveContentAction();
			this.addParameter(formITFieldName, "");
			this.addParameter(formENFieldName, "enValue");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, formITFieldName);
			
		} catch (Throwable t) {
			this.deleteTestContent();
			throw t;
		}
	}
	
	public void testValidate_MonolistCompositeElement() throws Throwable {
		try {
			Content content = this.executeCreateNewContent();
			AttributeTracer tracerIT = this.getTracer();
			MonoListAttribute monolist = (MonoListAttribute) content.getAttribute("MonoLCom");
			CompositeAttribute compositeElement = (CompositeAttribute) monolist.addAttribute();
			AttributeInterface attribute = compositeElement.getAttribute("Text");
			
			tracerIT.setListIndex(monolist.getAttributes().size() - 1);
			tracerIT.setListLang(this.getLangManager().getDefaultLang());
			tracerIT.setMonoListElement(true);
			tracerIT.setCompositeElement(true);
			tracerIT.setParentAttribute(compositeElement);
			
			String formITFieldName = tracerIT.getFormFieldName(attribute);
			assertEquals("it_MonoLCom_Text_0", formITFieldName);
			
			AttributeTracer tracerEN = tracerIT.clone();
			tracerEN.setLang(this.getLangManager().getLang("en"));
			String formENFieldName = tracerEN.getFormFieldName(attribute);
			assertEquals("en_MonoLCom_Text_0", formENFieldName);
			
			String monolistElementName = tracerIT.getMonolistElementFieldName(compositeElement);
			assertEquals("MonoLCom_0", monolistElementName);
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, monolistElementName);
			
			this.initSaveContentAction();
			this.addParameter(formITFieldName, "itValue");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, formITFieldName);
			
			this.initSaveContentAction();
			this.addParameter(formITFieldName, "");
			this.addParameter(formENFieldName, "enValue");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, monolistElementName);
			
			this.initSaveContentAction();
			this.addParameter(formITFieldName, "itValue");
			this.addParameter(formENFieldName, "enValue");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, monolistElementName);
		} catch (Throwable t) {
			this.deleteTestContent();
			throw t;
		}
	}
	
}
