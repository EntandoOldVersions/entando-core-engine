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
package org.entando.entando.aps.system.init;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;

import javax.sql.DataSource;

import org.entando.entando.aps.system.init.model.SystemInstallationReport;

/**
 * @author E.Santoboni
 */
public class InitializerManager extends AbstractInitializerManager {
	
	public void init() throws Exception {
		System.out.println(this.getClass() + " - INIT");
		if (!this.isCheckOnStartup()) {
			ApsSystemUtils.getLogger().config(this.getClass().getName() + ": short init executed");
			return;
		}
		SystemInstallationReport report = null;
		try {
			report = this.extractReport();
			((IDatabaseInstallerManager) this.getDatabaseManager()).installDatabase(report);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "init", "Error while initializating Db Installer");
			throw new Exception("Error while initializating Db Installer", t);
		} finally {
			if (null != report && report.isUpdated()) {
				this.saveReport(report);
			}
		}
		ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initializated");
	}
	
	protected void executePostInitProcesses() {
		System.out.println("*******************************");
		System.out.println("POST PROCESS");
		System.out.println("*******************************");
	}
	
	//-------------------- REPORT -------- START
	
	private void saveReport(SystemInstallationReport report) throws ApsSystemException {
		if (null == report || report.getReports().isEmpty()) {
			return;
		}
		try {
			InstallationReportDAO dao = new InstallationReportDAO();
			DataSource dataSource = (DataSource) this.getBeanFactory().getBean("portDataSource");
			dao.setDataSource(dataSource);
			dao.saveConfigItem(report.toXml(), this.getConfigVersion());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateReport");
			throw new ApsSystemException("Error updating report", t);
		}
	}
	
	protected boolean isCheckOnStartup() {
		return _checkOnStartup;
	}
	public void setCheckOnStartup(boolean checkOnStartup) {
		this._checkOnStartup = checkOnStartup;
	}
	
	protected IComponentManager getComponentManager() {
		return _componentManager;
	}
	public void setComponentManager(IComponentManager componentManager) {
		this._componentManager = componentManager;
	}
	
	protected IDatabaseManager getDatabaseManager() {
		return _databaseManager;
	}
	public void setDatabaseManager(IDatabaseManager databaseManager) {
		this._databaseManager = databaseManager;
	}
	
	private boolean _checkOnStartup;
	
	private IComponentManager _componentManager;
	private IDatabaseManager _databaseManager;
	
	public static final String REPORT_CONFIG_ITEM = "entandoComponentsReport";
	
}
