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
