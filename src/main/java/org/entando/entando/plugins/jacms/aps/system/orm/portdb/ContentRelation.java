/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.plugins.jacms.aps.system.orm.portdb;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.entando.entando.aps.system.orm.model.ExtendedColumnDefinition;
import org.entando.entando.aps.system.orm.IDbInstallerManager;
import org.entando.entando.aps.system.orm.model.portdb.Category;
import org.entando.entando.aps.system.orm.model.portdb.Page;

/**
 * @author E.Santoboni
 */
@DatabaseTable(tableName = ContentRelation.TABLE_NAME)
public class ContentRelation implements ExtendedColumnDefinition {
	
	public ContentRelation() {}
	
	@DatabaseField(foreign = true, columnName = "contentid", 
			width = 16, 
			canBeNull = false, index = true)
	private Content _content;
	
	@DatabaseField(foreign = true, columnName = "refpage", 
			width = 30)
	private Page _page;
	
	@DatabaseField(foreign = true, columnName = "refcontent", 
			width = 16)
	private Content _refContent;
	
	@DatabaseField(foreign = true, columnName = "refresource", 
			width = 16)
	private Resource _resource;
	
	@DatabaseField(foreign = true, columnName = "refcategory", 
			width = 30, index = true)
	private Category _category;
	
	@DatabaseField(columnName = "refgroup", 
			dataType = DataType.STRING, 
			width = 20, index = true)
	private String _group;
	
	@Override
	public String[] extensions(IDbInstallerManager.DatabaseType type) {
		String tableName = TABLE_NAME;
		String contentTableName = Content.TABLE_NAME;
		String pageTableName = Page.TABLE_NAME;
		String resourceTableName = Resource.TABLE_NAME;
		String categoryTableName = Category.TABLE_NAME;
		if (IDbInstallerManager.DatabaseType.MYSQL.equals(type)) {
			tableName = "`" + TABLE_NAME + "`";
			contentTableName = "`" + contentTableName + "`";
			pageTableName = "`" + pageTableName + "`";
			resourceTableName = "`" + resourceTableName + "`";
			categoryTableName = "`" + categoryTableName + "`";
		}
		return new String[]{"ALTER TABLE " + tableName + " " 
				+ "ADD CONSTRAINT " + TABLE_NAME + "_contentid_fkey FOREIGN KEY (contentid) "
				+ "REFERENCES " + contentTableName + " (contentid)",
				"ALTER TABLE " + tableName + " " 
				+ "ADD CONSTRAINT " + TABLE_NAME + "_refcategory_fkey FOREIGN KEY (refcategory) "
				+ "REFERENCES " + categoryTableName + " (catcode)",
				"ALTER TABLE " + tableName + " " 
				+ "ADD CONSTRAINT " + TABLE_NAME + "_refcontent_fkey FOREIGN KEY (refcontent) "
				+ "REFERENCES " + contentTableName + " (contentid)",
				"ALTER TABLE " + tableName + " " 
				+ "ADD CONSTRAINT " + TABLE_NAME + "_refpage_fkey FOREIGN KEY (refpage) "
				+ "REFERENCES " + pageTableName + " (code)",
				"ALTER TABLE " + tableName + " " 
				+ "ADD CONSTRAINT " + TABLE_NAME + "_refresource_fkey FOREIGN KEY (refresource) "
				+ "REFERENCES " + resourceTableName + " (resid)"};
	}
	
	public static final String TABLE_NAME = "contentrelations";
	
}
/*
CREATE TABLE contentrelations
(
  contentid character varying(16) NOT NULL,
  refpage character varying(30),
  refcontent character varying(16),
  refresource character varying(16),
  refcategory character varying(30),
  refgroup character varying(20),
  CONSTRAINT contentrelations_contentid_fkey FOREIGN KEY (contentid)
      REFERENCES contents (contentid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT contentrelations_refcategory_fkey FOREIGN KEY (refcategory)
      REFERENCES categories (catcode) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT contentrelations_refcontent_fkey FOREIGN KEY (refcontent)
      REFERENCES contents (contentid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT contentrelations_refpage_fkey FOREIGN KEY (refpage)
      REFERENCES pages (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT contentrelations_refresource_fkey FOREIGN KEY (refresource)
      REFERENCES resources (resid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
 */