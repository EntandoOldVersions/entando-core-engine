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
package org.entando.entando.aps.system.services.actionlog;

import com.agiletec.aps.system.common.FieldSearchFilter;
import java.util.List;

import org.entando.entando.aps.system.services.actionlog.model.ActionLogRecord;
import org.entando.entando.aps.system.services.actionlog.model.ActivityStreamLikeInfo;
import org.entando.entando.aps.system.services.actionlog.model.IActionLogRecordSearchBean;

/**
 * @author E.Santoboni - S.Puddu
 */
public interface IActionLogDAO {
	
	public List<Integer> getActionRecords(IActionLogRecordSearchBean searchBean);
	
	public List<Integer> getActionRecords(FieldSearchFilter[] filters);
	
	public void addActionRecord(ActionLogRecord actionRecord);
	
	public ActionLogRecord getActionRecord(int id);
	
	public void deleteActionRecord(int id);
	
	public List<Integer> getActivityStream(List<String> userGroupCodes);
	
	public void editActionLikeRecord(int id, String username, boolean add);
	
	public void addActionLikeRecord(int id, String username);
	
	public void deleteActionLikeRecord(int id, String username);
	
	public List<ActivityStreamLikeInfo> getActionLikeRecords(int id);
	
	public void cleanOldActivityStreamLogs(int maxActivitySizeByGroup);
	
}