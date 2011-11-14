/*
*
* Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
* This file is part of jAPS software.
* jAPS is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
*/
package org.entando.entando.aps.system.services.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.entando.entando.aps.system.services.api.IApiErrorCodes;


/**
 * @author E.Santoboni
 */
public final class ApiMethodResult implements Serializable {
	
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	
	public boolean addError(ErrorType type, String description) {
		if (null == type || null == description) return false;
		ApiError error = null;
		switch (type) {
		case FIELD_VALIDATION:
			error = new ApiError(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, description);
			break;
		case RUN_TIME:
			error = new ApiError(IApiErrorCodes.API_METHOD_ERROR, description);
			break;
		}
		if (null == error) return false;
		if (null == this.getErrors()) this.setErrors(new ArrayList<ApiError>());
		this.getErrors().add(error);
		return true;
	}
	
	public List<ApiError> getErrors() {
		return errors;
	}
	public void setErrors(List<ApiError> errors) {
		this.errors = errors;
	}
	
	private Object result;
	private List<ApiError> errors;
	
	public static enum ErrorType {
		FIELD_VALIDATION, RUN_TIME
	}
	
}
