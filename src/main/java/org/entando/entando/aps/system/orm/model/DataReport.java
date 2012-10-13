/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model;

import java.util.Iterator;
import java.util.List;
import org.jdom.Element;

/**
 * @author E.Santoboni
 */
public class DataReport extends AbstractReport {
	
	protected DataReport() {}
	
	protected DataReport(Element element) {
		List<Element> databaseElements = element.getChildren("database");
		for (int i = 0; i < databaseElements.size(); i++) {
			Element databaseElement = databaseElements.get(i);
			String dbName = databaseElement.getAttributeValue("name");
			String dbStatusString = databaseElement.getAttributeValue("status");
			InstallationReport.Status dbStatus = Enum.valueOf(InstallationReport.Status.class, dbStatusString.toUpperCase());
			this.getDatabaseStatus().put(dbName, dbStatus);
		}
	}

	protected Element toJdomElement() {
		Element element = new Element("data");
		element.setAttribute("status", this.getStatus().toString());
		Iterator<String> nameIter = this.getDatabaseStatus().keySet().iterator();
		while (nameIter.hasNext()) {
			String dbName = nameIter.next();
			Element dbElement = new Element("database");
			dbElement.setAttribute("name", dbName);
			dbElement.setAttribute("status", this.getDatabaseStatus().get(dbName).toString());
			element.addContent(dbElement);
		}
		return element;
	}
	
}
