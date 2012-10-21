/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model.report;

import java.util.Iterator;
import java.util.List;
import org.jdom.Element;

/**
 * @author E.Santoboni
 */
public class DataInstallation extends AbstractReport {
	
	protected DataInstallation() {}
	
	protected DataInstallation(Element element) {
		List<Element> databaseElements = element.getChildren(SystemInstallation.DATASOURCE_ELEMENT);
		for (int i = 0; i < databaseElements.size(); i++) {
			Element databaseElement = databaseElements.get(i);
			String dbName = databaseElement.getAttributeValue(SystemInstallation.NAME_ATTRIBUTE);
			String dbStatusString = databaseElement.getAttributeValue(SystemInstallation.STATUS_ATTRIBUTE);
			SystemInstallation.Status dbStatus = Enum.valueOf(SystemInstallation.Status.class, dbStatusString.toUpperCase());
			this.getDatabaseStatus().put(dbName, dbStatus);
		}
	}
	
	protected Element toJdomElement() {
		Element element = new Element(SystemInstallation.DATA_ELEMENT);
		element.setAttribute(SystemInstallation.STATUS_ATTRIBUTE, this.getStatus().toString());
		Iterator<String> nameIter = this.getDatabaseStatus().keySet().iterator();
		while (nameIter.hasNext()) {
			String dbName = nameIter.next();
			Element dbElement = new Element(SystemInstallation.DATASOURCE_ELEMENT);
			dbElement.setAttribute(SystemInstallation.NAME_ATTRIBUTE, dbName);
			dbElement.setAttribute(SystemInstallation.STATUS_ATTRIBUTE, this.getDatabaseStatus().get(dbName).toString());
			element.addContent(dbElement);
		}
		return element;
	}
	
}