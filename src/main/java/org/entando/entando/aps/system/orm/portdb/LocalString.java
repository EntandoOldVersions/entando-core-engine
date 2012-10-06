/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.portdb;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.entando.entando.aps.system.orm.ExtendedColumnDefinition;
import org.entando.entando.aps.system.orm.IDbInstallerManager;

/**
 * @author E.Santoboni
 */
@DatabaseTable(tableName = LocalString.TABLE_NAME)
public class LocalString implements ExtendedColumnDefinition {
	
	public LocalString() {}
	
	@DatabaseField(columnName = "keycode", 
			dataType = DataType.STRING, 
			width = 50, 
			canBeNull = false)
	private String _keyCode;
	
	@DatabaseField(columnName = "langcode", 
			dataType = DataType.STRING, 
			width = 2, 
			canBeNull = false)
	private String _langCode;
	
	@DatabaseField(columnName = "stringvalue", 
			dataType = DataType.LONG_STRING, 
			canBeNull = false)
	private String _stringValue;
	
	@Override
	public String[] extensions(IDbInstallerManager.DatabaseType type) {
		return new String[]{"ALTER TABLE " + TABLE_NAME + " ADD CONSTRAINT " + TABLE_NAME + "_pkey PRIMARY KEY(keycode , langcode )"};
	}
	
	public static final String TABLE_NAME = "localstrings";
	
}
/*
CREATE TABLE localstrings
(
  keycode character varying(50) NOT NULL,
  langcode character varying(2) NOT NULL,
  stringvalue character varying NOT NULL,
  CONSTRAINT localstrings_pkey PRIMARY KEY (keycode , langcode )
)
 */