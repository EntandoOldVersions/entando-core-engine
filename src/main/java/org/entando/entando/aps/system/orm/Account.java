/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author E.Santoboni
 */
@DatabaseTable(tableName = "accounts")
public class Account {

	// for QueryBuilder to be able to find the fields
	public static final String NAME_FIELD_NAME = "name";
	public static final String PASSWORD_FIELD_NAME = "passwd";
	
	@DatabaseField(columnName = "id", 
			dataType = DataType.INTEGER, 
			unique = true, canBeNull = false)
	private int id;

	@DatabaseField(columnName = NAME_FIELD_NAME, 
			dataType = DataType.STRING, 
			canBeNull = false, 
			width = 40)
	private String name;

	@DatabaseField(columnName = PASSWORD_FIELD_NAME)
	private String password;

	Account() {
		// all persisted classes must define a no-arg constructor with at least package visibility
	}

	public Account(String name) {
		this.name = name;
	}

	public Account(String name, String password) {
		this.name = name;
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != getClass()) {
			return false;
		}
		return name.equals(((Account) other).name);
	}
	
}
