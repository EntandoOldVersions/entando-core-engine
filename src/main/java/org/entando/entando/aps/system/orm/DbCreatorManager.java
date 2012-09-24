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
import com.j256.ormlite.db.DerbyClientServerDatabaseType;
import com.j256.ormlite.db.MysqlDatabaseType;
import com.j256.ormlite.db.OracleDatabaseType;
import com.j256.ormlite.db.PostgresDatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.InputStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.io.Resource;

/**
 * @author E.Santoboni
 */
public class DbCreatorManager extends AbstractService implements InitializingBean, IDbCreatorManager {
	
	@Override
	public void init() throws Exception {
		//for (int i = 0; i < this.getDataSources().size(); i++) {
		//	DataSource dataSource = this.getDataSources().get(i);
		//	this.doMain(dataSource);
		//}
		System.err.println("INIT###############################################à");
		ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initialized ");
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (!this.isCheckOnStartup()) return;
		System.err.println("****************INITTTTTTTTT*********** ");
		ListableBeanFactory factory = (ListableBeanFactory) super.getBeanFactory();
		String[] dataSourceNames = factory.getBeanNamesForType(BasicDataSource.class);
		for (int i = 0; i < dataSourceNames.length; i++) {
			BasicDataSource dataSource = (BasicDataSource) super.getBeanFactory().getBean(dataSourceNames[i]);
			int result = this.initDatabase(dataSourceNames[i], dataSource);
			System.err.println("****************aaaaaaaaa*********** " + result);
			if (result == 1) {
				this.valueDatabase(dataSourceNames[i], dataSource);
			}
		}
	}
	
	private void valueDatabase(String databaseName, BasicDataSource dataSource) throws ApsSystemException {
		DatabaseType type = this.getType(databaseName);
		Resource[] resources = this.getSqlResources().get(databaseName);
		Resource resource = null;
		for (int i = 0; i < resources.length; i++) {
            String name = resources[i].getFilename();
			if (name.toUpperCase().startsWith(type.toString().toUpperCase())) {
				resource = resources[i];
				break;
			}
		}
		if (null == resource) {
			ApsSystemUtils.getLogger().severe("No sql script for db " + databaseName);
			return;
		}
		Connection conn = null;
        PreparedStatement stat = null;
		ResultSet res = null;
		try {
			InputStream is = resource.getInputStream();
			if (null == is) {
				return;
			}
            String script = FileTextReader.getText(is);
			conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            stat = conn.prepareStatement(script);
			stat.execute();
			conn.commit();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "valueDatabase", "Error executing script into db " + databaseName);
			throw new ApsSystemException("Error executing script into db " + databaseName, t);
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
	
	private int initDatabase(String databaseName, BasicDataSource dataSource) throws ApsSystemException {
		int globalResult = 0;
		ConnectionSource connectionSource = null;
		try {
			DatabaseType type = this.getType(databaseName);
			String url = dataSource.getUrl(); //this.invokeGetMethod("getUrl", dataSource);
			String username = dataSource.getUsername(); //this.invokeGetMethod("getUsername", dataSource);
			String password = dataSource.getPassword(); //this.invokeGetMethod("getPassword", dataSource);
			// create our data-source for the database
			if (type.equals(DatabaseType.DERBY)) {
				connectionSource = new JdbcConnectionSource(url, new DerbyClientServerDatabaseType());//(DATABASE_URL);
			} else {
				com.j256.ormlite.db.DatabaseType dataType = null;
				if (type.equals(DatabaseType.POSTGRESQL)) {
					dataType = new PostgresDatabaseType();
				} else if (type.equals(DatabaseType.MYSQL)) {
					dataType = new MysqlDatabaseType();
				} else if (type.equals(DatabaseType.ORACLE)) {
					dataType = new OracleDatabaseType();
				}
				connectionSource = new JdbcConnectionSource(url, username, password, dataType);//(DATABASE_URL);
			}
			globalResult = this.setupDatabase(databaseName, connectionSource);
		} catch (Throwable t) {
			throw new ApsSystemException("Error", t);
		} finally {
			// destroy the data source which should close underlying connections
			if (connectionSource != null) {
				try {
					connectionSource.close();
				} catch (SQLException ex) {
					Logger.getLogger(DbCreatorManager.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		return globalResult;
	}
	
	/**
	 * Setup our database and DAOs
	 */
	private int setupDatabase(String databaseName, ConnectionSource connectionSource) throws ApsSystemException {
		int globalResult = 0;
		try {
			List<String> tableClassNames = this.getTableMapping().get(databaseName);
			if (null == tableClassNames || tableClassNames.isEmpty()) {
				ApsSystemUtils.getLogger().severe("No Tables defined for db - " + databaseName);
				return 0;
			}
			globalResult = 1;
			for (int i = 0; i < tableClassNames.size(); i++) {
				String tableClassName = tableClassNames.get(i);
				System.out.println("************** CLASSE " + tableClassName + " *********************");
				int result = 0;
				try {
					Class tableClass = Class.forName(tableClassName);
					result = this.createTable(databaseName, tableClass, connectionSource);
				} catch (Throwable t) {
					System.out.println("ERRORE CREAZIONE TABELLA " + tableClassName);
					//t.printStackTrace();
					ApsSystemUtils.logThrowable(t, this, "setupDatabase");
				}
				if (result == 0) globalResult = 0;
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "setupDatabase", "Error on setup Database");
			throw new ApsSystemException("Error on setup Database", t);
		}
		return globalResult;
	}
	
	private int createTable(String databaseName, Class tableClass, ConnectionSource connectionSource) throws ApsSystemException {
		DatabaseType type = this.getType(databaseName);
		int result = 0;
		String logTableName = databaseName.toLowerCase() + "/" + tableClass.getSimpleName().toLowerCase();
		System.out.println("CREAZIONE TABELLA " + logTableName);
		try {
			result = TableUtils.createTableIfNotExists(connectionSource, tableClass);
			if (result > 0) {
				System.out.println("Created table - " + logTableName);
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
		} catch (SQLException t) {
			//t.printStackTrace();
			System.out.println("Table creation not allowed - " + logTableName + " - " + t.getMessage());
			ApsSystemUtils.getLogger().info("Table creation not allowed - " + t.getMessage());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "setupDatabase", "Error creating table " + logTableName);
			throw new ApsSystemException("Error creating table " + logTableName, t);
		}
		return result;
	}
	
	protected DatabaseType getType(String databaseName) {
		String typeString = this.getDatabaseTypes().get(databaseName);
		if (null == typeString) return DatabaseType.DERBY;
		DatabaseType type = null;
		try {
			type = Enum.valueOf(DatabaseType.class, typeString.toUpperCase());
		} catch (Exception e) {
			ApsSystemUtils.getLogger().severe("Invalid type for db " + databaseName + " - '" + typeString + "'");
			type = DatabaseType.DERBY;
		}
		return type;
	}
	
	protected Map<String, String> getDatabaseTypes() {
		return _databaseTypes;
	}
	public void setDatabaseTypes(Map<String, String> databaseTypes) {
		this._databaseTypes = databaseTypes;
	}
	
	protected boolean isCheckOnStartup() {
		return _checkOnStartup;
	}
	public void setCheckOnStartup(boolean checkOnStartup) {
		this._checkOnStartup = checkOnStartup;
	}
	
	protected Map<String, List<String>> getTableMapping() {
		return _tableMapping;
	}
	public void setTableMapping(Map<String, List<String>> tableMapping) {
		this._tableMapping = tableMapping;
	}
	
	protected Map<String, Resource[]> getSqlResources() {
		return _sqlResources;
	}
	public void setSqlResources(Map<String, Resource[]> sqlResources) {
		System.out.println(sqlResources);
		this._sqlResources = sqlResources;
	}
	
	private boolean _checkOnStartup;
	
	private Map<String, String> _databaseTypes;
	private Map<String, List<String>> _tableMapping;
	private Map<String, Resource[]> _sqlResources;
	
}