/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* You can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.plugins.jacms.apsadmin.tags;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.IEntityManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.tags.EntityTypeInfoTag;

import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.helper.IContentAuthorizationHelper;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import javax.servlet.http.HttpSession;

/**
 * Returns a content type (or one of its property) through the code.
 * You can choose whether to return the entire object (leaving the attribute "property" empty) or a single property.
 * The names of the available property of "Content Type": "typeCode", "typeDescr", 
 * "attributeMap" (map of attributes indexed by the name), "attributeList" (list of attributes).
 * The special property "isAuthToEdit" return true if the current user is allowed to edit a content of the given type.
 * @author E.Santoboni
 */
public class ContentTypeInfoTag extends EntityTypeInfoTag {
	
	@Override
	protected String getEntityManagerName() {
		return JacmsSystemConstants.CONTENT_MANAGER;
	}
	
	@Override
	protected Object getPropertyValue(Object masterObject, String propertyValue) {
		if (null == propertyValue || !propertyValue.equals("isAuthToEdit")) {
			return super.getPropertyValue(masterObject, propertyValue);
		}
		try {
			HttpSession session = this.pageContext.getSession();
			UserDetails currentUser = (UserDetails) session.getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER);
			Content prototype = (Content) masterObject;
			IContentAuthorizationHelper helper = 
					(IContentAuthorizationHelper) ApsWebApplicationUtils.getBean(JacmsSystemConstants.CONTENT_AUTHORIZATION_HELPER, this.pageContext);
			return helper.isAuthToEdit(currentUser, prototype);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getPropertyValue", 
					"Error extracting property value : Master Object '" 
						+ masterObject.getClass().getName() + "' - property '" + propertyValue + "'");
		}
		return null;
	}
    
	
}