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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jdom.Element;

import com.agiletec.aps.system.common.entity.model.AttributeSearchInfo;
import com.agiletec.aps.system.common.entity.model.attribute.util.IAttributeValidationRules;
import com.agiletec.aps.system.common.entity.model.attribute.util.NumberAttributeValidationRules;
import com.agiletec.aps.system.services.lang.Lang;

/**
 * This class describes a numeric information common for all the languages.
 * @author W.Ambu - S.Didaci - E.Santoboni
 */
public class NumberAttribute extends AbstractAttribute {

	/**
	 * Return the number in the format used for the current language.
	 * @return The formatted number
	 */
	public String getNumber() {
		if (null != this.getValue()){
			return this.getValue().toString();
		}
		return null;
	}

	/**
	 * Return the number in the format used for the current language,
	 * expressed in form of percentage.
	 * Using this method, a fractional number like. eg., 0.53 is displayed as 53%. 
	 * @return The formatted number.
	 */
	public String getPercentNumber() {
		String number = "";
		if (null != this.getNumber()) {
			NumberFormat numberInstance = 
				NumberFormat.getPercentInstance(new Locale(getRenderingLang(), ""));
			number = numberInstance.format(this.getNumber());
		}
		return number;
	}

	/**
	 * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#getJDOMElement()
	 */
	@Override
	public Element getJDOMElement() {
		Element attributeElement = new Element("attribute");
		attributeElement.setAttribute("name", this.getName());
		attributeElement.setAttribute("attributetype", this.getType());
		String number = this.getNumber();
		if (null != number && number.trim().length() > 0) {
			Element numberElement = new Element("number");
			numberElement.setText(number);
			attributeElement.addContent(numberElement);
		}
		return attributeElement;
	}
	
	/**
	 * Return the number held by the attribute.
	 * @return The number held by the attribute.
	 */
	@Override
	public BigDecimal getValue() {
		return _number;
	}

	/**
	 * Associate the given number to the current attribute.
	 * @param number The number to associate to the current attribute.
	 */
	public void setValue(BigDecimal number) {
		this._number = number;
	}
	
	@Override
	public boolean isSearchableOptionSupported() {
		return true;
	}
	
	@Override
	public List<AttributeSearchInfo> getSearchInfos(List<Lang> systemLangs) {
		if (this.getValue() != null) {
			List<AttributeSearchInfo> infos = new ArrayList<AttributeSearchInfo>();
			AttributeSearchInfo info = new AttributeSearchInfo(null, null, this.getValue(), null);
			infos.add(info);
			return infos;
		}
		return null;
	}
	
	@Override
	protected IAttributeValidationRules getValidationRuleNewIntance() {
		return new NumberAttributeValidationRules();
	}
	
	/**
	 * Associate the (numeric) string submitted in the back-office form to the current attribute.
	 * This method is only invoked by the entity handling routines within the back-office area.
	 * @param failedNumberString The numeric string submitted in the back-office form.
	 */
	public void setFailedNumberString(String failedNumberString) {
		this._failedNumberString = failedNumberString;
	}

	/**
	 * Return the numeric string inserted in the back-office form; this method
	 * is only invoked by the entity handling routines within the back-office area. 
	 * @return The requested numeric string.
	 */
	public String getFailedNumberString() {
		return _failedNumberString;
	}
	
	@Override
	protected Object getJAXBValue(String langCode) {
		return this.getValue();
	}
	
	private BigDecimal _number;
	private String _failedNumberString;

}
