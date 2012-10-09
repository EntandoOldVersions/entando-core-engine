/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model;

import com.agiletec.aps.system.ApsSystemUtils;

import java.io.StringReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * @author E.Santoboni
 */
public class InstallationReport {
	
	private InstallationReport(Status status) {
		this.setStatus(status);
	}
	
	public InstallationReport(String xmlText) {
		if (null == xmlText || xmlText.trim().length() == 0) {
			this.setStatus(Status.PORTING);
			return;
		}
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		StringReader reader = new StringReader(xmlText);
		try {
			Document doc = builder.build(reader);
			Element rootElement = doc.getRootElement();
			List<Element> elements = rootElement.getChildren("report");
			for (int i = 0; i < elements.size(); i++) {
				Element element = elements.get(i);
				ComponentReport report = new ComponentReport(element);
				this.getReports().add(report);
			}
			String statusString = rootElement.getAttributeValue("status");
			if (null != statusString && statusString.trim().length() > 0) {
				InstallationReport.Status status = Enum.valueOf(InstallationReport.Status.class, statusString.toUpperCase());
				this.setStatus(status);
			}
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().severe("Error parsing Report: " + t.getMessage());
			throw new RuntimeException("Error detected while parsing the XML", t);
		}
	}
	
	public static InstallationReport getInstance() {
		return new InstallationReport(Status.INIT);
	}
	
	public static InstallationReport getPortingInstance() {
		return new InstallationReport(Status.PORTING);
	}
	
	public void addReport(String component, Date date) {
		ComponentReport report = new ComponentReport(component, date, this.getStatus());
		this.getReports().add(report);
	}
	
	public void addReport(ComponentReport report) {
		this.getReports().add(report);
	}
	
	public String toXml() {
		Document doc = new Document();
		Element rootElement = new Element("reports");
		if (null != this.getStatus()) {
			rootElement.setAttribute("status", this.getStatus().toString());
		}
		for (int i = 0; i < this.getReports().size(); i++) {
			ComponentReport singleReport = this.getReports().get(i);
			Element element = singleReport.toJdomElement();
			rootElement.addContent(element);
		}
		doc.setRootElement(rootElement);
		XMLOutputter out = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setIndent("\t");
		out.setFormat(format);
		return out.outputString(doc);
	}
	
	public boolean isInstallationDone(String component) {
		for (int i = 0; i < this.getReports().size(); i++) {
			ComponentReport singleReport = this.getReports().get(i);
			if (singleReport.getComponent().equals(component)) return true;
		}
		return false;
	}
	
	public ComponentReport getComponentReport(String component) {
		for (int i = 0; i < this.getReports().size(); i++) {
			ComponentReport singleReport = this.getReports().get(i);
			if (singleReport.getComponent().equals(component)) return singleReport;
		}
		return null;
	}
	
	public Status getStatus() {
		return _status;
	}
	public void setStatus(Status status) {
		this._status = status;
	}
	
	public List<ComponentReport> getReports() {
		return _reports;
	}
	
	private Status _status;
	private List<ComponentReport> _reports = new ArrayList<ComponentReport>();
	
	public enum Status {OK, PORTING, RESTORE, INCOMPLETE, NOT_AVAILABLE, INIT}
	
}