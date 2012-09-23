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
 *
 * @author eu
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
		Resource resource = this.getSqlResources().get(databaseName);
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
			String url = dataSource.getUrl(); //this.invokeGetMethod("getUrl", dataSource);
			String username = dataSource.getUsername(); //this.invokeGetMethod("getUsername", dataSource);
			String password = dataSource.getPassword(); //this.invokeGetMethod("getPassword", dataSource);
			// create our data-source for the database
			if (username == null || username.trim().length() == 0) {
				connectionSource = new JdbcConnectionSource(url);//(DATABASE_URL);
			} else {
				connectionSource = new JdbcConnectionSource(url, username, password);//(DATABASE_URL);
			}
			// setup our database and DAOs
			globalResult = setupDatabase(databaseName, connectionSource);
			// read and write some data
			//readWriteData();
			// do a bunch of bulk operations
			//readWriteBunch();
			// show how to use the SelectArg object
			//useSelectArgFeature();
			// show how to use the SelectArg object
			//useTransactions(connectionSource);
			System.out.println("\n\nIt seems to have worked\n\n    RESULYT " + globalResult);
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
			ApsSystemUtils.logThrowable(t, this, "setupDatabase", "Error creating table for class " + Account.class);
			throw new ApsSystemException("Error creating table for class " + Account.class, t);
		}
		return globalResult;
	}
	
	private int createTable(String databaseName, Class tableClass, ConnectionSource connectionSource) throws ApsSystemException {
		IDbCreatorManager.DatabaseType type = IDbCreatorManager.DatabaseType.MYSQL;
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
			t.printStackTrace();
			System.out.println("Table creation not allowed - " + logTableName + " - " + t.getMessage());
			ApsSystemUtils.getLogger().info("Table creation not allowed - " + t.getMessage());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "setupDatabase", "Error creating table " + logTableName);
			throw new ApsSystemException("Error creating table " + logTableName, t);
		}
		return result;
	}
	
	/**
	 * Read and write some example data.
	 */
	/*
	private void readWriteData() throws Exception {
		// create an instance of Account
		String name = "Jim Coakley";
		Account account = new Account(name);

		// persist the account object to the database
		accountDao.create(account);
		int id = account.getId();
		verifyDb(id, account);

		// assign a password
		account.setPassword("_secret");
		// update the database after changing the object
		accountDao.update(account);
		verifyDb(id, account);

		// query for all items in the database
		List<Account> accounts = accountDao.queryForAll();
		
		//TODO
		//assertEquals("Should have found 1 account matching our query", 1, accounts.size());
		
		verifyAccount(account, accounts.get(0));

		// loop through items in the database
		int accountC = 0;
		for (Account account2 : accountDao) {
			verifyAccount(account, account2);
			accountC++;
		}
		//assertEquals("Should have found 1 account in for loop", 1, accountC);

		// construct a query using the QueryBuilder
		QueryBuilder<Account, Integer> statementBuilder = accountDao.queryBuilder();
		// shouldn't find anything: name LIKE 'hello" does not match our account
		statementBuilder.where().like(Account.NAME_FIELD_NAME, "hello");
		accounts = accountDao.query(statementBuilder.prepare());
		//assertEquals("Should not have found any accounts matching our query", 0, accounts.size());

		// should find our account: name LIKE 'Jim%' should match our account
		statementBuilder.where().like(Account.NAME_FIELD_NAME, name.substring(0, 3) + "%");
		accounts = accountDao.query(statementBuilder.prepare());
		//assertEquals("Should have found 1 account matching our query", 1, accounts.size());
		verifyAccount(account, accounts.get(0));

		// delete the account since we are done with it
		accountDao.delete(account);
		// we shouldn't find it now
		//assertNull("account was deleted, shouldn't find any", accountDao.queryForId(id));
	}
	*/
	
	/**
	 * Example of reading and writing a large(r) number of objects.
	 */
	/*
	private void readWriteBunch() throws Exception {
		Map<String, Account> accounts = new HashMap<String, Account>();
		for (int i = 1; i <= 100; i++) {
			String name = Integer.toString(i);
			Account account = new Account(name);
			// persist the account object to the database, it should return 1
			accountDao.create(account);
			accounts.put(name, account);
		}
		
		// query for all items in the database
		List<Account> all = accountDao.queryForAll();
		//assertEquals("Should have found same number of accounts in map", accounts.size(), all.size());
		for (Account account : all) {
			//assertTrue("Should have found account in map", accounts.containsValue(account));
			verifyAccount(accounts.get(account.getName()), account);
		}

		// loop through items in the database
		int accountC = 0;
		for (Account account : accountDao) {
			//assertTrue("Should have found account in map", accounts.containsValue(account));
			verifyAccount(accounts.get(account.getName()), account);
			accountC++;
		}
		//assertEquals("Should have found the right number of accounts in for loop", accounts.size(), accountC);
	}
*/
	/**
	 * Example of created a query with a ? argument using the {@link SelectArg} object. You then can set the value of
	 * this object at a later time.
	 */
	/*
	private void useSelectArgFeature() throws Exception {

		String name1 = "foo";
		String name2 = "bar";
		String name3 = "baz";
		//assertEquals(1, accountDao.create(new Account(name1)));
		//assertEquals(1, accountDao.create(new Account(name2)));
		//assertEquals(1, accountDao.create(new Account(name3)));

		QueryBuilder<Account, Integer> statementBuilder = accountDao.queryBuilder();
		SelectArg selectArg = new SelectArg();
		// build a query with the WHERE clause set to 'name = ?'
		statementBuilder.where().like(Account.NAME_FIELD_NAME, selectArg);
		PreparedQuery<Account> preparedQuery = statementBuilder.prepare();

		// now we can set the select arg (?) and run the query
		selectArg.setValue(name1);
		List<Account> results = accountDao.query(preparedQuery);
		//assertEquals("Should have found 1 account matching our query", 1, results.size());
		//assertEquals(name1, results.get(0).getName());

		selectArg.setValue(name2);
		results = accountDao.query(preparedQuery);
		//assertEquals("Should have found 1 account matching our query", 1, results.size());
		//assertEquals(name2, results.get(0).getName());

		selectArg.setValue(name3);
		results = accountDao.query(preparedQuery);
		//assertEquals("Should have found 1 account matching our query", 1, results.size());
		//assertEquals(name3, results.get(0).getName());
	}
*/
	/**
	 * Example of created a query with a ? argument using the {@link SelectArg} object. You then can set the value of
	 * this object at a later time.
	 */
	/*
	private void useTransactions(ConnectionSource connectionSource) throws Exception {
		String name = "trans1";
		final Account account = new Account(name);
		//assertEquals(1, accountDao.create(account));

		TransactionManager transactionManager = new TransactionManager(connectionSource);
		try {
			// try something in a transaction
			transactionManager.callInTransaction(new Callable<Void>() {
				public Void call() throws Exception {
					// we do the delete
					//assertEquals(1, accountDao.delete(account));
					//assertNull(accountDao.queryForId(account.getId()));
					// but then (as an example) we throw an exception which rolls back the delete
					throw new Exception("We throw to roll back!!");
				}
			});
			//fail("This should have thrown");
		} catch (SQLException e) {
			// expected
		}

		//assertNotNull(accountDao.queryForId(account.getId()));
	}
*/
	/**
	 * Verify that the account stored in the database was the same as the expected object.
	 */
	/*
	private void verifyDb(int id, Account expected) throws SQLException, Exception {
		// make sure we can read it back
		Account account2 = accountDao.queryForId(id);
		if (account2 == null) {
			throw new Exception("Should have found id '" + id + "' in the database");
		}
		verifyAccount(expected, account2);
	}
*/
	/**
	 * Verify that the account is the same as expected.
	 */
	/*
	private static void verifyAccount(Account expected, Account account2) {
		//assertEquals("expected name does not equal account name", expected, account2);
		//assertEquals("expected password does not equal account name", expected.getPassword(), account2.getPassword());
	}
	*/
	
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
	
	private Map<String, List<String>> _tableMapping;
	private Map<String, Resource> _sqlResources;
	
}