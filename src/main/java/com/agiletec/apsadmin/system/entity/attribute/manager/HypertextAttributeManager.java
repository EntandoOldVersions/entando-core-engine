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
package com.agiletec.apsadmin.system.entity.attribute.manager;

import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.services.lang.Lang;

/**
 * Manager class for the 'Hypertext' Attribute.
 * @author E.Santoboni
/* @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
 */
public class HypertextAttributeManager extends TextAttributeManager {
    
    /**
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    protected String getTextForCheckLength(AttributeInterface attribute, Lang lang) {
        String text = super.getTextForCheckLength(attribute, lang);
        if (text != null) {
            // remove HTML tags, entities an multiple spaces
            text = text.replaceAll("<[^<>]+>", " ").replaceAll("&nbsp;", " ").replaceAll("\\&[^\\&;]+;", "_").replaceAll("([\t\n\r\f ])++", " ").trim();
        }
        return text;
    }
    
}