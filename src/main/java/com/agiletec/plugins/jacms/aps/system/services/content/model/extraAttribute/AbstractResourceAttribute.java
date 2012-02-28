/*
*
* Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
* This file is part of jAPS software.
* jAPS is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
*/
package com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute;

import com.agiletec.aps.system.ApsSystemUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.agiletec.aps.system.common.entity.model.attribute.DefaultJAXBAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.TextAttribute;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.model.CmsAttributeReference;
import com.agiletec.plugins.jacms.aps.system.services.resource.IResourceManager;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInterface;

/**
 * Classe astratta di appoggio agli attributi di tipo Risorsa.
 * @author E.Santoboni
 */
public abstract class AbstractResourceAttribute extends TextAttribute
        implements IReferenceableAttribute, ResourceAttributeInterface, BeanFactoryAware {
    
    public Object getAttributePrototype() {
        AbstractResourceAttribute resourceAttribute = (AbstractResourceAttribute) super.getAttributePrototype();
        resourceAttribute.setBeanFactory(this.getBeanFactory());
        return resourceAttribute;
    }
    
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
    
    protected Object getJAXBValue(String langCode) {
        Object text = super.getJAXBValue(langCode);
        JAXBResourceValue value = new JAXBResourceValue();
        value.setText(text);
        if (null != langCode) {
            this.setRenderingLang(langCode);
            String path = this.getDefaultPath();
            value.setPath(path);
            ResourceInterface resource = this.getResource(langCode);
            if (null != resource) {
                value.setResourceId(resource.getId());
            }
        } else {
            Map<String, ResourceInterface> resources = this.getResources();
            Iterator<String> langIter = resources.keySet().iterator();
            Map<String, String> resourceIds = new HashMap<String, String>();
            Map<String, String> paths = new HashMap<String, String>();
            while (langIter.hasNext()) {
                String resourceLangCode = langIter.next();
                this.setRenderingLang(resourceLangCode);
                String path = this.getDefaultPath();
                paths.put(resourceLangCode, path);
                ResourceInterface resource = this.getResource(langCode);
                resourceIds.put(resourceLangCode, resource.getId());
            }
            if (!resourceIds.isEmpty()) value.setResourceId(resourceIds);
            if (!paths.isEmpty()) value.setPath(paths);
        }
        return value;
    }
    
    public void valueFrom(DefaultJAXBAttribute jaxbAttribute) {
        super.valueFrom(jaxbAttribute);
        JAXBResourceValue value = (JAXBResourceValue) jaxbAttribute.getValue();
        if (null == value) return;
        Object resourceId = value.getResourceId();
        if (null == resourceId) return;
        try {
            IResourceManager resourceManager = this.getResourceManager();
            if (resourceId instanceof Map) {
                Map map = (Map) resourceId;
                Iterator<Object> keyIter = map.keySet().iterator();
                while (keyIter.hasNext()) {
                    String langCode = keyIter.next().toString();
                    String id = map.get(langCode).toString();
                    ResourceInterface resource = resourceManager.loadResource(id);
                    if (null != resource) {
                        this.setResource(resource, langCode);
                    }
                }
            } else if (resourceId instanceof String) {
                ResourceInterface resource = resourceManager.loadResource(resourceId.toString());
                if (null != resource) {
                    this.setResource(resource, this.getDefaultLangCode());
                }
            }
            Object text = value.getText();
            if (null == text) return;
            if (text instanceof Map) {
                this.getTextMap().putAll((Map) text);
            } else if (text instanceof String) {
                this.getTextMap().put(this.getDefaultLangCode(), (String) text);
            }
        } catch (Exception e) {
            ApsSystemUtils.logThrowable(e, this, "valueFrom", "Error extracting resource from jaxbAttribute");
        }
    }
    
    protected abstract String getDefaultPath();
    
    public Map<String, ResourceInterface> getResources() {
        return this._resources;
    }
    
    protected IResourceManager getResourceManager() {
        return (IResourceManager) this.getBeanFactory().getBean(JacmsSystemConstants.RESOURCE_MANAGER);
    }
    
    protected BeanFactory getBeanFactory() {
        return this._beanFactory;
    }
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this._beanFactory = beanFactory;
    }
    
    private Map<String, ResourceInterface> _resources = new HashMap<String, ResourceInterface>();
    public static final String REFERENCED_RESOURCE_INDICATOR = "ref";
    private BeanFactory _beanFactory;
    
}