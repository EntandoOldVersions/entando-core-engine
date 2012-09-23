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
@DatabaseTable(tableName = Categories.COLUMN_NAME)
public class Categories {

	public Categories() {}
	
	@DatabaseField(columnName = "catcode", 
			dataType = DataType.STRING, 
			width = 30, 
			canBeNull = false, id = true)
	private String _categoryCode;
	
	@DatabaseField(columnName = "parentcode", 
			dataType = DataType.STRING, 
			width = 30, 
			canBeNull = false)
	private String _parentCode;
	
	@DatabaseField(columnName = "titles", 
			dataType = DataType.LONG_STRING, 
			width = 30, 
			canBeNull = false)
	private String _titles;
	
	public static final String COLUMN_NAME = "categories_xxx";
	
}
/*
CREATE TABLE categories
(
  catcode character varying(30) NOT NULL,
  parentcode character varying(30) NOT NULL,
  titles character varying NOT NULL,
  CONSTRAINT categories_pkey PRIMARY KEY (catcode )
)
 */