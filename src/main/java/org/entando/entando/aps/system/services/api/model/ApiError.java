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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author E.Santoboni
 */
@XmlRootElement(name = "error")
@XmlType(propOrder = {"code", "message"})
public class ApiError implements Serializable {
	
	public ApiError() {}
	
	public ApiError(String code, String message) {
		this.setCode(code);
		this.setMessage(message);
	}
	
	public String getCode() {
		return _code;
	}
	public void setCode(String code) {
		this._code = code;
	}
	
	public String getMessage() {
		return _message;
	}
	public void setMessage(String message) {
		this._message = message;
	}
	
	private String _code;
	private String _message;
	
}