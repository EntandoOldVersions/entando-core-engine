/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agiletec.aps.system.common.entity.model;

import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import java.io.Serializable;

/**
 * @author E.Santoboni
 */
public class AttributeFieldError extends FieldError implements Serializable {
    
    public AttributeFieldError(AttributeInterface attribute, ErrorCode errorCode, AttributeTracer tracer) {
        super(null, errorCode);
        this.setTracer(tracer);
        this.setAttribute(attribute);
    }
    
    public AttributeFieldError(String fieldCode, ErrorCode errorCode, AttributeTracer tracer) {
        super(fieldCode, errorCode);
        this.setTracer(tracer);
    }
    public String getFieldCode() {
        String fieldCode = super.getFieldCode();
        if (null == fieldCode) {
            fieldCode = this.getTracer().getFormFieldName(this.getAttribute());
        }
        return fieldCode;
    }
    
    public AttributeTracer getTracer() {
        return _tracer;
    }
    protected void setTracer(AttributeTracer tracer) {
        this._tracer = tracer;
    }
    
    public AttributeInterface getAttribute() {
        return _attribute;
    }
    protected void setAttribute(AttributeInterface attribute) {
        this._attribute = attribute;
    }
    
    private AttributeTracer _tracer;
    private AttributeInterface _attribute;
    
    /*
EntityAttribute.fieldError.required=Mandatory
EntityAttribute.fieldError.invalidAttribute=Invalid

NumberAttribute.fieldError.invalidNumber=Number not valid
NumberAttribute.fieldError.lessValue=Number less than allowed ''{0}''
NumberAttribute.fieldError.greaterValue=Number greater than allowed ''{0}''

DateAttribute.fieldError.invalidDate=Date not valid
DateAttribute.fieldError.lessValue=Date less than allowed ''{0}''
DateAttribute.fieldError.greaterValue=Date greater than allowed ''{0}''

TextAttribute.fieldError.invalidMaxLength=Length ''{0}'' upper than allowed ''{1}'' on text of lang ''{2}''
TextAttribute.fieldError.invalidMinLength=Length ''{0}'' lower then allowed ''{1}'' on text of lang ''{2}''
TextAttribute.fieldError.invalidInsertedText=Invalid inserted text on text of lang ''{0}''

MonotextAttribute.fieldError.invalidMaxLength=Length ''{0}'' upper than allowed ''{1}''
MonotextAttribute.fieldError.invalidMinLength=Length ''{0}'' lower then allowed ''{1}''
MonotextAttribute.fieldError.invalidInsertedText=Invalid inserted text
     */
    
}