/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.servdb;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.entando.entando.aps.system.orm.ExtendedColumnDefinition;
import org.entando.entando.aps.system.orm.IDbCreatorManager;

/**
 * @author E.Santoboni
 */
@DatabaseTable(tableName = RolePermissionReference.TABLE_NAME)
public class RolePermissionReference implements ExtendedColumnDefinition {
	
	public RolePermissionReference() {}
	
	@DatabaseField(columnName = "rolename", 
			foreign = true,
			width = 20, 
			canBeNull = false)
	private Role _role;
	
	@DatabaseField(columnName = "permissionname", 
			foreign = true,
			width = 30, 
			canBeNull = false)
	private Permission _permission;
	
	@Override
	public String[] extensions(IDbCreatorManager.DatabaseType type) {
		String tableName = TABLE_NAME;
		String roleTableName = Role.TABLE_NAME;
		String permissionableName = Permission.TABLE_NAME;
		if (IDbCreatorManager.DatabaseType.MYSQL.equals(type)) {
			tableName = "`" + TABLE_NAME + "`";
			roleTableName = "`" + roleTableName + "`";
			permissionableName = "`" + permissionableName + "`";
		}
		return new String[]{"ALTER TABLE " + tableName + " " 
				+ "ADD CONSTRAINT " + TABLE_NAME + "_permissionname_fkey FOREIGN KEY (permissionname) "
				+ "REFERENCES " + permissionableName + " (permissionname)", 
			"ALTER TABLE " + tableName + " " 
				+ "ADD CONSTRAINT " + TABLE_NAME + "_rolename_fkey FOREIGN KEY (rolename) "
				+ "REFERENCES " + roleTableName + " (rolename)"};
	}
	
	public static final String TABLE_NAME = "authrolepermissions";
	
}
/*
CREATE TABLE authrolepermissions
(
  rolename character varying(20) NOT NULL,
  permissionname character varying(30) NOT NULL,
  CONSTRAINT authrolepermissions_pkey PRIMARY KEY (rolename , permissionname ),
  CONSTRAINT authrolepermissions_permissionname_fkey FOREIGN KEY (permissionname)
      REFERENCES authpermissions (permissionname) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT authrolepermissions_rolename_fkey FOREIGN KEY (rolename)
      REFERENCES authroles (rolename) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT
)
 */