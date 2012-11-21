/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.apsadmin.admin;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.apsadmin.system.BaseAction;
import java.io.InputStream;
import java.util.List;

import org.entando.entando.aps.system.init.IDatabaseManager;
import org.entando.entando.aps.system.init.model.DatabaseDumpReport;

/**
 * @author E.Santoboni
 */
public class DatabaseAction extends BaseAction {
	
	public String entryBackupDetails() {
		return SUCCESS;
	}
	
	public String executeBackup() {
		try {
			this.getDatabaseManager().createBackup();
			//TODO MESSAGE
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "executeBackup");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public List<DatabaseDumpReport> getDumpReports() {
		try {
			return this.getDatabaseManager().getBackupReports();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getDumpReports");
			throw new RuntimeException("Error extracting dump reports", t);
		}
	}
	
	public DatabaseDumpReport getDumpReport(String subFolderName) {
		try {
			return this.getDatabaseManager().getBackupReport(subFolderName);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getDumpReport");
			throw new RuntimeException("Error extracting report of subfolder " + subFolderName, t);
		}
	}
	
	public String restoreBackup() {
		try {
			//TODO VALIDATE
			this.getDatabaseManager().dropAndRestoreBackup(this.getSubFolderName());
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
			InputStream stream = this.getDatabaseManager().getTableDump(this.getTableName(), this.getDataSourceName(), this.getSubFolderName());
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
			this.getDatabaseManager().deleteBackup(this.getSubFolderName());
			//TODO MESSAGE
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "deleteBackup");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public int getManagerStatus() {
		return this.getDatabaseManager().getStatus();
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
	
	protected IDatabaseManager getDatabaseManager() {
		return _databaseManager;
	}
	public void setDatabaseManager(IDatabaseManager databaseManager) {
		this._databaseManager = databaseManager;
	}
	
	private String _subFolderName;
	
	private String _tableName;
	private String _dataSourceName;
	private InputStream _inputStream;
	private IDatabaseManager _databaseManager;
	
}
