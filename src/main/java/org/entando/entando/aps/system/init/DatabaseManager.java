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

import org.entando.entando.aps.system.init.model.ComponentEnvironment;
import org.entando.entando.aps.system.init.model.Component;
import org.entando.entando.aps.system.init.model.ComponentInstallationReport;
import org.entando.entando.aps.system.init.model.SystemInstallationReport;
import org.entando.entando.aps.system.init.model.DataSourceDumpReport;
import org.entando.entando.aps.system.init.model.DataSourceInstallationReport;
import org.entando.entando.aps.system.init.model.DataInstallationReport;
import org.entando.entando.aps.system.init.util.TableDataUtils;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.aps.util.DateConverter;
import com.agiletec.aps.util.FileTextReader;

import java.io.*;

import java.util.*;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import org.apache.commons.beanutils.BeanComparator;

import org.entando.entando.aps.system.init.util.TableFactory;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ServletContextAware;

/**
 * @author E.Santoboni
 */
public class DatabaseManager extends AbstractInitializerManager
		implements IDatabaseManager, IDatabaseInstallerManager, ServletContextAware {

	public void init() throws Exception {
		ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initializated");
	}

	@Override
	public SystemInstallationReport installDatabase(SystemInstallationReport report, boolean checkOnStatup) throws Exception {
		String lastLocalBackupFolder = null;
		if (null == report) {
			report = SystemInstallationReport.getInstance();
			if (checkOnStatup && !Environment.test.equals(this.getEnvironment())) {
				//non c'è db locale installato, cerca nei backup locali
				DataSourceDumpReport lastDumpReport = this.getLastDumpReport();
				if (null != lastDumpReport) {
					lastLocalBackupFolder = lastDumpReport.getSubFolderName();
					report.setStatus(SystemInstallationReport.Status.RESTORE);
				} else {
					//SE NON c'è cerca il default dump
					Map<String, Resource> defaultSqlDump = this.getDefaultSqlDump();
					if (null != defaultSqlDump && defaultSqlDump.size() > 0) {
						report.setStatus(SystemInstallationReport.Status.RESTORE);
					}
				}
			}
		}
		try {
			this.initMasterDatabases(report, checkOnStatup);
			List<Component> components = this.getComponentManager().getCurrentComponents();
			for (int i = 0; i < components.size(); i++) {
				Component entandoComponentConfiguration = components.get(i);
				this.initComponentDatabases(entandoComponentConfiguration, report, checkOnStatup);
			}
			this.initMasterDefaultResource(report, checkOnStatup);
			for (int i = 0; i < components.size(); i++) {
				Component entandoComponentConfiguration = components.get(i);
				this.initComponentDefaultResources(entandoComponentConfiguration, report, checkOnStatup);
			}
			if (checkOnStatup && report.getStatus().equals(SystemInstallationReport.Status.RESTORE)) {
				//ALTER SESSION SET NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH:MI:SS.FF'
				if (null != lastLocalBackupFolder) {
					this.restoreBackup(lastLocalBackupFolder);
				} else {
					this.restoreDefaultDump();
				}
			}
		} catch (Throwable t) {
			if (null != report && report.isUpdated()) {
				report.setUpdated();
				report.setStatus(SystemInstallationReport.Status.INCOMPLETE);
			}
			ApsSystemUtils.logThrowable(t, this, "installDatabase", "Error while initializating Db Installer");
			throw new Exception("Error while initializating Db Installer", t);
		}
		return report;
	}

	private void initMasterDatabases(SystemInstallationReport report, boolean checkOnStatup) throws ApsSystemException {
		String logPrefix = "|   ";
		System.out.println("+ [ Component: Core ] :: SCHEMA\n" + logPrefix);
		ComponentInstallationReport componentReport = report.getComponentReport("entandoCore", true);
		DataSourceInstallationReport dataSourceReport = componentReport.getDataSourceReport();
		if (/*componentReport.getStatus().equals(SystemInstallationReport.Status.PORTING)
				|| */componentReport.getStatus().equals(SystemInstallationReport.Status.OK)) {
			ApsSystemUtils.getLogger().info(logPrefix + "( ok )  Already installed\n" + logPrefix);
			System.out.println(logPrefix + "( ok )  Already installed\n" + logPrefix);
			return;
		}
		try {
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			Map<String, SystemInstallationReport.Status> databasesStatus = dataSourceReport.getDatabaseStatus();
			System.out.println(logPrefix + "Starting installation");
			for (int i = 0; i < dataSourceNames.length; i++) {
				String dataSourceName = dataSourceNames[i];
				if (report.getStatus().equals(SystemInstallationReport.Status.PORTING)) {
					System.out.println(logPrefix + " - Already present! db " + dataSourceName);
					SystemInstallationReport.Status status = (checkOnStatup)
							? report.getStatus()
							: SystemInstallationReport.Status.SKIPPED;
					databasesStatus.put(dataSourceName, status);
					report.setUpdated();
					continue;
				}
				SystemInstallationReport.Status status = databasesStatus.get(dataSourceName);
				if (status != null && (SystemInstallationReport.isSafeStatus(status))) {
					System.out.println(logPrefix + "\n" + logPrefix + "( ok )  " + dataSourceName + " already installed");
				} else if (status == null || !status.equals(SystemInstallationReport.Status.OK)) {
					DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
					//System.out.println(logPrefix + " - '" + dataSourceName + "' Installation Started... ");
					if (checkOnStatup) {
						databasesStatus.put(dataSourceName, SystemInstallationReport.Status.INCOMPLETE);
						System.out.println(logPrefix);
						this.initMasterDatabase(dataSourceName, dataSource, dataSourceReport);
						databasesStatus.put(dataSourceName, SystemInstallationReport.Status.OK);
					} else {
						databasesStatus.put(dataSourceName, SystemInstallationReport.Status.SKIPPED);
					}
					report.setUpdated();
				}
			}
			System.out.println(logPrefix + "\n" + logPrefix + "Installation complete\n" + logPrefix);
			ApsSystemUtils.getLogger().info(logPrefix + "\n" + logPrefix + "Installation complete\n" + logPrefix);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "initMasterDatabases");
			throw new ApsSystemException("Error initializating master databases", t);
		}
	}
	
	private void initMasterDatabase(String databaseName, DataSource dataSource, DataSourceInstallationReport schemaReport) throws ApsSystemException {
		try {
			DatabaseType type = this.getDatabaseRestorer().getType(dataSource);
			if (type.equals(DatabaseType.DERBY)) {
				this.getDatabaseRestorer().initDerbySchema(dataSource);
			}
			List<String> tableClassNames = this.getEntandoTableMapping().get(databaseName);
			if (null == tableClassNames || tableClassNames.isEmpty()) {
				ApsSystemUtils.getLogger().info("No Master Tables defined for db - " + databaseName);
				schemaReport.getDatabaseStatus().put(databaseName, SystemInstallationReport.Status.NOT_AVAILABLE);
			} else {
				this.createTables(databaseName, tableClassNames, dataSource, schemaReport);
			}
		} catch (Throwable t) {
			schemaReport.getDatabaseStatus().put(databaseName, SystemInstallationReport.Status.INCOMPLETE);
			ApsSystemUtils.logThrowable(t, this, "initMasterDatabase", "Error inizializating db " + databaseName);
			throw new ApsSystemException("Error creating master tables to db " + databaseName, t);
		}
	}

	private void initComponentDatabases(Component componentConfiguration, SystemInstallationReport report, boolean checkOnStatup) throws ApsSystemException {
		String logPrefix = "|   ";
		System.out.println("+ [ Component: " + componentConfiguration.getCode() + " ] :: SCHEMA\n" + logPrefix);
		ComponentInstallationReport componentReport = report.getComponentReport(componentConfiguration.getCode(), true);
		if (/*componentReport.getStatus().equals(SystemInstallationReport.Status.PORTING)
				 || */componentReport.getStatus().equals(SystemInstallationReport.Status.OK)) {
			ApsSystemUtils.getLogger().info(logPrefix + "( ok )  Already installed\n" + logPrefix);
			System.out.println(logPrefix + "( ok )  Already installed\n" + logPrefix);
			return;
		}
		try {
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			Map<String, List<String>> tableMapping = componentConfiguration.getTableMapping();
			DataSourceInstallationReport dataSourceReport = componentReport.getDataSourceReport();
			//String logTablePrefix = logPrefix + " TABLES - logTablePrefix";
			//System.out.println(logTablePrefix + " - Installation STARTED!!!");
			System.out.println(logPrefix + "Starting installation\n" + logPrefix);
			for (int j = 0; j < dataSourceNames.length; j++) {
				String dataSourceName = dataSourceNames[j];
				List<String> tableClassNames = (null != tableMapping) ? tableMapping.get(dataSourceName) : null;
				if (null == tableClassNames || tableClassNames.isEmpty()) {
					System.out.println(logPrefix + "( !! )  skipping " + dataSourceName + ": not available");
					dataSourceReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.NOT_AVAILABLE);
					report.setUpdated();
					continue;
				}
				if (report.getStatus().equals(SystemInstallationReport.Status.PORTING)) {
					SystemInstallationReport.Status status = (checkOnStatup)
							? report.getStatus()
							: SystemInstallationReport.Status.SKIPPED;
					dataSourceReport.getDatabaseStatus().put(dataSourceName, status);
					ApsSystemUtils.getLogger().info(logPrefix + "( ok )  " + dataSourceName + " already installed" + SystemInstallationReport.Status.PORTING);
					System.out.println(logPrefix + "( ok )  " + dataSourceName + " already installed" + SystemInstallationReport.Status.PORTING);
					continue;
				}
				SystemInstallationReport.Status schemaStatus = dataSourceReport.getDatabaseStatus().get(dataSourceName);
				//System.out.println(logDbTablePrefix + " - INIT!!!");
				if (SystemInstallationReport.isSafeStatus(schemaStatus)) {
					//Already Done!
					System.out.println(logPrefix + "( ok )  " + dataSourceName + " already installed" + SystemInstallationReport.Status.PORTING);
					continue;
				}
				if (null == dataSourceReport.getDataSourceTables().get(dataSourceName)) {
					dataSourceReport.getDataSourceTables().put(dataSourceName, new ArrayList<String>());
				}
				//System.out.println(logDbTablePrefix + " - Installation Started... ");
				if (checkOnStatup) {
					dataSourceReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.INCOMPLETE);
					DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
					this.createTables(dataSourceName, tableClassNames, dataSource, dataSourceReport);
					//System.out.println(logDbTablePrefix + " - INSTALLATION DONE! - installated tables " + schemaReport.getDatabaseTables().get(dataSourceName));
					System.out.println(logPrefix);
					dataSourceReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.OK);
				} else {
					dataSourceReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.SKIPPED);
				}
				report.setUpdated();
			}
			System.out.println(logPrefix + "\n" + logPrefix + "Installation complete\n" + logPrefix);
			ApsSystemUtils.getLogger().info(logPrefix + "\n" + logPrefix + "Installation complete\n" + logPrefix);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "initComponent",
					"Error initializating component " + componentConfiguration.getCode());
			throw new ApsSystemException("Error initializating component " + componentConfiguration.getCode(), t);
		}
	}

	private void createTables(String databaseName, List<String> tableClassNames,
			DataSource dataSource, DataSourceInstallationReport schemaReport) throws ApsSystemException {
		try {
			DatabaseType type = this.getDatabaseRestorer().getType(dataSource);
			TableFactory tableFactory = new TableFactory(databaseName, dataSource, type);
			tableFactory.createTables(tableClassNames, schemaReport);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "createTables", "Error creating tables into db " + databaseName);
			throw new ApsSystemException("Error creating tables to db " + databaseName, t);
		}
	}
	
	/*
	private String invokeGetMethod(String methodName, DataSource dataSource) throws Throwable {
		Method method = dataSource.getClass().getDeclaredMethod(methodName);
		return (String) method.invoke(dataSource);
	}
	*/
	//---------------- DATA ------------------- START

	private void initMasterDefaultResource(SystemInstallationReport report, boolean checkOnStatup) throws ApsSystemException {
		String logPrefix = "|   ";
		System.out.println("+ [ Component: Core ] :: DATA\n" + logPrefix);
		ComponentInstallationReport coreComponentReport = report.getComponentReport("entandoCore", false);
		if (coreComponentReport.getStatus().equals(SystemInstallationReport.Status.OK)
				/*|| coreComponentReport.getStatus().equals(SystemInstallationReport.Status.RESTORE)*/) {
			String message = logPrefix + "( ok )  Already installed. " + coreComponentReport.getStatus() + "\n" + logPrefix;
			ApsSystemUtils.getLogger().info(message);
			System.out.println(message);
			return;
		}
		DataInstallationReport dataReport = coreComponentReport.getDataReport();
		try {
			System.out.println(logPrefix + "Starting installation\n" + logPrefix);
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			for (int i = 0; i < dataSourceNames.length; i++) {
				String dataSourceName = dataSourceNames[i];
				if ((report.getStatus().equals(SystemInstallationReport.Status.PORTING)
						|| report.getStatus().equals(SystemInstallationReport.Status.RESTORE)) && checkOnStatup) {
					dataReport.getDatabaseStatus().put(dataSourceName, report.getStatus());
					report.setUpdated();
					String message = logPrefix + "( ok )  " + dataSourceName + " already installed. " + report.getStatus() + "\n" + logPrefix;
					ApsSystemUtils.getLogger().info(message);
					System.out.println(message);
					continue;
				}
				SystemInstallationReport.Status schemaStatus = dataReport.getDatabaseStatus().get(dataSourceName);
				//System.out.println(logDbTablePrefix + " - INIT!!!");
				if (SystemInstallationReport.isSafeStatus(schemaStatus)) {
					String message = logPrefix + "( ok )  " + dataSourceName + " already installed. " + report.getStatus() + "\n" + logPrefix;
					System.out.println(message);
					continue;
				}
				Resource resource = (Environment.test.equals(this.getEnvironment()))
						? this.getTestSqlResources().get(dataSourceName)
						: this.getEntandoDefaultSqlResources().get(dataSourceName);
				String script = this.readFile(resource);
				if (null != script && script.trim().length() != 0) {
					if (checkOnStatup) {
						dataReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.INCOMPLETE);
						DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
						this.getDatabaseRestorer().initOracleSchema(dataSource);
						TableDataUtils.valueDatabase(script, dataSourceName, dataSource, null);
						dataReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.OK);
						System.out.println("|   ( ok )  " + dataSourceName);
					} else {
						dataReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.SKIPPED);
					}
					report.setUpdated();
				} else {
					System.out.println(logPrefix + "( !! )  skipping " + dataSourceName + ": not available");
					dataReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.NOT_AVAILABLE);
					report.setUpdated();
				}
			}
			System.out.println(logPrefix + "\n" + logPrefix + "Installation complete\n" + logPrefix);
			ApsSystemUtils.getLogger().info(logPrefix + "\n" + logPrefix + "Installation complete\n" + logPrefix);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "initMasterDefaultResource");
			throw new ApsSystemException("Error initializating master DefaultResource", t);
		}
	}

	private void initComponentDefaultResources(Component componentConfiguration,
			SystemInstallationReport report, boolean checkOnStatup) throws ApsSystemException {
		String logPrefix = "|   ";
		System.out.println("+ [ Component: " + componentConfiguration.getCode() + " ] :: DATA\n" + logPrefix);
		ComponentInstallationReport componentReport = report.getComponentReport(componentConfiguration.getCode(), false);
		if (componentReport.getStatus().equals(SystemInstallationReport.Status.OK)
				/*|| componentReport.getStatus().equals(SystemInstallationReport.Status.RESTORE)*/) {
			ApsSystemUtils.getLogger().info(logPrefix + "( ok )  Already installed\n" + logPrefix);
			System.out.println(logPrefix + "( ok )  Already installed\n" + logPrefix);
			return;
		}
		DataInstallationReport dataReport = componentReport.getDataReport();
		try {
			System.out.println(logPrefix + "Starting installation\n" + logPrefix);
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			for (int j = 0; j < dataSourceNames.length; j++) {
				String dataSourceName = dataSourceNames[j];
				//String logDbDataPrefix = "logDataPrefix" + " / Datasource " + dataSourceName;
				if ((report.getStatus().equals(SystemInstallationReport.Status.PORTING)
						|| report.getStatus().equals(SystemInstallationReport.Status.RESTORE))  && checkOnStatup) {
					dataReport.getDatabaseStatus().put(dataSourceName, report.getStatus());
					System.out.println("|   ( ok )  " + dataSourceName);
					report.setUpdated();
					continue;
				}
				DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
				SystemInstallationReport.Status dataStatus = dataReport.getDatabaseStatus().get(dataSourceName);
				if (SystemInstallationReport.isSafeStatus(dataStatus)) {
			ApsSystemUtils.getLogger().info(logPrefix + "( ok )  Already installed\n" + logPrefix);
			System.out.println(logPrefix + "( ok )  Already installed\n" + logPrefix);
					continue;
				}
				Map<String, ComponentEnvironment> environments = componentConfiguration.getEnvironments();
				String compEnvKey = (Environment.test.equals(this.getEnvironment()))
						? Environment.test.toString() : Environment.production.toString();
				ComponentEnvironment componentEnvironment = (null != environments) ? environments.get(compEnvKey) : null;
				Resource resource = (null != componentEnvironment) ? componentEnvironment.getSqlResources(dataSourceName) : null;
				String script = (null != resource) ? this.readFile(resource) : null;
				if (null != script && script.trim().length() > 0) {
					if (checkOnStatup) {
						dataReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.INCOMPLETE);
						this.getDatabaseRestorer().initOracleSchema(dataSource);
						TableDataUtils.valueDatabase(script, dataSourceName, dataSource, dataReport);
						System.out.println("|   ( ok )  " + dataSourceName);
						dataReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.OK);
					} else {
						dataReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.SKIPPED);
					}
					report.setUpdated();
				} else {
					System.out.println(logPrefix + "( !! )  skipping " + dataSourceName + ": not available");
					dataReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.NOT_AVAILABLE);
					report.setUpdated();
				}
			}
			System.out.println(logPrefix + "\n" + logPrefix + "Installation complete\n" + logPrefix);
			ApsSystemUtils.getLogger().info(logPrefix + "\n" + logPrefix + "Installation complete\n" + logPrefix);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "initComponent",
					"Error restoring default resources of component " + componentConfiguration.getCode());
			throw new ApsSystemException("Error restoring default resources of component " + componentConfiguration.getCode(), t);
		}
	}
	
	private void restoreDefaultDump() throws ApsSystemException {
		try {
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			Map<String, Resource> defaultDump = this.getDefaultSqlDump();
			if (null == defaultDump || defaultDump.isEmpty()) {
				return;
			}
			for (int j = 0; j < dataSourceNames.length; j++) {
				String dataSourceName = dataSourceNames[j];
				DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
				Resource resource = (null != defaultDump) ? defaultDump.get(dataSourceName) : null;
				String script = this.readFile(resource);
				if (null != script && script.trim().length() > 0) {
					this.getDatabaseRestorer().initOracleSchema(dataSource);
					TableDataUtils.valueDatabase(script, dataSourceName, dataSource, null);
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "restoreDefaultDump",
					"Error restoring default Dump");
			throw new ApsSystemException("Error restoring default Dump", t);
		}
	}

	private String readFile(Resource resource) throws Throwable {
		if (resource == null) {
			return null;
		}
		InputStream is = null;
		String text = null;
		try {
			is = resource.getInputStream();
			if (null == is) {
				return null;
			}
			text = FileTextReader.getText(is);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "readFile", "Error reading resource");
			throw new ApsSystemException("Error reading resource", t);
		} finally {
			if (null != is) {
				is.close();
			}
		}
		return text;
	}

	//---------------- DATA ------------------- END
	@Override
	public void createBackup() throws ApsSystemException {
		if (this.getStatus() != STATUS_READY) {
			return;
		}
		try {
			DatabaseDumperThread thread = new DatabaseDumperThread(this);
			String threadName = "DatabaseDumper_" + DateConverter.getFormattedDate(new Date(), "yyyyMMddHHmmss");
			thread.setName(threadName);
			thread.start();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "createBackup");
			throw new ApsSystemException("Error while creating backup", t);
		}
	}
	
	protected void executeBackup() throws ApsSystemException {
		try {
			this.setStatus(DatabaseManager.STATUS_DUMPIMG_IN_PROGRESS);
			this.getDatabaseDumper().createBackup(this.getEnvironment(), this.extractReport());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "executeBackup");
			throw new ApsSystemException("Error while creating backup", t);
		} finally {
			this.setStatus(DatabaseManager.STATUS_READY);
		}
	}

	@Override
	public void deleteBackup(String subFolderName) throws ApsSystemException {
		try {
			String directoryName = this.getLocalBackupsFolder() + subFolderName;
			File backupFolder = new File(directoryName);
			this.deleteDirectory(backupFolder);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "deleteBackup");
			throw new ApsSystemException("Error while deleting backup", t);
		}
	}

	private void deleteDirectory(File directory) {
		if (directory.exists() && directory.isDirectory()) {
			String[] filesName = directory.list();
			for (int i = 0; i < filesName.length; i++) {
				File fileToDelete = new File(directory + File.separator + filesName[i]);
				if (fileToDelete.isDirectory()) {
					this.deleteDirectory(fileToDelete);
				}
				fileToDelete.delete();
			}
			directory.delete();
		}
	}

	protected DataSourceDumpReport getLastDumpReport() throws ApsSystemException {
		if (Environment.develop.equals(this.getEnvironment())) {
			return this.getBackupReport(this.getEnvironment().toString());
		}
		List<DataSourceDumpReport> reports = this.getBackupReports();
		if (null == reports || reports.isEmpty()) {
			return null;
		}
		return reports.get(reports.size() - 1);
	}

	@Override
	public DataSourceDumpReport getBackupReport(String subFolderName) throws ApsSystemException {
		try {
			if (this.checkBackupFolder(subFolderName)) {
				return this.getDumpReport(subFolderName);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getBackurReport");
			throw new RuntimeException("Error while extracting Backup Report of subfolder " + subFolderName);
		}
		return null;
	}

	@Override
	public List<DataSourceDumpReport> getBackupReports() throws ApsSystemException {
		List<DataSourceDumpReport> reports = new ArrayList<DataSourceDumpReport>();
		try {
			File backupsFolder = new File(this.getLocalBackupsFolder());
			String[] children = backupsFolder.list();
			if (null == children || children.length == 0) {
				return null;
			}
			for (int i = 0; i < children.length; i++) {
				String subFolderName = children[i];
				if (this.checkBackupFolder(subFolderName)) {
					DataSourceDumpReport report = this.getDumpReport(subFolderName);
					reports.add(report);
				}
			}
			Collections.sort(reports, new BeanComparator("date"));
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getBackupReports");
			throw new RuntimeException("Error while extracting Backup Reports");
		}
		return reports;
	}

	private boolean checkBackupFolder(String subFolderName) {
		String dirName = this.getLocalBackupsFolder();
		String[] dataSourceNames = this.extractBeanNames(DataSource.class);
		for (int i = 0; i < dataSourceNames.length; i++) {
			String folderName = dirName + File.separator + subFolderName + File.separator + dataSourceNames[i] + File.separator;
			File directory = new File(folderName);
			if (!directory.exists() || !directory.isDirectory() || directory.list().length == 0) {
				return false;
			}
		}
		File reportFile = new File(this.getLocalBackupsFolder() + subFolderName + File.separator + DUMP_REPORT_FILE_NAME);
		if (!reportFile.exists()) {
			return false;
		}
		return true;
	}

	private DataSourceDumpReport getDumpReport(String subFolderName) throws ApsSystemException {
		InputStream is = null;
		DataSourceDumpReport report = null;
		try {
			File reportFile = new File(this.getLocalBackupsFolder() + subFolderName + File.separator + DUMP_REPORT_FILE_NAME);
			is = new FileInputStream(reportFile);
			String xml = FileTextReader.getText(is);
			report = new DataSourceDumpReport(xml);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getDumpReport");
			throw new RuntimeException("Error while extracting Dump Report of subfolder " + subFolderName);
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException ex) {}
			}
		}
		return report;
	}

	@Override
	public boolean dropAndRestoreBackup(String subFolderName) throws ApsSystemException {
		try {
			if (!this.checkBackupFolder(subFolderName)) {
				ApsSystemUtils.getLogger().severe("backup not available - subfolder '" + subFolderName + "'");
				return false;
			}
			//TODO future improvement - execute 'lifeline' backup
			this.getDatabaseRestorer().dropAndRestoreBackup(subFolderName);
			ApsWebApplicationUtils.executeSystemRefresh(this.getServletContext());
			return true;
		} catch (Throwable t) {
			//TODO future improvement - restore 'lifeline' backup
			ApsSystemUtils.logThrowable(t, this, "dropAndRestoreBackup");
			throw new ApsSystemException("Error while restoring backup - subfolder " + subFolderName, t);
		} finally {
			//TODO future improvement - delete 'lifeline' backup
		}
	}
	
	private boolean restoreBackup(String subFolderName) throws ApsSystemException {
		try {
			if (!this.checkBackupFolder(subFolderName)) {
				ApsSystemUtils.getLogger().severe("backup not available - subfolder '" + subFolderName + "'");
				return false;
			}
			this.getDatabaseRestorer().restoreBackup(subFolderName);
			return true;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "restoreLastBackup");
			throw new ApsSystemException("Error while restoring local backup", t);
		}
	}

	private String[] extractBeanNames(Class beanClass) {
		ListableBeanFactory factory = (ListableBeanFactory) this.getBeanFactory();
		return factory.getBeanNamesForType(beanClass);
	}

	@Override
	public InputStream getTableDump(String tableName, String dataSourceName, String subFolderName) throws ApsSystemException {
		try {
			if (null == subFolderName) {
				return null;
			}
			StringBuilder fileName = new StringBuilder(this.getLocalBackupsFolder())
					.append(subFolderName).append(File.separator)
					.append(dataSourceName).append(File.separator).append(tableName).append(".sql");
			File tableSqlDumpFile = new File(fileName.toString());
			if (tableSqlDumpFile.exists()) {
				return new FileInputStream(tableSqlDumpFile);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getTableDump");
			throw new RuntimeException("Error while extracting table dump - "
					+ "table '" + tableName + "' - datasource '" + dataSourceName + "' - SubFolder '" + subFolderName + "'", t);
		}
		return null;
	}
	
	protected String getLocalBackupsFolder() {
		return this.getDatabaseDumper().getLocalBackupsFolder();
	}
	
	@Override
	public Map<String, List<String>> getEntandoTableMapping() {
		return _entandoTableMapping;
	}
	public void setEntandoTableMapping(Map<String, List<String>> entandoTableMapping) {
		this._entandoTableMapping = entandoTableMapping;
	}

	protected Map<String, Resource> getEntandoDefaultSqlResources() {
		return _entandoDefaultSqlResources;
	}
	public void setEntandoDefaultSqlResources(Map<String, Resource> entandoDefaultSqlResources) {
		this._entandoDefaultSqlResources = entandoDefaultSqlResources;
	}

	protected Map<String, Resource> getTestSqlResources() {
		return _testSqlResources;
	}
	public void setTestSqlResources(Map<String, Resource> testSqlResources) {
		this._testSqlResources = testSqlResources;
	}

	protected Map<String, Resource> getDefaultSqlDump() {
		return _defaultSqlDump;
	}
	public void setDefaultSqlDump(Map<String, Resource> defaultSqlDump) {
		this._defaultSqlDump = defaultSqlDump;
	}

	@Override
	public int getStatus() {
		return _status;
	}
	protected void setStatus(int status) {
		this._status = status;
	}

	protected IComponentManager getComponentManager() {
		return _componentManager;
	}
	public void setComponentManager(IComponentManager componentManager) {
		this._componentManager = componentManager;
	}
	
	protected DatabaseDumper getDatabaseDumper() {
		return _databaseDumper;
	}
	public void setDatabaseDumper(DatabaseDumper databaseDumper) {
		this._databaseDumper = databaseDumper;
	}
	
	protected DatabaseRestorer getDatabaseRestorer() {
		return _databaseRestorer;
	}
	public void setDatabaseRestorer(DatabaseRestorer databaseRestorer) {
		this._databaseRestorer = databaseRestorer;
	}
	
	protected ServletContext getServletContext() {
		return _servletContext;
	}
	@Override
	public void setServletContext(ServletContext servletContext) {
		this._servletContext = servletContext;
	}
	
	private Map<String, List<String>> _entandoTableMapping;
	private Map<String, Resource> _entandoDefaultSqlResources;
	private Map<String, Resource> _testSqlResources;
	private Map<String, Resource> _defaultSqlDump;
	private int _status;
	private IComponentManager _componentManager;
	public static final int STATUS_READY = 0;
	public static final int STATUS_DUMPIMG_IN_PROGRESS = 1;
	
	private DatabaseDumper _databaseDumper;
	private DatabaseRestorer _databaseRestorer;
	
	private ServletContext _servletContext;

}