/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.apsadmin.admin;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.apsadmin.system.BaseAction;
import java.io.InputStream;
import java.util.List;

import org.entando.entando.aps.system.orm.IDbInstallerManager;
import org.entando.entando.aps.system.orm.model.DatabaseDumpReport;

/**
 * @author E.Santoboni
 */
public class DatabaseAction extends BaseAction {
	
	public String entryBackupDetails() {
		return SUCCESS;
	}
	
	public String executeBackup() {
		try {
			this.getDbInstallerManager().createBackup();
			//TODO MESSAGE
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "executeBackup");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public List<DatabaseDumpReport> getDumpReports() {
		try {
			return this.getDbInstallerManager().getBackupReports();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getDumpReports");
			throw new RuntimeException("Error extracting dump reports", t);
		}
	}
	
	public DatabaseDumpReport getDumpReport(String subFolderName) {
		try {
			return this.getDbInstallerManager().getBackupReport(subFolderName);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getDumpReport");
			throw new RuntimeException("Error extracting report of subfolder " + subFolderName, t);
		}
	}
	
	public String restoreBackup() {
		try {
			//TODO VALIDATE
			this.getDbInstallerManager().dropAndRestoreBackup(this.getSubFolderName());
			//RELOAD CONFIGURATION
			//TODO MESSAGE
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "restoreBackup");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public String extractTableDump() {
		try {
			//TODO VALIDATE
			InputStream stream = this.getDbInstallerManager().getTableDump(this.getTableName(), this.getDataSourceName(), this.getSubFolderName());
			this.setInputStream(stream);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "extractLastTableDump");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public String trashBackup() {
		try {
			//TODO VALIDATE
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "trashBackup");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public String deleteBackup() {
		try {
			//TODO VALIDATE
			this.getDbInstallerManager().deleteBackup(this.getSubFolderName());
			//TODO MESSAGE
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "deleteBackup");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public int getManagerStatus() {
		return this.getDbInstallerManager().getStatus();
	}
	
	public String getSubFolderName() {
		return _subFolderName;
	}
	public void setSubFolderName(String subFolderName) {
		this._subFolderName = subFolderName;
	}
	
	public String getDataSourceName() {
		return _dataSourceName;
	}
	public void setDataSourceName(String dataSourceName) {
		this._dataSourceName = dataSourceName;
	}
	
	public String getTableName() {
		return _tableName;
	}
	public void setTableName(String tableName) {
		this._tableName = tableName;
	}
	
	public InputStream getInputStream() {
		return _inputStream;
	}
	protected void setInputStream(InputStream inputStream) {
		this._inputStream = inputStream;
	}
	
	protected IDbInstallerManager getDbInstallerManager() {
		return _dbInstallerManager;
	}
	public void setDbInstallerManager(IDbInstallerManager dbInstallerManager) {
		this._dbInstallerManager = dbInstallerManager;
	}
	
	private String _subFolderName;
	
	private String _tableName;
	private String _dataSourceName;
	private InputStream _inputStream;
	private IDbInstallerManager _dbInstallerManager;
	
}
