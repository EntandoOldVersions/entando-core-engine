/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.portdb;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.entando.entando.aps.system.orm.ExtendedColumnDefinition;
import org.entando.entando.aps.system.orm.IDbInstallerManager;

/**
 * @author E.Santoboni
 */
@DatabaseTable(tableName = ResourceRelation.TABLE_NAME)
public class ResourceRelation implements ExtendedColumnDefinition {
	
	public ResourceRelation() {}
	
	@DatabaseField(foreign = true, columnName = "resid", 
			width = 16, 
			canBeNull = false)
	private Resource _resource;
	
	@DatabaseField(foreign = true, columnName = "refcategory", 
			width = 30, 
			canBeNull = false)
	private Category _category;
	
	@Override
	public String[] extensions(IDbInstallerManager.DatabaseType type) {
		String tableName = TABLE_NAME;
		String categoryTableName = Category.TABLE_NAME;
		String resourceTableName = Resource.TABLE_NAME;
		if (IDbInstallerManager.DatabaseType.MYSQL.equals(type)) {
			tableName = "`" + tableName + "`";
			categoryTableName = "`" + categoryTableName + "`";
			resourceTableName = "`" + resourceTableName + "`";
		}
		return new String[]{"ALTER TABLE " + tableName + " " 
				+ "ADD CONSTRAINT " + TABLE_NAME + "_refcategory_fkey FOREIGN KEY (refcategory) "
				+ "REFERENCES " + categoryTableName + " (catcode)", 
			"ALTER TABLE " + tableName + " " 
				+ "ADD CONSTRAINT " + TABLE_NAME + "_resid_fkey FOREIGN KEY (resid) "
				+ "REFERENCES " + resourceTableName + " (resid)"};
	}
	
	public static final String TABLE_NAME = "resourcerelations";
	
}
/*
CREATE TABLE resourcerelations
(
  resid character varying(16) NOT NULL,
  refcategory character varying(30),
  CONSTRAINT resourcerelations_refcategory_fkey FOREIGN KEY (refcategory)
      REFERENCES categories (catcode) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT resourcerelations_resid_fkey FOREIGN KEY (resid)
      REFERENCES resources (resid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
 */