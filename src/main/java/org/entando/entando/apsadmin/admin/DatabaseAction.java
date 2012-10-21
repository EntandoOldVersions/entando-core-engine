/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.apsadmin.admin;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.apsadmin.system.BaseAction;
import org.entando.entando.aps.system.orm.IDbInstallerManager;
import org.entando.entando.aps.system.orm.model.report.DatabaseDump;

/**
 * @author E.Santoboni
 */
public class DatabaseAction extends BaseAction {
	
	public String executeBackup() {
		try {
			this.getDbInstallerManager().createBackup();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "executeBackup");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public DatabaseDump getLastDumpReport() {
		try {
			return this.getDbInstallerManager().getLastDumpReport();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "executeBackup");
			throw new RuntimeException("Error extracting last report", t);
		}
	}
	
	public int getManagerStatus() {
		return this.getDbInstallerManager().getStatus();
	}
	
	protected IDbInstallerManager getDbInstallerManager() {
		return _dbInstallerManager;
	}
	public void setDbInstallerManager(IDbInstallerManager dbInstallerManager) {
		this._dbInstallerManager = dbInstallerManager;
	}
	
	private IDbInstallerManager _dbInstallerManager;
	
}
