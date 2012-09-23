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
@DatabaseTable(tableName = Pagemodels.COLUMN_NAME)
public class Pagemodels {
	
	public Pagemodels() {}
	
	@DatabaseField(columnName = "code", 
			dataType = DataType.STRING, 
			width = 40, 
			canBeNull = false, id = true)
	private String _code;
	
	@DatabaseField(columnName = "descr", 
			dataType = DataType.STRING, 
			width = 50, 
			canBeNull = false)
	private String _description;
	
	@DatabaseField(columnName = "frames", 
			dataType = DataType.LONG_STRING, 
			canBeNull = true)
	private String _frame;
	
	@DatabaseField(columnName = "plugincode", 
			dataType = DataType.STRING, 
			width = 30, 
			canBeNull = true)
	private String _pluginCode;
	
	public static final String COLUMN_NAME = "pagemodels_xxx";
	
}
/*
CREATE TABLE pagemodels
(
  code character varying(40) NOT NULL,
  descr character varying(50) NOT NULL,
  frames character varying,
  plugincode character varying(30),
  CONSTRAINT pagemodels_pkey PRIMARY KEY (code )
)
 */
