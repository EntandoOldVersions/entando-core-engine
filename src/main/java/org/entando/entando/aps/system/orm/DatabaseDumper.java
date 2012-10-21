/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import org.entando.entando.aps.system.orm.model.report.DatabaseDump;

import org.entando.entando.aps.system.orm.model.TableDumpResult;
import org.entando.entando.aps.system.orm.util.TableDataUtils;
import org.entando.entando.aps.system.orm.util.TableFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;

/**
 * @author E.Santoboni
 */
public class DatabaseDumper {
	
	protected DatabaseDumper(String localBackupFolder, Map<String, List<String>> entandoTableMapping, 
			List<EntandoComponentConfiguration> components, BeanFactory beanFactory, DbInstallerManager manager) {
		this.setBeanFactory(beanFactory);
		this.setComponents(components);
		this.setEntandoTableMapping(entandoTableMapping);
		this.setLocalBackupFolder(localBackupFolder);
		this.setManager(manager);
	}
	
	protected void createBackup() throws ApsSystemException {
		try {
			this.getManager().setStatus(DbInstallerManager.STATUS_DUMPIMG_IN_PROGRESS);
			long start = System.currentTimeMillis();
			List<EntandoComponentConfiguration> components = this.getComponents();
			for (int i = 0; i < components.size(); i++) {
				EntandoComponentConfiguration componentConfiguration = components.get(i);
				this.createBackup(componentConfiguration.getTableMapping());
			}
			this.createBackup(this.getEntandoTableMapping());
			long time = System.currentTimeMillis() - start;
			this.getReport().setRequiredTime(time);
			this.getReport().setDate(new Date());
			this.save(DbInstallerManager.DUMP_REPORT_FILE_NAME, 
					this.getLocalBackupFolder(), this.getReport().toXml());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "createBackup");
			throw new ApsSystemException("Error while creating backup", t);
		} finally {
			this.getManager().setStatus(DbInstallerManager.STATUS_READY);
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
			StringBuilder dirName = new StringBuilder(this.getLocalBackupFolder());
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
	
	protected String getLocalBackupFolder() {
		return _localBackupFolder;
	}
	protected void setLocalBackupFolder(String localBackupFolder) {
		this._localBackupFolder = localBackupFolder;
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
	
	protected DatabaseDump getReport() {
		return _report;
	}
	protected void setReport(DatabaseDump report) {
		this._report = report;
	}
	
	protected DbInstallerManager getManager() {
		return _manager;
	}
	protected void setManager(DbInstallerManager manager) {
		this._manager = manager;
	}
	
	private String _localBackupFolder;
	private BeanFactory _beanFactory;
	private Map<String, List<String>> _entandoTableMapping;
	private List<EntandoComponentConfiguration> _components;
	private DbInstallerManager _manager;
	
	private DatabaseDump _report = new DatabaseDump();
	
}
