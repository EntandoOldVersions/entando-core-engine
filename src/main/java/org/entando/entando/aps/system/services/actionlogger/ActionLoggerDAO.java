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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.agiletec.aps.system.common.AbstractDAO;

import org.entando.entando.aps.system.services.actionlogger.model.ActionRecord;
import org.entando.entando.aps.system.services.actionlogger.model.IActionRecordSearchBean;

public class ActionLoggerDAO extends AbstractDAO implements IActionLoggerDAO {
	
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
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		List<Integer> actionRecords = new ArrayList<Integer>();
		try {
			conn = this.getConnection();
			stat = this.buildStatement(searchBean, conn);
			res = stat.executeQuery();
			while (res.next()) {
				int id = res.getInt(1);
				actionRecords.add(new Integer(id));
			}
		} catch (Throwable t) {
			processDaoException(t, "Error loading actionlogger records", "getActionRecords");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return actionRecords;
	}
	
	protected PreparedStatement buildStatement(IActionRecordSearchBean searchBean, Connection conn) {
		String query = this.createQueryString(searchBean).toString();
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(query);
			int index = 0;
			index = this.addSearchFilters(searchBean, index, stat);
		} catch (Throwable t) {
			processDaoException(t, "Error creating search statement", "buildStatement");
		}
		return stat;
	}
	
	protected StringBuffer createQueryString(IActionRecordSearchBean searchBean) {
		StringBuffer query = new StringBuffer(SELECT_ACTION_RECORDS);
		if (searchBean!=null) {
			boolean appendWhere = true;
			String username = searchBean.getUsername();
			if (username != null && username.length()>0) {
				query.append(APPEND_WHERE);
				query.append(APPEND_USERNAME_CLAUSE);
				appendWhere = false;
			}
			String namespace = searchBean.getNamespace();
			if (namespace!=null && namespace.length()>0) {
				query.append(appendWhere ? APPEND_WHERE : APPEND_AND);
				query.append(APPEND_NAMESPACE_CLAUSE);
				appendWhere = false;
			}
			String actionName = searchBean.getActionName();
			if (actionName!=null && actionName.length()>0) {
				query.append(appendWhere ? APPEND_WHERE : APPEND_AND);
				query.append(APPEND_ACTIONNAME_CLAUSE);
				appendWhere = false;
			}
			String params = searchBean.getParams();
			if (params!=null && params.length()>0) {
				query.append(appendWhere ? APPEND_WHERE : APPEND_AND);
				query.append(APPEND_PARAMS_CLAUSE);
				appendWhere = false;
			}
			Date start = searchBean.getStart();
			Date end = searchBean.getEnd();
			if (start != null || end != null) {
				query.append(appendWhere ? APPEND_WHERE : APPEND_AND);
				if (end == null) {
					query.append(APPEND_DATE_START_CLAUSE);
				} else if (start == null) {
					query.append(APPEND_DATE_END_CLAUSE);
				} else {
					query.append(APPEND_DATE_BETWEEN_CLAUSE);
				}
				appendWhere = false;
			}
		}
		query.append(APPEND_ORDERBY_CLAUSE);
		return query;
	}
	
	protected int addSearchFilters(IActionRecordSearchBean searchBean, int index, PreparedStatement stat) throws SQLException {
		if (searchBean != null) {
			String username = searchBean.getUsername();
			if (username != null && username.length()>0) {
				stat.setString(++index, this.searchLikeString(username));
			}
			String namespace = searchBean.getNamespace();
			if (namespace!=null && namespace.length()>0) {
				stat.setString(++index, this.searchLikeString(namespace));
			}
			String actionName = searchBean.getActionName();
			if (actionName!=null && actionName.length()>0) {
				stat.setString(++index, this.searchLikeString(actionName));
			}
			String params = searchBean.getParams();
			if (params!=null && params.length()>0) {
				stat.setString(++index, this.searchLikeString(params));
			}
			Date start = searchBean.getStart();
			if (start != null) {
				stat.setTimestamp(++index, new Timestamp(start.getTime()));
			}
			Date end = searchBean.getEnd();
			if (end != null) {
				stat.setTimestamp(++index, new Timestamp(end.getTime()));
			}
		}
		return index;
	}
	
	protected String searchLikeString(String searchValue) {
		String result = "";
		searchValue.trim();
		String[] titleSplit = searchValue.split(" ");
		for ( int i = 0;  i < titleSplit.length; i++) {
			if ( titleSplit[i].length() > 0 ) {
				result += "%" + titleSplit[i];
			}
		}
		return result + "%" ;
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
	
	private final String SELECT_ACTION_RECORDS = 
		"SELECT id FROM actionloggerrecords ";
	
	private final String APPEND_WHERE = "WHERE ";
	private final String APPEND_AND = "AND ";
	private final String APPEND_USERNAME_CLAUSE  = "username LIKE ? ";
	private final String APPEND_NAMESPACE_CLAUSE  = "namespace LIKE ? ";
	private final String APPEND_ACTIONNAME_CLAUSE  = "actionname LIKE ? ";
	private final String APPEND_PARAMS_CLAUSE  = "parameters LIKE ? ";
	private final String APPEND_DATE_START_CLAUSE  = "actiondate >= ? ";
	private final String APPEND_DATE_END_CLAUSE  = "actiondate <= ? ";
	private final String APPEND_DATE_BETWEEN_CLAUSE  = "( actiondate BETWEEN ? AND ? ) ";
	
	private final String APPEND_ORDERBY_CLAUSE = "ORDER BY actiondate DESC ";
	
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