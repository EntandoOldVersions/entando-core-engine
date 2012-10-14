/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm;

import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * @author E.Santoboni
 */
public interface IDbInstallerManager {
	
	public void createBackup() throws ApsSystemException;
	
	public enum DatabaseType {DERBY, POSTGRESQL, MYSQL, ORACLE, SQLSERVER, UNKNOWN}
	
}
