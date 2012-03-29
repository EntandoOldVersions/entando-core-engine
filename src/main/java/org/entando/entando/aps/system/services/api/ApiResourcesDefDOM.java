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
package org.entando.entando.aps.system.services.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.ApiResource;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * @author E.Santoboni
 */
public class ApiResourcesDefDOM {
    
    public ApiResourcesDefDOM(String xmlText, String definitionPath) throws ApsSystemException {
        //this.validate(xmlText, definitionPath);
        ApsSystemUtils.getLogger().info("Loading Resources from file : " + definitionPath);
        this.decodeDOM(xmlText);
    }
    
    private void validate(String xmlText, String definitionPath) throws ApsSystemException {
        SchemaFactory factory =
                SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        InputStream schemaIs = null;
        InputStream xmlIs = null;
        try {
            schemaIs = this.getClass().getResourceAsStream("apiMethodsDef-3.0.xsd");
            Source schemaSource = new StreamSource(schemaIs);
            Schema schema = factory.newSchema(schemaSource);
            Validator validator = schema.newValidator();
            xmlIs = new ByteArrayInputStream(xmlText.getBytes("UTF-8"));
            Source source = new StreamSource(xmlIs);
            validator.validate(source);
            ApsSystemUtils.getLogger().info("Valid api methods definition : " + definitionPath);
        } catch (Throwable t) {
            String message = "Error validating api methods definition : " + definitionPath;
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
    
    public Map<String, ApiResource> getResources() {
        Map<String, ApiResource> apiResources = new HashMap<String, ApiResource>();
        try {
            List<Element> methodElements = this._doc.getRootElement().getChildren(METHOD_ELEMENT_NAME);
            if (null != methodElements) {
                for (int i = 0; i < methodElements.size(); i++) {
                    Element methodElement = methodElements.get(i);
                    ApiMethod apiMethod = new ApiMethod(methodElement);
                    ApiResource resource = new ApiResource();
                    resource.setResourceName(apiMethod.getResourceName());
                    resource.setNamespace(apiMethod.getNamespace());
                    resource.setDescription(apiMethod.getDescription());
                    resource.setPluginCode(apiMethod.getPluginCode());
                    resource.setSource(apiMethod.getSource());
                    resource.setMethod(apiMethod);
                    this.checkResource(resource, apiResources);
                }
            }
            List<Element> resourceElements = this._doc.getRootElement().getChildren(RESOURCE_ELEMENT_NAME);
            if (null != resourceElements) {
                for (int j = 0; j < resourceElements.size(); j++) {
                    Element resourceElement = resourceElements.get(j);
                    String resourceName = resourceElement.getAttributeValue(RESOURCE_ATTRIBUTE_NAME);
                    String namespace = resourceElement.getAttributeValue(RESOURCE_ATTRIBUTE_NAMESPACE);
                    Element descriptionElement = resourceElement.getChild(RESOURCE_DESCRIPTION_ELEMENT_NAME);
                    String resourceDescription = (null != descriptionElement) ? descriptionElement.getText() : null;
                    Element sourceElement = resourceElement.getChild(ApiResourcesDefDOM.SOURCE_ELEMENT_NAME);
                    String source = null;
                    String pluginCode = null;
                    if (null != sourceElement) {
                        source = sourceElement.getText();
                        pluginCode = sourceElement.getAttributeValue(PLUGIN_CODE_ATTRIBUTE_NAME);
                    }
                    ApiResource resource = new ApiResource();
                    resource.setResourceName(resourceName);
					resource.setNamespace(namespace);
                    resource.setDescription(resourceDescription);
                    resource.setPluginCode(pluginCode);
                    resource.setSource(source);
                    List<Element> resourceMethodElements = resourceElement.getChildren(METHOD_ELEMENT_NAME);
                    for (int k = 0; k < resourceMethodElements.size(); k++) {
                        Element methodElement = resourceMethodElements.get(k);
                        ApiMethod apiMethod = new ApiMethod(resourceName, namespace, source, pluginCode, methodElement);
                        this.checkMethod(apiMethod, resource);
                    }
                    this.checkResource(resource, apiResources);
                }
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getResources", "Error building api resources");
        }
        return apiResources;
    }
    
    private void checkResource(ApiResource resource, Map<String, ApiResource> apiResources) {
        try {
			ApiResource extractedResource = apiResources.get(resource.getFullCode());
            if (null != extractedResource) {
                String alertMessage = "ALERT: There is more than one API with namespace '" + resource.getNamespace() + 
						"', resource '" + resource.getResourceName() + 
                        "' into the same definitions file - The second definition will be ignored!!!";
                ApsSystemUtils.getLogger().severe(alertMessage);
            } else {
                apiResources.put(resource.getFullCode(), resource);
            }
        } catch (Exception e) {
            ApsSystemUtils.logThrowable(e, this, "checkResource", "Error checking api resource");
        }
    }
    
    private void checkMethod(ApiMethod apiMethod, ApiResource resource) {
        try {
            ApiMethod extractedMethod = resource.getMethod(apiMethod.getHttpMethod());
            if (null != extractedMethod) {
                String alertMessage = "ALERT: There is more than one API method " + apiMethod.getHttpMethod() 
                        + " for resource '" + apiMethod.getResourceName() + "' into the same definitions file "
                        + "- The second definition will be ignored!!!";
                ApsSystemUtils.getLogger().severe(alertMessage);
            } else {
                resource.setMethod(apiMethod);
            }
        } catch (Exception e) {
            ApsSystemUtils.logThrowable(e, this, "checkMethod", "Error checking api method");
        }
    }
    
    private void decodeDOM(String xmlText) throws ApsSystemException {
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        StringReader reader = new StringReader(xmlText);
        try {
            this._doc = builder.build(reader);
        } catch (Throwable t) {
            ApsSystemUtils.getLogger().severe("Error while parsing: " + t.getMessage());
            throw new ApsSystemException("Error detected while parsing the XML", t);
        }
    }
    
    private Document _doc;
    public static final String ROOT_ELEMENT_NAME = "apiMethodDefinitions";
    public static final String METHOD_ELEMENT_NAME = "method";
    public static final String RESOURCE_ELEMENT_NAME = "resource";
    public static final String RESOURCE_ATTRIBUTE_NAME = "name";
	public static final String RESOURCE_ATTRIBUTE_NAMESPACE = "namespace";
    public static final String RESOURCE_DESCRIPTION_ELEMENT_NAME = "description";
    public static final String METHOD_DESCRIPTION_ELEMENT_NAME = "description";
    public static final String ACTIVE_ATTRIBUTE_NAME = "active";
    public static final String CAN_SPAWN_OTHER_ATTRIBUTE_NAME = "canSpawnOthers";
    public static final String SOURCE_ELEMENT_NAME = "source";
    public static final String PLUGIN_CODE_ATTRIBUTE_NAME = "pluginCode";
    public static final String SPRING_BEAN_ELEMENT_NAME = "springBean";
    public static final String SPRING_BEAN_NAME_ATTRIBUTE_NAME = "name";
    public static final String SPRING_BEAN_METHOD_ATTRIBUTE_NAME = "method";
    public static final String RESPONSE_CLASS_ELEMENT_NAME = "responseClass";
    public static final String PARAMETERS_ELEMENT_NAME = "parameters";
    public static final String PARAMETER_ELEMENT_NAME = "parameter";
    public static final String PARAMETER_KEY_ATTRIBUTE_NAME = "key";
    public static final String PARAMETER_REQUIRED_ATTRIBUTE_NAME = "required";
    public static final String PARAMETER_OVERRIDABLE_ATTRIBUTE_NAME = "override";
    public static final String PARAMETER_TYPE_ATTRIBUTE_NAME = "type";
    public static final String PARAMETER_OVERRIDE_ATTRIBUTE_NAME = "override";
    public static final String PARAMETER_DESCRIPTION_ELEMENT_NAME = "description";
    public static final String RELATED_SHOWLET_ELEMENT_NAME = "relatedShowlet";
    public static final String RELATED_SHOWLET_CODE_ATTRIBUTE_NAME = "code";
    public static final String RELATED_SHOWLET_MAP_PARAMETER_ELEMENT_NAME = "mapParameter";
    public static final String RELATED_SHOWLET_MAP_PARAMETER_SHOWLET_ATTRIBUTE_NAME = "showlet";
    public static final String RELATED_SHOWLET_MAP_PARAMETER_METHOD_ATTRIBUTE_NAME = "method";
	
}