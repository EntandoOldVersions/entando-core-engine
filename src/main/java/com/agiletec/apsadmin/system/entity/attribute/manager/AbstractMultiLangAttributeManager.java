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

import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.AttributeTracer;
import com.agiletec.aps.system.services.lang.Lang;


/**
 * Abstract class for the managers of simple multi-language attributes.
 * @author E.Santoboni
 */
public abstract class AbstractMultiLangAttributeManager extends AbstractAttributeManager {
    
    /**
     * @deprecated As of version 2.4.1 of Entando, use updateAttribute(AttributeInterface, AttributeTracer, HttpServletRequest).
     */
    protected void updateAttribute(AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer, HttpServletRequest request) {
        this.updateAttribute(attribute, (AttributeTracer) tracer, request);
    }
    
    protected void updateAttribute(AttributeInterface attribute, AttributeTracer tracer, HttpServletRequest request) {
        List<Lang> langs = this.getLangManager().getLangs();
        for (int i = 0; i < langs.size(); i++) {
            Lang currentLang = langs.get(i);
            tracer.setLang(currentLang);
            String value = this.getValueFromForm(attribute, tracer, request);
            //TODO PAY ATTENTION TO THIS CHECK
            if (value != null) {
                if (value.trim().length() == 0) {
                    value = null;
                }
                this.setValue(attribute, currentLang, value);
            }
        }
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected int getState(AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer) {
        Lang defaultLang = this.getLangManager().getDefaultLang();
        boolean valued = this.getValue(attribute, defaultLang) != null;
        if (valued) {
            return this.VALUED_ATTRIBUTE_STATE;
        }
        return this.EMPTY_ATTRIBUTE_STATE;
    }
    
    /**
     * Return the value held by the given attribute. 
     * This method is invoked by the getStatus when the request is null, that is when the
     * validation process is triggered by the approval of the list of contents.
     * This value can be referred to as the value typed in the form in the previous save or 
     * approval process; eg. for multi-language text attributes will be returned the text in
     * the specified language, for attributes of type image, the text in the specified
     * language and not the resource itself will be returned.
     * 
     * @param attribute The current attribute (simple or composed) which holds the desired value
     * @param lang The localization desired for the returned value.
     * @return The value held by the attribute.
     * @deprecated 
     */
    protected abstract Object getValue(AttributeInterface attribute, Lang lang);
    
    /**
     * Set the value of the specified attribute.
     * 
     * @param attribute The current attribute (simple or composed) to update.
     * @param lang The language in which the value is expressed.
     * @param value The value to assign to the attribute.
     */
    protected abstract void setValue(AttributeInterface attribute, Lang lang, String value);
    
    protected void setExtraPropertyTo(AttributeManagerInterface manager) {
        super.setExtraPropertyTo(manager);
        ((AbstractMultiLangAttributeManager) manager).setLangManager(this.getLangManager());
    }
    
}
