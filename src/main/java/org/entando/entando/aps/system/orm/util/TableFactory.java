/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.util;

import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.MysqlDatabaseType;
import com.j256.ormlite.db.OracleDatabaseType;
import com.j256.ormlite.db.PostgresDatabaseType;
import com.j256.ormlite.db.SqlServerDatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.lang.reflect.Method;

import java.sql.SQLException;
import java.util.ArrayList;
import javax.sql.DataSource;

import org.entando.entando.aps.system.orm.model.ExtendedColumnDefinition;
import org.entando.entando.aps.system.orm.IDbInstallerManager;
import org.entando.entando.aps.system.orm.model.report.SystemInstallation;
import org.entando.entando.aps.system.orm.model.report.DatabaseInstallation;

/**
 * @author E.Santoboni
 */
public class TableFactory {
	
	public TableFactory(String databaseName, DataSource dataSource, IDbInstallerManager.DatabaseType type) {
		this.setDataSource(dataSource);
		this.setDatabaseName(databaseName);
		this.setType(type);
	}
	
	public void createTables(List<String> tableClassNames, DatabaseInstallation schemaReport) throws ApsSystemException {
		ConnectionSource connectionSource = null;
		try {
			connectionSource = this.createConnectionSource();
			this.createTables(tableClassNames, connectionSource, schemaReport);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "createTables", "Error creating tables into db " + this.getDatabaseName());
			throw new ApsSystemException("Error creating tables to db " + this.getDatabaseName(), t);
		} finally {
			if (connectionSource != null) {
				try {
					connectionSource.close();
				} catch (SQLException ex) {}
			}
		}
	}
	
	private ConnectionSource createConnectionSource() throws ApsSystemException {
		ConnectionSource connectionSource = null;
		try {
			DataSource dataSource = this.getDataSource();
			IDbInstallerManager.DatabaseType type = this.getType();
			String url = this.invokeGetMethod("getUrl", dataSource);
			String username = this.invokeGetMethod("getUsername", dataSource);
			String password = this.invokeGetMethod("getPassword", dataSource);
			com.j256.ormlite.db.DatabaseType dataType = null;
			if (type.equals(IDbInstallerManager.DatabaseType.DERBY)) {
				dataType = new ApsDerbyEmbeddedDatabaseType();
				url = url + ";user=" + username + ";password=" + password;
				connectionSource = new JdbcConnectionSource(url, dataType);
			} else {
				if (type.equals(IDbInstallerManager.DatabaseType.POSTGRESQL)) {
					dataType = new PostgresDatabaseType();
				} else if (type.equals(IDbInstallerManager.DatabaseType.MYSQL)) {
					dataType = new MysqlDatabaseType();
				} else if (type.equals(IDbInstallerManager.DatabaseType.ORACLE)) {
					dataType = new OracleDatabaseType();
				} else if (type.equals(IDbInstallerManager.DatabaseType.SQLSERVER)) {
					dataType = new SqlServerDatabaseType();
				} 
				connectionSource = new JdbcConnectionSource(url, username, password, dataType);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "createConnectionSource", "Error creating connectionSource to db " + this.getDatabaseName());
			throw new ApsSystemException("Error creating connectionSource to db " + this.getDatabaseName(), t);
		}
		return connectionSource;
	}
	
	private String invokeGetMethod(String methodName, DataSource dataSource) throws Throwable {
		Method method = dataSource.getClass().getDeclaredMethod(methodName);
		return (String) method.invoke(dataSource);
	}
	
	private void createTables(List<String> tableClassNames, 
			ConnectionSource connectionSource, DatabaseInstallation schemaReport) throws ApsSystemException {
		try {
			List<String> tables = schemaReport.getDatabaseTables().get(this.getDatabaseName());
			if (null == tables) {
				tables = new ArrayList<String>();
				schemaReport.getDatabaseTables().put(this.getDatabaseName(), tables);
			}
			for (int i = 0; i < tableClassNames.size(); i++) {
				String tableClassName = tableClassNames.get(i);
				Class tableClass = Class.forName(tableClassName);
				String tableName = getTableName(tableClass);
				if (tables.contains(tableName)) {
					continue;
				}
				try {
					System.out.print("TABLE '" + tableName + "' - Installation... ");
					this.createTable(tableClass, connectionSource);
					//if (!tables.contains(tableName)) {
					tables.add(tableName);
					System.out.println("DONE!!!");
					//}
				} catch (Throwable t) {
					schemaReport.getDatabaseStatus().put(this.getDatabaseName(), SystemInstallation.Status.INCOMPLETE);
					String message = "Error creating table " + this.getDatabaseName() + "/" + tableClassName + " - " + t.getMessage();
					ApsSystemUtils.logThrowable(t, this, "createTables", message);
					throw new ApsSystemException(message, t);
				}
			}
		} catch (Throwable t) {
			schemaReport.getDatabaseStatus().put(this.getDatabaseName(), SystemInstallation.Status.INCOMPLETE);
			ApsSystemUtils.logThrowable(t, this, "setupDatabase", "Error on setup Database - " + this.getDatabaseName());
			throw new ApsSystemException("Error on setup Database", t);
		}
	}
	
	private void createTable(Class tableClass, ConnectionSource connectionSource) throws Throwable {
		int result = 0;
		String logTableName = this.getDatabaseName() + "/" + tableClass.getSimpleName().toLowerCase();
		try {
			result = TableUtils.createTable(connectionSource, tableClass);
			if (result > 0) {
				ApsSystemUtils.getLogger().info("Created table - " + logTableName);
				Object tableModel = tableClass.newInstance();
				if (tableModel instanceof ExtendedColumnDefinition) {
					String[] extensions = ((ExtendedColumnDefinition) tableModel).extensions(this.getType());
					if (null != extensions && extensions.length > 0) {
						Dao dao = DaoManager.createDao(connectionSource, tableClass);
						for (int i = 0; i < extensions.length; i++) {
							String query = extensions[i];
							dao.executeRaw(query);
						}
					}
				}
			} else {
				throw new RuntimeException("Error creating table from class " + logTableName);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "setupDatabase", "Error creating table " + logTableName + " - " + t.getMessage());
			if (result > 0) {
				TableUtils.dropTable(connectionSource, tableClass, true);
			}
			throw new ApsSystemException("Error creating table " + logTableName, t);
		}
	}
	
	public static String getTableName(Class tableClass) {
		DatabaseTable tableAnnotation = (DatabaseTable) tableClass.getAnnotation(DatabaseTable.class);
		return tableAnnotation.tableName();
	}
	
	protected DataSource getDataSource() {
		return _dataSource;
	}
	protected void setDataSource(DataSource dataSource) {
		this._dataSource = dataSource;
	}
	
	protected String getDatabaseName() {
		return _databaseName;
	}
	protected void setDatabaseName(String databaseName) {
		this._databaseName = databaseName;
	}
	
	protected IDbInstallerManager.DatabaseType getType() {
		return _type;
	}
	protected void setType(IDbInstallerManager.DatabaseType type) {
		this._type = type;
	}
	
	private String _databaseName;
	private DataSource _dataSource;
	private IDbInstallerManager.DatabaseType _type;
	
}
