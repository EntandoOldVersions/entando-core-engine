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
package com.agiletec.apsadmin.system.entity.attribute.manager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.ITextAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.MonoTextAttribute;
import com.agiletec.apsadmin.system.entity.attribute.AttributeTracer;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Manager class for the 'Monotext' Attribute.
 * @author E.Santoboni
 */
public class MonoTextAttributeManager extends AbstractMonoLangAttributeManager {
	
	@Override
	protected Object getValue(AttributeInterface attribute) {
		String text = ((MonoTextAttribute) attribute).getText();
		if (null != text && text.trim().length()>0) {
			return text;
		}
		return null;
	}
	
	@Override
	protected void setValue(AttributeInterface attribute, String value) {
		((MonoTextAttribute) attribute).setText(value);
	}
	
	@Override
	protected void checkSingleAttribute(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		super.checkSingleAttribute(action, attribute, tracer, entity);
		this.checkTextLengths(action, attribute, tracer);
		this.checkRegExp(action, attribute, tracer);
	}
	
	@Override
	protected void checkListElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		super.checkListElement(action, attribute, tracer, entity);
		this.checkTextLengths(action, attribute, tracer);
		this.checkRegExp(action, attribute, tracer);
	}
	
	@Override
	protected void checkMonoListCompositeElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		super.checkMonoListCompositeElement(action, attribute, tracer, entity);
		this.checkTextLengths(action, attribute, tracer);
		this.checkRegExp(action, attribute, tracer);
	}
	
	@Override
	protected void checkMonoListElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		super.checkMonoListElement(action, attribute, tracer, entity);
		this.checkTextLengths(action, attribute, tracer);
		this.checkRegExp(action, attribute, tracer);
	}
	
	protected void checkTextLengths(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer) {
		int maxLength = ((ITextAttribute) attribute).getMaxLength();
		int minLength = ((ITextAttribute) attribute).getMinLength();
		if (maxLength != -1 || minLength != -1) {
			String text = this.getTextForCheckLength(attribute);
			if (text != null) {
				text = text.trim();
				if (maxLength != -1 && text.length()>maxLength && text.length()>0) {
					String[] args = {String.valueOf(text.length()), String.valueOf(maxLength)};
					super.addFieldError(action, attribute, tracer, "MonotextAttribute.fieldError.invalidMaxLength", args);
				}
				if (minLength != -1 && text.length()<minLength && text.length()>0) {
					String[] args = {String.valueOf(text.length()), String.valueOf(minLength)};
					super.addFieldError(action, attribute, tracer, "MonotextAttribute.fieldError.invalidMinLength", args);
				}
			}
		}
	}
	
	protected void checkRegExp(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer) {
		String value = (String) this.getValue(attribute);
		ITextAttribute textAttribute = (ITextAttribute) attribute;
		if (null != value && null != textAttribute.getRegexp()) {
			Pattern pattern = Pattern.compile(textAttribute.getRegexp());
			Matcher matcher = pattern.matcher(value);
			if (!matcher.matches()) {
				super.addFieldError(action, attribute, tracer, "MonotextAttribute.fieldError.invalidInsertedText", null);
			}
		}
	}
	
	protected String getTextForCheckLength(AttributeInterface attribute) {
		return (String) this.getValue(attribute);
	}
	
}