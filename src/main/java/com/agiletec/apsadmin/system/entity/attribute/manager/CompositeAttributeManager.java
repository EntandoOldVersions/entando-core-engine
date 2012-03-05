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
package com.agiletec.apsadmin.system.entity.attribute.manager;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.CompositeAttribute;
import com.agiletec.apsadmin.system.entity.attribute.AttributeTracer;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Manager class for the the 'composite' attributes.
 * @author E.Santoboni
 */
public class CompositeAttributeManager extends AbstractAttributeManager {
	
	@Override
	protected void checkAttribute(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		super.checkAttribute(action, attribute, tracer, entity);
		this.manageCompositeAttribute(true, false, action, attribute, tracer, null, entity);
	}
	
	@Override
	protected void updateAttribute(AttributeInterface attribute, AttributeTracer tracer, HttpServletRequest request) {
		this.manageCompositeAttribute(false, true, null, attribute, tracer, request, null);
	}
	
	private void manageCompositeAttribute(boolean isCheck, boolean isUpdate, ActionSupport action, 
			AttributeInterface attribute, AttributeTracer tracer, HttpServletRequest request, IApsEntity entity) {
		List<AttributeInterface> attributes = ((CompositeAttribute) attribute).getAttributes();
		for (int i=0; i<attributes.size(); i++) {
			AttributeInterface attributeElement = attributes.get(i);
			AttributeTracer elementTracer = (AttributeTracer) tracer.clone();
			elementTracer.setCompositeElement(true);
			elementTracer.setParentAttribute(attribute);
			AbstractAttributeManager elementManager = (AbstractAttributeManager) this.getManager(attributeElement.getType());
			if (elementManager != null) {
				if (isCheck && !isUpdate) {
					elementManager.checkAttribute(action, attributeElement, elementTracer, entity);
				}
				if (!isCheck && isUpdate) {
					elementManager.updateAttribute(attributeElement, elementTracer, request);
				}
			}
		}
	}
	
	@Override
	protected int getState(AttributeInterface attribute, AttributeTracer tracer) {
		boolean isVoid = true;
		List<AttributeInterface> attributes = ((CompositeAttribute) attribute).getAttributes();
		for (int i=0; i<attributes.size(); i++) {
			AttributeInterface attributeElement = attributes.get(i);
			AbstractAttributeManager elementManager = (AbstractAttributeManager) this.getManager(attributeElement.getType());
			if (elementManager != null) {
				AttributeTracer elementTracer = (AttributeTracer) tracer.clone();
				elementTracer.setCompositeElement(true);
				elementTracer.setParentAttribute(attribute);
				int state = elementManager.getState(attributeElement, elementTracer);
				if (state != this.EMPTY_ATTRIBUTE_STATE) {
					isVoid = false;
					break;
				}
			}
		}
		if (!isVoid) {
			return VALUED_ATTRIBUTE_STATE;
		} else return EMPTY_ATTRIBUTE_STATE;
	}
	
}
