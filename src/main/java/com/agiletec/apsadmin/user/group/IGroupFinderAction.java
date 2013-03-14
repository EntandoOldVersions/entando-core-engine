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
package com.agiletec.apsadmin.user.group;

import java.util.List;

import com.agiletec.aps.system.services.group.Group;

/**
 * Interfaccia base per le classi action della lista Gruppi.
 * @version 1.0
 * @author E.Santoboni
 */
public interface IGroupFinderAction {
	
	/**
	 * Restituisce la lista dei gruppi che deve essere erogata dall'interfaccia di 
	 * visualizzazione dei gruppi.
	 * @return La lista di gruppi.
	 */
	public List<Group> getGroups();
	
}