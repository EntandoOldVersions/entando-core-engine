/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model.report;

import org.jdom.Element;

/**
 * @author E.Santoboni
 */
public class TableDump {
	
	public TableDump(String tableName) {
		this.setTableName(tableName);
	}
	
	protected TableDump(Element element) {
		String tableName = element.getAttributeValue(DatabaseDump.NAME_ATTRIBUTE);
		this.setTableName(tableName);
		String rowsString = element.getAttributeValue(DatabaseDump.ROWS_ATTRIBUTE);
		this.setRows(Integer.parseInt(rowsString));
		String requiredTimeString = element.getAttributeValue(DatabaseDump.REQUIRED_TIME_ATTRIBUTE);
		this.setRequiredTime(Integer.parseInt(requiredTimeString));
	}
	
	public long getRequiredTime() {
		return _requiredTime;
	}
	public void setRequiredTime(long requiredTime) {
		this._requiredTime = requiredTime;
	}
	
	public int getRows() {
		return _rows;
	}
	public void setRows(int rows) {
		this._rows = rows;
	}
	
	public String getTableName() {
		return _tableName;
	}
	protected void setTableName(String tableName) {
		this._tableName = tableName;
	}
	
	protected Element toJdomElement() {
		Element element = new Element(SystemInstallation.TABLE_ELEMENT);
		element.setAttribute(DatabaseDump.NAME_ATTRIBUTE, this.getTableName());
		element.setAttribute(DatabaseDump.REQUIRED_TIME_ATTRIBUTE, String.valueOf(this.getRequiredTime()));
		element.setAttribute(DatabaseDump.ROWS_ATTRIBUTE, String.valueOf(this.getRows()));
		return element;
	}
	
	private String _tableName;
	private int _rows;
	private long _requiredTime;
	
}
