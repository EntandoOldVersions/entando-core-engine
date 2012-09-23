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
@DatabaseTable(tableName = Contentsearch.COLUMN_NAME)
public class Localstrings implements ExtendedColumnDefinition {
	
	public Localstrings() {}
	
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
	public String[] extensions(IDbCreatorManager.DatabaseType type) {
		return new String[]{"ALTER TABLE " + COLUMN_NAME + " ADD CONSTRAINT " + COLUMN_NAME + "_pkey PRIMARY KEY(keycode , langcode )"};
	}
	
	public static final String COLUMN_NAME = "localstrings_xxx";
	
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