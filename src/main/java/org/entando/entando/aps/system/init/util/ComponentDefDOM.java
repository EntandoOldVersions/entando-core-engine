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
package org.entando.entando.aps.system.init.util;

import java.io.StringReader;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.entando.entando.aps.system.init.Component;
import org.entando.entando.aps.system.init.ComponentEnvinroment;
import org.jdom.Element;

/**
 * @author E.Santoboni
 */
public class ComponentDefDOM {
    
    public ComponentDefDOM(String xmlText, String path) throws ApsSystemException {
        //this.validate(xmlText, definitionPath);
        ApsSystemUtils.getLogger().info("Loading Component from file : " + path);
        this.decodeDOM(xmlText);
    }
    /*
    private void validate(String xmlText, String definitionPath) throws ApsSystemException {
        SchemaFactory factory =
                SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        InputStream schemaIs = null;
        InputStream xmlIs = null;
        try {
            schemaIs = this.getClass().getResourceAsStream("componentDef-3.0.xsd");
            Source schemaSource = new StreamSource(schemaIs);
            Schema schema = factory.newSchema(schemaSource);
            Validator validator = schema.newValidator();
            xmlIs = new ByteArrayInputStream(xmlText.getBytes("UTF-8"));
            Source source = new StreamSource(xmlIs);
            validator.validate(source);
            ApsSystemUtils.getLogger().info("Valid Component definition : " + definitionPath);
        } catch (Throwable t) {
            String message = "Error validating Component definition : " + definitionPath;
            ApsSystemUtils.logThrowable(t, this, "this", message);
            throw new ApsSystemException(message, t);
        } finally {
            try {
                if (null != schemaIs) {
                    schemaIs.close();
                }
                if (null != xmlIs) {
                    xmlIs.close();
                }
            } catch (IOException e) {
                ApsSystemUtils.logThrowable(e, this, "this");
            }
        }
    }
    */
    public Component getComponent() {
        Component component = new Component();
        try {
            Element rootElement = this._doc.getRootElement();
			String code = rootElement.getChildText("code");
			component.setCode(code);
			Element dependenciesElement = rootElement.getChild("dependencies");
			if (null != dependenciesElement) {
				List<Element> dependenciesElementd = dependenciesElement.getChildren("code");
				for (int i = 0; i < dependenciesElementd.size(); i++) {
					Element element = dependenciesElementd.get(i);
					component.addDependency(element.getText());
				}
			}
			Element installationElement = rootElement.getChild("installation");
			if (null != installationElement) {
				Element tableMappingElement = installationElement.getChild("tableMapping");
				this.extractTableMapping(tableMappingElement, component);
				
				List<Element> enviromentElements = installationElement.getChildren("environment");
				if (enviromentElements.size() > 0) {
					component.setEnvironments(new HashMap<String, ComponentEnvinroment>());
				}
				for (int i = 0; i < enviromentElements.size(); i++) {
					Element environmentElement = enviromentElements.get(i);
					this.extractEnvinroment(environmentElement, component);
				}
				//TODO COMPLETE WITH post Process
			}
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getComponent", "Error loading component");
        }
        return component;
    }
	
	private void extractTableMapping(Element tableMappingElement, Component component) {
		if (null != tableMappingElement) {
			component.setTableMapping(new HashMap<String, List<String>>());
			List<Element> datasourceElements = tableMappingElement.getChildren("datasource");
			for (int i = 0; i < datasourceElements.size(); i++) {
				Element datasourceElement = datasourceElements.get(i);
				String datasourceName = datasourceElement.getAttributeValue("name");
				List<String> tableMapping = new ArrayList<String>();
				List<Element> tableClasses = datasourceElement.getChildren("class");
				for (int j = 0; j < tableClasses.size(); j++) {
					tableMapping.add(tableClasses.get(j).getText());
				}
				if (tableMapping.size() > 0) {
					component.getTableMapping().put(datasourceName, tableMapping);
				}
			}
		}
	}
	
	private void extractEnvinroment(Element environmentElement, Component component) {
		String environmentCode = environmentElement.getAttributeValue("code");
		ComponentEnvinroment envinroment = new ComponentEnvinroment(environmentCode);
		Element defaultSqlResourcesElement = environmentElement.getChild("defaultSqlResources");
		if (null != defaultSqlResourcesElement) {
			List<Element> datasourceElements = defaultSqlResourcesElement.getChildren("datasource");
			for (int j = 0; j < datasourceElements.size(); j++) {
				Element datasourceElement = datasourceElements.get(j);
				String datasourceName = datasourceElement.getAttributeValue("name");
				String path = datasourceElement.getText().trim();
				envinroment.getDefaultSqlResourcesPaths().put(datasourceName, path);
			}
		}
		component.getEnvironments().put(environmentCode, envinroment);
	}
    
    private void decodeDOM(String xmlText) throws ApsSystemException {
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        StringReader reader = new StringReader(xmlText);
        try {
            this._doc = builder.build(reader);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "decodeDOM", "Error while parsing: " + t.getMessage());
            throw new ApsSystemException("Error detected while parsing the XML", t);
        }
    }
    
    private Document _doc;
    
}