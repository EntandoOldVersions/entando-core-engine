/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.servdb;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author E.Santoboni
 */
@DatabaseTable(tableName = Group.TABLE_NAME)
public class Group {
	
	public Group() {}
	
	@DatabaseField(columnName = "groupname", 
			dataType = DataType.STRING, 
			width = 20, 
			canBeNull = false, id = true)
	private String _groupName;
	
	@DatabaseField(columnName = "descr", 
			dataType = DataType.STRING, 
			width = 50, 
			canBeNull = false)
	private String _description;
	
	public static final String TABLE_NAME = "authgroups";
	
}
/*
CREATE TABLE authgroups
(
  groupname character varying(20) NOT NULL,
  descr character varying(50),
  CONSTRAINT authgroups_pkey PRIMARY KEY (groupname )
)
 */