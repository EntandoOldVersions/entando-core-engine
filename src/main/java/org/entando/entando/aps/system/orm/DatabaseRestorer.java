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
package org.entando.entando.aps.system.orm;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;

import com.agiletec.aps.util.FileTextReader;
import java.io.*;
import java.util.*;
import javax.sql.DataSource;

import org.entando.entando.aps.system.orm.util.TableDataUtils;
import org.entando.entando.aps.system.orm.util.TableFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;

/**
 * @author E.Santoboni
 */
public class DatabaseRestorer {
	
	protected DatabaseRestorer(String localBackupsFolder, String subFolderName, 
			Map<String, List<String>> entandoTableMapping, List<EntandoComponentConfiguration> components, BeanFactory beanFactory/*, DbInstallerManager manager*/) {
		this.setBeanFactory(beanFactory);
		this.setComponents(components);
		this.setEntandoTableMapping(entandoTableMapping);
		this.setLocalBackupsFolder(localBackupsFolder);
		this.setBackupSubFolder(subFolderName);
	}
	
	protected void dropAndRestoreBackup() throws ApsSystemException {
		try {
			List<EntandoComponentConfiguration> components = this.getComponents();
			int size = components.size();
			for (int i = 0; i < components.size(); i++) {
				EntandoComponentConfiguration componentConfiguration = components.get(size - i - 1);
				this.dropTables(componentConfiguration.getTableMapping());
			}
			this.dropTables(this.getEntandoTableMapping());
			this.restoreBackup();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "dropAndRestoreBackup");
			throw new ApsSystemException("Error while restoring backup", t);
		}
	}
	
	private void dropTables(Map<String, List<String>> tableMapping) throws ApsSystemException {
		if (null == tableMapping) return;
		try {
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			for (int i = 0; i < dataSourceNames.length; i++) {
				String dataSourceName = dataSourceNames[i];
				List<String> tableClasses = tableMapping.get(dataSourceName);
				if (null == tableClasses || tableClasses.isEmpty()) continue;
				DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
				int size = tableClasses.size();
				for (int j = 0; j < tableClasses.size(); j++) {
					String tableClassName = tableClasses.get(size - j - 1);
					Class tableClass = Class.forName(tableClassName);
					String tableName = TableFactory.getTableName(tableClass);
					String[] queries = {"DELETE FROM " + tableName};
					TableDataUtils.executeQueries(dataSource, queries, true);
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "dropTables");
			throw new RuntimeException("Error while dropping tables", t);
		}
	}
	
	protected void restoreBackup() throws ApsSystemException {
		try {
			this.restoreLocalDump(this.getEntandoTableMapping());
			List<EntandoComponentConfiguration> components = this.getComponents();
			for (int i = 0; i < components.size(); i++) {
				EntandoComponentConfiguration componentConfiguration = components.get(i);
				this.restoreLocalDump(componentConfiguration.getTableMapping());
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "restoreBackup");
			throw new ApsSystemException("Error while restoring local backup", t);
		}
	}
	
	private void restoreLocalDump(Map<String, List<String>> tableMapping) throws ApsSystemException {
		if (null == tableMapping) return;
		try {
			StringBuilder folder = new StringBuilder(this.getLocalBackupsFolder())
					.append(this.getBackupSubFolder()).append(File.separator);
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
					String fileName = folder.toString() + dataSourceName + File.separator + tableName + ".sql";
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
	
	protected BeanFactory getBeanFactory() {
		return _beanFactory;
	}
	protected void setBeanFactory(BeanFactory beanFactory) {
		this._beanFactory = beanFactory;
	}
	
	protected String getLocalBackupsFolder() {
		return _localBackupsFolder;
	}
	protected void setLocalBackupsFolder(String localBackupsFolder) {
		this._localBackupsFolder = localBackupsFolder;
	}
	
	protected String getBackupSubFolder() {
		return _backupSubFolder;
	}
	protected void setBackupSubFolder(String backupSubFolder) {
		this._backupSubFolder = backupSubFolder;
	}
	
	protected Map<String, List<String>> getEntandoTableMapping() {
		return _entandoTableMapping;
	}
	protected void setEntandoTableMapping(Map<String, List<String>> entandoTableMapping) {
		this._entandoTableMapping = entandoTableMapping;
	}
	
	protected List<EntandoComponentConfiguration> getComponents() {
		return _components;
	}
	protected void setComponents(List<EntandoComponentConfiguration> components) {
		this._components = components;
	}
	
	private String _localBackupsFolder;
	private String _backupSubFolder;
	
	private BeanFactory _beanFactory;
	private Map<String, List<String>> _entandoTableMapping;
	private List<EntandoComponentConfiguration> _components;
	
}
