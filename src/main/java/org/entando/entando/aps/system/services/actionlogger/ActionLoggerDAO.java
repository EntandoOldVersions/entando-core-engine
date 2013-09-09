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
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.agiletec.aps.system.common.AbstractSearcherDAO;
import com.agiletec.aps.system.common.FieldSearchFilter;
import java.util.Arrays;

import org.entando.entando.aps.system.services.actionlogger.model.ActionRecord;
import org.entando.entando.aps.system.services.actionlogger.model.IActionRecordSearchBean;

/**
 * @author E.Santoboni
 */
public class ActionLoggerDAO extends AbstractSearcherDAO implements IActionLoggerDAO {
	
	@Override
	public void addActionRecord(ActionRecord actionRecord) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(ADD_ACTION_RECORD);
			stat.setInt(1, actionRecord.getId());
			stat.setString(2, actionRecord.getUsername());
			Timestamp timestamp = new Timestamp(actionRecord.getActionDate().getTime());
			stat.setTimestamp(3, timestamp);
			stat.setString(4, actionRecord.getNamespace());
			stat.setString(5, actionRecord.getActionName());
			stat.setString(6, actionRecord.getParams());
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error on insert actionlogger record", "addActionRecord");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	@Override
	public List<Integer> getActionRecords(IActionRecordSearchBean searchBean) {
		List<Integer> actionRecords = new ArrayList<Integer>();
		try {
			FieldSearchFilter[] filters = this.createFilters(searchBean);
			List<String> ids = super.searchId(filters);
			if (null != ids) {
				for (int i = 0; i < ids.size(); i++) {
					String id = ids.get(i);
					actionRecords.add(Integer.parseInt(id));
				}
			}
		} catch (Throwable t) {
			processDaoException(t, "Error loading actionlogger records", "getActionRecords");
		}
		return actionRecords;
	}
	
	protected FieldSearchFilter[] createFilters(IActionRecordSearchBean searchBean) {
		FieldSearchFilter[] filters = new FieldSearchFilter[0];
		if (null != searchBean) {
			String username = searchBean.getUsername();
			if (null != username && username.trim().length() > 0) {
				FieldSearchFilter filter = new FieldSearchFilter("username", this.extractSearchValues(username), true);
				filters = super.addFilter(filters, filter);
			}
			String namespace = searchBean.getNamespace();
			if (null != namespace && namespace.trim().length() > 0) {
				FieldSearchFilter filter = new FieldSearchFilter("namespace", this.extractSearchValues(namespace), true);
				filters = super.addFilter(filters, filter);
			}
			String actionName = searchBean.getActionName();
			if (null != actionName && actionName.trim().length() > 0) {
				FieldSearchFilter filter = new FieldSearchFilter("actionname", this.extractSearchValues(actionName), true);
				filters = super.addFilter(filters, filter);
			}
			String parameters = searchBean.getParams();
			if (null != parameters && parameters.trim().length() > 0) {
				FieldSearchFilter filter = new FieldSearchFilter("parameters", this.extractSearchValues(parameters), true);
				filters = super.addFilter(filters, filter);
			}
			Date start = searchBean.getStart();
			Date end = searchBean.getEnd();
			if (null != start || null != end) {
				Timestamp tsStart = (null != start) ? new Timestamp(start.getTime()) : null;
				Timestamp tsEnd = (null != end) ? new Timestamp(end.getTime()) : null;
				FieldSearchFilter filter = new FieldSearchFilter("actiondate", tsStart, tsEnd);
				filters = super.addFilter(filters, filter);
			}
		}
		return filters;
	}
	
	protected List<String> extractSearchValues(String text) {
		String[] titleSplit = text.trim().split(" ");
		return (List<String>) Arrays.asList(titleSplit);
	}
	
	@Override
	public ActionRecord getActionRecord(int id) {
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		ActionRecord actionRecord = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(GET_ACTION_RECORD);
			stat.setInt(1, id);
			res = stat.executeQuery();
			if (null != res) {
				if (res.next()) {
					actionRecord = new ActionRecord();
					actionRecord.setId(id);
					Timestamp timestamp = res.getTimestamp("actiondate");
					actionRecord.setActionDate(new Date(timestamp.getTime()));
					actionRecord.setActionName(res.getString("actionname"));
					actionRecord.setNamespace(res.getString("namespace"));
					actionRecord.setParams(res.getString("parameters"));
					actionRecord.setUsername(res.getString("username"));
				}
			}
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error loading actionlogger record with id: " + id, "getActionRecord");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return actionRecord;
	}
	
	@Override
	public void deleteActionRecord(int id) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(DELETE_RECORD);
			stat.setInt(1, id);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error on delete record: " + id , "deleteActionRecord");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	@Override
	protected String getMasterTableName() {
		return "actionloggerrecords";
	}

	@Override
	protected String getMasterTableIdFieldName() {
		return "id";
	}
	
	@Override
	protected String getTableFieldName(String metadataFieldKey) {
		return metadataFieldKey;
	}
	
	@Override
	protected boolean isForceCaseInsensitiveLikeSearch() {
		return true;
	}
	
	private static final String ADD_ACTION_RECORD = 
		"INSERT INTO actionloggerrecords ( id, username, actiondate, namespace, actionname, parameters ) " +
		"VALUES (?, ?, ?, ?, ?, ?)";
	
	private static final String GET_ACTION_RECORD = 
		"SELECT username, actiondate, namespace, actionname, parameters " +
		"FROM actionloggerrecords " +
		"WHERE id = ?";
	
	private static final String DELETE_RECORD = 
		"DELETE from actionloggerrecords where id = ?";
	
}