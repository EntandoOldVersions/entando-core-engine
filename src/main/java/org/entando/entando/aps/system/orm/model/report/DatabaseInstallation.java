/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model.report;

import java.util.*;

import org.jdom.Element;

/**
 * @author E.Santoboni
 */
public class DatabaseInstallation extends AbstractReport {
	
	protected DatabaseInstallation() {}
	
	protected DatabaseInstallation(Element element) {
		List<Element> databaseElements = element.getChildren(SystemInstallation.DATASOURCE_ELEMENT);
		for (int i = 0; i < databaseElements.size(); i++) {
			Element databaseElement = databaseElements.get(i);
			String dbName = databaseElement.getAttributeValue(SystemInstallation.NAME_ATTRIBUTE);
			String dbStatusString = databaseElement.getAttributeValue(SystemInstallation.STATUS_ATTRIBUTE);
			SystemInstallation.Status dbStatus = Enum.valueOf(SystemInstallation.Status.class, dbStatusString.toUpperCase());
			this.getDatabaseStatus().put(dbName, dbStatus);
			List<String> tables = new ArrayList<String>();
			List<Element> databaseTableElements = databaseElement.getChildren(SystemInstallation.TABLE_ELEMENT);
			for (int j = 0; j < databaseTableElements.size(); j++) {
				Element databaseTableElement = databaseTableElements.get(j);
				tables.add(databaseTableElement.getAttributeValue(SystemInstallation.NAME_ATTRIBUTE));
			}
			this.getDatabaseTables().put(dbName, tables);
		}
	}
	
	protected Element toJdomElement() {
		Element element = new Element(SystemInstallation.SCHEMA_ELEMENT);
		element.setAttribute(SystemInstallation.STATUS_ATTRIBUTE, this.getStatus().toString());
		Iterator<String> nameIter = this.getDatabaseStatus().keySet().iterator();
		while (nameIter.hasNext()) {
			String dbName = nameIter.next();
			Element dbElement = new Element(SystemInstallation.DATASOURCE_ELEMENT);
			dbElement.setAttribute(SystemInstallation.NAME_ATTRIBUTE, dbName);
			dbElement.setAttribute(SystemInstallation.STATUS_ATTRIBUTE, this.getDatabaseStatus().get(dbName).toString());
			element.addContent(dbElement);
			List<String> tables = this.getDatabaseTables().get(dbName);
			if (null == tables) continue;
			for (int i = 0; i < tables.size(); i++) {
				String table = tables.get(i);
				Element tableElement = new Element(SystemInstallation.TABLE_ELEMENT);
				tableElement.setAttribute(SystemInstallation.NAME_ATTRIBUTE, table);
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