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
package org.entando.entando.aps.system.services.actionlog.model;

import java.util.Date;

/**
 * @author E.Santoboni - S.Puddu
 */
public class ActionLogRecord {
	
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
	
	public String getParameters() {
		return _parameters;
	}
	public void setParameters(String parameters) {
		this._parameters = parameters;
	}
	
	public ActivityStreamInfo getActivityStreamInfo() {
		return _activityStreamInfo;
	}
	public void setActivityStreamInfo(ActivityStreamInfo activityStreamInfo) {
		this._activityStreamInfo = activityStreamInfo;
	}
	
	private int _id;
	private Date _actionDate;
	private String _username;
	private String _namespace;
	private String _actionName;
	private String _parameters;
	private ActivityStreamInfo _activityStreamInfo;
	
}