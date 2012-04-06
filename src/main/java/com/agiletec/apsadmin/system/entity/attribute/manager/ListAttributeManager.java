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
import com.agiletec.aps.system.common.entity.model.attribute.ListAttribute;
import com.agiletec.aps.system.common.entity.model.AttributeTracer;
import com.agiletec.aps.system.services.lang.Lang;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Manager class for the 'Multi-Language List' Attributes
 * @author E.Santoboni
 */
public class ListAttributeManager extends AbstractAttributeManager {
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void updateAttribute(AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer, HttpServletRequest request) {
        this.updateAttribute(attribute, (AttributeTracer) tracer, request);
    }
    
    protected void updateAttribute(AttributeInterface attribute, AttributeTracer tracer, HttpServletRequest request) {
        List<Lang> langs = this.getLangManager().getLangs();
        for (int i = 0; i < langs.size(); i++) {
            Lang lang = langs.get(i);
            List<AttributeInterface> attributeList = ((ListAttribute) attribute).getAttributeList(lang.getCode());
            for (int j = 0; j < attributeList.size(); j++) {
                AttributeInterface attributeElement = attributeList.get(j);
                AttributeTracer elementTracer = (AttributeTracer) tracer.clone();
                elementTracer.setListElement(true);
                elementTracer.setListLang(lang);
                elementTracer.setListIndex(j);
                AbstractAttributeManager elementManager = (AbstractAttributeManager) this.getManager(attributeElement);
                if (elementManager != null) {
                    elementManager.updateAttribute(attributeElement, elementTracer, request);
                }
            }
        }
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkAttribute(ActionSupport action, AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer, IApsEntity entity) {
        super.checkAttribute(action, attribute, tracer, entity);
        List<Lang> langs = this.getLangManager().getLangs();
        for (int i = 0; i < langs.size(); i++) {
            Lang lang = langs.get(i);
            List<AttributeInterface> attributeList = ((ListAttribute) attribute).getAttributeList(lang.getCode());
            for (int j = 0; j < attributeList.size(); j++) {
                AttributeInterface attributeElement = attributeList.get(j);
                com.agiletec.apsadmin.system.entity.attribute.AttributeTracer elementTracer = (com.agiletec.apsadmin.system.entity.attribute.AttributeTracer) tracer.clone();
                elementTracer.setListElement(true);
                elementTracer.setListLang(lang);
                elementTracer.setListIndex(j);
                AbstractAttributeManager elementManager = (AbstractAttributeManager) this.getManager(attributeElement);
                if (elementManager != null) {
                    elementManager.checkAttribute(action, attributeElement, elementTracer, entity);
                }
            }
        }
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected int getState(AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer) {
        boolean valued = true;
        List<Lang> langs = this.getLangManager().getLangs();
        for (int i = 0; i < langs.size(); i++) {
            Lang lang = langs.get(i);
            List<AttributeInterface> attributeList = ((ListAttribute) attribute).getAttributeList(lang.getCode());
            if (attributeList == null || attributeList.size() == 0) {
                valued = false;
                break;
            }
        }
        if (valued) {
            return VALUED_ATTRIBUTE_STATE;
        } else {
            return EMPTY_ATTRIBUTE_STATE;
        }
    }
    
}