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
package org.entando.entando.aps.system.services.actionlogger;

import java.util.List;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.user.UserDetails;
import org.entando.entando.aps.system.services.actionlogger.model.ActionLogRecord;
import org.entando.entando.aps.system.services.actionlogger.model.IActionLogRecordSearchBean;

/**
 * Interface for the service that manages the {@link ActionLoggerRecord}
 * @author E.Santoboni - S.Puddu
 */
public interface IActionLoggerManager {
	
	/**
	 * Load a list of {@link ActionLoggerRecord} codes that match the search criteria rapresented by the searchBean
	 * @param searchBean object containing the search criteria
	 * @return a list of codes
	 * @throws ApsSystemException if an error occurs
	 */
	public List<Integer> getActionRecords(IActionLogRecordSearchBean searchBean) throws ApsSystemException;
	
	/**
	 * Save a new {@link ActionLoggerRecord}
	 * @param actionRecord
	 * @throws ApsSystemException
	 */
	public void addActionRecord(ActionLogRecord actionRecord) throws ApsSystemException;
	
	/**
	 * Load a {@link ActionLoggerRecord}
	 * @param id the code of the record to load
	 * @return an {@link ActionLoggerRecord} 
	 * @throws ApsSystemException if an error occurs
	 */
	public ActionLogRecord getActionRecord(int id) throws ApsSystemException;
	
	/**
	 * Delete a {@link ActionLoggerRecord}
	 * @param id the code of the record to delete
	 * @throws ApsSystemException if an error occurs
	 */
	public void deleteActionRecord(int id) throws ApsSystemException;
	
	public List<Integer> getActivityStream(List<String> userGroupCodes) throws ApsSystemException;
	
	public List<Integer> getActivityStream(UserDetails loggedUser) throws ApsSystemException;
	
}