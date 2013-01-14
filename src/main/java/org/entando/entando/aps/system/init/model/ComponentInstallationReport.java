/*
*
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* Entando is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package org.entando.entando.aps.system.init.model;

import com.agiletec.aps.util.DateConverter;

import java.util.Date;
import org.entando.entando.aps.system.init.model.SystemInstallationReport.Status;

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
		Element postProcessElement = element.getChild(SystemInstallationReport.COMPONENT_POST_PROCESS_ELEMENT);
		if (null != postProcessElement) {
			String postProcessStatusString = postProcessElement.getAttributeValue(SystemInstallationReport.STATUS_ATTRIBUTE);
			if (null != postProcessStatusString) {
				SystemInstallationReport.Status postProcessStatus = 
						Enum.valueOf(SystemInstallationReport.Status.class, postProcessStatusString.toUpperCase());
				this.setPostProcessStatus(postProcessStatus);
			}
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
		if (null != this.getPostProcessStatus()) {
			Element postProcessElement = new Element(SystemInstallationReport.COMPONENT_POST_PROCESS_ELEMENT);
			postProcessElement.setAttribute(SystemInstallationReport.STATUS_ATTRIBUTE, this.getPostProcessStatus().toString());
			element.addContent(postProcessElement);
		}
		return element;
	}
	
	public SystemInstallationReport.Status getStatus() {
		SystemInstallationReport.Status schemaStatus = this.getSchemaReport().getStatus();
		SystemInstallationReport.Status dataStatus = this.getDataReport().getStatus();
		SystemInstallationReport.Status incomplete = SystemInstallationReport.Status.INCOMPLETE;
		SystemInstallationReport.Status notAvailable = SystemInstallationReport.Status.NOT_AVAILABLE;
		SystemInstallationReport.Status ok = SystemInstallationReport.Status.OK;
		SystemInstallationReport.Status postProcessStatus = (null != this.getPostProcessStatus()) ? this.getPostProcessStatus() : notAvailable;
		boolean postProcessOk = (postProcessStatus.equals(ok) || postProcessStatus.equals(notAvailable));
		if (schemaStatus.equals(incomplete) || dataStatus.equals(incomplete) || 
				(!postProcessOk && !postProcessStatus.equals(SystemInstallationReport.Status.NOT_AVAILABLE))) {
			return SystemInstallationReport.Status.INCOMPLETE;
		} else if (schemaStatus.equals(ok) && dataStatus.equals(ok) && 
				(postProcessOk || postProcessStatus.equals(SystemInstallationReport.Status.NOT_AVAILABLE))) {
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
	
	public Status getPostProcessStatus() {
		if ("entandoCore".equals(this.getComponentName())) {
			return Status.NOT_AVAILABLE;
		}
		return _postProcessStatus;
	}
	public void setPostProcessStatus(Status postProcessStatus) {
		this._postProcessStatus = postProcessStatus;
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
	private SystemInstallationReport.Status _postProcessStatus = SystemInstallationReport.Status.INIT;
	
	private DatabaseInstallationReport _schemaReport;
	private DataInstallationReport _dataReport;
	
}
