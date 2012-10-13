/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model;

import java.util.*;

import org.jdom.Element;

/**
 * @author E.Santoboni
 */
public class SchemaReport extends AbstractReport {
	
	protected SchemaReport() {}
	
	protected SchemaReport(Element element) {
		List<Element> databaseElements = element.getChildren("database");
		for (int i = 0; i < databaseElements.size(); i++) {
			Element databaseElement = databaseElements.get(i);
			String dbName = databaseElement.getAttributeValue("name");
			String dbStatusString = databaseElement.getAttributeValue("status");
			InstallationReport.Status dbStatus = Enum.valueOf(InstallationReport.Status.class, dbStatusString.toUpperCase());
			this.getDatabaseStatus().put(dbName, dbStatus);
			List<String> tables = new ArrayList<String>();
			List<Element> databaseTableElements = databaseElement.getChildren("table");
			for (int j = 0; j < databaseTableElements.size(); j++) {
				Element databaseTableElement = databaseTableElements.get(j);
				tables.add(databaseTableElement.getAttributeValue("name"));
			}
			this.getDatabaseTables().put(dbName, tables);
		}
	}
	
	protected Element toJdomElement() {
		Element element = new Element("schema");
		element.setAttribute("status", this.getStatus().toString());
		Iterator<String> nameIter = this.getDatabaseStatus().keySet().iterator();
		while (nameIter.hasNext()) {
			String dbName = nameIter.next();
			Element dbElement = new Element("database");
			dbElement.setAttribute("name", dbName);
			dbElement.setAttribute("status", this.getDatabaseStatus().get(dbName).toString());
			element.addContent(dbElement);
			List<String> tables = this.getDatabaseTables().get(dbName);
			if (null == tables) {
				//TODO "entandoCore" case 
				continue;
			}
			for (int i = 0; i < tables.size(); i++) {
				String table = tables.get(i);
				Element tableElement = new Element("table");
				tableElement.setAttribute("name", table);
				dbElement.addContent(tableElement);
			}
		}
		return element;
	}
	
	public Map<String, List<String>> getDatabaseTables() {
		return _databaseTables;
	}
	
	private Map<String, List<String>> _databaseTables = new HashMap<String, List<String>>();
	
}
