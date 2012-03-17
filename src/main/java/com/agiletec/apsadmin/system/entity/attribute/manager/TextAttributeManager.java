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

import com.agiletec.aps.system.common.entity.model.AttributeFieldError;
import com.agiletec.aps.system.common.entity.model.FieldError;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.ITextAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.TextAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.util.TextAttributeValidationRules;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.apsadmin.system.entity.attribute.AttributeTracer;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Manager class for the 'Multi-language Text' Attribute. 
 * @author E.Santoboni
 */
public class TextAttributeManager extends AbstractMultiLangAttributeManager {
	
	@Deprecated
	protected Object getValue(AttributeInterface attribute, Lang lang) {
		return ((TextAttribute) attribute).getTextMap().get(lang.getCode());
	}
	
	protected void setValue(AttributeInterface attribute, Lang lang, String value) {
		((TextAttribute) attribute).setText(value, lang.getCode());
	}
        
        @Deprecated
	protected void checkSingleAttribute(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		AttributeTracer textTracer = (AttributeTracer) tracer.clone();
		Lang defaultLang = this.getLangManager().getDefaultLang();
		textTracer.setLang(defaultLang);
		super.checkSingleAttribute(action, attribute, textTracer, entity);
		this.checkText(action, attribute, tracer);
	}
	
	@Deprecated
	protected void checkMonoListCompositeElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		super.checkMonoListCompositeElement(action, attribute, tracer, entity);
		this.checkText(action, attribute, tracer);
	}
	
	@Deprecated
	protected void checkMonoListElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		super.checkMonoListElement(action, attribute, tracer, entity);
		this.checkText(action, attribute, tracer);
	}
	
        @Deprecated
	protected void checkText(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer) {
		Iterator<Lang> langsIter = this.getLangManager().getLangs().iterator();
		while (langsIter.hasNext()) {
			Lang lang = (Lang) langsIter.next();
			AttributeTracer textTracer = (AttributeTracer) tracer.clone();
			textTracer.setLang(lang);
			this.checkTextLengths(action, attribute, textTracer, lang);
			this.checkRegExp(action, attribute, textTracer, lang);
		}
	}
	
        @Deprecated
	protected void checkTextLengths(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, Lang lang) {
		int maxLength = ((ITextAttribute) attribute).getMaxLength();
		int minLength = ((ITextAttribute) attribute).getMinLength();
		if (maxLength != -1 || minLength != -1) {
			String text = this.getTextForCheckLength(attribute, lang);
			if (text != null) {
				text = text.trim();
				if (maxLength != -1 && text.length()>maxLength && text.length()>0) {
					String[] args = {String.valueOf(text.length()), String.valueOf(maxLength), lang.getDescr()};
					super.addFieldError(action, attribute, tracer, "TextAttribute.fieldError.invalidMaxLength", args);
				}
				if (minLength != -1 && text.length()<minLength && text.length()>0) {
					String[] args = {String.valueOf(text.length()), String.valueOf(minLength), lang.getDescr()};
					super.addFieldError(action, attribute, tracer, "TextAttribute.fieldError.invalidMinLength", args);
				}
			}
		}
	}
	
        @Deprecated
	protected void checkRegExp(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, Lang lang) {
		String value = (String) this.getValue(attribute, lang);
		ITextAttribute textAttribute = (ITextAttribute) attribute;
		if (null != value && null != textAttribute.getRegexp()) {
			Pattern pattern = Pattern.compile(textAttribute.getRegexp());
			Matcher matcher = pattern.matcher(value);
			if (!matcher.matches()) {
				String[] args = {lang.getDescr()};
				super.addFieldError(action, attribute, tracer, "TextAttribute.fieldError.invalidInsertedText", args);
			}
		}
	}
	
        @Deprecated
	protected String getTextForCheckLength(AttributeInterface attribute, Lang lang) {
		return (String) this.getValue(attribute, lang);
	}
        

    protected String getCustomAttributeErrorMessage(AttributeFieldError attributeFieldError, ActionSupport action, AttributeInterface attribute) {
        TextAttributeValidationRules valRules = (TextAttributeValidationRules) attribute.getValidationRules();
        if (null != valRules) {
            ITextAttribute textAttribute = (ITextAttribute) attribute;
            Lang lang = attributeFieldError.getTracer().getLang();
            String langCode = (null != lang) ? lang.getCode() : null;
            String text = textAttribute.getTextForLang(langCode);
            String errorCode = attributeFieldError.getErrorCode();
            if (errorCode.equals(FieldError.INVALID_MIN_LENGTH)) {
                String[] args = {String.valueOf(text.length()), String.valueOf(valRules.getMaxLength()), lang.getDescr()};
                return action.getText("TextAttribute.fieldError.invalidMaxLength", args);
            } else if (errorCode.equals(FieldError.INVALID_MIN_LENGTH)) {
                String[] args = {String.valueOf(text.length()), String.valueOf(valRules.getMinLength()), lang.getDescr()};
                return action.getText("TextAttribute.fieldError.invalidMinLength", args);
            } else if (errorCode.equals(FieldError.INVALID_FORMAT)) {
                String[] args = {lang.getDescr()};
                return action.getText("TextAttribute.fieldError.invalidInsertedText", args);
            }
        }
        return action.getText(this.getInvalidAttributeMessage());
    }
    
	
}