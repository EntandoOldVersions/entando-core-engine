/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agiletec.aps.system.common.entity.model;

import java.io.Serializable;

/**
 * @author E.Santoboni
 */
public class FieldError implements Serializable {
    
    public FieldError(String fieldCode, ErrorCode errorCode) {
        this.setErrorCode(errorCode);
        this.setFieldCode(fieldCode);
    }
    
    public String getFieldCode() {
        return _fieldCode;
    }
    protected void setFieldCode(String fieldCode) {
        this._fieldCode = fieldCode;
    }
    
    public ErrorCode getErrorCode() {
        return _errorCode;
    }
    protected void setErrorCode(ErrorCode errorCode) {
        this._errorCode = errorCode;
    }
    
    public String getMessage() {
        return _message;
    }
    public void setMessage(String message) {
        this._message = message;
    }
    
    public String getMessageKey() {
        return _messageKey;
    }
    public void setMessageKey(String messageKey) {
        this._messageKey = messageKey;
    }
    
    private String _fieldCode;
    private ErrorCode _errorCode;
    private String _message;
    private String _messageKey;
    
    public enum ErrorCode{MANDATORY, INVALID, INVALID_FORMAT, 
        INVALID_MIN_LENGTH, INVALID_MAX_LENGTH, LESS_THAN_ALLOWED, GREATEST_THAN_ALLOWED};
    
}