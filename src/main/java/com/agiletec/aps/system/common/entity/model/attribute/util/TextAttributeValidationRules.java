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
package com.agiletec.aps.system.common.entity.model.attribute.util;

import org.jdom.CDATA;
import org.jdom.Element;

/**
 * @author E.Santoboni
 */
public class TextAttributeValidationRules extends AbstractAttributeValidationRules {
	
	@Override
	public IAttributeValidationRules clone() {
		TextAttributeValidationRules clone = (TextAttributeValidationRules) super.clone();
		clone.setMaxLength(this.getMaxLength());
		clone.setMinLength(this.getMinLength());
		clone.setRegexp(this.getRegexp());
		return clone;
	}
	
	@Override
	protected void fillJDOMConfigElement(Element configElement) {
		super.fillJDOMConfigElement(configElement);
		if (this.getMinLength() > -1) {
			Element element = new Element("minlength");
			element.setText(String.valueOf(this.getMinLength()));
			configElement.addContent(element);
		}
		if (this.getMaxLength() > -1) {
			Element element = new Element("maxlength");
			element.setText(String.valueOf(this.getMaxLength()));
			configElement.addContent(element);
		}
		if (null != this.getRegexp() && this.getRegexp().trim().length() > 0) {
			Element regexpElem = new Element("regexp");
			CDATA cdata = new CDATA(this.getRegexp());
			regexpElem.addContent(cdata);
			configElement.addContent(regexpElem);
		}
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
		String maxLength = this.extractValue(validationElement, "maxlength");
		if (null != maxLength) {
			this.setMaxLength(Integer.parseInt(maxLength));
		}
		String minLength = this.extractValue(validationElement, "minlength");
		if (null != minLength) {
			this.setMinLength(Integer.parseInt(minLength));
		}
		String regexp = this.extractValue(validationElement, "regexp");
		if (null != regexp && regexp.trim().length() > 0) {
			this.setRegexp(regexp);
		}
		Element valueElement = validationElement.getChild("value");
		if (null != valueElement) {
			this.setValue(valueElement.getText());
			this.setValueAttribute(valueElement.getAttributeValue("attribute"));
		}
		Element rangeStartElement = validationElement.getChild("rangestart");
		if (null != rangeStartElement) {
			this.setRangeStart(rangeStartElement.getText());
			this.setRangeStartAttribute(rangeStartElement.getAttributeValue("attribute"));
		}
		Element rangeEndElement = validationElement.getChild("rangeend");
		if (null != rangeEndElement) {
			this.setRangeEnd(rangeEndElement.getText());
			this.setRangeEndAttribute(rangeEndElement.getAttributeValue("attribute"));
		}
	}
	
	@Override
	protected boolean isEmpty() {
		return (super.isEmpty() 
				&& (-1 == this.getMaxLength()) 
				&& (-1 == this.getMinLength()) 
				&& (null == this.getRegexp() || this.getRegexp().trim().length() == 0) );
	}
	
	public int getMaxLength() {
		return _maxLength;
	}
	public void setMaxLength(int maxLength) {
		this._maxLength = maxLength;
	}
	
	public int getMinLength() {
		return _minLength;
	}
	public void setMinLength(int minLength) {
		this._minLength = minLength;
	}
	
	public String getRegexp() {
		return _regexp;
	}
	public void setRegexp(String regexp) {
		this._regexp = regexp;
	}
	
	private int _maxLength = -1;
	private int _minLength = -1;
	
	private String _regexp;
	
}