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

import com.agiletec.aps.system.common.entity.model.attribute.BooleanAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.CheckBoxAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.ThreeStateAttribute;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.opensymphony.xwork2.Action;

/**
 * @author E.Santoboni
 */
public class TestSaveBooleanAttributes extends AbstractTestContentAttribute {
	
	public void testSaveBooleanAttribute() throws Throwable {
		try {
			Content content = this.executeCreateNewContent();
			BooleanAttribute attribute = (BooleanAttribute) content.getAttribute("Boolean");
			assertNull(attribute.getBooleanValue());
			assertFalse(attribute.getValue());
			
			this.initSaveContentAction();
			content = this.executeSaveAndReloadContent();
			attribute = (BooleanAttribute) content.getAttribute("Boolean");
			assertNotNull(attribute.getBooleanValue());
			assertFalse(attribute.getValue());
			
			this.initSaveContentAction();
			this.addParameter("Boolean", "false");
			content = this.executeSaveAndReloadContent();
			attribute = (BooleanAttribute) content.getAttribute("Boolean");
			assertNotNull(attribute.getBooleanValue());
			assertFalse(attribute.getValue());
			
			this.initSaveContentAction();
			this.addParameter("Boolean", "true");
			content = this.executeSaveAndReloadContent();
			attribute = (BooleanAttribute) content.getAttribute("Boolean");
			assertNotNull(attribute.getBooleanValue());
			assertTrue(attribute.getValue());
			
		} catch (Throwable t) {
			throw t;
		} finally {
			this.deleteTestContent();
		}
	}
	
	public void testSaveCheckBoxAttribute() throws Throwable {
		try {
			Content content = this.executeCreateNewContent();
			CheckBoxAttribute attribute = (CheckBoxAttribute) content.getAttribute("CheckBox");
			assertNull(attribute.getBooleanValue());
			assertFalse(attribute.getValue());
			
			this.initSaveContentAction();
			content = this.executeSaveAndReloadContent();
			attribute = (CheckBoxAttribute) content.getAttribute("CheckBox");
			assertNull(attribute.getBooleanValue());
			assertFalse(attribute.getValue());
			
			this.initSaveContentAction();
			this.addParameter("CheckBox", "false");
			content = this.executeSaveAndReloadContent();
			attribute = (CheckBoxAttribute) content.getAttribute("CheckBox");
			assertNull(attribute.getBooleanValue());
			assertFalse(attribute.getValue());
			
			this.initSaveContentAction();
			this.addParameter("CheckBox", "true");
			content = this.executeSaveAndReloadContent();
			attribute = (CheckBoxAttribute) content.getAttribute("CheckBox");
			assertNotNull(attribute.getBooleanValue());
			assertTrue(attribute.getValue());
			
		} catch (Throwable t) {
			throw t;
		} finally {
			this.deleteTestContent();
		}
	}
	
	public void testSaveThreeStateAttribute() throws Throwable {
		try {
			Content content = this.executeCreateNewContent();
			ThreeStateAttribute attribute = (ThreeStateAttribute) content.getAttribute("ThreeState");
			assertNull(attribute.getBooleanValue());
			assertFalse(attribute.getValue());
			
			this.initSaveContentAction();
			content = this.executeSaveAndReloadContent();
			attribute = (ThreeStateAttribute) content.getAttribute("ThreeState");
			assertNull(attribute.getBooleanValue());
			assertFalse(attribute.getValue());
			
			this.initSaveContentAction();
			this.addParameter("ThreeState", "false");
			content = this.executeSaveAndReloadContent();
			attribute = (ThreeStateAttribute) content.getAttribute("ThreeState");
			assertNotNull(attribute.getBooleanValue());
			assertFalse(attribute.getValue());
			
			this.initSaveContentAction();
			this.addParameter("ThreeState", "true");
			content = this.executeSaveAndReloadContent();
			attribute = (ThreeStateAttribute) content.getAttribute("ThreeState");
			assertNotNull(attribute.getBooleanValue());
			assertTrue(attribute.getValue());
			
		} catch (Throwable t) {
			throw t;
		} finally {
			this.deleteTestContent();
		}
	}
	
	private Content executeSaveAndReloadContent() throws Throwable {
		Content contentOnSession = super.getContentOnEdit();
		this.addParameter("MARKER", "MARKER");
		this.executeAction(Action.SUCCESS);
		String id = contentOnSession.getId();
		String result = super.executeEdit(id, "admin");
		assertEquals(Action.SUCCESS, result);
		return super.getContentOnEdit();
	}
	
}