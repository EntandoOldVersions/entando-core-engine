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
@DatabaseTable(tableName = Contentmodels.COLUMN_NAME)
public class Contentmodels {
	
	public Contentmodels() {}
	
	@DatabaseField(columnName = "modelid", 
			dataType = DataType.INTEGER, 
			canBeNull = false, id = true)
	private int _modelId;
	
	@DatabaseField(columnName = "contenttype", 
			dataType = DataType.STRING, 
			width = 30, 
			canBeNull = false)
	private String _contentType;
	
	@DatabaseField(columnName = "descr", 
			dataType = DataType.STRING, 
			width = 50, 
			canBeNull = false)
	private String _description;
	
	@DatabaseField(columnName = "model", 
			dataType = DataType.LONG_STRING, 
			canBeNull = true)
	private String _model;
	
	@DatabaseField(columnName = "stylesheet", 
			dataType = DataType.STRING, 
			width = 50, 
			canBeNull = false)
	private String _styleSheet;
	
	public static final String COLUMN_NAME = "contentmodels_xxx";
	
}
/*
CREATE TABLE contentmodels
(
  modelid integer NOT NULL,
  contenttype character varying(30) NOT NULL,
  descr character varying(50) NOT NULL,
  model character varying,
  stylesheet character varying(50),
  CONSTRAINT contentmodels_pkey PRIMARY KEY (modelid )
)
 */