/*
*
* Copyright 2014 Entando S.r.l. (http://www.entando.com) All rights reserved.
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
* Copyright 2014 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/

package org.entando.entando.apsadmin.common;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.apsadmin.system.BaseAction;
import org.entando.entando.aps.system.services.actionlog.IActionLogManager;

/**
 * @author S.Loru
 */
public class ActivityStreamCommentAction extends BaseAction{
	
	public String addComment() {
		try {
			this.getActionLogManager().addActionCommentRecord(this.getCurrentUser().getUsername(), this.getCommentText(), this.getStreamRecordId());
		} catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "addComment", "Error on adding comment on activity");
        }
		return SUCCESS;
	}
	
	public int getStreamRecordId() {
		return _streamRecordId;
	}

	public void setStreamRecordId(int streamRecordId) {
		this._streamRecordId = streamRecordId;
	}
	
	public String getCommentText() {
		return _commentText;
	}

	public void setCommentText(String commentText) {
		this._commentText = commentText;
	}
	
	public IActionLogManager getActionLogManager() {
		return _actionLogManager;
	}

	public void setActionLogManager(IActionLogManager actionLogManager) {
		this._actionLogManager = actionLogManager;
	}
	
	private IActionLogManager _actionLogManager;
	private int _streamRecordId;
	private String _commentText;
	
}
