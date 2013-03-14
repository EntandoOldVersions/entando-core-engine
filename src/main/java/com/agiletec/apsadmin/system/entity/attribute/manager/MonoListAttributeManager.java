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
package com.agiletec.apsadmin.system.entity.attribute.manager;

import com.agiletec.aps.system.common.entity.model.AttributeTracer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.MonoListAttribute;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Manager class for the 'Monolist' Attributes
 * @author E.Santoboni
 */
public class MonoListAttributeManager extends AbstractAttributeManager {
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkAttribute(ActionSupport action, AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer, IApsEntity entity) {
        super.checkAttribute(action, attribute, tracer, entity);
        List<AttributeInterface> attributes = ((MonoListAttribute) attribute).getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            AttributeInterface attributeElement = attributes.get(i);
            com.agiletec.apsadmin.system.entity.attribute.AttributeTracer elementTracer = (com.agiletec.apsadmin.system.entity.attribute.AttributeTracer) tracer.clone();
            elementTracer.setMonoListElement(true);
            elementTracer.setListIndex(i);
            AbstractAttributeManager elementManager = (AbstractAttributeManager) this.getManager(attributeElement);
            if (elementManager != null) {
                elementManager.checkAttribute(action, attributeElement, elementTracer, entity);
            }
        }
    }
    
    protected void updateAttribute(AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer, HttpServletRequest request) {
        this.updateAttribute(attribute, (AttributeTracer) tracer, request);
    }
    
    protected void updateAttribute(AttributeInterface attribute, AttributeTracer tracer, HttpServletRequest request) {
        List<AttributeInterface> attributes = ((MonoListAttribute) attribute).getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            AttributeInterface attributeElement = attributes.get(i);
            AttributeTracer elementTracer = (AttributeTracer) tracer.clone();
            elementTracer.setMonoListElement(true);
            elementTracer.setListIndex(i);
            AbstractAttributeManager elementManager = (AbstractAttributeManager) this.getManager(attributeElement);
            if (elementManager != null) {
                elementManager.updateAttribute(attributeElement, elementTracer, request);
            }
        }
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected int getState(AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer) {
        boolean valued = ((MonoListAttribute) attribute).getAttributes().size() > 0;
        if (valued) {
            return VALUED_ATTRIBUTE_STATE;
        } else {
            return EMPTY_ATTRIBUTE_STATE;
        }
    }
    
}