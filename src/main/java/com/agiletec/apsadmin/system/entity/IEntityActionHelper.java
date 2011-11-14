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
package com.agiletec.apsadmin.system.entity;

import javax.servlet.http.HttpServletRequest;

import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Base interface for the helper classes that support the actions which
 * handle the elements based on 'ApsEntity'.
 * 
 * @author E.Santoboni
 */
public interface IEntityActionHelper {
	
	/**
	 * Effettua l'aggiornamento degli Attributi della entità specificata 
	 * attraverso i valori corrispondenti dei parametri estratti dalla richiesta corrente.
	 * 
	 * Updates the attributes of the specified entity using the values in the parameters
	 * extracted from the current request.
	 * 
	 * @param currentEntity The ApsEntity to update.
	 * @param request The current request.
	 * @deprecated use updateEntity
	 */
	public void updateEntityAttributes(IApsEntity currentEntity, HttpServletRequest request);
	
	public void updateEntity(IApsEntity currentEntity, HttpServletRequest request);
	
	/**
	 * Perform the validation check of the given ApsEntity.
	 * The error messages related to the errors found are inserted in the action in the form of
	 * 'ActionFieldError'. 
	 * 
	 * @param currentEntity The ApsEntity to test.
	 * @param action The current action.
	 * @deprecated use scanEntity
	 */
	public void scanEntityAttributes(IApsEntity currentEntity, ActionSupport action);
	
	public void scanEntity(IApsEntity currentEntity, ActionSupport action);
	
	public EntitySearchFilter[] getSearchFilters(AbstractApsEntityFinderAction entityFinderAction, IApsEntity prototype);
	
}
