/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model;

import com.agiletec.aps.util.DateConverter;
import java.util.*;
import org.entando.entando.aps.system.orm.model.InstallationReport.Status;
import org.jdom.Element;

/**
 * @author E.Santoboni
 */
public class ComponentReport {
	
	private ComponentReport() {}
	
	protected ComponentReport(String component, Date date, InstallationReport.Status status) {
		this.setComponent(component);
		this.setDate(date);
		this.setStatus(status);
		this.setSchemaReport(new SchemaReport(status));
		this.setDataReport(new DataReport(status));
	}
	
	protected ComponentReport(Element element) {
		String component = element.getAttributeValue("component");
		this.setComponent(component);
		String dateString = element.getAttributeValue("date");
		Date date = DateConverter.parseDate(dateString, "yyyy-MM-dd HH:mm:ss");
		this.setDate(date);
		String statusString = element.getAttributeValue("status");
		if (null != statusString && statusString.trim().length() > 0) {
			InstallationReport.Status status = Enum.valueOf(InstallationReport.Status.class, statusString.toUpperCase());
			this.setStatus(status);
		}
		Element schemaElement = element.getChild("schema");
		this.setSchemaReport(new SchemaReport(schemaElement));
		Element dataElement = element.getChild("data");
		this.setDataReport(new DataReport(dataElement));
		
		/*
		String schemaResultString = element.getAttributeValue("schema");
		if (null != schemaResultString && schemaResultString.trim().length() > 0) {
			InstallationReport.Result res = Enum.valueOf(InstallationReport.Result.class, schemaResultString.toUpperCase());
			this.setSchemaResult(res);
		}
		String dataResultString = element.getAttributeValue("data");
		if (null != dataResultString && dataResultString.trim().length() > 0) {
			InstallationReport.Result res = Enum.valueOf(InstallationReport.Result.class, dataResultString.toUpperCase());
			this.setDataResult(res);
		}
		*/
	}
	
	public static ComponentReport getInstance(String component) {
		ComponentReport report = new ComponentReport();
		report.setStatus(Status.INIT);
		report.setDate(new Date());
		report.setComponent(component);
		report.setSchemaReport(new SchemaReport(Status.INIT));
		report.setDataReport(new DataReport(Status.INIT));
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
		/*
		if (null != this.getSchemaResult()) {
			element.setAttribute("schema", this.getSchemaResult().toString());
		}
		if (null != this.getDataResult()) {
			element.setAttribute("data", this.getDataResult().toString());
		}
		*/
		return element;
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
	
	public Status getStatus() {
		return _status;
	}
	public void setStatus(Status status) {
		this._status = status;
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
	
	/*
	public InstallationReport.Flag getFlag() {
		return _flag;
	}

	protected void setFlag(InstallationReport.Flag flag) {
		this._flag = flag;
	}

	public InstallationReport.Result getDataResult() {
		return _dataResult;
	}

	public void setDataResult(InstallationReport.Result dataResult) {
		this._dataResult = dataResult;
	}

	public InstallationReport.Result getSchemaResult() {
		return _schemaResult;
	}

	public void setSchemaResult(InstallationReport.Result schemaResult) {
		this._schemaResult = schemaResult;
	}
	*/
	private String _component;
	private Date _date;
	private Status _status;
	private SchemaReport _schemaReport;
	private DataReport _dataReport;
	/*
	private InstallationReport.Result _schemaResult;
	private InstallationReport.Result _dataResult;
	*/
	
	/*
	<report component="jacms" date="2012-10-07 06:17" status="..........">
		<schema status="">
			<database name="........" status=".....">
				<table name="...." status="OK">
			</database>
			<database name="........" status="NOT_AVAILABLE" />
		</schema>
		<data status="....">
			<database name="........" status="......." />
			<database name="........" status="NOT_AVAILABLE" />
		</schema>
	</report>
	*/
	
	
	
}
