/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm;

import org.entando.entando.aps.system.orm.model.report.ComponentInstallation;
import org.entando.entando.aps.system.orm.model.report.SystemInstallation;
import org.entando.entando.aps.system.orm.model.report.DatabaseDump;
import org.entando.entando.aps.system.orm.model.report.DatabaseInstallation;
import org.entando.entando.aps.system.orm.model.report.DataInstallation;
import org.entando.entando.aps.system.orm.util.TableDataUtils;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.util.DateConverter;
import com.agiletec.aps.util.FileTextReader;

import java.io.*;
import java.lang.reflect.Method;

import java.util.*;
import javax.sql.DataSource;

import org.entando.entando.aps.system.orm.util.TableFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.io.Resource;

/**
 * @author E.Santoboni
 */
public class DbInstallerManager implements BeanFactoryAware, IDbInstallerManager {
	
	public void init() throws Exception {
		if (!this.isCheckOnStartup()) {
			ApsSystemUtils.getLogger().config(this.getClass().getName() + ": short init executed");
			return;
		}
		boolean restoreLocalDump = false;
		SystemInstallation report = this.extractReport();
		if (null == report) {
			report = SystemInstallation.getInstance();
			//non c'è db locale installato, cerca nei backup locali
			DatabaseDump lastDumpReport = this.getLastDumpReport();
			if (null != lastDumpReport) {
				restoreLocalDump = true;
				report.setStatus(SystemInstallation.Status.RESTORE);
			} else {
				//SE NON c'è cerca il default dump
				Map<String, Resource> defaultSqlDump = this.getDefaultSqlDump();
				if (null != defaultSqlDump && defaultSqlDump.size() > 0) {
					report.setStatus(SystemInstallation.Status.RESTORE);
				}
			}
		}
		try {
			this.initMasterDatabases(report);
			List<EntandoComponentConfiguration> components = this.extractComponents();
			for (int i = 0; i < components.size(); i++) {
				EntandoComponentConfiguration entandoComponentConfiguration = components.get(i);
				this.initComponentDatabases(entandoComponentConfiguration, report);
			}
			this.initMasterDefaultResource(report);
			for (int i = 0; i < components.size(); i++) {
				EntandoComponentConfiguration entandoComponentConfiguration = components.get(i);
				this.initComponentDefaultResources(entandoComponentConfiguration, report);
			}
			if (report.getStatus().equals(SystemInstallation.Status.RESTORE)) {
				if (restoreLocalDump) {
					this.restoreLocalDump();
				} else {
					this.restoreDefaultDump();
				}
			}
		} catch (Throwable t) {
			report.setUpdated();
			report.setStatus(SystemInstallation.Status.INCOMPLETE);
			throw new Exception("Error while initializating Db Installer", t);
		} finally {
			if (report.isUpdated()) {
				this.saveReport(report);
			}
		}
		ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initializated");
	}
	
	//-------------------- REPORT -------- START
	
	private SystemInstallation extractReport() throws ApsSystemException {
		SystemInstallation report = null;
		try {
			InstallationReportDAO dao = new InstallationReportDAO();
			DataSource dataSource = (DataSource) this.getBeanFactory().getBean("portDataSource");
			dao.setDataSource(dataSource);
			report = dao.loadReport(this.getConfigVersion());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "extractReport");
			throw new ApsSystemException("Error extracting report", t);
		}
		return report;
	}
	
	private void saveReport(SystemInstallation report) throws ApsSystemException {
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
	
	//-------------------- REPORT -------- END
	
	private void initMasterDatabases(SystemInstallation report) throws ApsSystemException {
		String logPrefix = "Core Component SCHEMA";
		ComponentInstallation componentReport = report.getComponentReport("entandoCore", true);
		DatabaseInstallation schemaReport = componentReport.getSchemaReport();
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
			Map<String, SystemInstallation.Status> databasesStatus = schemaReport.getDatabaseStatus();
			System.out.println(logPrefix + " - Installation STARTED!!!");
			for (int i = 0; i < dataSourceNames.length; i++) {
				String dataSourceName = dataSourceNames[i];
				if (report.getStatus().equals(SystemInstallation.Status.PORTING)) {
					System.out.println(logPrefix + " - Already present! db " + dataSourceName);
					databasesStatus.put(dataSourceName, SystemInstallation.Status.PORTING);
					report.setUpdated();
					continue;
				}
				SystemInstallation.Status status = databasesStatus.get(dataSourceName);
				if (status != null && (status.equals(SystemInstallation.Status.OK) || status.equals(SystemInstallation.Status.PORTING))) {
					System.out.println(logPrefix + " - Already installed/verified! db " + dataSourceName);
				} else if (status == null || !status.equals(SystemInstallation.Status.OK)) {
					DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
					System.out.println(logPrefix + " - '" + dataSourceName + "' Installation Started... ");
					databasesStatus.put(dataSourceName, SystemInstallation.Status.INCOMPLETE);
					this.initMasterDatabase(dataSourceName, dataSource, schemaReport);
					System.out.println(logPrefix + " - '" + dataSourceName + "' Installation DONE!");
					databasesStatus.put(dataSourceName, SystemInstallation.Status.OK);
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
	
	private void initMasterDatabase(String databaseName, DataSource dataSource, DatabaseInstallation schemaReport) throws ApsSystemException {
		try {
			IDbInstallerManager.DatabaseType type = this.getType(dataSource);
			if (type.equals(IDbInstallerManager.DatabaseType.DERBY)) {
				this.initDerbySchema(dataSource);
			}
			List<String> tableClassNames = this.getEntandoTableMapping().get(databaseName);
			if (null == tableClassNames || tableClassNames.isEmpty()) {
				ApsSystemUtils.getLogger().info("No Master Tables defined for db - " + databaseName);
				schemaReport.getDatabaseStatus().put(databaseName, SystemInstallation.Status.NOT_AVAILABLE);
			} else {
				this.createTables(databaseName, tableClassNames, dataSource, schemaReport);
			}
		} catch (Throwable t) {
			schemaReport.getDatabaseStatus().put(databaseName, SystemInstallation.Status.INCOMPLETE);
			ApsSystemUtils.logThrowable(t, this, "initMasterDatabase", "Error inizializating db " + databaseName);
			throw new ApsSystemException("Error creating master tables to db " + databaseName, t);
		}
	}
	
	private void initComponentDatabases(EntandoComponentConfiguration componentConfiguration, SystemInstallation report) throws ApsSystemException {
		String logPrefix = "Component '" + componentConfiguration.getCode() + "' SCHEMA";
		ComponentInstallation componentReport = report.getComponentReport(componentConfiguration.getCode(), true);
		if (componentReport.getStatus().equals(SystemInstallation.Status.PORTING) || 
				componentReport.getStatus().equals(SystemInstallation.Status.OK)) {
			ApsSystemUtils.getLogger().info(logPrefix + " - Already installed/verified!");
			System.out.println(logPrefix + " - Already installed/verified!");
			return;
		}
		try {
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			Map<String, List<String>> tableMapping = componentConfiguration.getTableMapping();
			DatabaseInstallation schemaReport = componentReport.getSchemaReport();
			String logTablePrefix = logPrefix + " TABLES";
			System.out.println(logTablePrefix + " - Installation STARTED!!!");
			for (int j = 0; j < dataSourceNames.length; j++) {
				String dataSourceName = dataSourceNames[j];
				String logDbTablePrefix = logTablePrefix + " / Datasource " + dataSourceName;
				List<String> tableClassNames = (null != tableMapping) ? tableMapping.get(dataSourceName) : null;
				if (null == tableClassNames || tableClassNames.isEmpty()) {
					System.out.println(logDbTablePrefix + " - NOT AVAILABLE!");
					schemaReport.getDatabaseStatus().put(dataSourceName, SystemInstallation.Status.NOT_AVAILABLE);
					report.setUpdated();
					continue;
				}
				if (report.getStatus().equals(SystemInstallation.Status.PORTING)) {
					schemaReport.getDatabaseStatus().put(dataSourceName, SystemInstallation.Status.PORTING);
					String message = logTablePrefix + "- Already present! " + SystemInstallation.Status.PORTING + " - db " + dataSourceName;
					ApsSystemUtils.getLogger().info(message);
					System.out.println(message);
					continue;
				}
				SystemInstallation.Status schemaStatus = schemaReport.getDatabaseStatus().get(dataSourceName);
				//System.out.println(logDbTablePrefix + " - INIT!!!");
				if (null != schemaStatus && (schemaStatus.equals(SystemInstallation.Status.NOT_AVAILABLE) || 
						schemaStatus.equals(SystemInstallation.Status.OK))) {
					System.out.println(logDbTablePrefix + "- Already installed/verified! " + SystemInstallation.Status.PORTING + " - db " + dataSourceName);
					continue;
				}
				if (null == schemaReport.getDatabaseTables().get(dataSourceName)) {
					schemaReport.getDatabaseTables().put(dataSourceName, new ArrayList<String>());
				}
				System.out.println(logDbTablePrefix + " - Installation Started... ");
				DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
				this.createTables(dataSourceName, tableClassNames, dataSource, schemaReport);
				//System.out.println(logDbTablePrefix + " - INSTALLATION DONE! - installated tables " + schemaReport.getDatabaseTables().get(dataSourceName));
				System.out.println(logPrefix + " - '" + dataSourceName + "' Installation DONE!!!");
				schemaReport.getDatabaseStatus().put(dataSourceName, SystemInstallation.Status.OK);
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
			DataSource dataSource, DatabaseInstallation schemaReport) throws ApsSystemException {
		try {
			IDbInstallerManager.DatabaseType type = this.getType(dataSource);
			TableFactory tableFactory = new TableFactory(databaseName, dataSource, type);
			tableFactory.createTables(tableClassNames, schemaReport);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "createTables", "Error creating tables into db " + databaseName);
			throw new ApsSystemException("Error creating tables to db " + databaseName, t);
		}
	}
	
	private List<EntandoComponentConfiguration> extractComponents() throws ApsSystemException {
		List<EntandoComponentConfiguration> componentBeans = new ArrayList<EntandoComponentConfiguration>();
		try {
			String[] componentBeansNames = this.extractBeanNames(EntandoComponentConfiguration.class);
			if (null == componentBeansNames || componentBeansNames.length == 0) return componentBeans;
			for (int i = 0; i < componentBeansNames.length; i++) {
				EntandoComponentConfiguration componentConfiguration = (EntandoComponentConfiguration) this.getBeanFactory().getBean(componentBeansNames[i]);
				if (null != componentConfiguration) componentBeans.add(componentConfiguration);
			}
			Collections.sort(componentBeans);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "extractComponents");
			throw new ApsSystemException("Error extracting components", t);
		}
		return componentBeans;
	}
	
	protected IDbInstallerManager.DatabaseType getType(DataSource dataSource) throws ApsSystemException {
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
						+ "Recognized types '" + IDbInstallerManager.DatabaseType.values() + "'");
				return IDbInstallerManager.DatabaseType.UNKNOWN;
			}
			return Enum.valueOf(IDbInstallerManager.DatabaseType.class, typeString.toUpperCase());
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().severe("Invalid type for db - '" + typeString + "' - " + t.getMessage());
			throw new ApsSystemException("Invalid type for db - '" + typeString + "'", t);
		}
	}
	
	private void initDerbySchema(DataSource dataSource) throws Throwable {
		String username = this.invokeGetMethod("getUsername", dataSource);
		try {
			String[] queryCreateSchema = new String[] {"CREATE SCHEMA " + username.toUpperCase()};
			TableDataUtils.executeQueries(dataSource, queryCreateSchema, false);
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().info("Error creating derby schema - " + t.getMessage());
			throw new ApsSystemException("Error creating derby schema", t);
		}
		try {
			String[] initSchemaQuery = new String[] {"SET SCHEMA \"" + username.toUpperCase() + "\""};
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
	
	
	private void initMasterDefaultResource(SystemInstallation report) throws ApsSystemException {
		String logDbDataPrefix = "Core Component RESOURCES";
		ComponentInstallation coreComponentReport = report.getComponentReport("entandoCore", false);
		if (coreComponentReport.getStatus().equals(SystemInstallation.Status.OK) || 
				coreComponentReport.getStatus().equals(SystemInstallation.Status.RESTORE)) {
			String message = logDbDataPrefix + " - Already installed/verified/present! " + coreComponentReport.getStatus();
			ApsSystemUtils.getLogger().info(message);
			System.out.println(message);
			return;
		}
		DataInstallation dataReport = coreComponentReport.getDataReport();
		try {
			//System.out.println(logDbDataPrefix + " - INIT!!!");
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			for (int i = 0; i < dataSourceNames.length; i++) {
				String dataSourceName = dataSourceNames[i];
				if (report.getStatus().equals(SystemInstallation.Status.PORTING) || 
						report.getStatus().equals(SystemInstallation.Status.RESTORE)) {
					dataReport.getDatabaseStatus().put(dataSourceName, report.getStatus());
					report.setUpdated();
					String message = "Core Component RESOURCES - Already present! " + report.getStatus() + " - db " + dataSourceName;
					ApsSystemUtils.getLogger().info(message);
					System.out.println(message);
					continue;
				}
				Resource resource = this.getEntandoDefaultSqlResources().get(dataSourceName);
				String script = this.readFile(resource);
				if (null != script && script.trim().length() != 0) {
					System.out.print(logDbDataPrefix + " - Installation started... ");
					dataReport.getDatabaseStatus().put(dataSourceName, SystemInstallation.Status.INCOMPLETE);
					DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
					TableDataUtils.valueDatabase(script, dataSourceName, dataSource, null);
					dataReport.getDatabaseStatus().put(dataSourceName, SystemInstallation.Status.OK);
					System.out.println("DONE!!!");
					report.setUpdated();
				} else {
					System.out.println(logDbDataPrefix + " - NOT AVAILABLE!");
					dataReport.getDatabaseStatus().put(dataSourceName, SystemInstallation.Status.NOT_AVAILABLE);
					report.setUpdated();
				}
			}
			ApsSystemUtils.getLogger().info("Core Component DATA installation DONE!");
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "initMasterDefaultResource");
			throw new ApsSystemException("Error initializating master DefaultResource", t);
		}
	}
	
	private void initComponentDefaultResources(EntandoComponentConfiguration componentConfiguration, 
			SystemInstallation report) throws ApsSystemException {
		String logPrefix = "Component '" + componentConfiguration.getCode() + "' DATA";
		ComponentInstallation componentReport = report.getComponentReport(componentConfiguration.getCode(), false);
		if (componentReport.getStatus().equals(SystemInstallation.Status.OK) || 
				componentReport.getStatus().equals(SystemInstallation.Status.RESTORE)) {
			ApsSystemUtils.getLogger().info(logPrefix + " - Already installed/verified/present!");
			System.out.println(logPrefix + " - Already installed/verified/present!");
			return;
		}
		DataInstallation dataReport = componentReport.getDataReport();
		try {
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			Map<String, Resource> defaultSqlResources = componentConfiguration.getDefaultSqlResources();
			String logDataPrefix = "Component " + componentReport.getComponentName() + " DATA";
			//System.out.println(logDataPrefix + " - INIT!!!");
			for (int j = 0; j < dataSourceNames.length; j++) {
				String dataSourceName = dataSourceNames[j];
				String logDbDataPrefix = logDataPrefix + " / Datasource " + dataSourceName;
				if (report.getStatus().equals(SystemInstallation.Status.PORTING) || 
						report.getStatus().equals(SystemInstallation.Status.RESTORE)) {
					dataReport.getDatabaseStatus().put(dataSourceName, report.getStatus());
					report.setUpdated();
					continue;
				}
				DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
				SystemInstallation.Status dataStatus = dataReport.getDatabaseStatus().get(dataSourceName);
				if (null != dataStatus && (dataStatus.equals(SystemInstallation.Status.NOT_AVAILABLE) || 
						dataStatus.equals(SystemInstallation.Status.RESTORE) || 
						dataStatus.equals(SystemInstallation.Status.OK))) {
					System.out.println(logDbDataPrefix + " - Already installed/verified!");
					continue;
				}
				Resource resource = (null != defaultSqlResources) ? defaultSqlResources.get(dataSourceName) : null;
				String script = this.readFile(resource);
				if (null != script && script.trim().length() > 0) {
					System.out.print(logDbDataPrefix + " - Installation started... ");
					dataReport.getDatabaseStatus().put(dataSourceName, SystemInstallation.Status.INCOMPLETE);
					TableDataUtils.valueDatabase(script, dataSourceName, dataSource, dataReport);
					System.out.println("DONE!!!");
					dataReport.getDatabaseStatus().put(dataSourceName, SystemInstallation.Status.OK);
					report.setUpdated();
				} else {
					System.out.println(logDbDataPrefix + " - NOT AVAILABLE!");
					dataReport.getDatabaseStatus().put(dataSourceName, SystemInstallation.Status.NOT_AVAILABLE);
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
			if (null == defaultDump || defaultDump.isEmpty()) return; 
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
		if (resource == null) return null;
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
			if (null != is) is.close();
		}
		return text;
	}
	
	//---------------- DATA ------------------- END
	
	@Override
	public void createBackup() throws ApsSystemException {
		if (this.getStatus() != STATUS_READY) return;
		try {
			DatabaseDumperThread thread = new DatabaseDumperThread(this);
			String threadName = "DatabaseDumper_" + DateConverter.getFormattedDate(new Date(), "yyyyMMddHHmmss");
			thread.setName(threadName);
			thread.start();
			/*
			List<EntandoComponentConfiguration> components = this.extractComponents();
			for (int i = 0; i < components.size(); i++) {
				EntandoComponentConfiguration componentConfiguration = components.get(i);
				this.createBackup(componentConfiguration.getTableMapping());
			}
			this.createBackup(this.getEntandoTableMapping());
			*/
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "createBackup");
			throw new ApsSystemException("Error while creating backup", t);
		}
	}
	
	protected void executeBackup() throws ApsSystemException {
		try {
			DatabaseDumper dumper = new DatabaseDumper(this.getLocalBackupFolder(), 
					this.getEntandoTableMapping(), this.extractComponents(), this.getBeanFactory(), this);
			dumper.createBackup();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "executeBackup");
			throw new ApsSystemException("Error while creating backup", t);
		}
	}
	/*
	private void createBackup(Map<String, List<String>> tableMapping) throws ApsSystemException {
		if (null == tableMapping || tableMapping.isEmpty()) return;
		try {
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			for (int j = 0; j < dataSourceNames.length; j++) {
				String dataSourceName = dataSourceNames[j];
				List<String> tableClassNames = tableMapping.get(dataSourceName);
				if (null == tableClassNames || tableClassNames.isEmpty()) continue;
				DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
				for (int k = 0; k < tableClassNames.size(); k++) {
					String tableClassName = tableClassNames.get(k);
					Class tableClass = Class.forName(tableClassName);
					String tableName = TableFactory.getTableName(tableClass);
					TableDumperFactoryThread thread = new TableDumperFactoryThread(tableName, dataSourceName, dataSource, this);
					String threadName = "TableDumper_" + tableName + "_" + DateConverter.getFormattedDate(new Date(), "yyyyMMddHHmmss");
					thread.setName(threadName);
					thread.start();
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "createBackup");
			throw new ApsSystemException("Error while creating backup", t);
		}
	}
	
	protected void dumpTableData(String tableName, String dataSourceName, DataSource dataSource) {
		FileOutputStream outStream = null;
		InputStream is = null;
		try {
			TableDumpResult dumpResult = TableDataUtils.dumpTable(dataSource, tableName);
			StringBuilder dirName = new StringBuilder(this.getLocalBackupFolder());
			dirName.append(dataSourceName).append(File.separator);
			File dir = new File(dirName.toString());
			if (!dir.exists() || !dir.isDirectory()) {
				dir.mkdirs();
			}
			String filePath = dirName + tableName + ".sql";
			is = new ByteArrayInputStream(dumpResult.getSqlDump().getBytes());
			byte[] buffer = new byte[1024];
            int length = -1;
            outStream = new FileOutputStream(filePath);
            while ((length = is.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
                outStream.flush();
            }
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "dumpTableData");
		} finally {
			try {
				if (null != outStream) outStream.close();
			} catch (Throwable t) {
				throw new RuntimeException("Error while closing OutputStream ", t);
			}
			try {
				if (null != is) is.close();
			} catch (Throwable t) {
				throw new RuntimeException("Error while closing InputStream ", t);
			}
		}
	}
	*/
	@Override
	public DatabaseDump getLastDumpReport() throws ApsSystemException {
		InputStream is = null;
		DatabaseDump report = null;
		try {
			String dirName = this.getLocalBackupFolder();
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			for (int i = 0; i < dataSourceNames.length; i++) {
				String folderName = dirName + File.separator + dataSourceNames[i] + File.separator;
				File directory = new File(folderName);
				if (!directory.exists() || !directory.isDirectory() || directory.list().length == 0) {
					return null;
				}
			}
			File reportFile = new File(this.getLocalBackupFolder() + DUMP_REPORT_FILE_NAME);
			if (!reportFile.exists()) {
				return null;
			}
			is = new FileInputStream(reportFile);
			String xml = FileTextReader.getText(is);
			report = new DatabaseDump(xml);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getLastDumpReport");
			throw new RuntimeException("Error while extracting Last Dump Report");
		} finally {
			if (null != is) try {
				is.close();
			} catch (IOException ex) {}
		}
		return report;
	}
	/*
	private boolean checkLocalDump() throws ApsSystemException {
		try {
			String dirName = this.getLocalBackupFolder();
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			for (int i = 0; i < dataSourceNames.length; i++) {
				String folderName = dirName + File.separator + dataSourceNames[i] + File.separator;
				File directory = new File(folderName);
				if (!directory.exists() || !directory.isDirectory() || directory.list().length == 0) {
					return false;
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "checkLocalDump");
			throw new RuntimeException("Error while checking local dump");
		}
		return true;
	}
	*/
	private String getLocalBackupFolder() {
		StringBuilder dirName = new StringBuilder(this.getProtectedBaseDiskRoot());
		if (!dirName.toString().endsWith("\\") && !dirName.toString().endsWith("/")) {
			dirName.append(File.separator);
		}
		dirName.append("databaseBackup").append(File.separator);
		return dirName.toString();
	}
	
	private void restoreLocalDump() throws ApsSystemException {
		try {
			this.restoreLocalDump(this.getEntandoTableMapping());
			List<EntandoComponentConfiguration> components = this.extractComponents();
			for (int i = 0; i < components.size(); i++) {
				EntandoComponentConfiguration componentConfiguration = components.get(i);
				this.restoreLocalDump(componentConfiguration.getTableMapping());
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "restoreLocalDump");
			throw new ApsSystemException("Error while restoring local backup", t);
		}
	}
	
	private void restoreLocalDump(Map<String, List<String>> tableMapping) throws ApsSystemException {
		if (null == tableMapping) return;
		try {
			String dirName = this.getLocalBackupFolder();
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			for (int i = 0; i < dataSourceNames.length; i++) {
				String dataSourceName = dataSourceNames[i];
				List<String> tableClasses = tableMapping.get(dataSourceName);
				if (null == tableClasses || tableClasses.isEmpty()) continue;
				DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
				for (int j = 0; j < tableClasses.size(); j++) {
					String tableClassName = tableClasses.get(j);
					Class tableClass = Class.forName(tableClassName);
					String tableName = TableFactory.getTableName(tableClass);
					String fileName = dirName + dataSourceName + File.separator + tableName + ".sql";
					File tableSqlDumpFile = new File(fileName);
					if (tableSqlDumpFile.exists()) {
						FileInputStream is = new FileInputStream(tableSqlDumpFile);
						String sqlDump = FileTextReader.getText(is);
						TableDataUtils.valueDatabase(sqlDump, tableName, dataSource, null);
					}
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "restoreLocalDump");
			throw new RuntimeException("Error while restoring local dump", t);
		}
	}
	
	private String[] extractBeanNames(Class beanClass) {
		ListableBeanFactory factory = (ListableBeanFactory) this.getBeanFactory();
		return factory.getBeanNamesForType(beanClass);
	}
	
	protected boolean isCheckOnStartup() {
		return _checkOnStartup;
	}
	public void setCheckOnStartup(boolean checkOnStartup) {
		this._checkOnStartup = checkOnStartup;
	}
	
	protected String getConfigVersion() {
		return _configVersion;
	}
	public void setConfigVersion(String configVersion) {
		this._configVersion = configVersion;
	}
	
	protected Properties getDatabaseTypeDrivers() {
		return _databaseTypeDrivers;
	}
	public void setDatabaseTypeDrivers(Properties databaseTypeDrivers) {
		this._databaseTypeDrivers = databaseTypeDrivers;
	}
	
	protected Map<String, List<String>> getEntandoTableMapping() {
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
	
	protected BeanFactory getBeanFactory() {
		return _beanFactory;
	}
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this._beanFactory = beanFactory;
	}
	
	private boolean _checkOnStartup;
	private String _configVersion;
	private Properties _databaseTypeDrivers;
	private Map<String, List<String>> _entandoTableMapping;
	private Map<String, Resource> _entandoDefaultSqlResources;
	private Map<String, Resource> _defaultSqlDump;
	
	private int _status;
	
	private String _protectedBaseDiskRoot;
	
	private BeanFactory _beanFactory;
	
	public static final int STATUS_READY = 0;
	public static final int STATUS_DUMPIMG_IN_PROGRESS = 1;
	
	public static final String DUMP_REPORT_FILE_NAME = "dumpReport.xml";
	
}