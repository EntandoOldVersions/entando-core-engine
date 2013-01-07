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
import java.util.Map;
import org.entando.entando.aps.system.init.model.Component;
import org.jdom.Element;

/**
 * @author E.Santoboni
 */
public class ComponentDefDOM {
    
    protected ComponentDefDOM(String xmlText, String path) throws ApsSystemException {
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
    protected Component getComponent(Map<String, String> postProcessClasses) {
        Component component = null;
        try {
            Element rootElement = this._doc.getRootElement();
			component = new Component(rootElement, postProcessClasses);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getComponent", "Error loading component");
        }
        return component;
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