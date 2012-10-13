/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model;

import com.agiletec.aps.util.DateConverter;

import java.util.*;

import org.jdom.Element;

/**
 * @author E.Santoboni
 */
public class ComponentReport {
	
	private ComponentReport() {}
	
	protected ComponentReport(Element element) {
		String component = element.getAttributeValue("component");
		this.setComponent(component);
		String dateString = element.getAttributeValue("date");
		Date date = DateConverter.parseDate(dateString, "yyyy-MM-dd HH:mm:ss");
		this.setDate(date);
		Element schemaElement = element.getChild("schema");
		this.setSchemaReport(new SchemaReport(schemaElement));
		Element dataElement = element.getChild("data");
		this.setDataReport(new DataReport(dataElement));
	}
	
	public static ComponentReport getInstance(String component) {
		ComponentReport report = new ComponentReport();
		report.setDate(new Date());
		report.setComponent(component);
		report.setSchemaReport(new SchemaReport());
		report.setDataReport(new DataReport());
		return report;
	}

	protected Element toJdomElement() {
		Element element = new Element("report");
		element.setAttribute("component", this.getComponent());
		String dateString = DateConverter.getFormattedDate(this.getDate(), "yyyy-MM-dd HH:mm:ss");
		element.setAttribute("date", dateString);
		if (null != this.getStatus()) {
			element.setAttribute("status", this.getStatus().toString());
		}
		Element schemaElement = this.getSchemaReport().toJdomElement();
		element.addContent(schemaElement);
		Element dataElement = this.getDataReport().toJdomElement();
		element.addContent(dataElement);
		return element;
	}
	
	public InstallationReport.Status getStatus() {
		InstallationReport.Status schemaStatus = this.getSchemaReport().getStatus();
		InstallationReport.Status dataStatus = this.getDataReport().getStatus();
		InstallationReport.Status incomplete = InstallationReport.Status.INCOMPLETE;
		InstallationReport.Status ok = InstallationReport.Status.OK;
		if (schemaStatus.equals(incomplete) || dataStatus.equals(incomplete)) {
			return InstallationReport.Status.INCOMPLETE;
		} else if (schemaStatus.equals(ok) && dataStatus.equals(ok)) {
			return InstallationReport.Status.OK;
		} else {
			return InstallationReport.Status.INIT;
		}
	}
	
	public String getComponent() {
		return _component;
	}
	protected void setComponent(String component) {
		this._component = component;
	}
	
	public Date getDate() {
		return _date;
	}
	protected void setDate(Date date) {
		this._date = date;
	}
	
	public SchemaReport getSchemaReport() {
		return _schemaReport;
	}
	private void setSchemaReport(SchemaReport schemaReport) {
		this._schemaReport = schemaReport;
	}
	
	public DataReport getDataReport() {
		return _dataReport;
	}
	private void setDataReport(DataReport dataReport) {
		this._dataReport = dataReport;
	}
	
	private String _component;
	private Date _date;
	
	private SchemaReport _schemaReport;
	private DataReport _dataReport;
	
}
