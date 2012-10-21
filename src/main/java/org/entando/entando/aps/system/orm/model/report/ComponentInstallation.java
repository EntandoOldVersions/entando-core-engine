/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model.report;

import com.agiletec.aps.util.DateConverter;

import java.util.*;

import org.jdom.Element;

/**
 * @author E.Santoboni
 */
public class ComponentInstallation {
	
	private ComponentInstallation() {}
	
	protected ComponentInstallation(Element element) {
		String componentName = element.getAttributeValue(SystemInstallation.NAME_ATTRIBUTE);
		this.setComponentName(componentName);
		String dateString = element.getAttributeValue(SystemInstallation.DATE_ATTRIBUTE);
		Date date = DateConverter.parseDate(dateString, SystemInstallation.DATE_FORMAT);
		this.setDate(date);
		Element schemaElement = element.getChild(SystemInstallation.SCHEMA_ELEMENT);
		this.setSchemaReport(new DatabaseInstallation(schemaElement));
		Element dataElement = element.getChild(SystemInstallation.DATA_ELEMENT);
		this.setDataReport(new DataInstallation(dataElement));
	}
	
	public static ComponentInstallation getInstance(String componentName) {
		ComponentInstallation report = new ComponentInstallation();
		report.setDate(new Date());
		report.setComponentName(componentName);
		report.setSchemaReport(new DatabaseInstallation());
		report.setDataReport(new DataInstallation());
		return report;
	}
	
	protected Element toJdomElement() {
		Element element = new Element(SystemInstallation.COMPONENT_ELEMENT);
		element.setAttribute(SystemInstallation.NAME_ATTRIBUTE, this.getComponentName());
		String dateString = DateConverter.getFormattedDate(this.getDate(), SystemInstallation.DATE_FORMAT);
		element.setAttribute(SystemInstallation.DATE_ATTRIBUTE, dateString);
		if (null != this.getStatus()) {
			element.setAttribute(SystemInstallation.STATUS_ATTRIBUTE, this.getStatus().toString());
		}
		Element schemaElement = this.getSchemaReport().toJdomElement();
		element.addContent(schemaElement);
		Element dataElement = this.getDataReport().toJdomElement();
		element.addContent(dataElement);
		return element;
	}
	
	public SystemInstallation.Status getStatus() {
		SystemInstallation.Status schemaStatus = this.getSchemaReport().getStatus();
		SystemInstallation.Status dataStatus = this.getDataReport().getStatus();
		SystemInstallation.Status incomplete = SystemInstallation.Status.INCOMPLETE;
		SystemInstallation.Status ok = SystemInstallation.Status.OK;
		if (schemaStatus.equals(incomplete) || dataStatus.equals(incomplete)) {
			return SystemInstallation.Status.INCOMPLETE;
		} else if (schemaStatus.equals(ok) && dataStatus.equals(ok)) {
			return SystemInstallation.Status.OK;
		} else {
			return SystemInstallation.Status.INIT;
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
	
	public DatabaseInstallation getSchemaReport() {
		return _schemaReport;
	}
	private void setSchemaReport(DatabaseInstallation schemaReport) {
		this._schemaReport = schemaReport;
	}
	
	public DataInstallation getDataReport() {
		return _dataReport;
	}
	private void setDataReport(DataInstallation dataReport) {
		this._dataReport = dataReport;
	}
	
	private String _componentName;
	private Date _date;
	
	private DatabaseInstallation _schemaReport;
	private DataInstallation _dataReport;
	
}
