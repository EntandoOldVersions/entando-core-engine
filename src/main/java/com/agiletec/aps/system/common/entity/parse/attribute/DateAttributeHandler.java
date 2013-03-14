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
package com.agiletec.aps.system.common.entity.parse.attribute;

import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.attribute.DateAttribute;
import com.agiletec.aps.util.DateConverter;

/**
 * Handler class that interprets the XML defining a 'Date Attribute'
 * @author E.Santoboni
 */
public class DateAttributeHandler extends AbstractAttributeHandler {
	
	public void startAttribute(Attributes attributes, String qName) throws SAXException {
		if (qName.equals("date")) {
			this.startDate(attributes, qName);
		}
	}
	
	private void startDate(Attributes attributes, String qName) throws SAXException {
		//nothig to do
	}
	
	public void endAttribute(String qName, StringBuffer textBuffer) {
		if (qName.equals("date")) {
			this.endDate(textBuffer);
		}
	}
	
	private void endDate(StringBuffer textBuffer) {
		if (null != textBuffer && null != this.getCurrentAttr()) {
			Date date = DateConverter.parseDate(textBuffer.toString(), SystemConstants.SYSTEM_DATE_FORMAT);
			((DateAttribute) this.getCurrentAttr()).setDate(date);
		}
	}

}
