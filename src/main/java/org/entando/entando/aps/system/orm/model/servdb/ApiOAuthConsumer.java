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
@DatabaseTable(tableName = ApiOAuthConsumer.TABLE_NAME)
public class ApiOAuthConsumer {
	
	public ApiOAuthConsumer() {}
	
	@DatabaseField(columnName = "consumerkey", 
			dataType = DataType.STRING, 
			width = 100, 
			canBeNull = false, id = true)
	private String _consumerKey;
	
	@DatabaseField(columnName = "consumersecret", 
			dataType = DataType.STRING, 
			width = 100, 
			canBeNull = false)
	private String _consumerSecret;
	
	@DatabaseField(columnName = "description", 
			dataType = DataType.LONG_STRING, 
			canBeNull = false)
	private String _description;
	
	@DatabaseField(columnName = "callbackurl", 
			dataType = DataType.LONG_STRING)
	private String _callbackUrl;
	
	@DatabaseField(columnName = "expirationdate", 
			dataType = DataType.DATE)
	private Date _expirationDate;
	
	public static final String TABLE_NAME = "api_oauth_consumers";
	
}
/*
CREATE TABLE api_oauth_consumers
(
  consumerkey character varying(100) NOT NULL,
  consumersecret character varying(100) NOT NULL,
  description character varying(500) NOT NULL,
  callbackurl character varying(500),
  expirationdate date,
  CONSTRAINT api_oauth_consumers_pkey PRIMARY KEY (consumerkey )
)
*/