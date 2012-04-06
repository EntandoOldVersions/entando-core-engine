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

import com.agiletec.aps.system.common.entity.model.AttributeFieldError;
import com.agiletec.aps.system.common.entity.model.FieldError;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.DateAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.util.DateAttributeValidationRules;
import com.agiletec.aps.util.DateConverter;
import com.agiletec.apsadmin.util.CheckFormatUtil;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Manager class for the 'date' attribute.
 * @author E.Santoboni
 */
public class DateAttributeManager extends AbstractMonoLangAttributeManager {
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected Object getValue(AttributeInterface attribute) {
        return ((DateAttribute) attribute).getDate();
    }
    
    protected void setValue(AttributeInterface attribute, String value) {
        DateAttribute dateAttribute = (DateAttribute) attribute;
        Date data = null;
        if (value != null) {
            value = value.trim();
        }
        if (CheckFormatUtil.isValidDate(value)) {
            try {
                SimpleDateFormat dataF = new SimpleDateFormat("dd/MM/yyyy");
                data = dataF.parse(value);
                dateAttribute.setFailedDateString(null);
            } catch (ParseException ex) {
                throw new RuntimeException("Error while parsing the date submitted - " + value + " -", ex);
            }
        } else {
            dateAttribute.setFailedDateString(value);
        }
        dateAttribute.setDate(data);
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected int getState(AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer) {
        int state = super.getState(attribute, tracer);
        boolean valuedString = ((DateAttribute) attribute).getFailedDateString() != null;
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
        this.checkDate(action, attribute, tracer);
        this.validateDate(action, attribute, tracer);
    }

    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkListElement(ActionSupport action, AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer, IApsEntity entity) {
        super.checkListElement(action, attribute, tracer, entity);
        this.checkDate(action, attribute, tracer);
        this.validateDate(action, attribute, tracer);
    }

    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkMonoListCompositeElement(ActionSupport action, AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer, IApsEntity entity) {
        super.checkMonoListCompositeElement(action, attribute, tracer, entity);
        this.checkDate(action, attribute, tracer);
        this.validateDate(action, attribute, tracer);
    }

    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected void checkMonoListElement(ActionSupport action, AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer, IApsEntity entity) {
        super.checkMonoListElement(action, attribute, tracer, entity);
        this.checkDate(action, attribute, tracer);
        this.validateDate(action, attribute, tracer);
    }

    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    private void checkDate(ActionSupport action, AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer) {
        if (this.getState(attribute, tracer) == VALUED_ATTRIBUTE_STATE && !this.hasRightValue(attribute)) {
            this.addFieldError(action, attribute, tracer, "DateAttribute.fieldError.invalidDate", null);
        }
    }

    /**
     * Check for the coherency of the data the attribute was valued with
     * @param attribute The attribute to check
     * @return true if the attribute was valued with proper data, false otherwise.
     * @deprecated 
     */
    private boolean hasRightValue(AttributeInterface attribute) {
        if (this.getValue(attribute) != null) {
            return true;
        }
        String insertedDateString = ((DateAttribute) attribute).getFailedDateString();
        return CheckFormatUtil.isValidDate(insertedDateString);
    }
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    private void validateDate(ActionSupport action, AttributeInterface attribute, com.agiletec.apsadmin.system.entity.attribute.AttributeTracer tracer) {
        if (this.getState(attribute, tracer) == VALUED_ATTRIBUTE_STATE && this.hasRightValue(attribute)) {
            DateAttributeValidationRules valRule = (DateAttributeValidationRules) attribute.getValidationRules();
            Date attributeValue = ((DateAttribute) attribute).getDate();
            Date startValue = (valRule.getRangeStart() != null) ? (Date) valRule.getRangeStart() : this.getOtherAttributeValue(attribute, valRule.getRangeStartAttribute());
            if (null != startValue && attributeValue.before(startValue)) {
                String[] args = {DateConverter.getFormattedDate(startValue, "dd/MM/yyyy")};
                this.addFieldError(action, attribute, tracer, "DateAttribute.fieldError.lessValue", args);
            }
            Date endValue = (valRule.getRangeEnd() != null) ? (Date) valRule.getRangeEnd() : this.getOtherAttributeValue(attribute, valRule.getRangeEndAttribute());
            if (null != endValue && attributeValue.after(endValue)) {
                String[] args = {DateConverter.getFormattedDate(endValue, "dd/MM/yyyy")};
                this.addFieldError(action, attribute, tracer, "DateAttribute.fieldError.greaterValue", args);
            }
            Date value = (valRule.getValue() != null) ? (Date) valRule.getValue() : this.getOtherAttributeValue(attribute, valRule.getValueAttribute());
            if (null != value && !attributeValue.equals(value)) {
                String[] args = {DateConverter.getFormattedDate(value, "dd/MM/yyyy")};
                this.addFieldError(action, attribute, tracer, "DateAttribute.fieldError.wrongValue", args);
            }
        }
    }
    
    protected String getCustomAttributeErrorMessage(AttributeFieldError attributeFieldError, ActionSupport action) {
        AttributeInterface attribute = attributeFieldError.getAttribute();
        DateAttributeValidationRules valRule = (DateAttributeValidationRules) attribute.getValidationRules();
        if (null != valRule) {
            String errorCode = attributeFieldError.getErrorCode();
            if (errorCode.equals(FieldError.GREATER_THAN_ALLOWED)) {
                Date endValue = (valRule.getRangeEnd() != null) ? (Date) valRule.getRangeEnd() : this.getOtherAttributeValue(attribute, valRule.getRangeEndAttribute());
                String[] args = {DateConverter.getFormattedDate(endValue, "dd/MM/yyyy")};
                return action.getText("DateAttribute.fieldError.greaterValue", args);
            } else if (errorCode.equals(FieldError.LESS_THAN_ALLOWED)) {
                Date startValue = (valRule.getRangeStart() != null) ? (Date) valRule.getRangeStart() : this.getOtherAttributeValue(attribute, valRule.getRangeStartAttribute());
                String[] args = {DateConverter.getFormattedDate(startValue, "dd/MM/yyyy")};
                return action.getText("DateAttribute.fieldError.lessValue", args);
            } else if (errorCode.equals(FieldError.NOT_EQUALS_THAN_ALLOWED)) {
                Date value = (valRule.getValue() != null) ? (Date) valRule.getValue() : this.getOtherAttributeValue(attribute, valRule.getValueAttribute());
                String[] args = {DateConverter.getFormattedDate(value, "dd/MM/yyyy")};
                return action.getText("DateAttribute.fieldError.wrongValue", args);
            }
        }
        return action.getText(this.getInvalidAttributeMessage());
    }
    
    private Date getOtherAttributeValue(AttributeInterface attribute, String otherAttributeName) {
        AttributeInterface other = (AttributeInterface) attribute.getParentEntity().getAttribute(otherAttributeName);
        if (null != other && (other instanceof DateAttribute) && ((DateAttribute) other).getDate() != null) {
            return ((DateAttribute) other).getDate();
        }
        return null;
    }
    
    protected String getInvalidAttributeMessage() {
        return "DateAttribute.fieldError.invalidDate";
    }
    
}
