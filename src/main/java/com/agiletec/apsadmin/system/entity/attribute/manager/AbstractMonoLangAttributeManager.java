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

import javax.servlet.http.HttpServletRequest;

import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.AttributeTracer;

/**
 * Base abstract class for those manager which handle mono-language simple attributes.
 * @author E.Santoboni
 */
public abstract class AbstractMonoLangAttributeManager extends AbstractAttributeManager {
    
    /**
     * @deprecated As of version 2.4.1 of Entando, use updateAttribute(AttributeInterface, AttributeTracer, HttpServletRequest).
     */
    protected void updateAttribute(AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer, HttpServletRequest request) {
        this.updateAttribute(attribute, (AttributeTracer) tracer, request);
    }
    
    protected void updateAttribute(AttributeInterface attribute, AttributeTracer tracer, HttpServletRequest request) {
        String value = this.getValueFromForm(attribute, tracer, request);
        if (value != null) {
            if (value.trim().length() == 0) {
                value = null;
            }
            this.setValue(attribute, value);
        }
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected int getState(AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer) {
        boolean valued = this.getValue(attribute) != null;
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
     * approval process; eg. for mono-language text attributes it will be the text, for data
     * attributes will be the data as typed in the form. 
     * 
     * @param attribute The current attribute (simple or composed) which holds the desired value
     * @return The value held by the attribute.
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected abstract Object getValue(AttributeInterface attribute);
    
    /**.
     * Set the value of the specified attribute.
     * 
     * @param attribute The current attribute (simple or composed) to assign the value to.
     * @param value The value to assign to the attribute.
     */
    protected abstract void setValue(AttributeInterface attribute, String value);
    
}
