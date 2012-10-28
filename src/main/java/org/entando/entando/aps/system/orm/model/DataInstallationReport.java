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
public class DataInstallationReport extends AbstractReport {
	
	protected DataInstallationReport() {}
	
	protected DataInstallationReport(Element element) {
		List<Element> databaseElements = element.getChildren(SystemInstallationReport.DATASOURCE_ELEMENT);
		for (int i = 0; i < databaseElements.size(); i++) {
			Element databaseElement = databaseElements.get(i);
			String dbName = databaseElement.getAttributeValue(SystemInstallationReport.NAME_ATTRIBUTE);
			String dbStatusString = databaseElement.getAttributeValue(SystemInstallationReport.STATUS_ATTRIBUTE);
			SystemInstallationReport.Status dbStatus = Enum.valueOf(SystemInstallationReport.Status.class, dbStatusString.toUpperCase());
			this.getDatabaseStatus().put(dbName, dbStatus);
		}
	}
	
	protected Element toJdomElement() {
		Element element = new Element(SystemInstallationReport.DATA_ELEMENT);
		element.setAttribute(SystemInstallationReport.STATUS_ATTRIBUTE, this.getStatus().toString());
		Iterator<String> nameIter = this.getDatabaseStatus().keySet().iterator();
		while (nameIter.hasNext()) {
			String dbName = nameIter.next();
			Element dbElement = new Element(SystemInstallationReport.DATASOURCE_ELEMENT);
			dbElement.setAttribute(SystemInstallationReport.NAME_ATTRIBUTE, dbName);
			dbElement.setAttribute(SystemInstallationReport.STATUS_ATTRIBUTE, this.getDatabaseStatus().get(dbName).toString());
			element.addContent(dbElement);
		}
		return element;
	}
	
}