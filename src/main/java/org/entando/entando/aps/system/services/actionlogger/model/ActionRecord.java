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
package org.entando.entando.aps.system.services.actionlogger.model;

import java.util.Date;

public class ActionRecord {
	
	public void setId(int id) {
		this._id = id;
	}
	public int getId() {
		return _id;
	}
	
	public Date getActionDate() {
		return _actionDate;
	}
	public void setActionDate(Date actionDate) {
		this._actionDate = actionDate;
	}
	
	public String getUsername() {
		return _username;
	}
	public void setUsername(String username) {
		this._username = username;
	}
	
	public String getNamespace() {
		return _namespace;
	}
	public void setNamespace(String namespace) {
		this._namespace = namespace;
	}
	
	public String getActionName() {
		return _actionName;
	}
	public void setActionName(String actionName) {
		this._actionName = actionName;
	}
	
	public String getParams() {
		return _params;
	}
	public void setParams(String params) {
		this._params = params;
	}
	
	private int _id;
	private Date _actionDate;
	private String _username;
	private String _namespace;
	private String _actionName;
	private String _params;
	
}