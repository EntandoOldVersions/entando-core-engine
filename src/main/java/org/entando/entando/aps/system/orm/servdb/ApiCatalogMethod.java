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
@DatabaseTable(tableName = ApiCatalogMethod.TABLE_NAME)
public class ApiCatalogMethod implements ExtendedColumnDefinition {
	
	public ApiCatalogMethod() {}
	
	@DatabaseField(columnName = "resource", 
			dataType = DataType.STRING, 
			width = 100, 
			canBeNull = false)
	private String _resource;
	
	@DatabaseField(columnName = "httpmethod", 
			dataType = DataType.STRING, 
			width = 6, 
			canBeNull = false)
	private String _httpmethod;
	
	@DatabaseField(columnName = "isactive", 
			dataType = DataType.SHORT, 
			canBeNull = false)
	private short _active;
	
	@DatabaseField(columnName = "ishidden", 
			dataType = DataType.SHORT, 
			canBeNull = false)
	private short _hidden;
	
	@DatabaseField(columnName = "authenticationrequired", 
			dataType = DataType.SHORT)
	private short _authenticationRequired;
	
	@DatabaseField(columnName = "authorizationrequired", 
			foreign = true, width = 30)
	private Permission _authorizationRequired;
	
	@Override
	public String[] extensions(IDbInstallerManager.DatabaseType type) {
		String tableName = TABLE_NAME;
		String permissionsTableName = Permission.TABLE_NAME;
		if (IDbInstallerManager.DatabaseType.MYSQL.equals(type)) {
			tableName = "`" + tableName + "`";
			permissionsTableName = "`" + permissionsTableName + "`";
		}
		return new String[]{"ALTER TABLE " + TABLE_NAME + " ADD CONSTRAINT " 
				+ TABLE_NAME + "_pkey PRIMARY KEY(resource , httpmethod)", 
			"ALTER TABLE " + tableName + " "
				+ "ADD CONSTRAINT " + TABLE_NAME + "_authorizationrequired_fkey FOREIGN KEY (authorizationrequired) "
				+ "REFERENCES " + permissionsTableName + " (permissionname)"};
	}
	
	public static final String TABLE_NAME = "apicatalog_methods";
	
}
/*
CREATE TABLE apicatalog_methods
(
  resource character varying(100) NOT NULL,
  httpmethod character varying(6) NOT NULL,
  isactive smallint NOT NULL,
  ishidden smallint NOT NULL,
  authenticationrequired smallint,
  authorizationrequired character varying(30),
  CONSTRAINT apicatalog_status_pkey PRIMARY KEY (resource , httpmethod ),
  CONSTRAINT apicatalog_methods_authorizationrequired_fkey FOREIGN KEY (authorizationrequired)
      REFERENCES authpermissions (permissionname) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT
)
 */