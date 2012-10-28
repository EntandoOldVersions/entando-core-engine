/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm;

import com.agiletec.aps.system.exception.ApsSystemException;
import java.io.InputStream;
import java.util.List;

import org.entando.entando.aps.system.orm.model.DatabaseDumpReport;

/**
 * @author E.Santoboni
 */
public interface IDbInstallerManager {
	
	public void createBackup() throws ApsSystemException;
	
	public void deleteBackup(String subFolderName) throws ApsSystemException;
	
	public int getStatus();
	
	public InputStream getTableDump(String tableName, String dataSourceName, String subFolderName) throws ApsSystemException;
	
	public boolean dropAndRestoreBackup(String subFolderName) throws ApsSystemException;
	
	public DatabaseDumpReport getBackupReport(String subFolderName) throws ApsSystemException;
	
	public List<DatabaseDumpReport> getBackupReports() throws ApsSystemException;
	
	public enum DatabaseType {DERBY, POSTGRESQL, MYSQL, ORACLE, SQLSERVER, UNKNOWN}
	
	public static final String DUMP_REPORT_FILE_NAME = "dumpReport.xml";
	
	public static final String REPORT_CONFIG_ITEM = "entandoComponentsReport";
	
}
