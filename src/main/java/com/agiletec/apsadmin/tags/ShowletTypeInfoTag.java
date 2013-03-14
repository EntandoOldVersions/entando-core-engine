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
package com.agiletec.apsadmin.tags;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.showlettype.IShowletTypeManager;
import com.agiletec.aps.system.services.showlettype.ShowletTypeParameter;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Returns a showlet type (or one of its property) through the code.
 * You can choose whether to return the entire object (leaving the attribute "property" empty) or a single property.
 * The names of the available property of "ShowletType": "code", "titles" (map of titles indexed by the system languages), "parameters" (list of object {@link ShowletTypeParameter}), 
 * "action" (the code of the action used to manage the type), "pluginCode", "parentTypeCode", 
 * "config" (map of default parameter values indexed by the key), "locked".
 * @author E.Santoboni
 */
public class ShowletTypeInfoTag extends AbstractObjectInfoTag {
	
	@Override
	protected Object getMasterObject(String keyValue) throws Throwable {
		IShowletTypeManager showletTypeManager = (IShowletTypeManager) ApsWebApplicationUtils.getBean(SystemConstants.SHOWLET_TYPE_MANAGER, this.pageContext);
		return showletTypeManager.getShowletType(keyValue);
	}
	
}