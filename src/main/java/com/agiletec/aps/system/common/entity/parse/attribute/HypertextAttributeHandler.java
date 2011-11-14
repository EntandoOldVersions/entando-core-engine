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
package com.agiletec.aps.system.common.entity.parse.attribute;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Handler class that interprets the XML defining a 'Hypertext Attribute'
 * @author E.Santoboni
 */
public class HypertextAttributeHandler extends TextAttributeHandler {
	
	public void startAttribute(Attributes attributes, String qName) throws SAXException {
		if (qName.equals("hypertext")) {
			this.startText(attributes, qName);
		}
	}
	
	public void endAttribute(String qName, StringBuffer textBuffer) {
		if (qName.equals("hypertext")) {
			this.endText(textBuffer);
		}
	}
	
}
