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

import javax.sql.DataSource;

/**
 * @author E.Santoboni
 */
public class TableDumperFactoryThread extends Thread {
	
	public TableDumperFactoryThread(String tableName, 
			String dataSourceName, DataSource dataSource, DbInstallerManager manager) {
		this._dataSource = dataSource;
		this._dataSourceName = dataSourceName;
		this._manager = manager;
		this._tableName = tableName;
	}
	
	@Override
	public void run() {
		try {
			this._manager.dumpTableData(this._tableName, this._dataSourceName, this._dataSource);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "run");
		}
	}
	
	private String _tableName;
	private String _dataSourceName;
	private DataSource _dataSource;
	private DbInstallerManager _manager;
	
}