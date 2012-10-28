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
public class ComponentInstallationReport {
	
	private ComponentInstallationReport() {}
	
	protected ComponentInstallationReport(Element element) {
		String componentName = element.getAttributeValue(SystemInstallationReport.NAME_ATTRIBUTE);
		this.setComponentName(componentName);
		String dateString = element.getAttributeValue(SystemInstallationReport.DATE_ATTRIBUTE);
		Date date = DateConverter.parseDate(dateString, SystemInstallationReport.DATE_FORMAT);
		this.setDate(date);
		Element schemaElement = element.getChild(SystemInstallationReport.SCHEMA_ELEMENT);
		if (null != schemaElement) {
			this.setSchemaReport(new DatabaseInstallationReport(schemaElement));
		}
		Element dataElement = element.getChild(SystemInstallationReport.DATA_ELEMENT);
		if (null != dataElement) {
			this.setDataReport(new DataInstallationReport(dataElement));
		}
	}
	
	public static ComponentInstallationReport getInstance(String componentName) {
		ComponentInstallationReport report = new ComponentInstallationReport();
		report.setDate(new Date());
		report.setComponentName(componentName);
		report.setSchemaReport(new DatabaseInstallationReport());
		report.setDataReport(new DataInstallationReport());
		return report;
	}
	
	protected Element toJdomElement() {
		Element element = new Element(SystemInstallationReport.COMPONENT_ELEMENT);
		element.setAttribute(SystemInstallationReport.NAME_ATTRIBUTE, this.getComponentName());
		String dateString = DateConverter.getFormattedDate(this.getDate(), SystemInstallationReport.DATE_FORMAT);
		element.setAttribute(SystemInstallationReport.DATE_ATTRIBUTE, dateString);
		if (null != this.getStatus()) {
			element.setAttribute(SystemInstallationReport.STATUS_ATTRIBUTE, this.getStatus().toString());
		}
		Element schemaElement = this.getSchemaReport().toJdomElement();
		element.addContent(schemaElement);
		Element dataElement = this.getDataReport().toJdomElement();
		element.addContent(dataElement);
		return element;
	}
	
	public SystemInstallationReport.Status getStatus() {
		SystemInstallationReport.Status schemaStatus = this.getSchemaReport().getStatus();
		SystemInstallationReport.Status dataStatus = this.getDataReport().getStatus();
		SystemInstallationReport.Status incomplete = SystemInstallationReport.Status.INCOMPLETE;
		SystemInstallationReport.Status ok = SystemInstallationReport.Status.OK;
		if (schemaStatus.equals(incomplete) || dataStatus.equals(incomplete)) {
			return SystemInstallationReport.Status.INCOMPLETE;
		} else if (schemaStatus.equals(ok) && dataStatus.equals(ok)) {
			return SystemInstallationReport.Status.OK;
		} else {
			return SystemInstallationReport.Status.INIT;
		}
	}
	
	public String getComponentName() {
		return _componentName;
	}
	protected void setComponentName(String componentName) {
		this._componentName = componentName;
	}
	
	public Date getDate() {
		return _date;
	}
	protected void setDate(Date date) {
		this._date = date;
	}
	
	public DatabaseInstallationReport getSchemaReport() {
		return _schemaReport;
	}
	private void setSchemaReport(DatabaseInstallationReport schemaReport) {
		this._schemaReport = schemaReport;
	}
	
	public DataInstallationReport getDataReport() {
		return _dataReport;
	}
	private void setDataReport(DataInstallationReport dataReport) {
		this._dataReport = dataReport;
	}
	
	private String _componentName;
	private Date _date;
	
	private DatabaseInstallationReport _schemaReport;
	private DataInstallationReport _dataReport;
	
}
