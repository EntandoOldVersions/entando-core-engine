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

import org.jdom.Element;

import com.agiletec.aps.system.ApsSystemUtils;

/**
 * @author E.Santoboni
 */
public class NumberAttributeValidationRules extends AbstractAttributeValidationRules {
	
	@Override
	protected void fillJDOMConfigElement(Element configElement) {
		super.fillJDOMConfigElement(configElement);
		String toStringEqualValue = (this.getValue() != null) ? String.valueOf(this.getValue()) : null;
		this.insertJDOMConfigElement("value", this.getValueAttribute(), toStringEqualValue, configElement);
		
		String toStringStartValue = (this.getRangeStart() != null) ? String.valueOf(this.getRangeStart()) : null;
		this.insertJDOMConfigElement("rangestart", this.getRangeStartAttribute(), toStringStartValue, configElement);
		
		String toStringEndValue = (this.getRangeEnd() != null) ? String.valueOf(this.getRangeEnd()) : null;
		this.insertJDOMConfigElement("rangeend", this.getRangeEndAttribute(), toStringEndValue, configElement);
	}
	
	@Override
	protected void extractValidationRules(Element validationElement) {
		super.extractValidationRules(validationElement);
		Element valueElement = validationElement.getChild("value");
		if (null != valueElement) {
			this.setValue(this.getIntegerValue(valueElement.getText()));
			this.setValueAttribute(valueElement.getAttributeValue("attribute"));
		}
		Element rangeStartElement = validationElement.getChild("rangestart");
		if (null != rangeStartElement) {
			this.setRangeStart(this.getIntegerValue(rangeStartElement.getText()));
			this.setRangeStartAttribute(rangeStartElement.getAttributeValue("attribute"));
		}
		Element rangeEndElement = validationElement.getChild("rangeend");
		if (null != rangeEndElement) {
			this.setRangeEnd(this.getIntegerValue(rangeEndElement.getText()));
			this.setRangeEndAttribute(rangeEndElement.getAttributeValue("attribute"));
		}
	}
	
	private Integer getIntegerValue(String text) {
		if (null == text || text.trim().length() == 0) return null;
		Integer valueInteger = null;
		try {
			valueInteger = Integer.parseInt(text);
		} catch (NumberFormatException e) {
			ApsSystemUtils.logThrowable(e, this, "getIntegerValue", 
					"Error in parsing number '" + text + "' for extracting attribute roles");
		}
		return valueInteger;
	}
	
}
