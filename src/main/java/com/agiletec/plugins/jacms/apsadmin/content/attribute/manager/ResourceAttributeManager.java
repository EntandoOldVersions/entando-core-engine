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
package com.agiletec.plugins.jacms.apsadmin.content.attribute.manager;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.apsadmin.system.entity.attribute.AttributeTracer;
import com.agiletec.apsadmin.system.entity.attribute.manager.TextAttributeManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute.AbstractResourceAttribute;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInterface;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Classe manager degli attributi tipo risorsa (Image o Attach).
 * @author E.Santoboni
 */
public class ResourceAttributeManager extends TextAttributeManager {
	
	/**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkSingleAttribute(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		super.checkSingleAttribute(action, attribute, tracer, entity);
		int state = this.getState(attribute, tracer);
		if (state == INCOMPLETE_ATTRIBUTE_STATE) {
			this.addFieldError(action, attribute, tracer, this.getInvalidAttributeMessage(), null);
		}
		this.checkResource(action, attribute, tracer, entity);
	}
	
	/**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkMonoListCompositeElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		super.checkMonoListCompositeElement(action, attribute, tracer, entity);
		this.checkResource(action, attribute, tracer, entity);
	}
	
	/**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkMonoListElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		super.checkMonoListElement(action, attribute, tracer, entity);
		this.checkResource(action, attribute, tracer, entity);
	}
	
	/**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkResource(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		int state = this.getState(attribute, tracer);
		if (state == VALUED_ATTRIBUTE_STATE) {
			ResourceInterface resource = ((AbstractResourceAttribute) attribute).getResource();
			String resourceMainGroup = resource.getMainGroup();
			if (!resourceMainGroup.equals(Group.FREE_GROUP_NAME) && !resourceMainGroup.equals(entity.getMainGroup()) && !entity.getGroups().contains(resourceMainGroup)) {
				String messageKey = "ResourceAttribute.fieldError.invalidGroup";
				this.addFieldError(action, attribute, tracer, messageKey, null);
			}
		}
	}
	
	/**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected int getState(AttributeInterface attribute, AttributeTracer tracer) {
		boolean isTextValued = super.getState(attribute, tracer) == VALUED_ATTRIBUTE_STATE;
		boolean isResourceValued = ((AbstractResourceAttribute) attribute).getResource() != null;
		if (isResourceValued && isTextValued) return VALUED_ATTRIBUTE_STATE;
		if (!isResourceValued && !isTextValued) return EMPTY_ATTRIBUTE_STATE;
		return INCOMPLETE_ATTRIBUTE_STATE;
	}
	
	protected String getInvalidAttributeMessage() {
		return "ResourceAttribute.fieldError.invalidResource";
	}
	
}