/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.servdb;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * @author E.Santoboni
 */
@DatabaseTable(tableName = ApiOAuthToken.TABLE_NAME)
public class ApiOAuthToken {
	
	public ApiOAuthToken() {}
	
	@DatabaseField(columnName = "accesstoken", 
			dataType = DataType.STRING, 
			width = 100, 
			canBeNull = false, id = true)
	private String _accessToken;
	
	@DatabaseField(columnName = "tokensecret", 
			dataType = DataType.STRING, 
			width = 100, 
			canBeNull = false)
	private String _tokenSecret;
	
	@DatabaseField(columnName = "consumerkey", 
			dataType = DataType.STRING, 
			width = 100, 
			canBeNull = false)
	private String _consumerKey;
	
	@DatabaseField(columnName = "lastaccess", 
			dataType = DataType.DATE, 
			canBeNull = false)
	private Date _lastAccess;
	
	@DatabaseField(columnName = "username", 
			dataType = DataType.STRING, 
			width = 40, 
			canBeNull = false)
	private String _username;
	
	public static final String TABLE_NAME = "api_oauth_tokens";
	
}
/*
CREATE TABLE api_oauth_tokens
(
  accesstoken character(100) NOT NULL,
  tokensecret character varying(100) NOT NULL,
  consumerkey character varying(100) NOT NULL,
  lastaccess date NOT NULL,
  username character varying(40) NOT NULL,
  CONSTRAINT api_oauth_tokens_pkey PRIMARY KEY (accesstoken )
)
*/