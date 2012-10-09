/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm;

import org.entando.entando.aps.system.orm.model.InstallationReport;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.util.FileTextReader;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.*;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.InputStream;
import java.lang.reflect.Method;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.sql.DataSource;

import org.entando.entando.aps.system.orm.model.ComponentReport;
import org.entando.entando.aps.system.orm.model.DataReport;
import org.entando.entando.aps.system.orm.model.SchemaReport;

import org.entando.entando.aps.system.orm.util.ApsDerbyEmbeddedDatabaseType;
import org.entando.entando.aps.system.orm.util.QueryExtractor;
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
		//cerca nel db locale
		InstallationReport report = this.extractReport();
		if (null == report) {
			//non c'è db locale installato, cerca nei backup locali
			//TODO DA FARE
			//non c'è... fa l'inizzializazione es novo
			report = InstallationReport.getInstance();
		}
		try {
			System.out.println("report " + report.getStatus());
			this.initMasterDatabases(report);
			this.initComponents(report);
			//se è porting, fai il restore dei backup
			
			report.setStatus(InstallationReport.Status.OK);
		} catch (Throwable t) {
			report.setStatus(InstallationReport.Status.INCOMPLETE);
			throw new Exception("Error while initializating Db Installer", t);
		} finally {
			this.updateReport(report);
		}
		
		ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initializated");
	}
	
	private InstallationReport extractReport() throws ApsSystemException {
		InstallationReport report = null;
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
	
	private void updateReport(InstallationReport report) throws ApsSystemException {
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
	
	private void initMasterDatabases(InstallationReport report) throws ApsSystemException {
		//System.out.println("********* initMasterDatabases ");
		if (report.getStatus().equals(InstallationReport.Status.PORTING)) {
			ApsSystemUtils.getLogger().info("Core Component - PORTING");
			System.out.println("Core Component - PORTING!");
			report.addReport("entandoCore", new Date());
			//this.updateReport(report);
			return;
		} 
		ComponentReport coreComponentReport = report.getComponentReport("entandoCore");
		if (null == coreComponentReport) {
			coreComponentReport = ComponentReport.getInstance("entandoCore");
			report.addReport(coreComponentReport);
		}
		if (coreComponentReport.getStatus().equals(InstallationReport.Status.PORTING) || 
				coreComponentReport.getStatus().equals(InstallationReport.Status.OK)) {
			ApsSystemUtils.getLogger().info("Core Component - Already installed/verified!");
			System.out.println("Core Component - Already installed/verified!");
			return;
		}
		//NON é NECESSARIO FARE VERIFICHE IN QUANTO SI FERMA ALLA PRIMA TABELLA PRESENTE
		//System.out.println("********* initMasterDatabases avviato ");
		Map<String, InstallationReport.Status> schemaStatus = new HashMap<String, InstallationReport.Status>();
		Map<String, InstallationReport.Status> dataStatus = new HashMap<String, InstallationReport.Status>();
		int dataSourceNumber = 0;
		try {
			ListableBeanFactory factory = (ListableBeanFactory) this.getBeanFactory();
			String[] dataSourceNames = factory.getBeanNamesForType(DataSource.class);
			dataSourceNumber = dataSourceNames.length;
			for (int i = 0; i < dataSourceNames.length; i++) {
				String dataSourceName = dataSourceNames[i];
				//System.out.println("********* initMasterDatabases - DATASOURCE " + dataSourceNames[i]);
				DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
				//System.out.println("********* initMasterDatabases - DATASOURCE " + dataSourceNames[i] + " - " + dataSource);
				int result = this.initDatabase(dataSourceName, dataSource);
				schemaStatus.put(dataSourceName, InstallationReport.Status.OK);
				if (result == 1 && !report.getStatus().equals(InstallationReport.Status.RESTORE)) {
					Resource resource = this.getDefaultSqlResources().get(dataSourceName);
					String script = this.readFile(resource);
					this.valueDatabase(script, dataSourceName, dataSource, null, null);
					dataStatus.put(dataSourceName, InstallationReport.Status.OK);
				}
			}
			//System.out.println("Core Component installation DONE!");
			coreComponentReport.setStatus(InstallationReport.Status.OK);
			//report.addReport("entandoCore", new Date(), InstallationReport.Status.OK);
			//this.updateReport(report);
			ApsSystemUtils.getLogger().info("Core Component installation DONE!");
		} catch (Throwable t) {
			coreComponentReport.setStatus(InstallationReport.Status.INCOMPLETE);
			//t.printStackTrace();
			ApsSystemUtils.logThrowable(t, this, "initMasterDatabases");
			throw new ApsSystemException("Error initializating master databases", t);
		} finally {
			coreComponentReport.getSchemaReport().getDatabaseStatus().putAll(schemaStatus);
			if (schemaStatus.size() == dataSourceNumber) {
				coreComponentReport.getSchemaReport().setStatus(InstallationReport.Status.OK);
			} else {
				coreComponentReport.getSchemaReport().setStatus(InstallationReport.Status.INCOMPLETE);
			}
			coreComponentReport.getDataReport().getDatabaseStatus().putAll(dataStatus);
			if (dataStatus.size() == dataSourceNumber) {
				coreComponentReport.getDataReport().setStatus(InstallationReport.Status.OK);
			} else {
				coreComponentReport.getDataReport().setStatus(InstallationReport.Status.INCOMPLETE);
			}
		}
	}
	
	private void initComponents(InstallationReport report) throws ApsSystemException {
		try {
			ListableBeanFactory factory = (ListableBeanFactory) this.getBeanFactory();
			String[] componentBeansNames = factory.getBeanNamesForType(EntandoComponentConfiguration.class);
			if (null == componentBeansNames || componentBeansNames.length == 0) return;
			List<EntandoComponentConfiguration> componentBeans = new ArrayList<EntandoComponentConfiguration>();
			for (int i = 0; i < componentBeansNames.length; i++) {
				//System.out.println("* " + componentBeansNames[i] + " *");
				EntandoComponentConfiguration componentConfiguration = (EntandoComponentConfiguration) this.getBeanFactory().getBean(componentBeansNames[i]);
				if (null != componentConfiguration) componentBeans.add(componentConfiguration);
			}
			Collections.sort(componentBeans);
			String[] dataSourceNames = factory.getBeanNamesForType(DataSource.class);
			for (int i = 0; i < componentBeans.size(); i++) {
				EntandoComponentConfiguration componentConfiguration = componentBeans.get(i);
				this.initComponent(componentConfiguration, dataSourceNames, report);
			}
		} catch (Throwable t) {
			//t.printStackTrace();
			ApsSystemUtils.logThrowable(t, this, "initComponents");
			throw new ApsSystemException("Error initializating components", t);
		}
	}
	
	private void initComponent(EntandoComponentConfiguration componentConfiguration, 
			String[] dataSourceNames, InstallationReport report) throws ApsSystemException {
		String logPrefix = "Component " + componentConfiguration.getCode();
		if (report.getStatus().equals(InstallationReport.Status.PORTING)) {
			System.out.println(logPrefix + " - PORTING!!");
			ApsSystemUtils.getLogger().info(logPrefix + " - PORTING!!");
			report.addReport(componentConfiguration.getCode(), new Date());
			return;
		} 
		ComponentReport componentReport = report.getComponentReport(componentConfiguration.getCode());
		if (null == componentReport) {
			componentReport = ComponentReport.getInstance(componentConfiguration.getCode());
			report.addReport(componentReport);
		}
		if (componentReport.getStatus().equals(InstallationReport.Status.PORTING) || 
				componentReport.getStatus().equals(InstallationReport.Status.OK)) {
			ApsSystemUtils.getLogger().info(logPrefix + " - Already installed/verified!");
			System.out.println(logPrefix + " - Already installed/verified!");
			return;
		}
		try {
			Map<String, List<String>> tableMapping = componentConfiguration.getTableMapping();
			SchemaReport schemaReport = componentReport.getSchemaReport();
			String logTablePrefix = logPrefix + " TABLES";
			if (null != tableMapping && !tableMapping.isEmpty()) {
				System.out.println(logTablePrefix + " - Installation STARTED!!!");
				for (int j = 0; j < dataSourceNames.length; j++) {
					String dataSourceName = dataSourceNames[j];
					String logDbTablePrefix = logTablePrefix + " / Datasource " + dataSourceName;
					DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
					InstallationReport.Status schemaStatus = schemaReport.getDatabaseStatus().get(dataSourceName);
					System.out.println(logDbTablePrefix + " - INIT!!!");
					if (null != schemaStatus && (schemaStatus.equals(InstallationReport.Status.NOT_AVAILABLE) || 
							schemaStatus.equals(InstallationReport.Status.PORTING) || // FORSE NON PUO' ESSERE POSSIBILE
							schemaStatus.equals(InstallationReport.Status.OK))) {
						System.out.println(logDbTablePrefix + " - Already installed/verified!");
						continue;
					}
					if (null == schemaReport.getDatabaseTables().get(dataSourceName)) {
						schemaReport.getDatabaseTables().put(dataSourceName, new ArrayList<String>());
					}
					List<String> tableClassNames = tableMapping.get(dataSourceName);
					if (null != tableClassNames && !tableClassNames.isEmpty()) {
						System.out.println(logDbTablePrefix + " - INSTALLATION STARTED!");
						this.createComponentTables(dataSourceName, 
								tableClassNames, dataSource, schemaReport);
						System.out.println(logDbTablePrefix + " - INSTALLATION DONE! - installated tables " + schemaReport.getDatabaseTables().get(dataSourceName));
						schemaReport.getDatabaseStatus().put(dataSourceName, InstallationReport.Status.OK);
					} else {
						System.out.println(logDbTablePrefix + " - NOT AVAILABLE!");
						schemaReport.getDatabaseStatus().put(dataSourceName, InstallationReport.Status.NOT_AVAILABLE);
					}
				}
				System.out.println(logTablePrefix + " - Installation DONE!!!");
				schemaReport.setStatus(InstallationReport.Status.OK);
			} else {
				System.out.println(logTablePrefix + " - NOT AVAILABLE!");
				if (!schemaReport.getStatus().equals(InstallationReport.Status.NOT_AVAILABLE)) {
					schemaReport.setStatus(InstallationReport.Status.NOT_AVAILABLE);
				}
			}
			Map<String, Resource> defaultSqlResources = componentConfiguration.getDefaultSqlResources();
			String logDataPrefix = "Component " + componentReport.getComponent() + " DATA";
			DataReport dataReport = componentReport.getDataReport();
			if (null != defaultSqlResources && !defaultSqlResources.isEmpty()) {
				System.out.println(logDataPrefix + " - INIT!!!");
				for (int j = 0; j < dataSourceNames.length; j++) {
					String dataSourceName = dataSourceNames[j];
					String logDbDataPrefix = logDataPrefix + " / Datasource " + dataSourceName;
					DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
					InstallationReport.Status dataStatus = dataReport.getDatabaseStatus().get(dataSourceName);
					if (null != dataStatus && (dataStatus.equals(InstallationReport.Status.NOT_AVAILABLE) || 
							dataStatus.equals(InstallationReport.Status.RESTORE) || 
							dataStatus.equals(InstallationReport.Status.OK))) {
						System.out.println(logDbDataPrefix + " - Already installed/verified!");
						continue;
					}
					Resource resource = defaultSqlResources.get(dataSourceName);
					String script = this.readFile(resource);
					if (null != script && script.trim().length() > 0) {
						System.out.println(logDbDataPrefix + " - Installation STARTED!!!");
						this.valueDatabase(script, dataSourceName, dataSource, report, dataReport);
						System.out.println(logDbDataPrefix + " - Installation DONE!!!");
						dataReport.getDatabaseStatus().put(dataSourceName, InstallationReport.Status.OK);
					} else {
						System.out.println(logDbDataPrefix + " - NOT AVAILABLE!");
						dataReport.getDatabaseStatus().put(dataSourceName, InstallationReport.Status.NOT_AVAILABLE);
					}
				}
				System.out.println(logDataPrefix + " - Installation DONE!!!");
				dataReport.setStatus(InstallationReport.Status.OK);
			} else {
				System.out.println(logDataPrefix + " - NOT AVAILABLE!");
				if (!dataReport.getStatus().equals(InstallationReport.Status.NOT_AVAILABLE)) {
					dataReport.setStatus(InstallationReport.Status.NOT_AVAILABLE);
				}
			}
			System.out.println("Component " + componentReport.getComponent() + " - INSTALLATION DONE!!!");
			componentReport.setStatus(InstallationReport.Status.OK);
			//report.addReport(componentConfiguration.getCode(), new Date(), InstallationReport.Status.OK);
			ApsSystemUtils.getLogger().info("'" + componentConfiguration.getCode() 
					+ "' Component installation DONE!");
		} catch (Throwable t) {
			componentReport.setStatus(InstallationReport.Status.INCOMPLETE);
			ApsSystemUtils.logThrowable(t, this, "initComponent", 
					"Error initializating component " + componentConfiguration.getCode());
			throw new ApsSystemException("Error initializating component " + componentConfiguration.getCode(), t);
		}
	}
	
	private String invokeGetMethod(String methodName, DataSource dataSource) throws Throwable {
		Method method = dataSource.getClass().getDeclaredMethod(methodName);
		return (String) method.invoke(dataSource);
	}
	
	private int initDatabase(String databaseName, DataSource dataSource) throws ApsSystemException {
		int globalResult = 0;
		ConnectionSource connectionSource = null;
		try {
			connectionSource = this.createConnectionSource(databaseName, dataSource);
			globalResult = this.setupDatabase(databaseName, dataSource, connectionSource);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "initDatabase", "Error inizializating db " + databaseName);
			throw new ApsSystemException("Error creating component tables to db " + databaseName, t);
		} finally {
			if (connectionSource != null) {
				try {
					connectionSource.close();
				} catch (SQLException ex) {}
			}
		}
		return globalResult;
	}
	
	private int setupDatabase(String databaseName, DataSource dataSource, ConnectionSource connectionSource) throws ApsSystemException {
		int globalResult = 0;
		try {
			DatabaseType type = this.getType(dataSource);
			//System.out.println("DB TYPE " + type);
			if (type.equals(DatabaseType.DERBY)) {
				int result = this.initDerbySchema(dataSource);
				if (result == 0) return 0;
			}
			List<String> tableClassNames = this.getTableMapping().get(databaseName);
			if (null == tableClassNames || tableClassNames.isEmpty()) {
				ApsSystemUtils.getLogger().info("No Tables defined for db - " + databaseName);
				return 0;
			}
			globalResult = this.createTables(databaseName, dataSource, tableClassNames, connectionSource, null);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "setupDatabase", "Error on setup Database");
			throw new ApsSystemException("Error on setup Database", t);
		}
		return globalResult;
	}
	
	private void createComponentTables(String databaseName, List<String> tableClassNames, 
			DataSource dataSource, SchemaReport schemaReport) throws ApsSystemException {
		InstallationReport.Status schemaStatus = schemaReport.getDatabaseStatus().get(databaseName);
		if (null != schemaStatus && (databaseName.equals(InstallationReport.Status.OK)
				|| databaseName.equals(InstallationReport.Status.PORTING))) {
			return;
		}
		ConnectionSource connectionSource = null;
		try {
			connectionSource = this.createConnectionSource(databaseName, dataSource);
			this.createTables(databaseName, dataSource, tableClassNames, connectionSource, schemaReport);
		} catch (Throwable t) {
			schemaReport.getDatabaseStatus().put(databaseName, InstallationReport.Status.INCOMPLETE);
			//this.updateReport(report);
			ApsSystemUtils.logThrowable(t, this, "createComponentTables", "Error creating component tables into db " + databaseName);
			throw new ApsSystemException("Error creating component tables to db " + databaseName, t);
		} finally {
			if (connectionSource != null) {
				try {
					connectionSource.close();
				} catch (SQLException ex) {}
			}
		}
	}
	
	private ConnectionSource createConnectionSource(String databaseName, DataSource dataSource) throws ApsSystemException {
		ConnectionSource connectionSource = null;
		try {
			DatabaseType type = this.getType(dataSource);
			String url = /*dataSource.getUrl();*/this.invokeGetMethod("getUrl", dataSource);
			String username = /*dataSource.getUsername();*/this.invokeGetMethod("getUsername", dataSource);
			String password = /*dataSource.getPassword();*/this.invokeGetMethod("getPassword", dataSource);
			//System.out.println(url + " - " + username + " - " + password);
			com.j256.ormlite.db.DatabaseType dataType = null;
			//System.out.println("AAAAAAaaaaaaaaaaAAAAAAAAAAa " + type);
			if (type.equals(DatabaseType.DERBY)) {
				dataType = new ApsDerbyEmbeddedDatabaseType();
				//System.out.println("ESCAPE " + ((DerbyEmbeddedDatabaseType) dataType).isEntityNamesMustBeUpCase());
				url = url + ";user=" + username + ";password=" + password;//;user=XXX;password=YYY';
				//dataType.setDriver(new org.apache.derby.jdbc.EmbeddedDriver());
				connectionSource = new JdbcConnectionSource(url, dataType);
			} else {
				if (type.equals(DatabaseType.POSTGRESQL)) {
					dataType = new PostgresDatabaseType();
				} else if (type.equals(DatabaseType.MYSQL)) {
					dataType = new MysqlDatabaseType();
				} else if (type.equals(DatabaseType.ORACLE)) {
					dataType = new OracleDatabaseType();
				}
				connectionSource = new JdbcConnectionSource(url, username, password, dataType);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "createConnectionSource", "Error creating connectionSource to db " + databaseName);
			throw new ApsSystemException("Error creating connectionSource to db " + databaseName, t);
		}
		return connectionSource;
	}
	
	private int createTables(String databaseName, DataSource dataSource, List<String> tableClassNames, 
			ConnectionSource connectionSource, SchemaReport schemaReport) throws ApsSystemException {
		int globalResult = 0;
		List<String> tables = null;
		try {
			if (null != schemaReport) {
				//TABELLE DI COMPONENTI 
				tables = schemaReport.getDatabaseTables().get(databaseName);
				//System.out.println("INSTALLED TABLES - " + tables);
				/*
				if (null == tables) {
					tables = new ArrayList<String>();
					schemaReport.getDatabaseTables().put(databaseName, tables);
				}
				*/
			}// else {
			//	System.out.println("SCHEMA REPORT NULL - databaseName " + databaseName);
			//}
			DatabaseType type = this.getType(dataSource);
			globalResult = 1;
			for (int i = 0; i < tableClassNames.size(); i++) {
				String tableClassName = tableClassNames.get(i);
				if (null != tables && tables.contains(tableClassName)) {
					continue;
				}
				//System.out.println("************** CLASSE " + tableClassName + " *********************");
				int result = 0;
				try {
					System.out.println("TABLE '" + tableClassName + "' - INSTALLATION");
					Class tableClass = Class.forName(tableClassName);
					result = this.createTable(databaseName, type, tableClass, connectionSource);
					if (null != tables && !tables.contains(tableClassName)) {
						tables.add(tableClassName);
						System.out.println("TABLE '" + tableClassName + "' installed");
						//this.updateReport(report);
					}
					//System.out.println("risultato CREAZIONE TABELLA " + tableClassName + " - " + result);
				} catch (Throwable t) {
					schemaReport.getDatabaseStatus().put(databaseName, InstallationReport.Status.INCOMPLETE);
					//System.out.println("Impossibile CREARE TABELLA " + tableClassName);
					//t.printStackTrace();
					String message = "Error creating table " + databaseName + "/" + tableClassName + " - " + t.getMessage();
					ApsSystemUtils.logThrowable(t, this, "createTables", message);
					throw new ApsSystemException(message, t);
				}
				if (result == 0) globalResult = 0;
			}
		} catch (Throwable t) {
			schemaReport.getDatabaseStatus().put(databaseName, InstallationReport.Status.INCOMPLETE);
			ApsSystemUtils.logThrowable(t, this, "setupDatabase", "Error on setup Database - " + databaseName);
			throw new ApsSystemException("Error on setup Database", t);
		}
		return globalResult;
	}
	
	private int initDerbySchema(DataSource dataSource) throws Throwable {
		String username = /*dataSource.getUsername();*/this.invokeGetMethod("getUsername", dataSource);
		try {
			String[] queryCreateSchema = new String[] {"CREATE SCHEMA " + username.toUpperCase()};
			this.executeQueries(dataSource, queryCreateSchema, false);
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().info("Error creating derby schema - " + t.getMessage());
			return 0;
		}
		try {
			String[] initSchemaQuery = new String[] {"SET SCHEMA \"" + username.toUpperCase() + "\""};
			this.executeQueries(dataSource, initSchemaQuery, true);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "initDerbySchema", "Error initializating Derby Schema");
			throw new ApsSystemException("Error initializating Derby Schema", t);
		}
		return 1;
	}
	
	private int createTable(String databaseName, DatabaseType type, 
			Class tableClass, ConnectionSource connectionSource) throws Throwable {
		int result = 0;
		String logTableName = databaseName + "/" + tableClass.getSimpleName().toLowerCase();
		//System.out.println("CREAZIONE TABELLA " + logTableName);
		try {
			result = TableUtils.createTable(connectionSource, tableClass);
			if (result > 0) {
				//System.out.println("Created table - " + logTableName);
				ApsSystemUtils.getLogger().info("Created table - " + logTableName);
				Object tableModel = tableClass.newInstance();
				if (tableModel instanceof ExtendedColumnDefinition) {
					String[] extensions = ((ExtendedColumnDefinition) tableModel).extensions(type);
					if (null != extensions && extensions.length > 0) {
						Dao dao = DaoManager.createDao(connectionSource, tableClass);
						for (int i = 0; i < extensions.length; i++) {
							String query = extensions[i];
							dao.executeRaw(query);
						}
					}
				}
			}
		//} catch (SQLException t) {
			//t.printStackTrace();
			//System.out.println("Table creation not allowed - " + logTableName + " - " + t.getMessage());
		//	ApsSystemUtils.getLogger().info("Table creation not allowed - " + t.getMessage());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "setupDatabase", "Error creating table " + logTableName + " - " + t.getMessage());
			if (result > 0) {
				TableUtils.dropTable(connectionSource, tableClass, true);
			}
			throw new ApsSystemException("Error creating table " + logTableName, t);
		}
		return result;
	}
	/*
	private void valueDatabase(String databaseName, DataSource dataSource) throws ApsSystemException {
		
	}
	*/
	private void valueDatabase(String script, String databaseName, 
			DataSource dataSource, InstallationReport report, DataReport schemaReport) throws ApsSystemException {
		/*
		if (null == resource) {
			ApsSystemUtils.getLogger().info("No resource script for db " + databaseName);
			if (null != report) {
				schemaReport.getDatabaseStatus().put(databaseName, InstallationReport.Status.NOT_AVAILABLE);
				//this.updateReport(report);
			}
			return;
		}
		*/
		try {
            if (null == script || script.trim().length() == 0) {
				ApsSystemUtils.getLogger().info("No sql script for db " + databaseName);
				if (null != report) {
					schemaReport.getDatabaseStatus().put(databaseName, InstallationReport.Status.NOT_AVAILABLE);
					//this.updateReport(report);
				}
				return;
			}
            String[] queries = QueryExtractor.extractQueries(script);
			if (null == queries || queries.length == 0) {
				ApsSystemUtils.getLogger().info("Script file for db " + databaseName + " void");
				if (null != report) {
					schemaReport.getDatabaseStatus().put(databaseName, InstallationReport.Status.NOT_AVAILABLE);
					//this.updateReport(report);
				}
				return;
			}
			this.executeQueries(dataSource, queries, true);
			if (null != report) {
				schemaReport.getDatabaseStatus().put(databaseName, InstallationReport.Status.OK);
				//this.updateReport(report);
			}
		} catch (Throwable t) {
			if (null != report) {
				schemaReport.getDatabaseStatus().put(databaseName, InstallationReport.Status.INCOMPLETE);
				//this.updateReport(report);
			}
			ApsSystemUtils.logThrowable(t, this, "valueDatabase", "Error executing script into db " + databaseName);
			throw new ApsSystemException("Error executing script into db " + databaseName, t);
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
	
	private void executeQueries(DataSource dataSource, String[] queries, boolean traceException) throws ApsSystemException {
		if (queries.length == 0) return;
		Connection conn = null;
        PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = dataSource.getConnection();
            conn.setAutoCommit(false);
			for (int i = 0; i < queries.length; i++) {
				stat = conn.prepareStatement(queries[i]);
				stat.execute();
			}
			conn.commit();
		} catch (Throwable t) {
			if (traceException) {
				ApsSystemUtils.logThrowable(t, this, "valueDatabase", "Error executing script into db " + dataSource);
			}
			throw new ApsSystemException("Error executing script into db " + dataSource, t);
		} finally {
			try {
				if (res != null) res.close();
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "closeDaoResources", "Error while closing the resultset");
			}
			try {
				if (stat != null) stat.close();
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "closeDaoResources", "Error while closing the statement");
			}
			try {
				if (conn != null) conn.close();
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "closeDaoStatement", "Error closing the connection");
			}
		}
	}
	
	protected DatabaseType getType(DataSource dataSource) {
		DatabaseType type = null;
		String typeString = null;
		try {
			//System.out.println(((BasicDataSource) dataSource).getDriverClassName());
			String driverClassName = this.invokeGetMethod("getDriverClassName", dataSource);
			//System.out.println(driverClassName);
			Iterator<Object> typesIter = this.getDatabaseTypeDrivers().keySet().iterator();
			while (typesIter.hasNext()) {
				String typeCode = (String) typesIter.next();
				List<String> driverClassNames = (List<String>) this.getDatabaseTypeDrivers().get(typeCode);
				if (null != driverClassNames && driverClassNames.contains(driverClassName)) {
					typeString = typeCode;
					//System.out.println(typeCode);
					break;
				}
			}
			if (null == typeString) return DatabaseType.DERBY;
			type = Enum.valueOf(DatabaseType.class, typeString.toUpperCase());
			//System.out.println(type);
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().severe("Invalid type for db - '" + typeString + "' - " + t.getMessage());
			type = DatabaseType.DERBY;
		}
		return type;
	}
	
	/*
	protected Map<String, String> getDatabaseTypes() {
		return _databaseTypes;
	}
	public void setDatabaseTypes(Map<String, String> databaseTypes) {
		this._databaseTypes = databaseTypes;
	}
	*/
	
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
	
	protected Map<String, List<String>> getTableMapping() {
		return _tableMapping;
	}
	public void setTableMapping(Map<String, List<String>> tableMapping) {
		this._tableMapping = tableMapping;
	}
	
	protected Map<String, Resource> getDefaultSqlResources() {
		return _defaultSqlResources;
	}
	public void setDefaultSqlResources(Map<String, Resource> defaultSqlResources) {
		this._defaultSqlResources = defaultSqlResources;
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
	//private Map<String, String> _databaseTypes;
	private Properties _databaseTypeDrivers;
	private Map<String, List<String>> _tableMapping;
	private Map<String, Resource> _defaultSqlResources;
	
	private BeanFactory _beanFactory;
	
}