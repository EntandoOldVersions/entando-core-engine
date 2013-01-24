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
import java.lang.reflect.Method;

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
	public SystemInstallationReport installDatabase(SystemInstallationReport report) throws Exception {
		String lastLocalBackupFolder = null;
		if (null == report) {
			report = SystemInstallationReport.getInstance();
			if (!Environment.test.equals(this.getEnvironment())) {
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
			this.initMasterDatabases(report);
			List<Component> components = this.getComponentManager().getCurrentComponents();
			for (int i = 0; i < components.size(); i++) {
				Component entandoComponentConfiguration = components.get(i);
				this.initComponentDatabases(entandoComponentConfiguration, report);
			}
			this.initMasterDefaultResource(report);
			for (int i = 0; i < components.size(); i++) {
				Component entandoComponentConfiguration = components.get(i);
				this.initComponentDefaultResources(entandoComponentConfiguration, report);
			}
			if (report.getStatus().equals(SystemInstallationReport.Status.RESTORE)) {
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

	private void initMasterDatabases(SystemInstallationReport report) throws ApsSystemException {
		String logPrefix = "Core Component SCHEMA";
		ComponentInstallationReport componentReport = report.getComponentReport("entandoCore", true);
		DataSourceInstallationReport dataSourceReport = componentReport.getDataSourceReport();
		/*
		 if (componentReport.getStatus().equals(InstallationReport.Status.OK) 
		 || schemaReport.getStatus().equals(InstallationReport.Status.OK)) {
		 ApsSystemUtils.getLogger().info("Core Schema Component - Already installed/verified!");
		 System.out.println("Core Schema Component - Already installed/verified!");
		 return;
		 }
		 */
		//TODO COMMENTATO PER DARE LA POSSIBILITA' DI INIZIALIZZAZIONE DI NUOVI DATASOURCE
		try {
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			Map<String, SystemInstallationReport.Status> databasesStatus = dataSourceReport.getDatabaseStatus();
			System.out.println(logPrefix + " - Installation STARTED!!!");
			for (int i = 0; i < dataSourceNames.length; i++) {
				String dataSourceName = dataSourceNames[i];
				if (report.getStatus().equals(SystemInstallationReport.Status.PORTING)) {
					System.out.println(logPrefix + " - Already present! db " + dataSourceName);
					databasesStatus.put(dataSourceName, SystemInstallationReport.Status.PORTING);
					report.setUpdated();
					continue;
				}
				SystemInstallationReport.Status status = databasesStatus.get(dataSourceName);
				if (status != null && (status.equals(SystemInstallationReport.Status.OK) || status.equals(SystemInstallationReport.Status.PORTING))) {
					System.out.println(logPrefix + " - Already installed/verified! db " + dataSourceName);
				} else if (status == null || !status.equals(SystemInstallationReport.Status.OK)) {
					DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
					System.out.println(logPrefix + " - '" + dataSourceName + "' Installation Started... ");
					databasesStatus.put(dataSourceName, SystemInstallationReport.Status.INCOMPLETE);
					this.initMasterDatabase(dataSourceName, dataSource, dataSourceReport);
					System.out.println(logPrefix + " - '" + dataSourceName + "' Installation DONE!");
					databasesStatus.put(dataSourceName, SystemInstallationReport.Status.OK);
					report.setUpdated();
				}
			}
			System.out.println(logPrefix + " - Installation DONE!!!");
			ApsSystemUtils.getLogger().info(logPrefix + " - Installation DONE!!!");
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "initMasterDatabases");
			throw new ApsSystemException("Error initializating master databases", t);
		}
	}

	private void initMasterDatabase(String databaseName, DataSource dataSource, DataSourceInstallationReport schemaReport) throws ApsSystemException {
		try {
			DatabaseType type = this.getType(dataSource);
			if (type.equals(DatabaseType.DERBY)) {
				this.initDerbySchema(dataSource);
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

	private void initComponentDatabases(Component componentConfiguration, SystemInstallationReport report) throws ApsSystemException {
		String logPrefix = "Component '" + componentConfiguration.getCode() + "' SCHEMA";
		ComponentInstallationReport componentReport = report.getComponentReport(componentConfiguration.getCode(), true);
		if (componentReport.getStatus().equals(SystemInstallationReport.Status.PORTING)
				|| componentReport.getStatus().equals(SystemInstallationReport.Status.OK)) {
			ApsSystemUtils.getLogger().info(logPrefix + " - Already installed/verified!");
			System.out.println(logPrefix + " - Already installed/verified!");
			return;
		}
		try {
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			Map<String, List<String>> tableMapping = componentConfiguration.getTableMapping();
			DataSourceInstallationReport dataSourceReport = componentReport.getDataSourceReport();
			String logTablePrefix = logPrefix + " TABLES";
			System.out.println(logTablePrefix + " - Installation STARTED!!!");
			for (int j = 0; j < dataSourceNames.length; j++) {
				String dataSourceName = dataSourceNames[j];
				String logDbTablePrefix = logTablePrefix + " / Datasource " + dataSourceName;
				List<String> tableClassNames = (null != tableMapping) ? tableMapping.get(dataSourceName) : null;
				if (null == tableClassNames || tableClassNames.isEmpty()) {
					System.out.println(logDbTablePrefix + " - NOT AVAILABLE!");
					dataSourceReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.NOT_AVAILABLE);
					report.setUpdated();
					continue;
				}
				if (report.getStatus().equals(SystemInstallationReport.Status.PORTING)) {
					dataSourceReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.PORTING);
					String message = logTablePrefix + "- Already present! " + SystemInstallationReport.Status.PORTING + " - db " + dataSourceName;
					ApsSystemUtils.getLogger().info(message);
					System.out.println(message);
					continue;
				}
				SystemInstallationReport.Status schemaStatus = dataSourceReport.getDatabaseStatus().get(dataSourceName);
				//System.out.println(logDbTablePrefix + " - INIT!!!");
				if (null != schemaStatus && (schemaStatus.equals(SystemInstallationReport.Status.NOT_AVAILABLE)
						|| schemaStatus.equals(SystemInstallationReport.Status.OK))) {
					System.out.println(logDbTablePrefix + "- Already installed/verified! " + SystemInstallationReport.Status.PORTING + " - db " + dataSourceName);
					continue;
				}
				if (null == dataSourceReport.getDataSourceTables().get(dataSourceName)) {
					dataSourceReport.getDataSourceTables().put(dataSourceName, new ArrayList<String>());
				}
				System.out.println(logDbTablePrefix + " - Installation Started... ");
				dataSourceReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.INCOMPLETE);
				DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
				this.createTables(dataSourceName, tableClassNames, dataSource, dataSourceReport);
				//System.out.println(logDbTablePrefix + " - INSTALLATION DONE! - installated tables " + schemaReport.getDatabaseTables().get(dataSourceName));
				System.out.println(logPrefix + " - '" + dataSourceName + "' Installation DONE!!!");
				dataSourceReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.OK);
				report.setUpdated();
			}
			System.out.println(logTablePrefix + " - Installation DONE!!!");
			ApsSystemUtils.getLogger().info(logTablePrefix + " - Installation DONE!");
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "initComponent",
					"Error initializating component " + componentConfiguration.getCode());
			throw new ApsSystemException("Error initializating component " + componentConfiguration.getCode(), t);
		}
	}

	private void createTables(String databaseName, List<String> tableClassNames,
			DataSource dataSource, DataSourceInstallationReport schemaReport) throws ApsSystemException {
		try {
			DatabaseType type = this.getType(dataSource);
			TableFactory tableFactory = new TableFactory(databaseName, dataSource, type);
			tableFactory.createTables(tableClassNames, schemaReport);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "createTables", "Error creating tables into db " + databaseName);
			throw new ApsSystemException("Error creating tables to db " + databaseName, t);
		}
	}
	/*
	 private List<Component> extractComponents() throws ApsSystemException {
	 List<Component> componentBeans = new ArrayList<Component>();
	 try {
	 String[] componentBeansNames = this.extractBeanNames(Component.class);
	 if (null == componentBeansNames || componentBeansNames.length == 0) {
	 return componentBeans;
	 }
	 for (int i = 0; i < componentBeansNames.length; i++) {
	 Component componentConfiguration = (Component) this.getBeanFactory().getBean(componentBeansNames[i]);
	 if (null != componentConfiguration) {
	 componentBeans.add(componentConfiguration);
	 }
	 }
	 Collections.sort(componentBeans);
	 } catch (Throwable t) {
	 ApsSystemUtils.logThrowable(t, this, "extractComponents");
	 throw new ApsSystemException("Error extracting components", t);
	 }
	 return componentBeans;
	 }
	 */

	protected DatabaseType getType(DataSource dataSource) throws ApsSystemException {
		String typeString = null;
		try {
			String driverClassName = this.invokeGetMethod("getDriverClassName", dataSource);
			Iterator<Object> typesIter = this.getDatabaseTypeDrivers().keySet().iterator();
			while (typesIter.hasNext()) {
				String typeCode = (String) typesIter.next();
				List<String> driverClassNames = (List<String>) this.getDatabaseTypeDrivers().get(typeCode);
				if (null != driverClassNames && driverClassNames.contains(driverClassName)) {
					typeString = typeCode;
					break;
				}
			}
			if (null == typeString) {
				ApsSystemUtils.getLogger().severe("Type not recognized for Driver '" + driverClassName + "' - "
						+ "Recognized types '" + DatabaseType.values() + "'");
				return DatabaseType.UNKNOWN;
			}
			return Enum.valueOf(DatabaseType.class, typeString.toUpperCase());
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().severe("Invalid type for db - '" + typeString + "' - " + t.getMessage());
			throw new ApsSystemException("Invalid type for db - '" + typeString + "'", t);
		}
	}

	private void initDerbySchema(DataSource dataSource) throws Throwable {
		String username = this.invokeGetMethod("getUsername", dataSource);
		try {
			String[] queryCreateSchema = new String[]{"CREATE SCHEMA " + username.toUpperCase()};
			TableDataUtils.executeQueries(dataSource, queryCreateSchema, false);
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().info("Error creating derby schema - " + t.getMessage());
			throw new ApsSystemException("Error creating derby schema", t);
		}
		try {
			String[] initSchemaQuery = new String[]{"SET SCHEMA \"" + username.toUpperCase() + "\""};
			TableDataUtils.executeQueries(dataSource, initSchemaQuery, true);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "initDerbySchema", "Error initializating Derby Schema");
			throw new ApsSystemException("Error initializating Derby Schema", t);
		}
	}

	private String invokeGetMethod(String methodName, DataSource dataSource) throws Throwable {
		Method method = dataSource.getClass().getDeclaredMethod(methodName);
		return (String) method.invoke(dataSource);
	}

	//---------------- DATA ------------------- START
	private void initMasterDefaultResource(SystemInstallationReport report) throws ApsSystemException {
		String logDbDataPrefix = "Core Component RESOURCES";
		ComponentInstallationReport coreComponentReport = report.getComponentReport("entandoCore", false);
		if (coreComponentReport.getStatus().equals(SystemInstallationReport.Status.OK)
				|| coreComponentReport.getStatus().equals(SystemInstallationReport.Status.RESTORE)) {
			String message = logDbDataPrefix + " - Already installed/verified/present! " + coreComponentReport.getStatus();
			ApsSystemUtils.getLogger().info(message);
			System.out.println(message);
			return;
		}
		DataInstallationReport dataReport = coreComponentReport.getDataReport();
		try {
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			for (int i = 0; i < dataSourceNames.length; i++) {
				String dataSourceName = dataSourceNames[i];
				if (report.getStatus().equals(SystemInstallationReport.Status.PORTING)
						|| report.getStatus().equals(SystemInstallationReport.Status.RESTORE)) {
					dataReport.getDatabaseStatus().put(dataSourceName, report.getStatus());
					report.setUpdated();
					String message = "Core Component RESOURCES - Already present! " + report.getStatus() + " - db " + dataSourceName;
					ApsSystemUtils.getLogger().info(message);
					System.out.println(message);
					continue;
				}
				Resource resource = (Environment.test.equals(this.getEnvironment()))
						? this.getTestSqlResources().get(dataSourceName)
						: this.getEntandoDefaultSqlResources().get(dataSourceName);
				String script = this.readFile(resource);
				if (null != script && script.trim().length() != 0) {
					System.out.print(logDbDataPrefix + " - Installation started... ");
					dataReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.INCOMPLETE);
					DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
					TableDataUtils.valueDatabase(script, dataSourceName, dataSource, null);
					dataReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.OK);
					System.out.println("DONE!!!");
					report.setUpdated();
				} else {
					System.out.println(logDbDataPrefix + " - NOT AVAILABLE!");
					dataReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.NOT_AVAILABLE);
					report.setUpdated();
				}
			}
			ApsSystemUtils.getLogger().info("Core Component DATA installation DONE!");
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "initMasterDefaultResource");
			throw new ApsSystemException("Error initializating master DefaultResource", t);
		}
	}

	private void initComponentDefaultResources(Component componentConfiguration,
			SystemInstallationReport report) throws ApsSystemException {
		String logPrefix = "Component '" + componentConfiguration.getCode() + "' DATA";
		ComponentInstallationReport componentReport = report.getComponentReport(componentConfiguration.getCode(), false);
		if (componentReport.getStatus().equals(SystemInstallationReport.Status.OK)
				|| componentReport.getStatus().equals(SystemInstallationReport.Status.RESTORE)) {
			ApsSystemUtils.getLogger().info(logPrefix + " - Already installed/verified/present!");
			System.out.println(logPrefix + " - Already installed/verified/present!");
			return;
		}
		DataInstallationReport dataReport = componentReport.getDataReport();
		try {
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			String logDataPrefix = "Component " + componentReport.getComponentCode() + " DATA";
			//System.out.println(logDataPrefix + " - INIT!!!");
			for (int j = 0; j < dataSourceNames.length; j++) {
				String dataSourceName = dataSourceNames[j];
				String logDbDataPrefix = logDataPrefix + " / Datasource " + dataSourceName;
				if (report.getStatus().equals(SystemInstallationReport.Status.PORTING)
						|| report.getStatus().equals(SystemInstallationReport.Status.RESTORE)) {
					dataReport.getDatabaseStatus().put(dataSourceName, report.getStatus());
					report.setUpdated();
					continue;
				}
				DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
				SystemInstallationReport.Status dataStatus = dataReport.getDatabaseStatus().get(dataSourceName);
				if (null != dataStatus && (dataStatus.equals(SystemInstallationReport.Status.NOT_AVAILABLE)
						|| dataStatus.equals(SystemInstallationReport.Status.RESTORE)
						|| dataStatus.equals(SystemInstallationReport.Status.OK))) {
					System.out.println(logDbDataPrefix + " - Already installed/verified!");
					continue;
				}
				Map<String, ComponentEnvironment> environments = componentConfiguration.getEnvironments();
				String compEnvKey = (Environment.test.equals(this.getEnvironment()))
						? Environment.test.toString() : Environment.production.toString();
				ComponentEnvironment componentEnvironment = (null != environments) ? environments.get(compEnvKey) : null;
				Resource resource = (null != componentEnvironment) ? componentEnvironment.getSqlResources(dataSourceName) : null;
				String script = (null != resource) ? this.readFile(resource) : null;
				if (null != script && script.trim().length() > 0) {
					System.out.print(logDbDataPrefix + " - Installation started... ");
					dataReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.INCOMPLETE);
					TableDataUtils.valueDatabase(script, dataSourceName, dataSource, dataReport);
					System.out.println("DONE!!!");
					dataReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.OK);
					report.setUpdated();
				} else {
					System.out.println(logDbDataPrefix + " - NOT AVAILABLE!");
					dataReport.getDatabaseStatus().put(dataSourceName, SystemInstallationReport.Status.NOT_AVAILABLE);
					report.setUpdated();
				}
			}
			System.out.println(logDataPrefix + " - INSTALLATION DONE!!!");
			ApsSystemUtils.getLogger().info(logDataPrefix + "' Component installation DONE!");
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
			List<Component> components = this.getComponentManager().getCurrentComponents();
			DatabaseDumper dumper = new DatabaseDumper(this.getLocalBackupsFolder(), this.extractReport(),
					this.getEntandoTableMapping(), components, this.getBeanFactory());
			dumper.createBackup(this.getEnvironment());
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
				} catch (IOException ex) {
				}
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
			List<Component> components = this.getComponentManager().getCurrentComponents();
			DatabaseRestorer restorer = new DatabaseRestorer(this.getLocalBackupsFolder(), subFolderName,
					this.getEntandoTableMapping(), components, this.getBeanFactory());
			restorer.dropAndRestoreBackup();
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

	private String getLocalBackupsFolder() {
		StringBuilder dirName = new StringBuilder(this.getProtectedBaseDiskRoot());
		if (!dirName.toString().endsWith("\\") && !dirName.toString().endsWith("/")) {
			dirName.append(File.separator);
		}
		dirName.append("databaseBackups").append(File.separator);
		return dirName.toString();
	}

	private boolean restoreBackup(String subFolderName) throws ApsSystemException {
		try {
			if (!this.checkBackupFolder(subFolderName)) {
				ApsSystemUtils.getLogger().severe("backup not available - subfolder '" + subFolderName + "'");
				return false;
			}
			List<Component> components = this.getComponentManager().getCurrentComponents();
			DatabaseRestorer restorer = new DatabaseRestorer(this.getLocalBackupsFolder(), subFolderName,
					this.getEntandoTableMapping(), components, this.getBeanFactory());
			restorer.restoreBackup();
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

	protected Properties getDatabaseTypeDrivers() {
		return _databaseTypeDrivers;
	}
	public void setDatabaseTypeDrivers(Properties databaseTypeDrivers) {
		this._databaseTypeDrivers = databaseTypeDrivers;
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
	
	protected String getProtectedBaseDiskRoot() {
		return _protectedBaseDiskRoot;
	}
	public void setProtectedBaseDiskRoot(String protBaseDiskRoot) {
		this._protectedBaseDiskRoot = protBaseDiskRoot;
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
	
	protected ServletContext getServletContext() {
		return _servletContext;
	}
	@Override
	public void setServletContext(ServletContext servletContext) {
		this._servletContext = servletContext;
	}
	
	private Properties _databaseTypeDrivers;
	private Map<String, List<String>> _entandoTableMapping;
	private Map<String, Resource> _entandoDefaultSqlResources;
	private Map<String, Resource> _testSqlResources;
	private Map<String, Resource> _defaultSqlDump;
	private int _status;
	private String _protectedBaseDiskRoot;
	private IComponentManager _componentManager;
	public static final int STATUS_READY = 0;
	public static final int STATUS_DUMPIMG_IN_PROGRESS = 1;
	
	private ServletContext _servletContext;
	
}