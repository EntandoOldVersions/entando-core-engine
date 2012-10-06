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
@DatabaseTable(tableName = Page.TABLE_NAME)
public class Page implements ExtendedColumnDefinition {
	
	public Page() {}
	
	@DatabaseField(columnName = "code", 
			dataType = DataType.STRING, 
			width = 30, 
			canBeNull = false, id = true)
	private String _code;
	
	@DatabaseField(columnName = "parentcode", 
			dataType = DataType.STRING, 
			width = 30, 
			canBeNull = false)
	private String _parentCode;
	
	@DatabaseField(columnName = "pos", 
			dataType = DataType.INTEGER, 
			canBeNull = false)
	private int _position;
	
	@DatabaseField(foreign = true, columnName = "modelcode", 
			width = 40, 
			canBeNull = false)
	private PageModel _model;
	
	@DatabaseField(columnName = "titles", 
			dataType = DataType.LONG_STRING, 
			canBeNull = false)
	private String _titles;
	
	@DatabaseField(columnName = "groupcode", 
			dataType = DataType.STRING, 
			width = 30, 
			canBeNull = false)
	private String _groupCode;
	
	@DatabaseField(columnName = "showinmenu", 
			dataType = DataType.SHORT, 
			canBeNull = false)
	private short _showInMenu;
	
	@DatabaseField(columnName = "extraconfig", 
			dataType = DataType.LONG_STRING)
	private String _extraConfig;
	
	@Override
	public String[] extensions(IDbInstallerManager.DatabaseType type) {
		String tableName = TABLE_NAME;
		String pageModelTableName = PageModel.TABLE_NAME;
		if (IDbInstallerManager.DatabaseType.MYSQL.equals(type)) {
			tableName = "`" + tableName + "`";
			pageModelTableName = "`" + pageModelTableName + "`";
		}
		return new String[]{"ALTER TABLE " + tableName + " " 
				+ "ADD CONSTRAINT " + TABLE_NAME + "_modelcode_fkey FOREIGN KEY (modelcode) "
				+ "REFERENCES " + pageModelTableName + " (code)"};
	}
	
	public static final String TABLE_NAME = "pages";
	
}
/*
CREATE TABLE pages
(
  code character varying(30) NOT NULL,
  parentcode character varying(30),
  pos integer NOT NULL,
  modelcode character varying(40) NOT NULL,
  titles character varying,
  groupcode character varying(30) NOT NULL,
  showinmenu smallint NOT NULL,
  extraconfig character varying,
  CONSTRAINT pages_pkey PRIMARY KEY (code ),
  CONSTRAINT pages_modelcode_fkey FOREIGN KEY (modelcode)
      REFERENCES pagemodels (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
 */
