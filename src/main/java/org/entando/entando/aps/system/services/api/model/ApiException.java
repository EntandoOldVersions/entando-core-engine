package org.entando.entando.aps.system.services.api.model;

import java.util.ArrayList;
import java.util.List;

public class ApiException extends Exception {
	
	public ApiException(String errorKey) {
		super();
		this.addError(errorKey);
	}
	
	public ApiException(List<ApiError> errors) {
		super();
		this.getErrors().addAll(errors);
	}
	
	public ApiException(String errorKey, String message, Throwable cause) {
		super(message, cause);
		this.addError(errorKey);
	}
	
	public ApiException(List<ApiError> errors, Throwable cause) {
		super(cause);
		this.getErrors().addAll(errors);
	}
	
	public ApiException(String errorKey, String message) {
		super(message);
		this.addError(errorKey);
	}
	
	public ApiException(String errorKey, Throwable cause) {
		super(cause);
		this.addError(errorKey);
	}
	
	protected void addError(String key) {
		this.getErrors().add(new ApiError(key, getMessage()));
	}
	
	public List<ApiError> getErrors() {
		return this._errors;
	}
	
	private List<ApiError> _errors = new ArrayList<ApiError>();
	
}