/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model.servdb;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.entando.entando.aps.system.orm.model.ExtendedColumnDefinition;
import org.entando.entando.aps.system.orm.IDbInstallerManager;

/**
 * @author E.Santoboni
 */
@DatabaseTable(tableName = UserGroupReference.TABLE_NAME)
public class UserGroupReference implements ExtendedColumnDefinition {
	
	public UserGroupReference() {}
	
	@DatabaseField(columnName = "username", 
			dataType = DataType.STRING, 
			width = 40, 
			canBeNull = false)
	private String _username;
	
	@DatabaseField(columnName = "groupname", 
			foreign = true,
			width = 20, 
			canBeNull = false)
	private Group _permission;
	
	@Override
	public String[] extensions(IDbInstallerManager.DatabaseType type) {
		String tableName = TABLE_NAME;
		String groupTableName = Group.TABLE_NAME;
		if (IDbInstallerManager.DatabaseType.MYSQL.equals(type)) {
			tableName = "`" + tableName + "`";
			groupTableName = "`" + groupTableName + "`";
		}
		return new String[]{"ALTER TABLE " + tableName + " " 
				+ "ADD CONSTRAINT " + TABLE_NAME + "_groupname_fkey FOREIGN KEY (groupname) "
				+ "REFERENCES " + groupTableName + " (groupname)"};
	}
	
	public static final String TABLE_NAME = "authusergroups";
	
}
/*
CREATE TABLE authusergroups
(
  username character varying(40) NOT NULL,
  groupname character varying(20) NOT NULL,
  CONSTRAINT authusergroups_pkey PRIMARY KEY (username , groupname ),
  CONSTRAINT authusergroups_groupname_fkey FOREIGN KEY (groupname)
      REFERENCES authgroups (groupname) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT
)
 */