/*
*
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* Entando is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package org.entando.entando.aps.system.orm.util;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;

import java.sql.*;

import javax.sql.DataSource;

import org.entando.entando.aps.system.orm.model.DataInstallationReport;
import org.entando.entando.aps.system.orm.model.SystemInstallationReport;
import org.entando.entando.aps.system.orm.model.TableDumpResult;

/**
 * @author E.Santoboni
 */
public class TableDataUtils {
	
	public static void valueDatabase(String script, String databaseName, 
			DataSource dataSource, DataInstallationReport schemaReport) throws ApsSystemException {
		try {
            String[] queries = (null != script) ? QueryExtractor.extractQueries(script) : null;
			if (null == queries || queries.length == 0) {
				ApsSystemUtils.getLogger().info("Script file for db " + databaseName + " void");
				if (null != schemaReport) {
					schemaReport.getDatabaseStatus().put(databaseName, SystemInstallationReport.Status.NOT_AVAILABLE);
				}
				return;
			}
			executeQueries(dataSource, queries, true);
			if (null != schemaReport) {
				schemaReport.getDatabaseStatus().put(databaseName, SystemInstallationReport.Status.OK);
			}
		} catch (Throwable t) {
			if (null != schemaReport) {
				schemaReport.getDatabaseStatus().put(databaseName, SystemInstallationReport.Status.INCOMPLETE);
			}
			ApsSystemUtils.logThrowable(t, TableDataUtils.class, "valueDatabase", "Error executing script into db " + databaseName);
			throw new ApsSystemException("Error executing script into db " + databaseName, t);
		}
	}
	
	public static void executeQueries(DataSource dataSource, String[] queries, boolean traceException) throws ApsSystemException {
		if (null == queries || queries.length == 0) return;
		Connection conn = null;
        PreparedStatement stat = null;
		String currentQuery = null;
		try {
			conn = dataSource.getConnection();
            conn.setAutoCommit(false);
			for (int i = 0; i < queries.length; i++) {
				currentQuery = queries[i];
				stat = conn.prepareStatement(currentQuery);
				stat.execute();
			}
			conn.commit();
		} catch (Throwable t) {
			try {
				if (conn != null) conn.rollback();
			} catch (Throwable tr) {
				ApsSystemUtils.logThrowable(tr, TableDataUtils.class, 
						"dropData", "Error executing rollback");
			}
			String errorMessage = "Error executing script - QUERY:\n" + currentQuery;
			if (traceException) {
				ApsSystemUtils.logThrowable(t, TableDataUtils.class, "executeQueries", errorMessage);
			}
			throw new ApsSystemException(errorMessage, t);
		} finally {
			try {
				if (stat != null) stat.close();
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, TableDataUtils.class, 
						"closeDaoResources", "Error while closing the statement");
			}
			try {
				if (conn != null) conn.close();
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, TableDataUtils.class, 
						"closeDaoStatement", "Error closing the connection");
			}
		}
	}
	
	public static TableDumpResult dumpTable(DataSource dataSource, String tableName) throws ApsSystemException {
		TableDumpResult report = new TableDumpResult(tableName);
		StringBuilder sqlDump = new StringBuilder();
		StringBuilder scriptPrefix = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
		Connection conn = null;
        PreparedStatement stat = null;
		ResultSet res = null;
		long start = System.currentTimeMillis();
		try {
			conn = dataSource.getConnection();
			stat = conn.prepareStatement("SELECT * FROM " + tableName);
			res = stat.executeQuery();
			ResultSetMetaData metaData = res.getMetaData();
            int columnCount = metaData.getColumnCount();
			int[] types = new int[columnCount];
			for (int i = 0; i < columnCount; i++) {
				if (i>0) scriptPrefix.append(", ");
				int indexColumn = i+1;
				types[i] = metaData.getColumnType(indexColumn);
				scriptPrefix.append(metaData.getColumnName(indexColumn).toLowerCase());
			}
			scriptPrefix.append(") VALUES (");
			int rows = 0;
			while (res.next()) {
                sqlDump.append(scriptPrefix);
                for (int i=0; i<columnCount; i++) {
                    if (i > 0) {
                        sqlDump.append(", ");
                    }
                    Object value = res.getObject(i+1);
                    if (value == null) {
                        sqlDump.append("NULL");
                    } else {
                        String outputValue = value.toString();
						outputValue = outputValue.replaceAll("'","\\''");
						if (isDataNeedsQuotes(types[i])) {
							sqlDump.append("'").append(outputValue).append("'");
						} else {
							sqlDump.append(outputValue);
						}
                    }
                }
                sqlDump.append(");\n");
				rows++;
            }
			report.setSqlDump(sqlDump.toString());
			report.setRows(rows);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, TableDataUtils.class, 
					"dumpTable", "Error creating backup");
			throw new ApsSystemException("Error creating backup", t);
		} finally {
			try {
				if (res != null) res.close();
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, TableDataUtils.class, 
						"dumpTable", "Error while closing the resultset");
			}
			try {
				if (stat != null) stat.close();
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, TableDataUtils.class, 
						"dumpTable", "Error while closing the statement");
			}
			try {
				if (conn != null) conn.close();
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, TableDataUtils.class, 
						"dumpTable", "Error closing the connection");
			}
		}
		long time = System.currentTimeMillis() - start;
		report.setRequiredTime(time);
		return report;
	}
	
	private static boolean isDataNeedsQuotes(int type) {
		switch (type) {
            case Types.BIGINT: return false;
            case Types.BOOLEAN: return false;
            case Types.DECIMAL: return false;
            case Types.DOUBLE: return false;
            case Types.FLOAT: return false;
            case Types.INTEGER: return false;
            case Types.NUMERIC: return false;
            case Types.REAL: return false;
            case Types.SMALLINT: return false;
            case Types.TINYINT: return false;
            default: return true;
        }
	}
	
}
