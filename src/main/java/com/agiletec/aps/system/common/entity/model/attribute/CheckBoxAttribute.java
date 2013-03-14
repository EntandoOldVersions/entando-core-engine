/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando Enterprise Edition software.
* You can redistribute it and/or modify it
* under the terms of the Entando's EULA
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.aps.system.common.entity.model.attribute;

/**
 * This attribute represent an information of type CheckBox. 
 * This attribute does not support multiple languages.
 * @author E.Santoboni
 */
public class CheckBoxAttribute extends BooleanAttribute {
	
	@Override
	protected boolean saveBooleanJDOMElement() {
		return (null != super.getBooleanValue() && super.getBooleanValue());
	}
	
	@Override
	protected boolean addSearchInfo() {
		return (null != super.getBooleanValue() && super.getBooleanValue());
	}
	
}