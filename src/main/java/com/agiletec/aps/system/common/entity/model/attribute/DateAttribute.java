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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jdom.Element;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.AttributeSearchInfo;
import com.agiletec.aps.system.common.entity.model.attribute.util.DateAttributeValidationRules;
import com.agiletec.aps.system.common.entity.model.attribute.util.IAttributeValidationRules;
import com.agiletec.aps.system.services.lang.Lang;

/**
 * This class describes the "Date" attribute; obviously it does not support multiple
 * languages (mono-language)
 * @author W.Ambu - E.Santoboni
 */
public class DateAttribute extends AbstractAttribute {
	
	/**
	 * Get the FULLy qualified date such as Tuesday, April 12, 1952 AD or 3:30:42pm PST.
	 * @return The date format as formatted by the FULL DateFormat.
	 */
	public String getFullDate() {
		return this.getFormattedDate(DateFormat.FULL);
	}
	
	/**
	 * LONG return the dates such as January 12, 1952 or 3:30:32pm 
	 * @return The date formatted using the DateFormat LONG.
	 */
	public String getLongDate() {
		return this.getFormattedDate(DateFormat.LONG);
	}
	
	/**
	 * MEDIUM is longer, such as Jan 12, 1952
	 * @return The date formatted using the  DateFormat MEDIUM.
	 */
	public String getMediumDate() {
		return this.getFormattedDate(DateFormat.MEDIUM);
	}
	
	/**
	 * SHORT is completely numeric, such as 12.13.52 or 3:30pm
	 * @return The date formatted using the  DateFormat SHORT.
	 */
	public String getShortDate() {
		return this.getFormattedDate(DateFormat.SHORT);
	}
	
	private String getFormattedDate(int stypePatternId) {
		String date = "";
		if (null != this.getDate()){
			DateFormat dateInstance = DateFormat.getDateInstance(
					stypePatternId, new Locale(this.getRenderingLang(), ""));
			date = dateInstance.format(this.getDate());
		}
		return date;
	}
	
	@Override
	public String toString() {
		return (null != this.getDate())? this.getDate().toString(): "";
	}
	
	/**
	 * Return the date formatted using the given pattern.
	 * @param formatPattern The pattern to format the date with.
	 * @return The date in the requested format.
	 */
	public String getFormattedDate(String formatPattern) {
		String date = "";
		if (null != this.getDate()) {
			String langCode = this.getRenderingLang();
			if (null == langCode) langCode = this.getDefaultLangCode();
			SimpleDateFormat formatter = new SimpleDateFormat(formatPattern, new Locale(langCode, ""));
			date = formatter.format(this.getDate());
		}
		return date;
	}
	
	/**
	 * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#getJDOMElement()
	 */
	@Override
	public Element getJDOMElement() {
		Element attributeElement = new Element("attribute");
		attributeElement.setAttribute("name", this.getName());
		attributeElement.setAttribute("attributetype", this.getType());
		if (null != this.getDate()) {
			Element dateElement = new Element("date");
			dateElement.setText(this.getFormattedDate(SystemConstants.SYSTEM_DATE_FORMAT));
			attributeElement.addContent(dateElement);
		}
		return attributeElement;
	}
	
	@Override
	public Object getValue() {
		return this.getDate();
	}
	
	/**
	 * Return the Date object.
	 * @return The date held by the attribute.
	 */
	public Date getDate() {
		return _date;
	}

	/**
	 * Set up the date this attribute will carry.
	 * @param date The date of this attribute
	 */
	public void setDate(Date date) {
		this._date = date;
	}

	@Override
	public boolean isSearchableOptionSupported() {
		return true;
	}
	
	@Override
	public List<AttributeSearchInfo> getSearchInfos(List<Lang> systemLangs) {
		if (this.getDate() != null) {
			List<AttributeSearchInfo> infos = new ArrayList<AttributeSearchInfo>();
			AttributeSearchInfo info = new AttributeSearchInfo(null, this.getDate(), null, null);
			infos.add(info);
			return infos;
		}
		return null;
	}
	
	@Override
	protected IAttributeValidationRules getValidationRuleNewIntance() {
		return new DateAttributeValidationRules();
	}
	
	/**
	 * Set up the "date" as submitted in the back-office area.
	 * This method is used to handle the entity in the back-office
	 * @param failedDateString The date string as submitted in the back-office area
	 */
	public void setFailedDateString(String failedDateString) {
		this._failedDateString = failedDateString;
	}

	/**
	 * Return the "date" string as submitted in the back-office area.
	 * This method is used to handle the entity in the back-office.
	 * @return The date string as submitted in the back-office area
	 */
	public String getFailedDateString() {
		return _failedDateString;
	}
	
	@Override
	protected Object getJAXBValue(String langCode) {
		return this.getDate();
	}
	
	private Date _date;
	private String _failedDateString;
	
}
