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
package com.agiletec.aps.system.common.entity.model.attribute;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 * @author E.Santoboni
 */
@XmlType(propOrder = {"attributes"})
public class JAXBListAttribute extends DefaultJAXBAttribute {
    
    @XmlElement(name = "attribute", required = true)
    @XmlElementWrapper(name = "attributes")
    public List<DefaultJAXBAttribute> getAttributes() {
        return _attributes;
    }
    
    public void setAttributes(List<DefaultJAXBAttribute> attributes) {
        this._attributes = attributes;
    }
    
    private List<DefaultJAXBAttribute> _attributes = null;
    
}