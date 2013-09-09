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

public class ActionRecordSearchBean implements IActionRecordSearchBean {
	
	@Override
	public Date getStart() {
		return _start;
	}
	public void setStart(Date start) {
		this._start = start;
	}
	
	@Override
	public Date getEnd() {
		return _end;
	}
	public void setEnd(Date end) {
		this._end = end;
	}
	
	@Override
	public String getUsername() {
		return _username;
	}
	public void setUsername(String username) {
		this._username = username;
	}
	
	@Override
	public String getNamespace() {
		return _namespace;
	}
	public void setNamespace(String namespace) {
		this._namespace = namespace;
	}
	
	@Override
	public String getActionName() {
		return _actionName;
	}
	public void setActionName(String actionName) {
		this._actionName = actionName;
	}
	
	@Override
	public String getParams() {
		return _params;
	}
	public void setParams(String params) {
		this._params = params;
	}
	
	private Date _start;
	private Date _end;
	private String _username;
	private String _namespace;
	private String _actionName;
	private String _params;
	
}