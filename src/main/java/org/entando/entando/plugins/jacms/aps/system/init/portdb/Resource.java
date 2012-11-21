/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.plugins.jacms.aps.system.init.portdb;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author E.Santoboni
 */
@DatabaseTable(tableName = Resource.TABLE_NAME)
public class Resource {
	
	public Resource() {}
	
	@DatabaseField(columnName = "resid", 
			dataType = DataType.STRING, 
			width = 16, 
			canBeNull = false, id = true)
	private String _resourceId;
	
	@DatabaseField(columnName = "restype", 
			dataType = DataType.STRING, 
			width = 30, 
			canBeNull = false)
	private String _resourceType;
	
	@DatabaseField(columnName = "descr", 
			dataType = DataType.STRING, 
			canBeNull = false)
	private String _description;
	
	@DatabaseField(columnName = "maingroup", 
			dataType = DataType.STRING, 
			width = 20, 
			canBeNull = false)
	private String _mainGroup;
	
	@DatabaseField(columnName = "resourcexml", 
			dataType = DataType.LONG_STRING, 
			canBeNull = false)
	private String _resourceXml;
	
	@DatabaseField(columnName = "masterfilename", 
			dataType = DataType.STRING, 
			width = 100, 
			canBeNull = false)
	private String _masterFileName;
	
	public static final String TABLE_NAME = "resources";
	
}
/*
CREATE TABLE resources
(
  resid character varying(16) NOT NULL,
  restype character varying(30) NOT NULL,
  descr character varying(260) NOT NULL,
  maingroup character varying(20) NOT NULL,
  resourcexml character varying NOT NULL,
  masterfilename character varying(100) NOT NULL,
  CONSTRAINT resources_pkey PRIMARY KEY (resid )
)
 */