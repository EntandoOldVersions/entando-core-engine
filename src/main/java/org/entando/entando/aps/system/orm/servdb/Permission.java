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
@DatabaseTable(tableName = Permission.TABLE_NAME)
public class Permission {
	
	public Permission() {}
	
	@DatabaseField(columnName = "permissionname", 
			dataType = DataType.STRING, 
			width = 30, 
			canBeNull = false, id = true)
	private String _groupName;
	
	@DatabaseField(columnName = "descr", 
			dataType = DataType.STRING, 
			width = 50, 
			canBeNull = false)
	private String _description;
	
	public static final String TABLE_NAME = "authpermissions";
	
}
/*
CREATE TABLE authpermissions
(
  permissionname character varying(30) NOT NULL,
  descr character varying(50),
  CONSTRAINT authpermissions_pkey PRIMARY KEY (permissionname )
)
*/