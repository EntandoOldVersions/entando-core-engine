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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;

import com.agiletec.aps.system.common.AbstractDAO;

import org.entando.entando.aps.system.services.actionlogger.model.ActionRecord;
import org.entando.entando.aps.system.services.actionlogger.model.ActionRecordSearchBean;

public class ActionLoggerTestHelper extends AbstractDAO {
	
	public ActionLoggerTestHelper(ApplicationContext applicationContext) {
		DataSource dataSource = (DataSource) applicationContext.getBean("servDataSource");
		this.setDataSource(dataSource);
		
		ActionLoggerDAO actionLoggerDAO = new ActionLoggerDAO();
		actionLoggerDAO.setDataSource(dataSource);
		this._actionLoggerDAO = actionLoggerDAO;
	}
	
	public void addActionRecord(ActionRecord actionRecord) {
		this._actionLoggerDAO.addActionRecord(actionRecord);
	}
	
	public void cleanRecords() {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(CLEAN_RECORDS);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			this.processDaoException(t, "Error cleaning actionRecords table", "cleanRecords");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	public ActionRecord createActionRecord(int id, String username, 
			String actionName, String namespace, Date date, String params) {
		ActionRecord record = new ActionRecord();
		record.setId(id);
		record.setUsername(username);
		record.setActionName(actionName);
		record.setNamespace(namespace);
		record.setActionDate(date);
		record.setParams(params);
		return record;
	}
	
	public ActionRecordSearchBean createSearchBean(String username, String actionName, 
			String namespace, String params, Date start, Date end) {
		ActionRecordSearchBean searchBean = new ActionRecordSearchBean();
		searchBean.setUsername(username);
		searchBean.setActionName(actionName);
		searchBean.setNamespace(namespace);
		searchBean.setParams(params);
		searchBean.setStart(start);
		searchBean.setEnd(end);
		return searchBean;
	}
	
	private static final String CLEAN_RECORDS = 
		"DELETE FROM actionloggerrecords";
	
	private IActionLoggerDAO _actionLoggerDAO;
	
}