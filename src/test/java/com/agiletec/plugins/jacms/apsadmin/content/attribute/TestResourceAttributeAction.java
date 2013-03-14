/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
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
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.plugins.jacms.apsadmin.content.attribute;

import javax.servlet.http.HttpSession;

import com.agiletec.plugins.jacms.apsadmin.content.util.AbstractBaseTestContentAction;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute.AttachAttribute;
import com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute.ImageAttribute;
import com.agiletec.plugins.jacms.aps.system.services.resource.IResourceManager;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInterface;
import com.agiletec.plugins.jacms.apsadmin.content.attribute.action.resource.ResourceAttributeActionHelper;
import com.opensymphony.xwork2.Action;

/**
 * @author E.Santoboni
 */
public class TestResourceAttributeAction extends AbstractBaseTestContentAction {
	
	public void testChooseImageResource() throws Throwable {
		this.executeEdit("ART1", "admin");
		this.initAction("/do/jacms/Content", "chooseResource");
		this.addParameter("attributeName", "Foto");
		this.addParameter("resourceTypeCode", "Image");
		this.addParameter("resourceLangCode", "it");
		String result = this.executeAction();
		assertEquals(Action.SUCCESS, result);
		
		HttpSession session = this.getRequest().getSession();
		assertEquals("Foto", session.getAttribute(ResourceAttributeActionHelper.ATTRIBUTE_NAME_SESSION_PARAM));
		assertEquals("Image", session.getAttribute(ResourceAttributeActionHelper.RESOURCE_TYPE_CODE_SESSION_PARAM));
		assertEquals("it", session.getAttribute(ResourceAttributeActionHelper.RESOURCE_LANG_CODE_SESSION_PARAM));
		assertNull(session.getAttribute(ResourceAttributeActionHelper.LIST_ELEMENT_INDEX_SESSION_PARAM));
	}
	
	public void testRemoveImageResource_1() throws Throwable {
		this.initForImageRemovingTest();
		
		this.initAction("/do/jacms/Content", "removeResource");
		this.addParameter("attributeName", "Foto");
		this.addParameter("resourceTypeCode", "Image");
		this.addParameter("resourceLangCode", "it");
		assertEquals(Action.SUCCESS, this.executeAction());
		
		Content contentOnEdit = this.getContentOnEdit();
		ImageAttribute imageAttribute = (ImageAttribute) contentOnEdit.getAttribute("Foto");
		assertNull(imageAttribute.getResource("it"));
		assertNull(imageAttribute.getResource("en"));
	}
	
	public void testRemoveImageResource_2() throws Throwable {
		this.initForImageRemovingTest();
		
		this.initAction("/do/jacms/Content", "removeResource");
		this.addParameter("attributeName", "Foto");
		this.addParameter("resourceTypeCode", "Image");
		this.addParameter("resourceLangCode", "en");
		assertEquals(Action.SUCCESS, this.executeAction());
		
		Content contentOnEdit = this.getContentOnEdit();
		ImageAttribute imageAttribute = (ImageAttribute) contentOnEdit.getAttribute("Foto");
		assertNotNull(imageAttribute.getResource("it"));
		assertEquals("44", imageAttribute.getResource("it").getId());
		assertNull(imageAttribute.getResource("en"));
	}

	private void initForImageRemovingTest() throws Throwable, ApsSystemException {
		this.executeEdit("ART180", "admin");
		Content contentOnEdit = this.getContentOnEdit();
		ImageAttribute imageAttribute = (ImageAttribute) contentOnEdit.getAttribute("Foto");
		assertEquals("44", imageAttribute.getResource("it").getId());
		assertNull(imageAttribute.getResource("en"));
		
		IResourceManager resourceManager = (IResourceManager) this.getService(JacmsSystemConstants.RESOURCE_MANAGER);
		ResourceInterface res = resourceManager.loadResource("22");
		assertNotNull(res);
		assertEquals("jAPS Team", res.getDescr());
		imageAttribute.getResources().put("en", res);
	}
	
	public void testRemoveAttachResource() throws Throwable {
		this.executeEdit("RAH1", "admin");
		Content contentOnEdit = this.getContentOnEdit();
		AttachAttribute attachAttribute = (AttachAttribute) contentOnEdit.getAttribute("Allegati");
		assertEquals("7", attachAttribute.getResource("it").getId());
		assertNull(attachAttribute.getResource("en"));
		
		this.initAction("/do/jacms/Content", "removeResource");
		this.addParameter("attributeName", "Allegati");
		this.addParameter("resourceTypeCode", "Attach");
		this.addParameter("resourceLangCode", "it");
		assertEquals(Action.SUCCESS, this.executeAction());
		
		contentOnEdit = this.getContentOnEdit();
		attachAttribute = (AttachAttribute) contentOnEdit.getAttribute("Allegati");
		assertNull(attachAttribute.getResource("it"));
		assertNull(attachAttribute.getResource("en"));
	}
	
}