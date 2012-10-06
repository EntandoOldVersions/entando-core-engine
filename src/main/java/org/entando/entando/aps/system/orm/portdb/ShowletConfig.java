/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.portdb;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.entando.entando.aps.system.orm.ExtendedColumnDefinition;
import org.entando.entando.aps.system.orm.IDbInstallerManager;

/**
 * @author E.Santoboni
 */
@DatabaseTable(tableName = ShowletConfig.TABLE_NAME)
public class ShowletConfig implements ExtendedColumnDefinition {
	
	public ShowletConfig() {}
	
	@DatabaseField(foreign=true, columnName = "pagecode", 
			width = 30, 
			canBeNull = false)
	private Page _page;
	
	@DatabaseField(columnName = "framepos", 
			dataType = DataType.INTEGER, 
			canBeNull = false)
	private int _framePos;
	
	@DatabaseField(foreign=true, columnName = "showletcode", 
			width = 40, 
			canBeNull = false)
	private ShowletCatalog _showlet;
	
	@DatabaseField(columnName = "config", 
			dataType = DataType.LONG_STRING)
	private String _config;
	
	@DatabaseField(columnName = "publishedcontent", 
			dataType = DataType.STRING, 
			width = 30)
	private String _publishedContent;
	
	@Override
	public String[] extensions(IDbInstallerManager.DatabaseType type) {
		String tableName = TABLE_NAME;
		String pageTableName = Page.TABLE_NAME;
		String showletCatalogTableName = ShowletCatalog.TABLE_NAME;
		if (IDbInstallerManager.DatabaseType.MYSQL.equals(type)) {
			tableName = "`" + tableName + "`";
			pageTableName = "`" + pageTableName + "`";
			showletCatalogTableName = "`" + showletCatalogTableName + "`";
		}
		String[] queries = new String[3];
		queries[0] = "ALTER TABLE " + TABLE_NAME + " ADD CONSTRAINT " + 
				TABLE_NAME + "_pkey PRIMARY KEY(pagecode , framepos)";
		queries[1] = "ALTER TABLE " + tableName + " " 
				+ "ADD CONSTRAINT " + TABLE_NAME + "_pagecode_fkey FOREIGN KEY (pagecode) "
				+ "REFERENCES " + pageTableName + " (code)";
		queries[2] = "ALTER TABLE " + tableName + " " 
				+ "ADD CONSTRAINT " + TABLE_NAME + "_showletcode_fkey FOREIGN KEY (showletcode) "
				+ "REFERENCES " + showletCatalogTableName + " (code)";
		return queries;
	}
	
	public static final String TABLE_NAME = "showletconfig";
	
}
/*
CREATE TABLE showletconfig
(
  pagecode character varying(30) NOT NULL,
  framepos integer NOT NULL,
  showletcode character varying(40) NOT NULL,
  config character varying,
  publishedcontent character varying(30),
  CONSTRAINT showletconfig_pkey PRIMARY KEY (pagecode , framepos ),
  CONSTRAINT showletconfig_pagecode_fkey FOREIGN KEY (pagecode)
      REFERENCES pages (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT showletconfig_showletcode_fkey FOREIGN KEY (showletcode)
      REFERENCES showletcatalog (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
*/