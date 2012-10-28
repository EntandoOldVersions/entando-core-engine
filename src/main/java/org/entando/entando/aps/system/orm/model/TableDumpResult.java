/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model;

/**
 * @author E.Santoboni
 */
public class TableDumpResult extends TableDumpReport {
	
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