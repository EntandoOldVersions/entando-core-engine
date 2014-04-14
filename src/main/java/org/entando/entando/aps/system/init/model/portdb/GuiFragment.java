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
package org.entando.entando.aps.system.init.model.portdb;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.entando.entando.aps.system.init.IDatabaseManager;
import org.entando.entando.aps.system.init.model.ExtendedColumnDefinition;

@DatabaseTable(tableName = GuiFragment.TABLE_NAME)
public class GuiFragment implements ExtendedColumnDefinition {
	
	public GuiFragment() {}
	
	@DatabaseField(columnName = "id",
			dataType = DataType.INTEGER,
			canBeNull = false, id = true)
	private int _id;
	
	@DatabaseField(columnName = "code",
			dataType = DataType.LONG_STRING,
			canBeNull = false)
	private String _code;
	
	@DatabaseField(foreign = true, columnName = "widgetcode",
			width = 40,
			canBeNull = true)
	private WidgetCatalog _widget;
	
	@DatabaseField(columnName = "plugincode",
			dataType = DataType.STRING,
			width = 30, canBeNull = true)
	private String _pluginCode;
	
	@DatabaseField(columnName = "gui",
			dataType = DataType.LONG_STRING,
			canBeNull = false)
	private String _gui;

	@Override
	public String[] extensions(IDatabaseManager.DatabaseType type) {
		String tableName = TABLE_NAME;
		String widgetCatalogTableName = WidgetCatalog.TABLE_NAME;
		if (IDatabaseManager.DatabaseType.MYSQL.equals(type)) {
			tableName = "`" + tableName + "`";
			widgetCatalogTableName = "`" + widgetCatalogTableName + "`";
		}
		String[] queries = new String[1];
		queries[0] = "ALTER TABLE " + tableName + " "
				+ "ADD CONSTRAINT " + TABLE_NAME + "_widgetcode_fkey FOREIGN KEY (widgetcode) "
				+ "REFERENCES " + widgetCatalogTableName + " (code)";
		return queries;
	}
	
	public static final String TABLE_NAME = "guifragment";
	
}
