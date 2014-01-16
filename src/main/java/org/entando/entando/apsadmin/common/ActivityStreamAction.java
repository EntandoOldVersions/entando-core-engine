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

package org.entando.entando.apsadmin.common;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.apsadmin.system.BaseAction;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.entando.entando.aps.system.services.actionlog.IActionLogManager;
import org.entando.entando.aps.system.services.actionlog.model.ActionLogRecordSearchBean;

/**
 * @author S.Loru
 */
public class ActivityStreamAction extends BaseAction{
	
	public String viewMore(){
		return SUCCESS;
	}
	
	public String update(){
		return SUCCESS;
	}
	
	public List<Integer> getViewMore() {
		List<Integer> actionRecordIds = null;
		try {
			Date timestamp = this.getTimestamp();
			ActionLogRecordSearchBean searchBean = new ActionLogRecordSearchBean();
			List<Group> userGroups = this.getAuthorizationManager().getUserGroups(this.getCurrentUser());
			searchBean.setUserGroupCodes(groupsToStringCode(userGroups));
			searchBean.setEnd(timestamp);
			actionRecordIds = this.getActionLogManager().getActionRecords(searchBean);
		} catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "editLikeActivity", "Error on like/unlike activity");
        }
		return actionRecordIds;
	}
	
	public List<Integer> getUpdate() {
		List<Integer> actionRecordIds = null;
		try {
			Date timestamp = this.getTimestamp();
			ActionLogRecordSearchBean searchBean = new ActionLogRecordSearchBean();
			List<Group> userGroups = this.getAuthorizationManager().getUserGroups(this.getCurrentUser());
			searchBean.setUserGroupCodes(groupsToStringCode(userGroups));
			searchBean.setStart(timestamp);
			searchBean.setEnd(new Date());
			actionRecordIds = this.getActionLogManager().getActionRecords(searchBean);
		} catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "editLikeActivity", "Error on like/unlike activity");
        }
		return actionRecordIds;
	}
	
	private List<String> groupsToStringCode(List<Group> groups) {
		List<String> groupCodes = new ArrayList<String>();
		for (int i = 0; i < groups.size(); i++) {
			Group group = groups.get(i);
			groupCodes.add(group.getName());
		}
		return groupCodes;
	}

	public IActionLogManager getActionLogManager() {
		return _actionLogManager;
	}

	public void setActionLogManager(IActionLogManager actionLogManager) {
		this._actionLogManager = actionLogManager;
	}

	public Date getTimestamp() {
		return _timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this._timestamp = timestamp;
	}
	
	private IActionLogManager _actionLogManager;
	private Date _timestamp;

}
