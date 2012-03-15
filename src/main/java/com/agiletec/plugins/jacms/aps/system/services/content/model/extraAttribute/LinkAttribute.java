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

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.agiletec.aps.system.common.entity.model.attribute.DefaultJAXBAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.TextAttribute;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.model.CmsAttributeReference;
import com.agiletec.plugins.jacms.aps.system.services.content.model.SymbolicLink;
import com.agiletec.plugins.jacms.aps.system.services.linkresolver.ILinkResolverManager;

/**
 * Rappresenta una informazione di tipo "link". La destinazione del link è
 * la stessa per tutte le lingue, ma il testo associato varia con la lingua.
 * @author W.Ambu - S.Didaci
 */
public class LinkAttribute extends TextAttribute implements IReferenceableAttribute {
    
    public Element getJDOMElement() {
        Element attributeElement = new Element("attribute");
        attributeElement.setAttribute("name", this.getName());
        attributeElement.setAttribute("attributetype", this.getType());
        if (null != this.getSymbolicLink()) {
            Element linkElement = new Element("link");
            attributeElement.addContent(linkElement);
            Element dest;
            int type = this.getSymbolicLink().getDestType();
            switch (type) {
                case SymbolicLink.URL_TYPE:
                    linkElement.setAttribute("type", "external");
                    dest = new Element("urldest");
                    dest.addContent(this.getSymbolicLink().getUrlDest());
                    linkElement.addContent(dest);
                    break;
                case SymbolicLink.PAGE_TYPE:
                    linkElement.setAttribute("type", "page");
                    dest = new Element("pagedest");
                    dest.addContent(this.getSymbolicLink().getPageDest());
                    linkElement.addContent(dest);
                    break;
                case SymbolicLink.CONTENT_TYPE:
                    linkElement.setAttribute("type", "content");
                    dest = new Element("contentdest");
                    dest.addContent(this.getSymbolicLink().getContentDest());
                    linkElement.addContent(dest);
                    break;
                case SymbolicLink.CONTENT_ON_PAGE_TYPE:
                    linkElement.setAttribute("type", "contentonpage");
                    dest = new Element("pagedest");
                    dest.addContent(this.getSymbolicLink().getPageDest());
                    linkElement.addContent(dest);
                    dest = new Element("contentdest");
                    dest.addContent(this.getSymbolicLink().getContentDest());
                    linkElement.addContent(dest);
                    break;
                default:
                    linkElement.setAttribute("type", "");
            }
        }
        super.addTextElements(attributeElement);
        return attributeElement;
    }
    
    /**
     * Setta il link simbolico caratterizzante l'attributo.
     * @param symbolicLink Il link simbolico.
     */
    public void setSymbolicLink(SymbolicLink symbolicLink) {
        this._symbolicLink = symbolicLink;
    }
    
    /**
     * Restituisce il link simbolico caratterizzante l'attributo.
     * @return Il link simbolico.
     */
    public SymbolicLink getSymbolicLink() {
        return _symbolicLink;
    }
    
    /**
     * Restituisce la stringa rappresentante la destinazione simbolica.
     * Il metodo è atto ad essere utilizzato nel modello di renderizzazione 
     * e la stringa restituita sarà successivamente risolta in fase di 
     * renderizzazione dal servizio linkResolver.
     * @return La stringa rappresentante la destinazione simbolica.
     */
    public String getDestination() {
        String destination = "";
        if (null != this.getSymbolicLink()) {
            destination = this.getSymbolicLink().getSymbolicDestination();
            if (this.getSymbolicLink().getDestType() == SymbolicLink.URL_TYPE) {
                destination = destination.replaceAll("&(?![a-z]+;)", "&amp;");
            }
        }
        return destination;
    }
    
    /**
     * Sovrascrittura del metodo della classe astratta da cui deriva. Poichè
     * questo tipo di attributo non può mai essere "searcheable", restituisce sempre false.
     * @return Restituisce sempre false
     */
    public boolean isSearcheable() {
        return false;
    }
    
    public boolean isSearchableOptionSupported() {
        return false;
    }
    
    public List<CmsAttributeReference> getReferences(List<Lang> systemLangs) {
        List<CmsAttributeReference> refs = new ArrayList<CmsAttributeReference>();
        SymbolicLink symbLink = this.getSymbolicLink();
        if (null != symbLink && (symbLink.getDestType() != SymbolicLink.URL_TYPE)) {
            CmsAttributeReference ref = new CmsAttributeReference(symbLink.getPageDest(),
                    symbLink.getContentDest(), null);
            refs.add(ref);
        }
        return refs;
    }
    
    public Object getValue() {
        return this.getSymbolicLink();
    }
    
    protected JAXBLinkValue getJAXBValue(String langCode) {
        Object text = super.getJAXBValue(langCode);
        JAXBLinkValue value = new JAXBLinkValue();
        value.setText(text);
        value.setUrl(this.getLinkResolverManager().resolveLink(this.getSymbolicLink(), null));
        value.setSymbolikLink(this.getSymbolicLink());
        return value;
    }
    
    public void valueFrom(DefaultJAXBAttribute jaxbAttribute) {
        JAXBLinkValue value = (JAXBLinkValue) jaxbAttribute.getValue();
        if (null == value) return;
        this.setSymbolicLink(value.getSymbolikLink());
        Object textValue = value.getText();
        if (null == textValue) return;
        this.getTextMap().put(this.getDefaultLangCode(), textValue.toString());
    }
    
    protected ILinkResolverManager getLinkResolverManager() {
        return (ILinkResolverManager) this.getBeanFactory().getBean(JacmsSystemConstants.LINK_RESOLVER_MANAGER);
    }
    
    public Status getStatus() {
        Status textStatus = super.getStatus();
        Status linkStatus = (null != this.getSymbolicLink()) ? Status.VALUED : Status.EMPTY;
        if (!textStatus.equals(linkStatus)) return Status.INCOMPLETE;
        if (textStatus.equals(linkStatus) && textStatus.equals(Status.VALUED)) return Status.VALUED;
        return Status.EMPTY;
    }
    
    private SymbolicLink _symbolicLink;
    
}