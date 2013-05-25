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
package com.agiletec.apsadmin.system.entity.attribute.manager;

import com.agiletec.aps.system.common.entity.model.AttributeFieldError;
import com.agiletec.aps.system.common.entity.model.FieldError;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.ITextAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.MonoTextAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.util.TextAttributeValidationRules;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Manager class for the 'Monotext' Attribute.
 * @author E.Santoboni
 */
public class MonoTextAttributeManager extends AbstractMonoLangAttributeManager {
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected Object getValue(AttributeInterface attribute) {
        String text = ((MonoTextAttribute) attribute).getText();
        if (null != text && text.trim().length() > 0) {
            return text;
        }
        return null;
    }
    
    protected void setValue(AttributeInterface attribute, String value) {
        ((MonoTextAttribute) attribute).setText(value);
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkSingleAttribute(ActionSupport action, AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer, IApsEntity entity) {
        super.checkSingleAttribute(action, attribute, tracer, entity);
        this.checkTextLengths(action, attribute, tracer);
        this.checkRegExp(action, attribute, tracer);
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkListElement(ActionSupport action, AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer, IApsEntity entity) {
        super.checkListElement(action, attribute, tracer, entity);
        this.checkTextLengths(action, attribute, tracer);
        this.checkRegExp(action, attribute, tracer);
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkMonoListCompositeElement(ActionSupport action, AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer, IApsEntity entity) {
        super.checkMonoListCompositeElement(action, attribute, tracer, entity);
        this.checkTextLengths(action, attribute, tracer);
        this.checkRegExp(action, attribute, tracer);
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkMonoListElement(ActionSupport action, AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer, IApsEntity entity) {
        super.checkMonoListElement(action, attribute, tracer, entity);
        this.checkTextLengths(action, attribute, tracer);
        this.checkRegExp(action, attribute, tracer);
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkTextLengths(ActionSupport action, AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer) {
        int maxLength = ((ITextAttribute) attribute).getMaxLength();
        int minLength = ((ITextAttribute) attribute).getMinLength();
        if (maxLength != -1 || minLength != -1) {
            String text = this.getTextForCheckLength(attribute);
            if (text != null) {
                text = text.trim();
                if (maxLength != -1 && text.length() > maxLength && text.length() > 0) {
                    String[] args = {String.valueOf(text.length()), String.valueOf(maxLength)};
                    super.addFieldError(action, attribute, tracer, "MonotextAttribute.fieldError.invalidMaxLength", args);
                }
                if (minLength != -1 && text.length() < minLength && text.length() > 0) {
                    String[] args = {String.valueOf(text.length()), String.valueOf(minLength)};
                    super.addFieldError(action, attribute, tracer, "MonotextAttribute.fieldError.invalidMinLength", args);
                }
            }
        }
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkRegExp(ActionSupport action, AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer) {
        String value = (String) this.getValue(attribute);
        ITextAttribute textAttribute = (ITextAttribute) attribute;
        if (null != value && null != textAttribute.getRegexp()) {
            Pattern pattern = Pattern.compile(textAttribute.getRegexp());
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                super.addFieldError(action, attribute, tracer, "MonotextAttribute.fieldError.invalidInsertedText", null);
            }
        }
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected String getTextForCheckLength(AttributeInterface attribute) {
        return (String) this.getValue(attribute);
    }
    
    protected String getCustomAttributeErrorMessage(AttributeFieldError attributeFieldError, ActionSupport action) {
        AttributeInterface attribute = attributeFieldError.getAttribute();
        TextAttributeValidationRules valRules = (TextAttributeValidationRules) attribute.getValidationRules();
        if (null != valRules) {
            ITextAttribute textAttribute = (ITextAttribute) attribute;
            String text = textAttribute.getTextForLang(null);
            String errorCode = attributeFieldError.getErrorCode();
            if (errorCode.equals(FieldError.INVALID_MIN_LENGTH)) {
                String[] args = {String.valueOf(text.length()), String.valueOf(valRules.getMinLength())};
                return action.getText("MonotextAttribute.fieldError.invalidMinLength", args);
            } else if (errorCode.equals(FieldError.INVALID_MAX_LENGTH)) {
                String[] args = {String.valueOf(text.length()), String.valueOf(valRules.getMaxLength())};
                return action.getText("MonotextAttribute.fieldError.invalidMaxLength", args);
            } else if (errorCode.equals(FieldError.INVALID_FORMAT)) {
                return action.getText("MonotextAttribute.fieldError.invalidInsertedText");
            }
        }
        return action.getText(this.getInvalidAttributeMessage());
    }
    
    
}