/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model;

import java.util.*;
import org.entando.entando.aps.system.orm.model.InstallationReport.Status;
import org.jdom.Element;

/**
 * @author E.Santoboni
 */
public class SchemaReport extends AbstractReport {
	/*
	protected SchemaReport() {
		this.setStatus(Status.INIT);
	}
	*/
	protected SchemaReport(Status status) {
		this.setStatus(status);
	}
	
	/*
	<schema status="">
		<database name="........" status=".....">
			<table name="...." status="OK">
		</database>
		<database name="........" status="NOT_AVAILABLE" />
	</schema>
		*/

	protected SchemaReport(Element element) {
		String statusString = element.getAttributeValue("status");
		InstallationReport.Status status = Enum.valueOf(InstallationReport.Status.class, statusString.toUpperCase());
		this.setStatus(status);
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
				tableElement.setAttribute("status", "OK");
				dbElement.addContent(tableElement);
			}
		}
		return element;
	}
	/*
	public static SchemaReport getInstance() {
		SchemaReport report = new SchemaReport();

		return report;
	}
	*/
	/*
	public Status getStatus() {
		return _status;
	}
	public void setStatus(Status status) {
		this._status = status;
	}

	public Map<String, Status> getDatabaseStatus() {
		return _databaseStatus;
	}
	*/
	/*
	public void setDatabaseStatus(Map<String, Status> databaseStatus) {
		this._databaseStatus = databaseStatus;
	}
	*/
	public Map<String, List<String>> getDatabaseTables() {
		return _databaseTables;
	}
	/*
	public void setDatabaseTables(Map<String, List<String>> databaseTables) {
		this._databaseTables = databaseTables;
	}
	*/
	//private InstallationReport.Status _status;
	//private Map<String, InstallationReport.Status> _databaseStatus = new HashMap<String, InstallationReport.Status>();
	private Map<String, List<String>> _databaseTables = new HashMap<String, List<String>>();
	
}
