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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.dbcp.BasicDataSource;
import org.entando.entando.aps.system.orm.util.ApsDerbyEmbeddedDatabaseType;
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
		//System.out.println("INIT###############################################");
		ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initialized ");
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (!this.isCheckOnStartup()) return;
		//System.out.println("****************INITTTTTTTTT*********** ");
		ListableBeanFactory factory = (ListableBeanFactory) super.getBeanFactory();
		String[] dataSourceNames = factory.getBeanNamesForType(BasicDataSource.class);
		for (int i = 0; i < dataSourceNames.length; i++) {
			BasicDataSource dataSource = (BasicDataSource) super.getBeanFactory().getBean(dataSourceNames[i]);
			int result = this.initDatabase(dataSourceNames[i], dataSource);
			System.out.println("****************aaaaaaaaa*********** " + result);
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
		try {
			String script = this.readFile(resource);
            if (null == script) {
				return;
			}
            String[] lines = readLines(script);
            if (lines.length == 0) return;
			String[] queries = extractQueries(lines);
			if (queries.length == 0) return;
			/*
			if (type.equals(DatabaseType.DERBY)) {
				String[] schemaQueries = new String[] {"SET SCHEMA \"" + dataSource.getUsername().toUpperCase() + "\""};
				this.executeQueries(dataSource, schemaQueries);
				System.out.println("************** ESEGUITO *********************");
			}
			*/
			this.executeQueries(dataSource, queries);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "valueDatabase", "Error executing script into db " + databaseName);
			throw new ApsSystemException("Error executing script into db " + databaseName, t);
		}
	}
	
	private void executeQueries(BasicDataSource dataSource, String[] queries) throws ApsSystemException {
		if (queries.length == 0) return;
		Connection conn = null;
        PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = dataSource.getConnection();
            conn.setAutoCommit(false);
			for (int i = 0; i < queries.length; i++) {
				//System.out.println(queries[i]);
				//System.out.println("******************");
				stat = conn.prepareStatement(queries[i]);
				stat.execute();
			}
			conn.commit();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "valueDatabase", "Error executing script into db " + dataSource.getUrl());
			throw new ApsSystemException("Error executing script into db " + dataSource.getUrl(), t);
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
	
	//*************************************
	
	private String readFile(Resource resource) throws Throwable {
		InputStream is = null;
		String text = null;
		try {
			is = resource.getInputStream();
			if (null == is) {
				return null;
			}
			text = FileTextReader.getText(is);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != is) is.close();
		}
		return text;
	}
	
	private String[] readLines(String text) throws Throwable {
		InputStream is = null;
		String[] lines = new String[0];
		try {
			is = new ByteArrayInputStream(text.getBytes());
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				lines = addChild(lines, strLine);
				//System.out.println(strLine);
				//System.out.println("------------------------------------------------------");
			}
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != is) is.close();
		}
		return lines;
	}
	
	public String[] extractQueries(String[] lines) {
		String[] queries = new String[0];
		StringBuilder builder = new StringBuilder();
		int length = lines.length;
		String lastValuedLine = null;
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			builder.append(line);
			if (line.trim().length() > 0) lastValuedLine = line;
			if ((i+1) < length 
					&& lines[i+1].toLowerCase().trim().startsWith("insert into") 
					&& (null != lastValuedLine && lastValuedLine.toLowerCase().trim().endsWith(");"))) {
				String query = this.purgeQuery(builder);
				queries = this.addChild(queries, query);
				lastValuedLine = null;
			} else {
				//if (lines[i].trim().length() > 0) prev = lines[i];
				builder.append("\n");
			}
		}
		String query = this.purgeQuery(builder);
		queries = this.addChild(queries, query);
		return queries;
	}
	
	private String purgeQuery(StringBuilder builder) {
		String query = builder.toString().trim();
		query = query.substring(0, query.length()-1);//cut ";"
		//query = this.insertQuotes(query);//inser quotes for column names into statement
		builder.delete(0, builder.length());
		return query;
	}
	
	private String insertQuotes(String query) {
		int start = query.indexOf("(");
		int end = query.indexOf(")");
		String section = query.substring(start+1, end);
		//System.out.println(section);
		String[] fields = section.split(",");
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < fields.length; i++) {
			if (i > 0) buffer.append(", ");
			String field = fields[i].trim();
			field = field.replaceAll("\"", "");
			buffer.append("\"").append(field.trim()).append("\"");
		}
		//System.out.println(buffer.toString());
		String newQuery = query.replaceFirst(section, buffer.toString());
		//System.out.println(newQuery);
		// TODO Auto-generated method stub
		return newQuery;
	}
	
	public String[] addChild(String[] lines, String newLine) {
		int len = lines.length;
		String[] newChildren = new String[len + 1];
		for (int i = 0; i < len; i++) {
			newChildren[i] = lines[i];
		}
		newChildren[len] = newLine;
		return newChildren;
	}
	
	//******************************************************
	
	private int initDatabase(String databaseName, BasicDataSource dataSource) throws ApsSystemException {
		int globalResult = 0;
		ConnectionSource connectionSource = null;
		try {
			DatabaseType type = this.getType(databaseName);
			String url = dataSource.getUrl(); //this.invokeGetMethod("getUrl", dataSource);
			String username = dataSource.getUsername(); //this.invokeGetMethod("getUsername", dataSource);
			String password = dataSource.getPassword(); //this.invokeGetMethod("getPassword", dataSource);
			// create our data-source for the database
			com.j256.ormlite.db.DatabaseType dataType = null;
			//System.out.println("AAAAAAaaaaaaaaaaAAAAAAAAAAa " + type);
			if (type.equals(DatabaseType.DERBY)) {
				dataType = new ApsDerbyEmbeddedDatabaseType();
				System.out.println("ESCAPE " + ((DerbyEmbeddedDatabaseType) dataType).isEntityNamesMustBeUpCase());
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
			globalResult = this.setupDatabase(databaseName, dataSource, connectionSource);
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
	private int setupDatabase(String databaseName, BasicDataSource dataSource, ConnectionSource connectionSource) throws ApsSystemException {
		int globalResult = 0;
		try {
			DatabaseType type = this.getType(databaseName);
			if (type.equals(DatabaseType.DERBY)) {
				String[] queries = new String[] {"CREATE SCHEMA " + dataSource.getUsername().toUpperCase(), "SET SCHEMA \"" + dataSource.getUsername().toUpperCase() + "\""};
				this.executeQueries(dataSource, queries);
				System.out.println("************** ESEGUITO *********************");
			}
			List<String> tableClassNames = this.getTableMapping().get(databaseName);
			if (null == tableClassNames || tableClassNames.isEmpty()) {
				ApsSystemUtils.getLogger().info("No Tables defined for db - " + databaseName);
				return 0;
			}
			globalResult = 1;
			for (int i = 0; i < tableClassNames.size(); i++) {
				String tableClassName = tableClassNames.get(i);
				//System.out.println("************** CLASSE " + tableClassName + " *********************");
				int result = 0;
				try {
					Class tableClass = Class.forName(tableClassName);
					result = this.createTable(databaseName, tableClass, connectionSource);
					//System.out.println("risultato CREAZIONE TABELLA " + tableClassName + " - " + result);
				} catch (Throwable t) {
					//System.out.println("Inpossibile CREAZIONE TABELLA " + tableClassName);
					//t.printStackTrace();
					ApsSystemUtils.getLogger().info("Inpossibile CREAZIONE TABELLA " + tableClassName + " - " + t.getMessage());
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
			ApsSystemUtils.getLogger().info("Table creation not allowed - " + t.getNextException().getMessage());
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
		this._sqlResources = sqlResources;
	}
	
	private boolean _checkOnStartup;
	
	private Map<String, String> _databaseTypes;
	private Map<String, List<String>> _tableMapping;
	private Map<String, Resource[]> _sqlResources;
	
}