/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando Enterprise Edition software.
* You can redistribute it and/or modify it
* under the terms of the Entando's EULA
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package org.entando.entando.aps.system.init.model.servdb;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author E.Santoboni
 */
@DatabaseTable(tableName = Group.TABLE_NAME)
public class Group {
	
	public Group() {}
	
	@DatabaseField(columnName = "groupname", 
			dataType = DataType.STRING, 
			width = 20, 
			canBeNull = false, id = true)
	private String _groupName;
	
	@DatabaseField(columnName = "descr", 
			dataType = DataType.STRING, 
			width = 50, 
			canBeNull = false)
	private String _description;
	
	public static final String TABLE_NAME = "authgroups";
	
}
/*
CREATE TABLE authgroups
(
  groupname character varying(20) NOT NULL,
  descr character varying(50),
  CONSTRAINT authgroups_pkey PRIMARY KEY (groupname )
)
 */