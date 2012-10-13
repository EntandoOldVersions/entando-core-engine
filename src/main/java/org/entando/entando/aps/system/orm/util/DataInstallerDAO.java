/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.util;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.entando.entando.aps.system.orm.model.DataReport;
import org.entando.entando.aps.system.orm.model.InstallationReport;
import org.entando.entando.aps.system.orm.util.QueryExtractor;

/**
 * @author E.Santoboni
 */
public class DataInstallerDAO {
	
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
			ApsSystemUtils.logThrowable(t, DataInstallerDAO.class, "valueDatabase", "Error executing script into db " + databaseName);
			throw new ApsSystemException("Error executing script into db " + databaseName, t);
		}
	}
	
	public static void executeQueries(DataSource dataSource, String[] queries, boolean traceException) throws ApsSystemException {
		if (queries.length == 0) return;
		Connection conn = null;
        PreparedStatement stat = null;
		ResultSet res = null;
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
				ApsSystemUtils.logThrowable(t, DataInstallerDAO.class, 
						"executeQueries", "Error executing script into db " + dataSource);
			}
			throw new ApsSystemException("Error executing script into db " + dataSource, t);
		} finally {
			try {
				if (res != null) res.close();
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, DataInstallerDAO.class, 
						"closeDaoResources", "Error while closing the resultset");
			}
			try {
				if (stat != null) stat.close();
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, DataInstallerDAO.class, 
						"closeDaoResources", "Error while closing the statement");
			}
			try {
				if (conn != null) conn.close();
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, DataInstallerDAO.class, 
						"closeDaoStatement", "Error closing the connection");
			}
		}
	}
	
}
