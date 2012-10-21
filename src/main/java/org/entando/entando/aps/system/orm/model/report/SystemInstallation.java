/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model.report;

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
public class SystemInstallation {
	
	private SystemInstallation(Status status) {
		this.setStatus(status);
		this.setCreation(new Date());
	}
	
	public SystemInstallation(String xmlText) {
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
			String statusString = rootElement.getAttributeValue(STATUS_ATTRIBUTE);
			if (null != statusString && statusString.trim().length() > 0) {
				SystemInstallation.Status status = Enum.valueOf(SystemInstallation.Status.class, statusString.toUpperCase());
				this.setStatus(status);
			}
			Element creationElement = rootElement.getChild(CREATION_ELEMENT);
			if (null != creationElement) {
				Date date = DateConverter.parseDate(creationElement.getText(), DATE_FORMAT);
				this.setCreation(date);
			}
			Element lastUpdateElement = rootElement.getChild(LAST_UPDATE_ELEMENT);
			if (null != lastUpdateElement) {
				Date date = DateConverter.parseDate(lastUpdateElement.getText(), DATE_FORMAT);
				this.setLastUpdate(date);
			}
			Element componentsElement = rootElement.getChild(COMPONENTS_ELEMENT);
			if (null == componentsElement) return;
			List<Element> elements = componentsElement.getChildren(COMPONENT_ELEMENT);
			for (int i = 0; i < elements.size(); i++) {
				Element element = elements.get(i);
				ComponentInstallation report = new ComponentInstallation(element);
				this.getReports().add(report);
			}
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().severe("Error parsing Report: " + t.getMessage());
			throw new RuntimeException("Error detected while parsing the XML", t);
		}
	}
	
	public static SystemInstallation getInstance() {
		return new SystemInstallation(Status.INIT);
	}
	
	public static SystemInstallation getPortingInstance() {
		return new SystemInstallation(Status.PORTING);
	}
	
	public ComponentInstallation addReport(String component) {
		ComponentInstallation report = ComponentInstallation.getInstance(component);//new ComponentReport(component, new Date(), this.getStatus());
		this.getReports().add(report);
		return report;
	}
	
	public void addReport(ComponentInstallation report) {
		this.getReports().add(report);
	}
	
	public String toXml() {
		Document doc = new Document();
		Element rootElement = new Element(ROOT_ELEMENT);
		Status status = Status.OK;
		for (int i = 0; i < this.getReports().size(); i++) {
			ComponentInstallation componentReport = this.getReports().get(i);
			if (!componentReport.getStatus().equals(Status.OK)) {
				status = componentReport.getStatus();
				break;
			} 
		}
		rootElement.setAttribute(STATUS_ATTRIBUTE, status.toString());
		
		Element creationElement = new Element(CREATION_ELEMENT);
		creationElement.setText(DateConverter.getFormattedDate(this.getCreation(), DATE_FORMAT));
		rootElement.addContent(creationElement);
		
		Element lastUpdateElement = new Element(LAST_UPDATE_ELEMENT);
		lastUpdateElement.setText(DateConverter.getFormattedDate(this.getLastUpdate(), DATE_FORMAT));
		rootElement.addContent(lastUpdateElement);
		
		Element componentsElement = new Element(COMPONENTS_ELEMENT);
		rootElement.addContent(componentsElement);
		for (int i = 0; i < this.getReports().size(); i++) {
			ComponentInstallation singleReport = this.getReports().get(i);
			Element componentElement = singleReport.toJdomElement();
			componentsElement.addContent(componentElement);
		}
		doc.setRootElement(rootElement);
		XMLOutputter out = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setIndent("\t");
		out.setFormat(format);
		return out.outputString(doc);
	}
	
	public ComponentInstallation getComponentReport(String componentName, boolean addIfNotExist) {
		for (int i = 0; i < this.getReports().size(); i++) {
			ComponentInstallation singleReport = this.getReports().get(i);
			if (singleReport.getComponentName().equals(componentName)) return singleReport;
		}
		if (addIfNotExist) {
			return this.addReport(componentName);
		}
		return null;
	}
	
	public Date getCreation() {
		return _creation;
	}
	protected void setCreation(Date creation) {
		this._creation = creation;
	}
	
	public Date getLastUpdate() {
		return _lastUpdate;
	}
	protected void setLastUpdate(Date lastUpdate) {
		this._lastUpdate = lastUpdate;
	}
	
	public Status getStatus() {
		return _status;
	}
	public void setStatus(Status status) {
		this._status = status;
	}
	
	public boolean isUpdated() {
		return _updated;
	}
	public void setUpdated() {
		this.setLastUpdate(new Date());
		this._updated = true;
	}
	
	public List<ComponentInstallation> getReports() {
		return _reports;
	}
	
	private Date _creation;
	private Date _lastUpdate;
	private Status _status;
	private boolean _updated;
	private List<ComponentInstallation> _reports = new ArrayList<ComponentInstallation>();
	
	public enum Status {OK, PORTING, RESTORE, INCOMPLETE, NOT_AVAILABLE, INIT}
	
	protected static final String ROOT_ELEMENT = "reports";
	protected static final String CREATION_ELEMENT = "creation";
	protected static final String LAST_UPDATE_ELEMENT = "lastupdate";
	protected static final String COMPONENTS_ELEMENT = "components";
	protected static final String COMPONENT_ELEMENT = "component";
	protected static final String NAME_ATTRIBUTE = "name";
	protected static final String DATE_ATTRIBUTE = "date";
	protected static final String DATA_ELEMENT = "data";
	protected static final String SCHEMA_ELEMENT = "schema";
	protected static final String STATUS_ATTRIBUTE = "status";
	protected static final String DATASOURCE_ELEMENT = "datasource";
	protected static final String TABLE_ELEMENT = "table";
	
	protected static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
}