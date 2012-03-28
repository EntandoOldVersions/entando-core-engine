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
package com.agiletec.apsadmin.system.entity;

import javax.servlet.http.HttpServletRequest;

import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Base interface for the helper classes that support the actions which
 * handle the elements based on 'ApsEntity'.
 * @author E.Santoboni
 */
public interface IEntityActionHelper {
	
	public void updateEntity(IApsEntity currentEntity, HttpServletRequest request);
	
	public void scanEntity(IApsEntity currentEntity, ActionSupport action);
	
	public EntitySearchFilter[] getSearchFilters(AbstractApsEntityFinderAction entityFinderAction, IApsEntity prototype);
	
}
