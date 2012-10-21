/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model.report;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.util.DateConverter;

import java.io.StringReader;

import java.util.*;

import org.apache.commons.beanutils.BeanComparator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * @author E.Santoboni
 */
public class DatabaseDump {
	
	public DatabaseDump() {}
	
	public DatabaseDump(String xmlText) {
		if (null == xmlText || xmlText.trim().length() == 0) {
			return;
		}
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		StringReader reader = new StringReader(xmlText);
		try {
			Document doc = builder.build(reader);
			Element rootElement = doc.getRootElement();
			Element dateElement = rootElement.getChild(DATE_ELEMENT);
			if (null != dateElement) {
				Date date = DateConverter.parseDate(dateElement.getText(), DATE_FORMAT);
				this.setDate(date);
			}
			List<Element> elements = rootElement.getChildren(DATASOURCE_ELEMENT);
			for (int i = 0; i < elements.size(); i++) {
				Element dataSourceElement = elements.get(i);
				String dataSourceName = dataSourceElement.getAttributeValue(NAME_ATTRIBUTE);
				List<Element> tableElements = dataSourceElement.getChildren(TABLE_ELEMENT);
				for (int j = 0; j < tableElements.size(); j++) {
					Element tableElement = tableElements.get(j);
					TableDump tableDumpReport = new TableDump(tableElement);
					this.addTableReport(dataSourceName, tableDumpReport);
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().severe("Error parsing Report: " + t.getMessage());
			throw new RuntimeException("Error detected while parsing the XML", t);
		}
	}
	
	public String toXml() {
		try {
			Document doc = new Document();
			Element rootElement = new Element(ROOT_ELEMENT);
			Element dateElement = new Element(DATE_ELEMENT);
			dateElement.setText(DateConverter.getFormattedDate(this.getDate(), DATE_FORMAT));
			rootElement.addContent(dateElement);
			List<String> dataSourceNames = new ArrayList<String>();
			dataSourceNames.addAll(this.getDataSourcesReports().keySet());
			for (int i = 0; i < dataSourceNames.size(); i++) {
				String dataSourceName = dataSourceNames.get(i);
				Element dataSourceElement = new Element(DATASOURCE_ELEMENT);
				rootElement.addContent(dataSourceElement);
				dataSourceElement.setAttribute(NAME_ATTRIBUTE, dataSourceName);
				List<TableDump> tableReports = this.getDataSourcesReports().get(dataSourceName);
				BeanComparator comparator = new BeanComparator("tableName");
				Collections.sort(tableReports, comparator);
				for (int j = 0; j < tableReports.size(); j++) {
					TableDump tableDumpReport = tableReports.get(j);
					dataSourceElement.addContent(tableDumpReport.toJdomElement());
				}
			}
			doc.setRootElement(rootElement);
			XMLOutputter out = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setIndent("\t");
			out.setFormat(format);
			return out.outputString(doc);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "toXml");
			throw new RuntimeException("Error creating XML", t);
		}
	}
	
	public Date getDate() {
		return _date;
	}
	public void setDate(Date date) {
		this._date = date;
	}
	
	public void addTableReport(String dataSourceName, TableDump tableDumpReport) {
		List<TableDump> dataSourceReports = this.getDataSourcesReports().get(dataSourceName);
		if (null == dataSourceReports) {
			dataSourceReports = new ArrayList<TableDump>();
			this.getDataSourcesReports().put(dataSourceName, dataSourceReports);
		}
		dataSourceReports.add(tableDumpReport);
	}
	
	public List<String> getDataSourceNames() {
		List<String> list = new ArrayList<String>();
		if (null != this.getDataSourcesReports()) {
			list.addAll(this.getDataSourcesReports().keySet());
		}
		Collections.sort(list);
		return list;
	}
	
	public Map<String, List<TableDump>> getDataSourcesReports() {
		return _dataSourcesReports;
	}
	public void setDataSourcesReports(Map<String, List<TableDump>> dataSourcesReports) {
		this._dataSourcesReports = dataSourcesReports;
	}
	
	public long getRequiredTime() {
		return _requiredTime;
	}
	public void setRequiredTime(long requiredTime) {
		this._requiredTime = requiredTime;
	}
	
	private Date _date;
	private Map<String, List<TableDump>> _dataSourcesReports = new HashMap<String, List<TableDump>>();
	private long _requiredTime;
	
	private static final String ROOT_ELEMENT = "backup";
	private static final String DATE_ELEMENT = "date";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String DATASOURCE_ELEMENT = "datasource";
	
	protected static final String TABLE_ELEMENT = "table";
	protected static final String NAME_ATTRIBUTE = "name";
	protected static final String REQUIRED_TIME_ATTRIBUTE = "requiredTime";
	protected static final String ROWS_ATTRIBUTE = "rows";
	
}