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

/**
 * @author S.Loru
 */
public class ActivityStreamSeachBean extends ActionLogRecordSearchBean implements IActivityStreamSearchBean {

	@Override
	public String getActivityStreamInfo() {
		return _activityStreamInfo;
	}


	public void setActivityStreamInfo(String activityStreamInfo) {
		this._activityStreamInfo = activityStreamInfo;
	}
	
	private String _activityStreamInfo;

}
