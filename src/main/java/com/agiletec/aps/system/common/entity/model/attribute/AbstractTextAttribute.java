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
package com.agiletec.aps.system.common.entity.model.attribute;

import org.jdom.Element;

import com.agiletec.aps.system.common.entity.model.attribute.util.IAttributeValidationRules;
import com.agiletec.aps.system.common.entity.model.attribute.util.TextAttributeValidationRules;
import com.agiletec.aps.system.common.searchengine.IndexableAttributeInterface;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * This abstract class is the base for the 'Text' Attributes.
 * @author E.Santoboni
 */
public abstract class AbstractTextAttribute extends AbstractAttribute implements IndexableAttributeInterface, ITextAttribute {
	
	/**
	 * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#isTextAttribute()
	 */
	@Override
	public boolean isTextAttribute() {
		return true;
	}
	
	@Override
	@Deprecated (/** to guaranted compatibility with previsous version of jAPS 2.0.12 */)
	public void setAttributeConfig(Element attributeElement) throws ApsSystemException {
		super.setAttributeConfig(attributeElement);
		String maxLength = this.extractXmlAttribute(attributeElement, "maxlength", false);
		if (null != maxLength) {
			this.setMaxLength(Integer.parseInt(maxLength));
		}
		String minLength = this.extractXmlAttribute(attributeElement, "minlength", false);
		if (null != minLength) {
			this.setMinLength(Integer.parseInt(minLength));
		}
		Element regexpElement = attributeElement.getChild("regexp");
		if (null != regexpElement) {
			String regexp = regexpElement.getText();
			if (null != regexp && regexp.trim().length() > 0) {
				this.setRegexp(regexp);
			}
		}
	}
	
	@Override
	protected IAttributeValidationRules getValidationRuleNewIntance() {
		return new TextAttributeValidationRules();
	}
	
	@Override
	public int getMaxLength() {
		return ((TextAttributeValidationRules) this.getValidationRules()).getMaxLength();
	}
	
	@Override
	@Deprecated (/** to guaranted compatibility with previsous version of jAPS 2.0.12 */)
	public void setMaxLength(int maxLength) {
		((TextAttributeValidationRules) this.getValidationRules()).setMaxLength(maxLength);
	}
	
	@Override
	public int getMinLength() {
		return ((TextAttributeValidationRules) this.getValidationRules()).getMinLength();
	}
	
	@Override
	@Deprecated (/** to guaranted compatibility with previsous version of jAPS 2.0.12 */)
	public void setMinLength(int minLength) {
		((TextAttributeValidationRules) this.getValidationRules()).setMinLength(minLength);
		//this._minLength = minLength;
	}
	
	@Override
	public String getRegexp() {
		return ((TextAttributeValidationRules) this.getValidationRules()).getRegexp();
	}
	
	@Override
	@Deprecated (/** to guaranted compatibility with previsous version of jAPS 2.0.12 */)
	public void setRegexp(String regexp) {
		((TextAttributeValidationRules) this.getValidationRules()).setRegexp(regexp);
	}
	
	@Override
	protected Object getJAXBValue(String langCode) {
		return this.getTextForLang(langCode);
	}
	
}