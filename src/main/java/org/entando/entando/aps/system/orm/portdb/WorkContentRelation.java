/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.portdb;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.entando.entando.aps.system.orm.ExtendedColumnDefinition;
import org.entando.entando.aps.system.orm.IDbCreatorManager;

/**
 * @author E.Santoboni
 */
@DatabaseTable(tableName = WorkContentRelation.TABLE_NAME)
public class WorkContentRelation implements ExtendedColumnDefinition {
	
	public WorkContentRelation() {}
	
	@DatabaseField(foreign = true, columnName = "contentid", 
			width = 16, 
			canBeNull = false)
	private Content _content;
	
	@DatabaseField(foreign = true, columnName = "refcategory", 
			width = 30)
	private Category _category;
	
	@Override
	public String[] extensions(IDbCreatorManager.DatabaseType type) {
		String tableName = TABLE_NAME;
		String contentTableName = Content.TABLE_NAME;
		if (IDbCreatorManager.DatabaseType.MYSQL.equals(type)) {
			tableName = "`" + TABLE_NAME + "`";
			contentTableName = "`" + contentTableName + "`";
		}
		return new String[]{"ALTER TABLE " + tableName + " " 
				+ "ADD CONSTRAINT " + TABLE_NAME + "_contentid_fkey FOREIGN KEY (contentid) "
				+ "REFERENCES " + contentTableName + " (contentid)"};
	}
	
	public static final String TABLE_NAME = "workcontentrelations";
	
}
/*
CREATE TABLE workcontentrelations
(
  contentid character varying(16) NOT NULL,
  refcategory character varying(30),
  CONSTRAINT workcontentrelations_contentid_fkey FOREIGN KEY (contentid)
      REFERENCES contents (contentid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
 */