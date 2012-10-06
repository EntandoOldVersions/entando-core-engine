/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm;

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

import org.entando.entando.aps.system.orm.util.ApsDerbyEmbeddedDatabaseType;
import org.entando.entando.aps.system.orm.util.QueryExtractor;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.io.Resource;

/**
 * @author E.Santoboni
 */
public class DbInstallerManager extends AbstractService implements /*InitializingBean, */IDbInstallerManager {
	
	@Override
	public void init() throws Exception {
		//for (int i = 0; i < this.getDataSources().size(); i++) {
		//	DataSource dataSource = this.getDataSources().get(i);
		//	this.doMain(dataSource);
		//}
		System.out.println("INIT###############################################");
		//ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initialized ");
		//}
		
		//@Override
		//public void afterPropertiesSet() throws Exception {
		if (!this.isCheckOnStartup()) {
			ApsSystemUtils.getLogger().config(this.getClass().getName() + ": short init executed");
			return;
		}
		InstallationReport report = this.extractReport();
		this.initMasterDatabases(report);
		this.initComponents(report);
		this.updateReport(report);
		ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initializated");
	}
	
	private InstallationReport extractReport() throws ApsSystemException {
		InstallationReport report = null;
		try {
			InstallationReportDAO dao = new InstallationReportDAO();
			DataSource dataSource = (DataSource) super.getBeanFactory().getBean("portDataSource");
			dao.setDataSource(dataSource);
			String xml = dao.loadReport(this.getConfigVersion());
			report = new InstallationReport(xml);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "extractReport");
			throw new ApsSystemException("Error extracting report", t);
		}
		return report;
	}
	
	private void updateReport(InstallationReport report) throws ApsSystemException {
		try {
			InstallationReportDAO dao = new InstallationReportDAO();
			DataSource dataSource = (DataSource) super.getBeanFactory().getBean("portDataSource");
			dao.setDataSource(dataSource);
			dao.saveConfigItem(report.toXml(), this.getConfigVersion());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateReport");
			throw new ApsSystemException("Error updating report", t);
		}
	}
	
	private void initMasterDatabases(InstallationReport report) {
		if (report.isInstallationDone("entandoCore")) {
			ApsSystemUtils.getLogger().info("Core Component installation Already DONE!");
			return;
		}
		try {
			ListableBeanFactory factory = (ListableBeanFactory) super.getBeanFactory();
			String[] dataSourceNames = factory.getBeanNamesForType(DataSource.class);
			for (int i = 0; i < dataSourceNames.length; i++) {
				DataSource dataSource = (DataSource) super.getBeanFactory().getBean(dataSourceNames[i]);
				int result = this.initDatabase(dataSourceNames[i], dataSource);
				if (result == 1) {
					Resource resource = this.getSqlResources().get(dataSourceNames[i]);
					this.valueDatabase(resource, dataSourceNames[i], dataSource);
				}
			}
			report.addReport("entandoCore", new Date());
			ApsSystemUtils.getLogger().info("Core Component installation DONE!");
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "initMasterDatabases");
		}
	}
	
	private void initComponents(InstallationReport report) {
		try {
			ListableBeanFactory factory = (ListableBeanFactory) super.getBeanFactory();
			String[] componentBeansNames = factory.getBeanNamesForType(EntandoComponentConfiguration.class);
			if (null == componentBeansNames || componentBeansNames.length == 0) return;
			List<EntandoComponentConfiguration> addOnBeans = new ArrayList<EntandoComponentConfiguration>();
			for (int i = 0; i < componentBeansNames.length; i++) {
				EntandoComponentConfiguration componentConfiguration = (EntandoComponentConfiguration) super.getBeanFactory().getBean(componentBeansNames[i]);
				if (null != componentConfiguration) addOnBeans.add(componentConfiguration);
			}
			Collections.sort(addOnBeans);
			String[] dataSourceNames = factory.getBeanNamesForType(DataSource.class);
			for (int i = 0; i < addOnBeans.size(); i++) {
				EntandoComponentConfiguration addOnConfiguration = addOnBeans.get(i);
				this.initAddOnPlugin(addOnConfiguration, dataSourceNames, report);
			}
		} catch (Throwable t) {
			t.printStackTrace();
			ApsSystemUtils.logThrowable(t, this, "initComponents");
		}
	}
	
	private void initAddOnPlugin(EntandoComponentConfiguration addOnConfiguration, 
			String[] dataSourceNames, InstallationReport report) {
		if (report.isInstallationDone(addOnConfiguration.getCode())) {
			ApsSystemUtils.getLogger().info("'" + addOnConfiguration.getCode() 
					+ "' Component installation Already DONE!");
			return;
		}
		try {
			System.out.println("*************************** " + addOnConfiguration.getCode());
			for (int j = 0; j < dataSourceNames.length; j++) {
				DataSource dataSource = (DataSource) super.getBeanFactory().getBean(dataSourceNames[j]);
				if (null != addOnConfiguration.getTableMapping()) {
					List<String> tableClassNames = addOnConfiguration.getTableMapping().get(dataSourceNames[j]);
					if (null != tableClassNames && tableClassNames.isEmpty()) {
						this.createPluginTables(dataSourceNames[j], tableClassNames, dataSource);
					}
				}
				if (null != addOnConfiguration.getSqlResources()) {
					Resource resource = addOnConfiguration.getSqlResources().get(dataSourceNames[j]);//this.getSqlResources().get(dataSourceNames[i]);
					this.valueDatabase(resource, dataSourceNames[j], dataSource);
				}
			}
			report.addReport(addOnConfiguration.getCode(), new Date());
			ApsSystemUtils.getLogger().info("'" + addOnConfiguration.getCode() 
					+ "' Component installation DONE!");
		} catch (Throwable t) {
			t.printStackTrace();
			ApsSystemUtils.logThrowable(t, this, "initAddOnPlugin", 
					"Errot initializating AddOn/plugin " + addOnConfiguration.getCode());
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
		} finally {
			// destroy the data source which should close underlying connections
			if (connectionSource != null) {
				try {
					connectionSource.close();
				} catch (SQLException ex) {}
			}
		}
		return globalResult;
	}
	
	private int createPluginTables(String databaseName, List<String> tableClassNames, DataSource dataSource) throws ApsSystemException {
		int globalResult = 0;
		ConnectionSource connectionSource = null;
		try {
			connectionSource = this.createConnectionSource(databaseName, dataSource);
			this.createTables(databaseName, dataSource, tableClassNames, connectionSource);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "createPluginTables", "Error creating plugin tables into db " + databaseName);
		} finally {
			// destroy the data source which should close underlying connections
			if (connectionSource != null) {
				try {
					connectionSource.close();
				} catch (SQLException ex) {}
			}
		}
		return globalResult;
	}
	
	private ConnectionSource createConnectionSource(String databaseName, DataSource dataSource) throws ApsSystemException {
		ConnectionSource connectionSource = null;
		try {
			DatabaseType type = this.getType(dataSource);
			String url = /*dataSource.getUrl();*/this.invokeGetMethod("getUrl", dataSource);
			String username = /*dataSource.getUsername();*/this.invokeGetMethod("getUsername", dataSource);
			String password = /*dataSource.getPassword();*/this.invokeGetMethod("getPassword", dataSource);
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
	
	private int setupDatabase(String databaseName, DataSource dataSource, ConnectionSource connectionSource) throws ApsSystemException {
		int globalResult = 0;
		try {
			DatabaseType type = this.getType(dataSource);
			if (type.equals(DatabaseType.DERBY)) {
				int result = this.initDerbySchema(dataSource);
				if (result == 0) return 0;
			}
			List<String> tableClassNames = this.getTableMapping().get(databaseName);
			if (null == tableClassNames || tableClassNames.isEmpty()) {
				ApsSystemUtils.getLogger().info("No Tables defined for db - " + databaseName);
				return 0;
			}
			globalResult = this.createTables(databaseName, dataSource, tableClassNames, connectionSource);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "setupDatabase", "Error on setup Database");
			throw new ApsSystemException("Error on setup Database", t);
		}
		return globalResult;
	}
	
	private int createTables(String databaseName, DataSource dataSource, 
			List<String> tableClassNames, ConnectionSource connectionSource) throws ApsSystemException {
		int globalResult = 0;
		try {
			DatabaseType type = this.getType(dataSource);
			globalResult = 1;
			for (int i = 0; i < tableClassNames.size(); i++) {
				String tableClassName = tableClassNames.get(i);
				//System.out.println("************** CLASSE " + tableClassName + " *********************");
				int result = 0;
				try {
					Class tableClass = Class.forName(tableClassName);
					result = this.createTable(databaseName, type, tableClass, connectionSource);
					//System.out.println("risultato CREAZIONE TABELLA " + tableClassName + " - " + result);
				} catch (Throwable t) {
					//System.out.println("Inpossibile CREAZIONE TABELLA " + tableClassName);
					//t.printStackTrace();
					ApsSystemUtils.getLogger().info("Error creating table " + databaseName + "/" + tableClassName + " - " + t.getMessage());
				}
				if (result == 0) globalResult = 0;
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "setupDatabase", "Error on setup Database");
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
	
	private int createTable(String databaseName, DatabaseType type, Class tableClass, ConnectionSource connectionSource) throws ApsSystemException {
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
						//dao.executeRaw("SET SCHEMA \"AGILE\"");
						for (int i = 0; i < extensions.length; i++) {
							String query = extensions[i];
							dao.executeRaw(query);
						}
					}
				}
			}
		} catch (SQLException t) {
			//t.printStackTrace();
			//System.out.println("Table creation not allowed - " + logTableName + " - " + t.getMessage());
			ApsSystemUtils.getLogger().info("Table creation not allowed - " + t.getMessage());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "setupDatabase", "Error creating table " + logTableName);
			throw new ApsSystemException("Error creating table " + logTableName, t);
		}
		return result;
	}
	/*
	private void valueDatabase(String databaseName, DataSource dataSource) throws ApsSystemException {
		
	}
	*/
	private void valueDatabase(Resource resource, String databaseName, DataSource dataSource) throws ApsSystemException {
		if (null == resource) {
			ApsSystemUtils.getLogger().severe("No resource script for db " + databaseName);
			return;
		}
		try {
			String script = this.readFile(resource);
            if (null == script) {
				ApsSystemUtils.getLogger().severe("No sql script for db " + databaseName);
				return;
			}
            String[] queries = QueryExtractor.extractQueries(script);
			if (null == queries || queries.length == 0) {
				ApsSystemUtils.getLogger().severe("Script file for db " + databaseName + " void");
				return;
			}
			this.executeQueries(dataSource, queries, true);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "valueDatabase", "Error executing script into db " + databaseName);
			throw new ApsSystemException("Error executing script into db " + databaseName, t);
		}
	}
	
	private String readFile(Resource resource) throws Throwable {
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
			String driverClassName = this.invokeGetMethod("getDriverClassName", dataSource);
			//System.out.println(driverClassName);
			Iterator<Object> typesIter = this.getDatabaseTypeDrivers().keySet().iterator();
			while (typesIter.hasNext()) {
				String typeCode = (String) typesIter.next();
				List<String> driverClassNames = (List<String>) this.getDatabaseTypeDrivers().get(typeCode);
				if (null != driverClassNames && driverClassNames.contains(driverClassName)) {
					typeString = typeCode;
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
	
	protected Map<String, Resource> getSqlResources() {
		return _sqlResources;
	}
	public void setSqlResources(Map<String, Resource> sqlResources) {
		this._sqlResources = sqlResources;
	}
	
	private boolean _checkOnStartup;
	private String _configVersion;
	//private Map<String, String> _databaseTypes;
	private Properties _databaseTypeDrivers;
	private Map<String, List<String>> _tableMapping;
	private Map<String, Resource> _sqlResources;
	
}