/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model;

import org.entando.entando.aps.system.orm.model.report.TableDump;

/**
 * @author E.Santoboni
 */
public class TableDumpResult extends TableDump {
	
	public TableDumpResult(String tableName) {
		super(tableName);
	}
	
	public String getSqlDump() {
		return _sqlDump;
	}
	public void setSqlDump(String sqlDump) {
		this._sqlDump = sqlDump;
	}
	
	private String _sqlDump;
	
}