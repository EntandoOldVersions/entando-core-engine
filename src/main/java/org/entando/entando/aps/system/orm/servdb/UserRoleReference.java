/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.servdb;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.entando.entando.aps.system.orm.ExtendedColumnDefinition;
import org.entando.entando.aps.system.orm.IDbInstallerManager;

/**
 * @author E.Santoboni
 */
@DatabaseTable(tableName = UserRoleReference.TABLE_NAME)
public class UserRoleReference implements ExtendedColumnDefinition {
	
	public UserRoleReference() {}
	
	@DatabaseField(columnName = "username", 
			dataType = DataType.STRING, 
			width = 40, 
			canBeNull = false)
	private String _username;
	
	@DatabaseField(columnName = "rolename", 
			foreign = true,
			width = 20, 
			canBeNull = false)
	private Role _role;
	
	@Override
	public String[] extensions(IDbInstallerManager.DatabaseType type) {
		String tableName = TABLE_NAME;
		String roleTableName = Role.TABLE_NAME;
		if (IDbInstallerManager.DatabaseType.MYSQL.equals(type)) {
			tableName = "`" + tableName + "`";
			roleTableName = "`" + roleTableName + "`";
		}
		return new String[]{"ALTER TABLE " + tableName + " " 
				+ "ADD CONSTRAINT " + TABLE_NAME + "_rolename_fkey FOREIGN KEY (rolename) "
				+ "REFERENCES " + roleTableName + " (rolename)"};
	}
	
	public static final String TABLE_NAME = "authuserroles";
	
}
/*
CREATE TABLE authuserroles
(
  username character varying(40) NOT NULL,
  rolename character varying(20) NOT NULL,
  CONSTRAINT authuserroles_pkey PRIMARY KEY (username , rolename ),
  CONSTRAINT authuserroles_rolename_fkey FOREIGN KEY (rolename)
      REFERENCES authroles (rolename) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT
)
 */