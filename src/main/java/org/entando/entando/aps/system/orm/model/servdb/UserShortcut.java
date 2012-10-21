/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model.servdb;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author E.Santoboni
 */
@DatabaseTable(tableName = UserShortcut.TABLE_NAME)
public class UserShortcut {
	
	public UserShortcut() {}
	
	@DatabaseField(columnName = "username", 
			dataType = DataType.STRING, 
			width = 40, 
			canBeNull = false, id = true)
	private String _username;
	
	@DatabaseField(columnName = "config", 
			dataType = DataType.LONG_STRING, 
			canBeNull = false)
	private String _config;
	
	public static final String TABLE_NAME = "authusershortcuts";
	
}
/*
CREATE TABLE authusershortcuts
(
  username character varying(40) NOT NULL,
  config character varying NOT NULL,
  CONSTRAINT authusershortcuts_pkey PRIMARY KEY (username )
)
 */