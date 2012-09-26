/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.servdb;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.entando.entando.aps.system.orm.ExtendedColumnDefinition;
import org.entando.entando.aps.system.orm.IDbCreatorManager;

/**
 * @author E.Santoboni
 */
@DatabaseTable(tableName = ApiCatalogService.TABLE_NAME)
public class ApiCatalogService implements ExtendedColumnDefinition {
	
	public ApiCatalogService() {}
	
	@DatabaseField(columnName = "servicekey", 
			dataType = DataType.STRING, 
			width = 100, 
			canBeNull = false, id = true)
	private String _serviceKey;
	
	@DatabaseField(columnName = "resource", 
			dataType = DataType.STRING, 
			width = 100, 
			canBeNull = false)
	private String _resourceName;
	
	@DatabaseField(columnName = "description", 
			dataType = DataType.LONG_STRING, 
			canBeNull = false)
	private String _description;
	
	@DatabaseField(columnName = "parameters", 
			dataType = DataType.LONG_STRING)
	private String _parameters;
	
	@DatabaseField(columnName = "tag", 
			dataType = DataType.STRING, 
			width = 100)
	private String _tag;
	
	@DatabaseField(columnName = "freeparameters", 
			dataType = DataType.LONG_STRING)
	private String _freeParameters;
	
	@DatabaseField(columnName = "isactive", 
			dataType = DataType.SHORT, 
			canBeNull = false)
	private short _active;
	
	@DatabaseField(columnName = "ishidden", 
			dataType = DataType.SHORT, 
			canBeNull = false)
	private short _hidden;
	
	@DatabaseField(columnName = "myentando", 
			dataType = DataType.SHORT, 
			canBeNull = false)
	private short _myentando;
	
	@DatabaseField(columnName = "authenticationrequired", 
			dataType = DataType.SHORT)
	private short _authenticationRequired;
	
	@DatabaseField(columnName = "requiredpermission", 
			foreign = true, width = 30)
	private Permission _requiredPermission;
	
	@DatabaseField(columnName = "requiredgroup", 
			foreign = true, width = 20)
	private Group _requiredGroup;
	
	@Override
	public String[] extensions(IDbCreatorManager.DatabaseType type) {
		String tableName = TABLE_NAME;
		String permissionsTableName = Permission.TABLE_NAME;
		String groupsTableName = Group.TABLE_NAME;
		if (IDbCreatorManager.DatabaseType.MYSQL.equals(type)) {
			tableName = "`" + TABLE_NAME + "`";
			permissionsTableName = "`" + permissionsTableName + "`";
			groupsTableName = "`" + groupsTableName + "`";
		}
		return new String[]{"ALTER TABLE " + tableName + " "
				+ "ADD CONSTRAINT " + TABLE_NAME + "_requiredgroup_fkey FOREIGN KEY (requiredgroup) "
				+ "REFERENCES " + groupsTableName + " (groupname)", 
			"ALTER TABLE " + tableName + " "
				+ "ADD CONSTRAINT " + TABLE_NAME + "_requiredpermission_fkey FOREIGN KEY (requiredpermission) "
				+ "REFERENCES " + permissionsTableName + " (permissionname)"};
	}
	
	public static final String TABLE_NAME = "apicatalog_services";
	
}
/*
CREATE TABLE apicatalog_services
(
  servicekey character varying(100) NOT NULL,
  resource character varying(100) NOT NULL,
  description character varying NOT NULL,
  parameters character varying,
  tag character varying(100),
  freeparameters character varying,
  isactive smallint NOT NULL,
  ishidden smallint NOT NULL,
  myentando smallint NOT NULL,
  authenticationrequired smallint,
  requiredpermission character varying(30),
  requiredgroup character varying(20),
  CONSTRAINT apicatalog_services_pkey PRIMARY KEY (servicekey ),
  CONSTRAINT apicatalog_services_requiredgroup_fkey FOREIGN KEY (requiredgroup)
      REFERENCES authgroups (groupname) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT apicatalog_services_requiredpermission_fkey FOREIGN KEY (requiredpermission)
      REFERENCES authpermissions (permissionname) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT
)
 */