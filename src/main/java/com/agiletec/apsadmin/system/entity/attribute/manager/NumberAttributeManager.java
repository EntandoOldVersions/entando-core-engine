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
package com.agiletec.apsadmin.system.entity.attribute.manager;

import java.math.BigDecimal;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.NumberAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.util.NumberAttributeValidationRules;
import com.agiletec.apsadmin.system.entity.attribute.AttributeTracer;
import com.agiletec.apsadmin.util.CheckFormatUtil;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Manager class for the 'Number' Attribute 
 * @author E.Santoboni
 */
public class NumberAttributeManager extends AbstractMonoLangAttributeManager {
	
	@Override
	protected void setValue(AttributeInterface attribute, String value) {
		NumberAttribute numberAttribute = (NumberAttribute) attribute;
		BigDecimal number = null;
		if (value != null) value = value.trim();
		if (CheckFormatUtil.isValidNumber(value)) {
			try {
				number = new BigDecimal(value);
				numberAttribute.setFailedNumberString(null);
			} catch (NumberFormatException e) {
				throw new RuntimeException("The submitted string is not recognized as a valid number - " + value + " -");
			}
		} else {
			numberAttribute.setFailedNumberString(value);
		}
		numberAttribute.setValue(number);
	}
	
	@Override
	protected Object getValue(AttributeInterface attribute) {
		return ((NumberAttribute) attribute).getValue();
	}
	
	@Override
	protected int getState(AttributeInterface attribute, AttributeTracer tracer) {
		int state = super.getState(attribute, tracer);
		boolean valuedString = ((NumberAttribute) attribute).getFailedNumberString() != null;
		if (state == VALUED_ATTRIBUTE_STATE || valuedString) return this.VALUED_ATTRIBUTE_STATE;
		return this.EMPTY_ATTRIBUTE_STATE;
	}
	
	@Override
	protected void checkSingleAttribute(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		super.checkSingleAttribute(action, attribute, tracer, entity);
		this.checkNumber(action, attribute, tracer);
		this.validateNumber(action, attribute, tracer);
	}
	
	@Override
	protected void checkListElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		super.checkListElement(action, attribute, tracer, entity);
		this.checkNumber(action, attribute, tracer);
		this.validateNumber(action, attribute, tracer);
	}
	
	@Override
	protected void checkMonoListCompositeElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		super.checkMonoListCompositeElement(action, attribute, tracer, entity);
		this.checkNumber(action, attribute, tracer);
		this.validateNumber(action, attribute, tracer);
	}
	
	@Override
	protected void checkMonoListElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		super.checkMonoListElement(action, attribute, tracer, entity);
		this.checkNumber(action, attribute, tracer);
		this.validateNumber(action, attribute, tracer);
	}
	
	private void checkNumber(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer) {
		if (this.getState(attribute, tracer) == VALUED_ATTRIBUTE_STATE && !this.hasRightValue(attribute)) {
			this.addFieldError(action, attribute, tracer, "NumberAttribute.fieldError.invalidNumber", null);
		}
	}
	
	private void validateNumber(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer) {
		if (this.getState(attribute, tracer) == VALUED_ATTRIBUTE_STATE && this.hasRightValue(attribute)) {
			NumberAttributeValidationRules valRules = (NumberAttributeValidationRules) attribute.getValidationRules();
			Integer attributeValue = ((NumberAttribute) attribute).getValue().intValue();
			Integer startValue = (valRules.getRangeStart() != null) ? (Integer) valRules.getRangeStart() : this.getOtherAttributeValue(attribute, valRules.getRangeStartAttribute());
			if (null != startValue && attributeValue < startValue) {
				String[] args = {startValue.toString()};
				this.addFieldError(action, attribute, tracer, "NumberAttribute.fieldError.lessValue", args);
			}
			Integer endValue = (valRules.getRangeEnd() != null) ? (Integer) valRules.getRangeEnd() : this.getOtherAttributeValue(attribute, valRules.getRangeEndAttribute());
			if (null != endValue && attributeValue > endValue) {
				String[] args = {endValue.toString()};
				this.addFieldError(action, attribute, tracer, "NumberAttribute.fieldError.greaterValue", args);
			}
			Integer value = (valRules.getValue() != null) ? (Integer) valRules.getValue() : this.getOtherAttributeValue(attribute, valRules.getValueAttribute());
			if (null != value && attributeValue != value) {
				String[] args = {value.toString()};
				this.addFieldError(action, attribute, tracer, "NumberAttribute.fieldError.wrongValue", args);
			}
		}
	}
	
	private Integer getOtherAttributeValue(AttributeInterface attribute, String otherAttributeName) {
		AttributeInterface other = (AttributeInterface) attribute.getParentEntity().getAttribute(otherAttributeName);
		if (null != other && (other instanceof NumberAttribute) && ((NumberAttribute) other).getValue() != null) {
			return ((NumberAttribute) other).getValue().intValue();
		}
		return null;
	}
	
	/**
	 * Check for the coherency of the data of the attribute. 
	 * @param attribute The attribute to check.
	 * @return true if the attribute is properly valued, false otherwise.
	 */
	private boolean hasRightValue(AttributeInterface attribute) {
		if (this.getValue(attribute) != null) return true;
		String insertedNumberString = ((NumberAttribute) attribute).getFailedNumberString();
		return CheckFormatUtil.isValidNumber(insertedNumberString);
	}
	
	@Override
	protected String getInvalidAttributeMessage() {
		return "NumberAttribute.fieldError.invalidNumber";
	}
	
}