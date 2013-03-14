/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
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
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package org.entando.entando.aps.system.init;

import org.entando.entando.aps.system.init.model.Component;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.util.DateConverter;

import java.io.*;
import java.util.*;

import javax.sql.DataSource;
import org.entando.entando.aps.system.init.model.DataSourceDumpReport;

import org.entando.entando.aps.system.init.model.TableDumpResult;
import org.entando.entando.aps.system.init.model.SystemInstallationReport;
import org.entando.entando.aps.system.init.util.TableDataUtils;
import org.entando.entando.aps.system.init.util.TableFactory;

/**
 * @author E.Santoboni
 */
public class DatabaseDumper extends AbstractDatabaseUtils {
	
	protected void createBackup(AbstractInitializerManager.Environment environment, SystemInstallationReport installationReport) throws ApsSystemException {
		try {
			DataSourceDumpReport report = new DataSourceDumpReport(installationReport);
			long start = System.currentTimeMillis();
			String backupSubFolder = (AbstractInitializerManager.Environment.develop.equals(environment)) ? 
					environment.toString() : DateConverter.getFormattedDate(new Date(), "yyyyMMddHHmmss");
			//this.setBackupSubFolder(subFolder);
			report.setSubFolderName(backupSubFolder);
			List<Component> components = this.getComponents();
			for (int i = 0; i < components.size(); i++) {
				Component componentConfiguration = components.get(i);
				this.createBackup(componentConfiguration.getTableMapping(), report, backupSubFolder);
			}
			this.createBackup(this.getEntandoTableMapping(), report, backupSubFolder);
			long time = System.currentTimeMillis() - start;
			report.setRequiredTime(time);
			report.setDate(new Date());
			StringBuilder reportFolder = new StringBuilder(this.getLocalBackupsFolder());
			if (null != backupSubFolder) {
				reportFolder.append(backupSubFolder).append(File.separator);
			}
			this.save(DatabaseManager.DUMP_REPORT_FILE_NAME, 
					reportFolder.toString(), report.toXml());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "createBackup");
			throw new ApsSystemException("Error while creating backup", t);
		}
	}
	
	private void createBackup(Map<String, List<String>> tableMapping, DataSourceDumpReport report, String backupSubFolder) throws ApsSystemException {
		if (null == tableMapping || tableMapping.isEmpty()) {
			return;
		}
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
					this.dumpTableData(tableName, dataSourceName, dataSource, report, backupSubFolder);
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "createBackup");
			throw new ApsSystemException("Error while creating backup", t);
		}
	}
	
	protected void dumpTableData(String tableName, String dataSourceName, 
			DataSource dataSource, DataSourceDumpReport report, String backupSubFolder) throws ApsSystemException {
		try {
			TableDumpResult tableDumpResult = TableDataUtils.dumpTable(dataSource, tableName);
			report.addTableReport(dataSourceName, tableDumpResult);
			StringBuilder dirName = new StringBuilder(this.getLocalBackupsFolder());
			if (null != backupSubFolder) {
				dirName.append(backupSubFolder).append(File.separator);
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
	
}