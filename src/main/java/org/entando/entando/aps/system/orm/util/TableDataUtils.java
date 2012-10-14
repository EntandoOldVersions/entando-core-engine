/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.util;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;

import java.sql.*;

import javax.sql.DataSource;

import org.entando.entando.aps.system.orm.model.DataReport;
import org.entando.entando.aps.system.orm.model.InstallationReport;

/**
 * @author E.Santoboni
 */
public class TableDataUtils {
	
	public static void valueDatabase(String script, String databaseName, 
			DataSource dataSource, DataReport schemaReport) throws ApsSystemException {
		/*
		if (null == resource) {
			ApsSystemUtils.getLogger().info("No resource script for db " + databaseName);
			if (null != report) {
				schemaReport.getDatabaseStatus().put(databaseName, InstallationReport.Status.NOT_AVAILABLE);
				//this.updateReport(report);
			}
			return;
		}
		*/
		try {
            if (null == script || script.trim().length() == 0) {
				ApsSystemUtils.getLogger().info("No sql script for db " + databaseName);
				if (null != schemaReport) {
					schemaReport.getDatabaseStatus().put(databaseName, InstallationReport.Status.NOT_AVAILABLE);
					//this.updateReport(report);
				}
				return;
			}
            String[] queries = QueryExtractor.extractQueries(script);
			if (null == queries || queries.length == 0) {
				ApsSystemUtils.getLogger().info("Script file for db " + databaseName + " void");
				if (null != schemaReport) {
					schemaReport.getDatabaseStatus().put(databaseName, InstallationReport.Status.NOT_AVAILABLE);
					//this.updateReport(report);
				}
				return;
			}
			executeQueries(dataSource, queries, true);
			if (null != schemaReport) {
				schemaReport.getDatabaseStatus().put(databaseName, InstallationReport.Status.OK);
				//this.updateReport(report);
			}
		} catch (Throwable t) {
			if (null != schemaReport) {
				schemaReport.getDatabaseStatus().put(databaseName, InstallationReport.Status.INCOMPLETE);
				//this.updateReport(report);
			}
			ApsSystemUtils.logThrowable(t, TableDataUtils.class, "valueDatabase", "Error executing script into db " + databaseName);
			throw new ApsSystemException("Error executing script into db " + databaseName, t);
		}
	}
	
	public static void executeQueries(DataSource dataSource, String[] queries, boolean traceException) throws ApsSystemException {
		if (queries.length == 0) return;
		Connection conn = null;
        PreparedStatement stat = null;
		try {
			conn = dataSource.getConnection();
            conn.setAutoCommit(false);
			for (int i = 0; i < queries.length; i++) {
				stat = conn.prepareStatement(queries[i]);
				stat.execute();
			}
			conn.commit();
		} catch (Throwable t) {
			if (traceException) {
				ApsSystemUtils.logThrowable(t, TableDataUtils.class, 
						"executeQueries", "Error executing script into db " + dataSource);
			}
			throw new ApsSystemException("Error executing script into db " + dataSource, t);
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
	
	public static String dumpTable(DataSource dataSource, String tableName) throws ApsSystemException {
		StringBuilder result = new StringBuilder();
		StringBuilder scriptPrefix = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
		Connection conn = null;
        PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = dataSource.getConnection();
			stat = conn.prepareStatement("SELECT * FROM " + tableName);
			res = stat.executeQuery();
			ResultSetMetaData metaData = res.getMetaData();
            int columnCount = metaData.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				if (i>0) scriptPrefix.append(", ");
				scriptPrefix.append(metaData.getColumnName(i+1));
			}
			scriptPrefix.append(") VALUES (");
			while (res.next()) {
                result.append(scriptPrefix);
                for (int i=0; i<columnCount; i++) {
                    if (i > 0) {
                        result.append(", ");
                    }
                    Object value = res.getObject(i+1);
                    if (value == null) {
                        result.append("NULL");
                    } else {
                        String outputValue = value.toString();
                        outputValue = outputValue.replaceAll("'","\\''");
                        result.append("'").append(outputValue).append("'");
                    }
                }
                result.append(");\n");
            }
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
		return result.toString();
	}
	
}
