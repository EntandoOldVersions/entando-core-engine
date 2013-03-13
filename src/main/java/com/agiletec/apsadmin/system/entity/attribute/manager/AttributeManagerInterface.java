/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
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
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.apsadmin.system.entity.attribute.manager;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.agiletec.aps.system.common.entity.model.AttributeFieldError;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Basic interface for the attributes managers.
 * The manager, specific for every entity type, handles the update and verification processes of
 * every single Entity Attribute.
 * @author E.Santoboni
 */
public interface AttributeManagerInterface {
	
    /**
     * Update the attribute with the data passed through the entity edit form.
     * @param attribute The attribute of the entity
     * @param attributeManagers The map of the attribute managers, indexed by type.
     * @param request The request.
	 * @deprecated As of version 2.4.1 of Entando, use updateEntityAttribute(AttributeInterface, HttpServletRequest).
     */
    public void updateEntityAttribute(AttributeInterface attribute, Map<String, AttributeManagerInterface> attributeManagers, HttpServletRequest request);
    
    public void updateEntityAttribute(AttributeInterface attribute, HttpServletRequest request);
    
    /**
     * Check the validity of the given attribute eventually adding the 
     * proper error messages in the action.
     * @param action The action where to insert the error messages, if any.
     * @param attributeManagers The map of the attributes manager, indexed by type.
     * @param attribute The entity attribute.
     * @param entity The entity to check.
     * @deprecated As of version 2.4.1 of Entando, moved validation within single attribute.
     */
    public void checkEntityAttribute(ActionSupport action, Map<String, AttributeManagerInterface> attributeManagers, AttributeInterface attribute, IApsEntity entity);
    
	/**
	 * Return the error message for the given Attribute field error.
	 * @param attributeFieldError The Field error
	 * @param action The current action.
	 * @return The error message for the given attribute error.
	 */
    public String getErrorMessage(AttributeFieldError attributeFieldError, ActionSupport action);
    
}
