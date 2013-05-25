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
package org.entando.entando.plugins.jacms.apsadmin.content;

import com.agiletec.aps.system.common.entity.model.AttributeTracer;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.CompositeAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.MonoListAttribute;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute.AbstractResourceAttribute;
import com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute.AttachAttribute;
import com.agiletec.plugins.jacms.aps.system.services.resource.IResourceManager;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInterface;
import com.opensymphony.xwork2.Action;

/**
 * @author E.Santoboni
 */
public class TestValidateResourceAttribute extends AbstractTestContentAttribute {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.init();
	}
	
	public void testValidate_SingleAttach() throws Throwable {
		this.testValidate_Single("Attach", "7");
	}
	
	public void testValidate_SingleImage() throws Throwable {
		this.testValidate_Single("Image", "22");
	}
	
	protected void testValidate_Single(String attributeName, String testResourceId) throws Throwable {
		try {
			Content content = this.executeCreateNewContent();
			AttributeTracer tracer = this.getTracer();
			AttributeInterface resourceAttribute = (AttributeInterface) content.getAttribute(attributeName);
			String formFieldName = tracer.getFormFieldName(resourceAttribute);
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, attributeName);
			this.checkFieldErrors(0, formFieldName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldName, "resourceDescription");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, attributeName);
			
			content = this.getContentOnEdit();
			AbstractResourceAttribute attachAttribute = (AbstractResourceAttribute) content.getAttribute(attributeName);
			ResourceInterface resource = this._resourceManager.loadResource(testResourceId);
			attachAttribute.setResource(resource, this.getLangManager().getDefaultLang().getCode());
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, attributeName);
		} catch (Throwable t) {
			this.deleteTestContent();
			throw t;
		}
	}
	
	public void testValidate_AttachMonoList() throws Throwable {
		this.testValidate_MonoListElement("MonoLAtta");
	}
	
	public void testValidate_ImageMonoList() throws Throwable {
		this.testValidate_MonoListElement("MonoLImage");
	}
	
	protected void testValidate_MonoListElement(String mlAttributeName) throws Throwable {
		try {
			Content content = this.executeCreateNewContent();
			AttributeTracer tracer = this.getTracer();
			MonoListAttribute monolistAttribute = (MonoListAttribute) content.getAttribute(mlAttributeName);
			AttributeInterface newResourceAttribute = monolistAttribute.addAttribute();
			
			tracer.setListIndex(monolistAttribute.getAttributes().size() - 1);
			tracer.setListLang(this.getLangManager().getDefaultLang());
			tracer.setMonoListElement(true);
			tracer.setParentAttribute(monolistAttribute);
			
			String monolistElementName = tracer.getMonolistElementFieldName(newResourceAttribute);
			assertEquals(mlAttributeName + "_0", monolistElementName);
			
			String formFieldName = tracer.getFormFieldName(newResourceAttribute);
			assertEquals("it_" + mlAttributeName + "_0", formFieldName);
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, monolistElementName);
			
			this.initSaveContentAction();
			this.addParameter(formFieldName, "resourceDescrMonolElement0Value");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, monolistElementName);
			
			content = this.getContentOnEdit();
			monolistAttribute = (MonoListAttribute) content.getAttribute(mlAttributeName);
			AbstractResourceAttribute resourceAttribute = (AbstractResourceAttribute) monolistAttribute.getAttribute(0);
			ResourceInterface resource = this._resourceManager.loadResource("7");
			resourceAttribute.setResource(resource, this.getLangManager().getDefaultLang().getCode());
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, monolistElementName);
			
			AttributeInterface attribute2 = monolistAttribute.addAttribute();
			tracer.setListIndex(monolistAttribute.getAttributes().size() - 1);
			String formFieldName2 = tracer.getFormFieldName(attribute2);
			assertEquals("it_" + mlAttributeName + "_1", formFieldName2);
			String monolistElementName2 = tracer.getMonolistElementFieldName(attribute2);
			assertEquals(mlAttributeName + "_1", monolistElementName2);
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, monolistElementName2);
		} catch (Throwable t) {
			this.deleteTestContent();
			throw t;
		}
	}
	
	public void testValidate_AttachCompositeElement() throws Throwable {
		this.testValidate_CompositeElement("Attach", "7");
	}
	
	public void testValidate_ImageCompositeElement() throws Throwable {
		this.testValidate_CompositeElement("Image", "44");
	}
	
	protected void testValidate_CompositeElement(String elementName, String testResourceId) throws Throwable {
		try {
			Content content = this.executeCreateNewContent();
			AttributeTracer tracerIT = this.getTracer();
			CompositeAttribute compositeAttribute = (CompositeAttribute) content.getAttribute("Composite");
			AttributeInterface attribute = compositeAttribute.getAttribute(elementName);
			
			tracerIT.setCompositeElement(true);
			tracerIT.setParentAttribute(compositeAttribute);
			
			String formITFieldName = tracerIT.getFormFieldName(attribute);
			assertEquals("it_Composite_" + elementName, formITFieldName);
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, formITFieldName);
			
			this.initSaveContentAction();
			this.addParameter(formITFieldName, "itValue");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, "Composite_" + elementName);
			
			content = this.getContentOnEdit();
			compositeAttribute = (CompositeAttribute) content.getAttribute("Composite");
			AbstractResourceAttribute resourceAttribute = (AbstractResourceAttribute) compositeAttribute.getAttribute(elementName);
			ResourceInterface resource = this._resourceManager.loadResource(testResourceId);
			resourceAttribute.setResource(resource, this.getLangManager().getDefaultLang().getCode());
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, "Composite_" + elementName);
			
		} catch (Throwable t) {
			this.deleteTestContent();
			throw t;
		}
	}
	
	public void testValidate_AttachMonolistCompositeElement() throws Throwable {
		this.testValidate_MonolistCompositeElement("Attach", "7");
	}
	
	public void testValidate_ImageMonolistCompositeElement() throws Throwable {
		this.testValidate_MonolistCompositeElement("Image", "44");
	}
	
	protected void testValidate_MonolistCompositeElement(String elementName, String testResourceId) throws Throwable {
		try {
			Content content = this.executeCreateNewContent();
			AttributeTracer tracerIT = this.getTracer();
			MonoListAttribute monolistAttribute = (MonoListAttribute) content.getAttribute("MonoLCom");
			CompositeAttribute compositeElement = (CompositeAttribute) monolistAttribute.addAttribute();
			AttributeInterface attribute = compositeElement.getAttribute(elementName);
			
			tracerIT.setListIndex(monolistAttribute.getAttributes().size() - 1);
			tracerIT.setListLang(this.getLangManager().getDefaultLang());
			tracerIT.setMonoListElement(true);
			tracerIT.setCompositeElement(true);
			tracerIT.setParentAttribute(compositeElement);
			
			String formITFieldName = tracerIT.getFormFieldName(attribute);
			assertEquals("it_MonoLCom_" + elementName + "_0", formITFieldName);
			
			String monolistElementName = tracerIT.getMonolistElementFieldName(compositeElement);
			assertEquals("MonoLCom_0", monolistElementName);
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, monolistElementName);
			
			this.initSaveContentAction();
			this.addParameter(formITFieldName, "itValue");
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(1, "MonoLCom_" + elementName + "_0");
			
			content = this.getContentOnEdit();
			monolistAttribute = (MonoListAttribute) content.getAttribute("MonoLCom");
			compositeElement = (CompositeAttribute) monolistAttribute.getAttribute(0);
			AbstractResourceAttribute resourceAttribute = (AbstractResourceAttribute) compositeElement.getAttribute(elementName);
			ResourceInterface resource = this._resourceManager.loadResource(testResourceId);
			resourceAttribute.setResource(resource, this.getLangManager().getDefaultLang().getCode());
			
			this.initSaveContentAction();
			this.executeAction(Action.INPUT);
			this.checkFieldErrors(0, "MonoLCom_" + elementName + "_0");
			
		} catch (Throwable t) {
			this.deleteTestContent();
			throw t;
		}
	}
	
	private void init() throws Exception {
    	try {
    		this._resourceManager = (IResourceManager) this.getService(JacmsSystemConstants.RESOURCE_MANAGER);
    	} catch (Throwable t) {
            throw new Exception(t);
        }
    }
	
	protected IResourceManager getResourceManager() {
		return this._resourceManager;
	}
    
    private IResourceManager _resourceManager = null;
	
}
