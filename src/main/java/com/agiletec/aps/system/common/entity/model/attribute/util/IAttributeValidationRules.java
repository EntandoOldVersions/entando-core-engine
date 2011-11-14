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

import java.io.Serializable;

import org.jdom.Element;

/**
 * @author E.Santoboni
 */
public interface IAttributeValidationRules extends Serializable {
	
	public IAttributeValidationRules clone();
	
	public void setConfig(Element attributeElement);
	
	public Element getJDOMConfigElement();
	
	/**
	 * Set up the required (mandatory) condition for the current attribute.
	 * @param required True if the attribute is mandatory
	 */
	public void setRequired(boolean required);
	
	/**
	 * Test whether this attribute is declared mandatory or not.
	 * @return True if the attribute is mandatory, false otherwise.
	 */
	public boolean isRequired();

	public OgnlValidationRule getOgnlValidationRule();
	
	public void setOgnlValidationRule(OgnlValidationRule ognlValidationRule);
	
	public static final String VALIDATIONS_ELEMENT_NAME = "validations";
	
}