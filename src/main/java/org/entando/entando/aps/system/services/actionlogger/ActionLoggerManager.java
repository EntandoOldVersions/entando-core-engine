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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.keygenerator.IKeyGeneratorManager;

import org.entando.entando.aps.system.services.actionlogger.model.ActionLoggerRecord;
import org.entando.entando.aps.system.services.actionlogger.model.IActionLoggerRecordSearchBean;

/**
 * @author E.Santoboni - S.Puddu
 */
public class ActionLoggerManager extends AbstractService implements IActionLoggerManager {
	
	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().config(this.getClass().getName() + ": ready");
	}
	
	@Override
	public void addActionRecord(ActionLoggerRecord actionRecord) throws ApsSystemException {
		try {
			int key = this.getKeyGeneratorManager().getUniqueKeyCurrentValue();
			actionRecord.setId(key);
			actionRecord.setActionDate(new Date());
			this.getActionLoggerDAO().addActionRecord(actionRecord);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "addActionRecord");
			throw new ApsSystemException("Error adding an jpactionlogger record", t);
		}
	}
	
	@Override
	public void deleteActionRecord(int id) throws ApsSystemException {
		try {
			this.getActionLoggerDAO().deleteActionRecord(id);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "deleteActionRecord");
			throw new ApsSystemException("Error deleting the jpactionlogger record: " + id, t);
		}
	}
	
	@Override
	public List<Integer> getActionRecords(IActionLoggerRecordSearchBean searchBean) throws ApsSystemException {
		List<Integer> records = new ArrayList<Integer>();
		try {
			records = this.getActionLoggerDAO().getActionRecords(searchBean);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getActionRecords");
			throw new ApsSystemException("Error loading jpactionlogger records", t);
		}
		return records;
	}
	
	@Override
	public ActionLoggerRecord getActionRecord(int id) throws ApsSystemException {
		ActionLoggerRecord record = null;
		try {
			record = this.getActionLoggerDAO().getActionRecord(id);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getActionRecords");
			throw new ApsSystemException("Error loading jpactionlogger record with id: " + id, t);
		}
		return record;
	}
	
	public void setActionLoggerDAO(IActionLoggerDAO actionLoggerDAO) {
		this._actionLoggerDAO = actionLoggerDAO;
	}
	protected IActionLoggerDAO getActionLoggerDAO() {
		return _actionLoggerDAO;
	}
	
	public void setKeyGeneratorManager(IKeyGeneratorManager keyGeneratorManager) {
		this._keyGeneratorManager = keyGeneratorManager;
	}
	protected IKeyGeneratorManager getKeyGeneratorManager() {
		return _keyGeneratorManager;
	}
	
	private IActionLoggerDAO _actionLoggerDAO;
	private IKeyGeneratorManager _keyGeneratorManager;
	
}