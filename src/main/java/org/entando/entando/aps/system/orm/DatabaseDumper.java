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
import com.agiletec.aps.util.DateConverter;

import java.io.*;
import java.util.*;

import javax.sql.DataSource;
import org.entando.entando.aps.system.orm.model.DatabaseDumpReport;

import org.entando.entando.aps.system.orm.model.TableDumpResult;
import org.entando.entando.aps.system.orm.model.SystemInstallationReport;
import org.entando.entando.aps.system.orm.util.TableDataUtils;
import org.entando.entando.aps.system.orm.util.TableFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;

/**
 * @author E.Santoboni
 */
public class DatabaseDumper {
	
	protected DatabaseDumper(String localBackupsFolder, SystemInstallationReport installationReport, 
			Map<String, List<String>> entandoTableMapping, List<EntandoComponentConfiguration> components, BeanFactory beanFactory/*, DbInstallerManager manager*/) {
		this.setBeanFactory(beanFactory);
		this.setComponents(components);
		this.setEntandoTableMapping(entandoTableMapping);
		this.setLocalBackupsFolder(localBackupsFolder);
		this.setReport(new DatabaseDumpReport(installationReport));
	}
	
	protected void createBackup() throws ApsSystemException {
		try {
			long start = System.currentTimeMillis();
			String subFolder = DateConverter.getFormattedDate(new Date(), "yyyyMMddHHmmss");
			this.setBackupSubFolder(subFolder);
			this.getReport().setSubFolderName(subFolder);
			List<EntandoComponentConfiguration> components = this.getComponents();
			for (int i = 0; i < components.size(); i++) {
				EntandoComponentConfiguration componentConfiguration = components.get(i);
				this.createBackup(componentConfiguration.getTableMapping());
			}
			this.createBackup(this.getEntandoTableMapping());
			long time = System.currentTimeMillis() - start;
			this.getReport().setRequiredTime(time);
			this.getReport().setDate(new Date());
			StringBuilder reportFolder = new StringBuilder(this.getLocalBackupsFolder());
			if (null != this.getBackupSubFolder()) {
				reportFolder.append(this.getBackupSubFolder()).append(File.separator);
			}
			this.save(DbInstallerManager.DUMP_REPORT_FILE_NAME, 
					reportFolder.toString(), this.getReport().toXml());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "createBackup");
			throw new ApsSystemException("Error while creating backup", t);
		}
	}
	
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
					this.dumpTableData(tableName, dataSourceName, dataSource);
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "createBackup");
			throw new ApsSystemException("Error while creating backup", t);
		}
	}
	
	protected void dumpTableData(String tableName, String dataSourceName, DataSource dataSource) throws ApsSystemException {
		try {
			TableDumpResult tableDumpResult = TableDataUtils.dumpTable(dataSource, tableName);
			this.getReport().addTableReport(dataSourceName, tableDumpResult);
			StringBuilder dirName = new StringBuilder(this.getLocalBackupsFolder());
			if (null != this.getBackupSubFolder()) {
				dirName.append(this.getBackupSubFolder()).append(File.separator);
			}
			dirName.append(dataSourceName).append(File.separator);
			this.save(tableName + ".sql", dirName.toString(), tableDumpResult.getSqlDump());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "dumpTableData");
			throw new ApsSystemException("Error dumping table '" + tableName + "' - datasource '" + dataSourceName + "'", t);
		}
	}
	
	protected void save(String filename, String folder, String content) {
		FileOutputStream outStream = null;
		InputStream is = null;
		try {
			File dir = new File(folder);
			if (!dir.exists() || !dir.isDirectory()) {
				dir.mkdirs();
			}
			String filePath = folder + filename;
			is = new ByteArrayInputStream(content.getBytes());
			byte[] buffer = new byte[1024];
            int length = -1;
            outStream = new FileOutputStream(filePath);
            while ((length = is.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
                outStream.flush();
            }
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "save");
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
	
	protected DatabaseDumpReport getReport() {
		return _report;
	}
	protected void setReport(DatabaseDumpReport report) {
		this._report = report;
	}
	
	private String _localBackupsFolder;
	private String _backupSubFolder;
	
	private BeanFactory _beanFactory;
	private Map<String, List<String>> _entandoTableMapping;
	private List<EntandoComponentConfiguration> _components;
	private DatabaseDumpReport _report;
	
}