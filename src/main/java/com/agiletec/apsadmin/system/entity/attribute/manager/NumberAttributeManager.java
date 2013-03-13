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

import com.agiletec.aps.system.common.entity.model.AttributeFieldError;
import com.agiletec.aps.system.common.entity.model.FieldError;
import java.math.BigDecimal;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.NumberAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.util.NumberAttributeValidationRules;
import com.agiletec.apsadmin.util.CheckFormatUtil;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Manager class for the 'Number' Attribute 
 * @author E.Santoboni
 */
public class NumberAttributeManager extends AbstractMonoLangAttributeManager {
    
    protected void setValue(AttributeInterface attribute, String value) {
        NumberAttribute numberAttribute = (NumberAttribute) attribute;
        BigDecimal number = null;
        if (value != null) {
            value = value.trim();
        }
        if (CheckFormatUtil.isValidNumber(value)) {
            try {
                number = new BigDecimal(value);
                numberAttribute.setFailedNumberString(null);
            } catch (NumberFormatException e) {
                throw new RuntimeException("The submitted string is not recognized as a valid number - " + value + " -");
            }
        } else {
            numberAttribute.setFailedNumberString(value);
        }
        numberAttribute.setValue(number);
    }

    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected Object getValue(AttributeInterface attribute) {
        return ((NumberAttribute) attribute).getValue();
    }

    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected int getState(AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer) {
        int state = super.getState(attribute, tracer);
        boolean valuedString = ((NumberAttribute) attribute).getFailedNumberString() != null;
        if (state == VALUED_ATTRIBUTE_STATE || valuedString) {
            return this.VALUED_ATTRIBUTE_STATE;
        }
        return this.EMPTY_ATTRIBUTE_STATE;
    }

    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkSingleAttribute(ActionSupport action, AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer, IApsEntity entity) {
        super.checkSingleAttribute(action, attribute, tracer, entity);
        this.checkNumber(action, attribute, tracer);
        this.validateNumber(action, attribute, tracer);
    }

    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkListElement(ActionSupport action, AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer, IApsEntity entity) {
        super.checkListElement(action, attribute, tracer, entity);
        this.checkNumber(action, attribute, tracer);
        this.validateNumber(action, attribute, tracer);
    }

    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkMonoListCompositeElement(ActionSupport action, AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer, IApsEntity entity) {
        super.checkMonoListCompositeElement(action, attribute, tracer, entity);
        this.checkNumber(action, attribute, tracer);
        this.validateNumber(action, attribute, tracer);
    }

    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkMonoListElement(ActionSupport action, AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer, IApsEntity entity) {
        super.checkMonoListElement(action, attribute, tracer, entity);
        this.checkNumber(action, attribute, tracer);
        this.validateNumber(action, attribute, tracer);
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    private void checkNumber(ActionSupport action, AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer) {
        if (this.getState(attribute, tracer) == VALUED_ATTRIBUTE_STATE && !this.hasRightValue(attribute)) {
            this.addFieldError(action, attribute, tracer, "NumberAttribute.fieldError.invalidNumber", null);
        }
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    private void validateNumber(ActionSupport action, AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer) {
        if (this.getState(attribute, tracer) == VALUED_ATTRIBUTE_STATE && this.hasRightValue(attribute)) {
            NumberAttributeValidationRules valRules = (NumberAttributeValidationRules) attribute.getValidationRules();
            Integer attributeValue = ((NumberAttribute) attribute).getValue().intValue();
            Integer startValue = (valRules.getRangeStart() != null) ? (Integer) valRules.getRangeStart() : this.getOtherAttributeValue(attribute, valRules.getRangeStartAttribute());
            if (null != startValue && attributeValue < startValue) {
                String[] args = {startValue.toString()};
                this.addFieldError(action, attribute, tracer, "NumberAttribute.fieldError.lessValue", args);
            }
            Integer endValue = (valRules.getRangeEnd() != null) ? (Integer) valRules.getRangeEnd() : this.getOtherAttributeValue(attribute, valRules.getRangeEndAttribute());
            if (null != endValue && attributeValue > endValue) {
                String[] args = {endValue.toString()};
                this.addFieldError(action, attribute, tracer, "NumberAttribute.fieldError.greaterValue", args);
            }
            Integer value = (valRules.getValue() != null) ? (Integer) valRules.getValue() : this.getOtherAttributeValue(attribute, valRules.getValueAttribute());
            if (null != value && attributeValue != value) {
                String[] args = {value.toString()};
                this.addFieldError(action, attribute, tracer, "NumberAttribute.fieldError.wrongValue", args);
            }
        }
    }
    
    protected String getCustomAttributeErrorMessage(AttributeFieldError attributeFieldError, ActionSupport action) {
        AttributeInterface attribute = attributeFieldError.getAttribute();
        NumberAttributeValidationRules valRules = (NumberAttributeValidationRules) attribute.getValidationRules();
        if (null != valRules) {
            String errorCode = attributeFieldError.getErrorCode();
            if (errorCode.equals(FieldError.LESS_THAN_ALLOWED)) {
                Integer startValue = (valRules.getRangeStart() != null) ? (Integer) valRules.getRangeStart() : this.getOtherAttributeValue(attribute, valRules.getRangeStartAttribute());
                String[] args = {startValue.toString()};
                return action.getText("NumberAttribute.fieldError.lessValue", args);
            } else if (errorCode.equals(FieldError.GREATER_THAN_ALLOWED)) {
                Integer endValue = (valRules.getRangeEnd() != null) ? (Integer) valRules.getRangeEnd() : this.getOtherAttributeValue(attribute, valRules.getRangeEndAttribute());
                String[] args = {endValue.toString()};
                return action.getText("NumberAttribute.fieldError.greaterValue", args);
            } else if (errorCode.equals(FieldError.NOT_EQUALS_THAN_ALLOWED)) {
                Integer value = (valRules.getValue() != null) ? (Integer) valRules.getValue() : this.getOtherAttributeValue(attribute, valRules.getValueAttribute());
                String[] args = {value.toString()};
                return action.getText("NumberAttribute.fieldError.wrongValue", args);
            }
        }
        return action.getText(this.getInvalidAttributeMessage());
    }
    
    private Integer getOtherAttributeValue(AttributeInterface attribute, String otherAttributeName) {
        AttributeInterface other = (AttributeInterface) attribute.getParentEntity().getAttribute(otherAttributeName);
        if (null != other && (other instanceof NumberAttribute) && ((NumberAttribute) other).getValue() != null) {
            return ((NumberAttribute) other).getValue().intValue();
        }
        return null;
    }

    /**
     * Check for the coherency of the data of the attribute. 
     * @param attribute The attribute to check.
     * @return true if the attribute is properly valued, false otherwise.
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    private boolean hasRightValue(AttributeInterface attribute) {
        if (this.getValue(attribute) != null) {
            return true;
        }
        String insertedNumberString = ((NumberAttribute) attribute).getFailedNumberString();
        return CheckFormatUtil.isValidNumber(insertedNumberString);
    }
    
    protected String getInvalidAttributeMessage() {
        return "NumberAttribute.fieldError.invalidNumber";
    }
    
}