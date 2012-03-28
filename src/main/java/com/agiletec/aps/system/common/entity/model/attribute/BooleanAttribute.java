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
package com.agiletec.aps.system.common.entity.model.attribute;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.agiletec.aps.system.common.entity.model.AttributeSearchInfo;
import com.agiletec.aps.system.common.searchengine.IndexableAttributeInterface;
import com.agiletec.aps.system.services.lang.Lang;

/**
 * This attribute represent an information of type boolean. Obviously this attribute does not
 * support multiple languages.
 * @author E.Santoboni
 */
public class BooleanAttribute extends AbstractAttribute {

    public Element getJDOMElement() {
        Element attributeElement = new Element("attribute");
        attributeElement.setAttribute("name", this.getName());
        attributeElement.setAttribute("attributetype", this.getType());
        if (null != this.getBooleanValue()) {
            Element booleanElement = new Element("boolean");
            booleanElement.setText(this.getBooleanValue().toString());
            attributeElement.addContent(booleanElement);
        }
        return attributeElement;
    }

    /**
     * Return the object characterizing the attribute.
     * @return The boolean
     */
    public Boolean getValue() {
        if (null != _boolean) {
            return _boolean.booleanValue();
        }
        return false;
    }

    /**
     * Return the object characterizing the attribute.
     * @return The boolean
     */
    public Boolean getBooleanValue() {
        return _boolean;
    }

    /**
     * Set up the boolean for the current attribute
     * @param booleanObject The boolean
     */
    public void setBooleanValue(Boolean booleanObject) {
        this._boolean = booleanObject;
    }

    public boolean isSearchableOptionSupported() {
        return true;
    }

    public List<AttributeSearchInfo> getSearchInfos(List<Lang> systemLangs) {
        List<AttributeSearchInfo> infos = new ArrayList<AttributeSearchInfo>();
        if (null != this.getBooleanValue()) {
            AttributeSearchInfo info = new AttributeSearchInfo(this.getBooleanValue().toString(), null, null, null);
            infos.add(info);
        }
        return infos;
    }

    public String getIndexingType() {
        return IndexableAttributeInterface.INDEXING_TYPE_NONE;
    }
    
    protected Object getJAXBValue(String langCode) {
        return (null != this.getBooleanValue()) ? this.getBooleanValue().toString() : "false";
    }
    
    public void valueFrom(DefaultJAXBAttribute jaxbAttribute) {
        super.valueFrom(jaxbAttribute);
        String value = (String) jaxbAttribute.getValue();
        if (null != value) {
            this.setBooleanValue(new Boolean(value));
        }
    }
    
    public Status getStatus() {
        if (null != this.getBooleanValue()) {
            return Status.VALUED;
        }
        return Status.EMPTY;
    }
    
    private Boolean _boolean;
    
}