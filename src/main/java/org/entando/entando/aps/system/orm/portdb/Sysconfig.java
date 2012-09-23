/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.portdb;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.entando.entando.aps.system.orm.ExtendedColumnDefinition;
import org.entando.entando.aps.system.orm.IDbCreatorManager;

/**
 * @author E.Santoboni
 */
@DatabaseTable(tableName = Sysconfig.COLUMN_NAME)
public class Sysconfig implements ExtendedColumnDefinition {
	
	public Sysconfig() {}
	
	@DatabaseField(columnName = "version", 
			dataType = DataType.STRING, 
			width = 10, 
			canBeNull = false)
	private String _version;
	
	@DatabaseField(columnName = "item", 
			dataType = DataType.STRING, 
			width = 40, 
			canBeNull = false)
	private String _item;
	
	@DatabaseField(columnName = "descr", 
			dataType = DataType.STRING, 
			width = 100, 
			canBeNull = false)
	private String _descr;
	
	@DatabaseField(columnName = "config", 
			dataType = DataType.LONG_STRING, 
			canBeNull = false)
	private String _config;
	
	@Override
	public String[] extensions(IDbCreatorManager.DatabaseType type) {
		return new String[]{"ALTER TABLE " + COLUMN_NAME + " ADD CONSTRAINT " + COLUMN_NAME + "_pkey PRIMARY KEY(version , item )"};
	}
	
	public static final String COLUMN_NAME = "sysconfig_xxx";
	
}

/*
CREATE TABLE sysconfig
(
  version character varying(10) NOT NULL,
  item character varying(40) NOT NULL,
  descr character varying(100),
  config character varying,
  CONSTRAINT system_pkey PRIMARY KEY (version , item )
)
 */