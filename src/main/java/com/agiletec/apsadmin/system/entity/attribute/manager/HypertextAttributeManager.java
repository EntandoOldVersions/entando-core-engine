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
package com.agiletec.apsadmin.system.entity.attribute.manager;

import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.services.lang.Lang;

/**
 * Manager class for the 'Hypertext' Attribute.
 * @author E.Santoboni
 */
public class HypertextAttributeManager extends TextAttributeManager {
	
	@Override
	protected String getTextForCheckLength(AttributeInterface attribute, Lang lang) {
		String text = super.getTextForCheckLength(attribute, lang);
		if (text != null) {
			// remove HTML tags, entities an multiple spaces
			text = text.replaceAll("<[^<>]+>", " ").replaceAll("&nbsp;", " ").replaceAll("\\&[^\\&;]+;", "_").replaceAll("([\t\n\r\f ])++", " ").trim();
		}
		return text;
	}
	
}