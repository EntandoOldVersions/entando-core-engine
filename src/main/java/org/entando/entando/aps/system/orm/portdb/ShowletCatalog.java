/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.portdb;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author E.Santoboni
 */
@DatabaseTable(tableName = ShowletCatalog.TABLE_NAME)
public class ShowletCatalog {
	
	public ShowletCatalog() {}
	
	@DatabaseField(columnName = "code", 
			dataType = DataType.STRING, 
			width = 40, 
			canBeNull = false, id = true)
	private String _code;
	
	@DatabaseField(columnName = "titles", 
			dataType = DataType.LONG_STRING, 
			canBeNull = false)
	private String _titles;
	
	@DatabaseField(columnName = "parameters", 
			dataType = DataType.LONG_STRING)
	private String _parameters;
	
	@DatabaseField(columnName = "plugincode", 
			dataType = DataType.STRING, 
			width = 30)
	private String _pluginCode;
	
	@DatabaseField(columnName = "parenttypecode", 
			dataType = DataType.STRING, 
			width = 40)
	private String _parentTypeCode;
	
	@DatabaseField(columnName = "defaultconfig", 
			dataType = DataType.LONG_STRING)
	private String _defaultConfig;
	
	@DatabaseField(columnName = "locked", 
			dataType = DataType.INTEGER, 
			canBeNull = false)
	private int _locked;
	
	@DatabaseField(columnName = "maingroup", 
			dataType = DataType.STRING, 
			width = 20)
	private String _mainGroup;
	
	public static final String TABLE_NAME = "showletcatalog";
	
}
/*
CREATE TABLE showletcatalog
(
  code character varying(40) NOT NULL,
  titles character varying NOT NULL,
  parameters character varying,
  plugincode character varying(30),
  parenttypecode character varying(40),
  defaultconfig character varying,
  locked smallint NOT NULL,
  maingroup character varying(20),
  CONSTRAINT showletcatalog_pkey PRIMARY KEY (code )
)
 */