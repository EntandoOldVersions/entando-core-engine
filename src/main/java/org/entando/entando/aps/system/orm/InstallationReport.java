/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.util.DateConverter;

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
	
	public InstallationReport(String xmlText) {
		if (null == xmlText || xmlText.trim().length() == 0) return;
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		StringReader reader = new StringReader(xmlText);
		try {
			Document doc = builder.build(reader);
			Element rootElement = doc.getRootElement();
			List<Element> elements = rootElement.getChildren("report");
			for (int i = 0; i < elements.size(); i++) {
				Element element = elements.get(i);
				SingleReport report = new SingleReport(element);
				this.getReports().add(report);
			}
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().severe("Error parsing Report: " + t.getMessage());
			throw new RuntimeException("Error detected while parsing the XML", t);
		}
	}
	
	public void addReport(String component, Date date) {
		SingleReport report = new SingleReport(component, date);
		this.getReports().add(report);
	}
	
	public String toXml() {
		Document doc = new Document();
		Element rootElement = new Element("reports");
		for (int i = 0; i < this.getReports().size(); i++) {
			SingleReport singleReport = this.getReports().get(i);
			Element element = singleReport.toJdomElement();
			rootElement.addContent(element);
		}
		doc.setRootElement(rootElement);
		XMLOutputter out = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setIndent("");
		out.setFormat(format);
		return out.outputString(doc);
	}
	
	public boolean isInstallationDone(String component) {
		for (int i = 0; i < this.getReports().size(); i++) {
			SingleReport singleReport = this.getReports().get(i);
			if (singleReport.getComponent().equals(component)) return true;
		}
		return false;
	}
	
	public SingleReport getReport(String component) {
		for (int i = 0; i < this.getReports().size(); i++) {
			SingleReport singleReport = this.getReports().get(i);
			if (singleReport.getComponent().equals(component)) return singleReport;
		}
		return null;
	}
	
	public List<SingleReport> getReports() {
		return _reports;
	}
	
	private List<SingleReport> _reports = new ArrayList<SingleReport>();
	
	public class SingleReport {
		
		protected SingleReport(String component, Date date) {
			this.setComponent(component);
			this.setDate(date);
		}
		
		protected SingleReport(Element element) {
			String component = element.getAttributeValue("component");
			String dateString = element.getAttributeValue("date");
			Date date = DateConverter.parseDate(dateString, "yyyy-MM-dd hh:mm");
			this.setComponent(component);
			this.setDate(date);
		}
		
		protected Element toJdomElement() {
			Element element = new Element("report");
			element.setAttribute("component", this.getComponent());
			String dateString = DateConverter.getFormattedDate(this.getDate(), "yyyy-MM-dd hh:mm");
			element.setAttribute("date", dateString);
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
		
		private String _component;
		private Date _date;
		
	}
	
}