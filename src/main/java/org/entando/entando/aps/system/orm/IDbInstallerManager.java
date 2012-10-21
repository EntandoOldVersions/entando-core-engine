/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm;

import com.agiletec.aps.system.exception.ApsSystemException;

import org.entando.entando.aps.system.orm.model.report.DatabaseDump;

/**
 * @author E.Santoboni
 */
public interface IDbInstallerManager {
	
	public void createBackup() throws ApsSystemException;
	
	public int getStatus();
	
	public DatabaseDump getLastDumpReport() throws ApsSystemException;
	
	public enum DatabaseType {DERBY, POSTGRESQL, MYSQL, ORACLE, SQLSERVER, UNKNOWN}
	
}
