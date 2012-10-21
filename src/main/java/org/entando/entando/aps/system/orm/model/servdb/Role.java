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
@DatabaseTable(tableName = Role.TABLE_NAME)
public class Role {
	
	public Role() {}
	
	@DatabaseField(columnName = "rolename", 
			dataType = DataType.STRING, 
			width = 20, 
			canBeNull = false, id = true)
	private String _roleName;
	
	@DatabaseField(columnName = "descr", 
			dataType = DataType.STRING, 
			width = 50, 
			canBeNull = false)
	private String _description;
	
	public static final String TABLE_NAME = "authroles";
	
}
/*
CREATE TABLE authroles
(
  rolename character varying(20) NOT NULL,
  descr character varying(50),
  CONSTRAINT authroles_pkey PRIMARY KEY (rolename )
)
 */