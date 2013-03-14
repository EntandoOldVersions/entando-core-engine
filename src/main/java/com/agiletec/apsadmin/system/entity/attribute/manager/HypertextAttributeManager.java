/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando Enterprise Edition software.
* You can redistribute it and/or modify it
* under the terms of the Entando's EULA
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
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