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
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.lang.reflect.Method;

import java.sql.SQLException;
import java.util.ArrayList;
import javax.sql.DataSource;

import org.entando.entando.aps.system.orm.ExtendedColumnDefinition;
import org.entando.entando.aps.system.orm.IDbInstallerManager;
import org.entando.entando.aps.system.orm.model.InstallationReport;
import org.entando.entando.aps.system.orm.model.SchemaReport;

/**
 * @author eu
 */
public class TableFactory {
	
	public TableFactory(String databaseName, DataSource dataSource, IDbInstallerManager.DatabaseType type) {
		this.setDataSource(dataSource);
		this.setDatabaseName(databaseName);
		this.setType(type);
	}
	
	public void createTables(List<String> tableClassNames, SchemaReport schemaReport) throws ApsSystemException {
		ConnectionSource connectionSource = null;
		try {
			connectionSource = this.createConnectionSource();
			this.createTables(tableClassNames, connectionSource, schemaReport);
		} catch (Throwable t) {
			//this.updateReport(report);
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
			if (type.equals(org.entando.entando.aps.system.orm.IDbInstallerManager.DatabaseType.DERBY)) {
				dataType = new ApsDerbyEmbeddedDatabaseType();
				url = url + ";user=" + username + ";password=" + password;
				connectionSource = new JdbcConnectionSource(url, dataType);
			} else {
				if (type.equals(org.entando.entando.aps.system.orm.IDbInstallerManager.DatabaseType.POSTGRESQL)) {
					dataType = new PostgresDatabaseType();
				} else if (type.equals(org.entando.entando.aps.system.orm.IDbInstallerManager.DatabaseType.MYSQL)) {
					dataType = new MysqlDatabaseType();
				} else if (type.equals(org.entando.entando.aps.system.orm.IDbInstallerManager.DatabaseType.ORACLE)) {
					dataType = new OracleDatabaseType();
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
			ConnectionSource connectionSource, SchemaReport schemaReport) throws ApsSystemException {
		List<String> tables = null;
		try {
			//TABELLE DI COMPONENTI 
			tables = schemaReport.getDatabaseTables().get(this.getDatabaseName());
			if (null == tables) {
				tables = new ArrayList<String>();
				schemaReport.getDatabaseTables().put(this.getDatabaseName(), tables);
			}
			for (int i = 0; i < tableClassNames.size(); i++) {
				String tableClassName = tableClassNames.get(i);
				if (tables.contains(tableClassName)) {
					continue;
				}
				//System.out.println("************** CLASSE " + tableClassName + " *********************");
				try {
					System.out.println("TABLE '" + tableClassName + "' - INSTALLATION");
					Class tableClass = Class.forName(tableClassName);
					this.createTable(tableClass, connectionSource);
					if (!tables.contains(tableClassName)) {
						tables.add(tableClassName);
						System.out.println("TABLE '" + tableClassName + "' installed");
						//this.updateReport(report);
					}
					//System.out.println("risultato CREAZIONE TABELLA " + tableClassName + " - " + result);
				} catch (Throwable t) {
					schemaReport.getDatabaseStatus().put(this.getDatabaseName(), InstallationReport.Status.INCOMPLETE);
					//System.out.println("Impossibile CREARE TABELLA " + tableClassName);
					//t.printStackTrace();
					String message = "Error creating table " + this.getDatabaseName() + "/" + tableClassName + " - " + t.getMessage();
					ApsSystemUtils.logThrowable(t, this, "createTables", message);
					throw new ApsSystemException(message, t);
				}
			}
		} catch (Throwable t) {
			schemaReport.getDatabaseStatus().put(this.getDatabaseName(), InstallationReport.Status.INCOMPLETE);
			ApsSystemUtils.logThrowable(t, this, "setupDatabase", "Error on setup Database - " + this.getDatabaseName());
			throw new ApsSystemException("Error on setup Database", t);
		}
	}
	
	private void createTable(Class tableClass, ConnectionSource connectionSource) throws Throwable {
		int result = 0;
		String logTableName = this.getDatabaseName() + "/" + tableClass.getSimpleName().toLowerCase();
		//System.out.println("CREAZIONE TABELLA " + logTableName);
		try {
			result = TableUtils.createTable(connectionSource, tableClass);
			if (result > 0) {
				//System.out.println("Created table - " + logTableName);
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
