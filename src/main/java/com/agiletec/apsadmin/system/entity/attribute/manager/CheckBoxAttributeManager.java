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
package com.agiletec.apsadmin.system.entity.attribute.manager;

import javax.servlet.http.HttpServletRequest;

import com.agiletec.aps.system.common.entity.model.AttributeTracer;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.BooleanAttribute;

/**
 * Manager class for the 'checkbox' attributes.
 * @author E.Santoboni
 */
public class CheckBoxAttributeManager extends AbstractMonoLangAttributeManager {
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
	@Override
    protected Object getValue(AttributeInterface attribute) {
        return ((BooleanAttribute) attribute).getBooleanValue();
    }
    
	@Override
    protected void setValue(AttributeInterface attribute, String value) {
        if (null != value) {
            ((BooleanAttribute) attribute).setBooleanValue(Boolean.parseBoolean(value));
        } else {
            ((BooleanAttribute) attribute).setBooleanValue(null);
        }
    }
    
	@Override
    protected void updateAttribute(AttributeInterface attribute, AttributeTracer tracer, HttpServletRequest request) {
        String value = this.getValueFromForm(attribute, tracer, request);
        if (value != null) {
            if (value.trim().length() == 0) {
                value = null;
            }
            this.setValue(attribute, value);
        } else {
            this.setValue(attribute, null);
        }
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando
     */
	@Override
    protected void updateAttribute(AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer, HttpServletRequest request) {
        this.updateAttribute(attribute, (AttributeTracer) tracer, request);
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
	@Override
    protected boolean isValidListElement(AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer) {
        return true;
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
	@Override
    protected boolean isValidMonoListElement(AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer) {
        return true;
    }
    
}