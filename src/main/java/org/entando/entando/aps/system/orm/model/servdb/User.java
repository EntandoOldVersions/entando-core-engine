/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model.servdb;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;

/**
 * @author E.Santoboni
 */
@DatabaseTable(tableName = User.TABLE_NAME)
public class User {
	
	public User() {}
	
	@DatabaseField(columnName = "username", 
			dataType = DataType.STRING, 
			width = 40, 
			canBeNull = false, id = true)
	private String _username;
	
	@DatabaseField(columnName = "passwd", 
			dataType = DataType.STRING, 
			width = 40)
	private String _password;
	
	@DatabaseField(columnName = "registrationdate", 
			dataType = DataType.DATE, 
			canBeNull = false)
	private Date _registrationDate;
	
	@DatabaseField(columnName = "lastaccess", 
			dataType = DataType.DATE)
	private Date _lastAccess;
	
	@DatabaseField(columnName = "lastpasswordchange", 
			dataType = DataType.DATE)
	private Date _lastPasswordChange;
	
	@DatabaseField(columnName = "active", 
			dataType = DataType.SHORT)
	private short _active;
	
	public static final String TABLE_NAME = "authusers";
	
}
/*
CREATE TABLE authusers
(
  username character varying(40) NOT NULL,
  passwd character varying(40),
  registrationdate date NOT NULL,
  lastaccess date,
  lastpasswordchange date,
  active smallint,
  CONSTRAINT authusers_pkey PRIMARY KEY (username )
)
 */