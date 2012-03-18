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
    
    public AttributeFieldError(AttributeInterface attribute, String errorCode, AttributeTracer tracer) {
        super(null, errorCode);
        this.setTracer(tracer);
        this.setAttribute(attribute);
    }
    
    public String getFieldCode() {
        String fieldCode = super.getFieldCode();
        if (null == fieldCode) {
            fieldCode = this.getTracer().getFormFieldName(this.getAttribute());
        }
        return fieldCode;
    }
    
    public String getFullMessage() {
        StringBuffer buffer = new StringBuffer(this.getAttributePositionMessage());
        buffer.append(" : ");
        if (null != this.getMessageKey()) {
            buffer.append(this.getMessageKey());
        } else if (null != this.getMessage()) {
            buffer.append(this.getMessage());
        } else {
            buffer.append(this.getErrorCode());
        }
        return buffer.toString();
    }
    
    public String getAttributePositionMessage() {
        return this.getTracer().getPositionMessage(this.getAttribute());
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
    
    public static final String OGNL_VALIDATION = "OGNL_VALIDATION";
    
}