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
package com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.AttributeFieldError;
import com.agiletec.aps.system.common.entity.model.AttributeTracer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import com.agiletec.aps.system.common.entity.model.attribute.DefaultJAXBAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.TextAttribute;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.model.CmsAttributeReference;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute.util.ICmsAttributeErrorCodes;
import com.agiletec.plugins.jacms.aps.system.services.resource.IResourceManager;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInterface;

/**
 * Classe astratta di appoggio agli attributi di tipo Risorsa.
 * @author E.Santoboni
 */
public abstract class AbstractResourceAttribute extends TextAttribute
        implements IReferenceableAttribute, ResourceAttributeInterface {
    
    /**
     * Setta una risorsa sull'attributo.
     * @param resource La risorsa da associare all'attributo.
     * @param langCode il codice della lingua.
     */
    public void setResource(ResourceInterface resource, String langCode) {
        if (null == langCode) {
            langCode = this.getDefaultLangCode();
        }
        if (null == resource) {
            this.getResources().remove(langCode);
        } else {
            this.getResources().put(langCode, resource);
        }
    }

    /**
     * Restituisce la risorsa associata all'attributo.
     * @param langCode il codice della lingua.
     * @return la risorsa associata all'attributo.
     */
    public ResourceInterface getResource(String langCode) {
        return (ResourceInterface) this.getResources().get(langCode);
    }

    /**
     * Restituisce la risorsa associata all'attributo.
     * @return la risorsa associata all'attributo.
     */
    public ResourceInterface getResource() {
        ResourceInterface res = this.getResource(this.getRenderingLang());
        if (null == res) {
            res = this.getResource(this.getDefaultLangCode());
        }
        return res;
    }

    /**
     * Sovrascrittura del metodo della classe astratta da cui deriva. Poichè
     * questo tipo di attributo non può mai essere "searcheable", restituisce sempre false.
     * @return Restituisce sempre false
     * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#isSearcheable()
     */
    public boolean isSearcheable() {
        return false;
    }
    
    public boolean isSearchableOptionSupported() {
        return false;
    }
    
    public Element getJDOMElement() {
        Element attributeElement = new Element("attribute");
        attributeElement.setAttribute("name", this.getName());
        attributeElement.setAttribute("attributetype", this.getType());
        Iterator<String> langIter = this.getResources().keySet().iterator();
        while (langIter.hasNext()) {
            String currentLangCode = (String) langIter.next();
            ResourceInterface res = this.getResource(currentLangCode);
            if (null != res) {
                Element resourceElement = new Element("resource");
                resourceElement.setAttribute("resourcetype", res.getType());
                String resourceId = String.valueOf(res.getId());
                resourceElement.setAttribute("id", resourceId);
                resourceElement.setAttribute("lang", currentLangCode);
                attributeElement.addContent(resourceElement);
            }
        }
        super.addTextElements(attributeElement);
        return attributeElement;
    }

    /**
     * Appende, nella stringa rappresentante l'url della risorsa interna ad un entità, 
     * il riferimento al entità padre con la sintassi 
     * <baseUrl>/<REFERENCED_RESOURCE_INDICATOR>/<PARENT_CONTENT_ID>/. 
     * Tale operazione viene effettuata nel caso che la risorsa non sia libera.
     * @param basePath Il path base della risorsa.
     * @return Il path corretto.
     */
    protected String appendContentReference(String basePath) {
        ResourceInterface res = this.getResource();
        if (null == res) {
            return "";
        }
        String resourceGroup = res.getMainGroup();
        if (!Group.FREE_GROUP_NAME.equals(resourceGroup)
                && !this.getParentEntity().getGroups().isEmpty()) {
            if (!basePath.endsWith("/")) {
                basePath += "/";
            }
            basePath += REFERENCED_RESOURCE_INDICATOR
                    + "/" + this.getParentEntity().getId() + "/";
        }
        return basePath;
    }
    
    public List<CmsAttributeReference> getReferences(List<Lang> systemLangs) {
        List<CmsAttributeReference> refs = new ArrayList<CmsAttributeReference>();
        for (int i = 0; i < systemLangs.size(); i++) {
            Lang lang = systemLangs.get(i);
            ResourceInterface res = this.getResource(lang.getCode());
            if (null != res) {
                CmsAttributeReference ref = new CmsAttributeReference(null, null, res.getId());
                refs.add(ref);
            }
        }
        return refs;
    }
    
    public Object getValue() {
        if (null == this.getResources() || this.getResources().size() == 0) {
            return null;
        }
        return this;
    }
    
    protected JAXBResourceValue getJAXBValue(String langCode) {
        Object text = super.getJAXBValue(langCode);
        JAXBResourceValue value = new JAXBResourceValue();
        value.setText(text);
        if (null == langCode) {
            langCode = this.getDefaultLangCode();
        }
        this.setRenderingLang(langCode);
        String path = this.getDefaultPath();
        value.setPath(path);
        ResourceInterface resource = this.getResource();
        if (null != resource) {
            value.setResourceId(resource.getId());
        }
        return value;
    }
    
    public void valueFrom(DefaultJAXBAttribute jaxbAttribute) {
        JAXBResourceValue value = (JAXBResourceValue) jaxbAttribute.getValue();
        if (null == value) return;
        Object resourceId = value.getResourceId();
        if (null == resourceId) return;
        try {
            IResourceManager resourceManager = this.getResourceManager();
            ResourceInterface resource = resourceManager.loadResource(resourceId.toString());
            if (null != resource) {
                this.setResource(resource, this.getDefaultLangCode());
            }
            Object text = value.getText();
            if (null == text) return;
            this.getTextMap().put(this.getDefaultLangCode(), text.toString());
        } catch (Exception e) {
            ApsSystemUtils.logThrowable(e, this, "valueFrom", "Error extracting resource from jaxbAttribute");
        }
    }
    
    public Status getStatus() {
        Status textStatus = super.getStatus();
        Status resourceStatus = (null != this.getResource()) ? Status.VALUED : Status.EMPTY;
        if (!textStatus.equals(resourceStatus)) return Status.INCOMPLETE;
        if (textStatus.equals(resourceStatus) && textStatus.equals(Status.VALUED)) return Status.VALUED;
        return Status.EMPTY;
    }
    
    protected abstract String getDefaultPath();
    
    public Map<String, ResourceInterface> getResources() {
        return this._resources;
    }
    
    protected IResourceManager getResourceManager() {
        return (IResourceManager) this.getBeanFactory().getBean(JacmsSystemConstants.RESOURCE_MANAGER);
    }
    
    public List<AttributeFieldError> validate(AttributeTracer tracer) {
        List<AttributeFieldError> errors = super.validate(tracer);
        try {
            if (null == this.getResources()) return errors;
            ILangManager langManager = this.getBeanFactory().getBean(SystemConstants.LANGUAGE_MANAGER, ILangManager.class);
            List<Lang> langs = langManager.getLangs();
            for (int i = 0; i < langs.size(); i++) {
                Lang lang = langs.get(i);
                ResourceInterface resource = this.getResource(lang.getCode());
                if (null == resource) continue;
                AttributeTracer resourceTracer = (AttributeTracer) tracer.clone();
                resourceTracer.setLang(lang);
                String resourceMainGroup = resource.getMainGroup();
                Content parentContent = (Content) this.getParentEntity();
                if (!resourceMainGroup.equals(Group.FREE_GROUP_NAME) 
                        && !resourceMainGroup.equals(parentContent.getMainGroup()) 
                        && !parentContent.getGroups().contains(resourceMainGroup)) {
                    AttributeFieldError fieldError = new AttributeFieldError(this, ICmsAttributeErrorCodes.INVALID_RESOURCE_GROUPS, resourceTracer);
                    fieldError.setMessage("Invalid resource group - " + resourceMainGroup);
                    errors.add(fieldError);
                }
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "validate");
            throw new RuntimeException("Error validating text attribute", t);
        }
        return errors;
    }
    
    private Map<String, ResourceInterface> _resources = new HashMap<String, ResourceInterface>();
    public static final String REFERENCED_RESOURCE_INDICATOR = "ref";
    
}