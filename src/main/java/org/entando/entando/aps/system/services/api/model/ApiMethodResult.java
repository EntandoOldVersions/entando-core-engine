/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando Enterprise Edition software.
* You can redistribute it and/or modify it
* under the terms of the Entando's EULA
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package org.entando.entando.aps.system.services.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author E.Santoboni
 */
public final class ApiMethodResult implements Serializable {
    
    public Object getResult() {
        return _result;
    }
    public void setResult(Object result) {
        this._result = result;
    }
    
    public void addError(String errorCode, String description) {
        ApiError error = new ApiError(errorCode, description);
        this.addError(error);
    }
    
    public void addError(ApiError error) {
        if (null == this.getErrors()) {
            this.setErrors(new ArrayList<ApiError>());
        }
        this.getErrors().add(error);
    }
    
    public List<ApiError> getErrors() {
        return _errors;
    }
    public void setErrors(List<ApiError> errors) {
        this._errors = errors;
    }
    
    private Object _result;
    private List<ApiError> _errors;
    
}
