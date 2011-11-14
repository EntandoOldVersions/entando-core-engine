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
package com.agiletec.aps.system.common.entity.model.attribute.util;

import java.util.Date;

import org.jdom.Element;

import com.agiletec.aps.util.DateConverter;

/**
 * @author E.Santoboni
 */
public class DateAttributeValidationRules extends AbstractAttributeValidationRules {
	
	@Override
	protected void fillJDOMConfigElement(Element configElement) {
		super.fillJDOMConfigElement(configElement);
		
		String toStringEqualValue = (this.getValue() != null) ? DateConverter.getFormattedDate((Date) this.getValue(), DATE_PATTERN) : null;
		this.insertJDOMConfigElement("value", this.getValueAttribute(), toStringEqualValue, configElement);
		
		String toStringStartValue = (this.getRangeStart() != null) ? DateConverter.getFormattedDate((Date) this.getRangeStart(), DATE_PATTERN) : null;
		this.insertJDOMConfigElement("rangestart", this.getRangeStartAttribute(), toStringStartValue, configElement);
		
		String toStringEndValue = (this.getRangeEnd() != null) ? DateConverter.getFormattedDate((Date) this.getRangeEnd(), DATE_PATTERN) : null;
		this.insertJDOMConfigElement("rangeend", this.getRangeEndAttribute(), toStringEndValue, configElement);
	}
	
	@Override
	protected void extractValidationRules(Element validationElement) {
		super.extractValidationRules(validationElement);
		Element valueElement = validationElement.getChild("value");
		if (null != valueElement) {
			this.setValue(DateConverter.parseDate(valueElement.getText(), DATE_PATTERN));
			this.setValueAttribute(valueElement.getAttributeValue("attribute"));
		}
		Element rangeStartElement = validationElement.getChild("rangestart");
		if (null != rangeStartElement) {
			this.setRangeStart(DateConverter.parseDate(rangeStartElement.getText(), DATE_PATTERN));
			this.setRangeStartAttribute(rangeStartElement.getAttributeValue("attribute"));
		}
		Element rangeEndElement = validationElement.getChild("rangeend");
		if (null != rangeEndElement) {
			this.setRangeEnd(DateConverter.parseDate(rangeEndElement.getText(), DATE_PATTERN));
			this.setRangeEndAttribute(rangeEndElement.getAttributeValue("attribute"));
		}
	}
	
	public static final String DATE_PATTERN = "dd/MM/yyyy";
	
}
