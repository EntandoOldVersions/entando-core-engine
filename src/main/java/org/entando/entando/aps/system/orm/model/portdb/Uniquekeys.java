/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model.portdb;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author E.Santoboni
 */
@DatabaseTable(tableName = Uniquekeys.TABLE_NAME)
public class Uniquekeys {
	
	public Uniquekeys() {}
	
	@DatabaseField(columnName = "id", 
			dataType = DataType.INTEGER, 
			canBeNull = false, id = true)
	private int _id;
	
	@DatabaseField(columnName = "keyvalue", 
			dataType = DataType.INTEGER, 
			canBeNull = false)
	private int _keyValue;
	
	public static final String TABLE_NAME = "uniquekeys";
	
}
/*
CREATE TABLE uniquekeys
(
  id integer NOT NULL,
  keyvalue integer NOT NULL,
  CONSTRAINT uniquekeys_pkey PRIMARY KEY (id )
)
 */