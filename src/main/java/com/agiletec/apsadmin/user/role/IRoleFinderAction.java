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
package com.agiletec.apsadmin.user.role;

import java.util.List;

import com.agiletec.aps.system.services.role.Role;

/**
 * Interfaccia base per le classi action della lista Ruoli.
 * @version 1.0
 * @author E.Santoboni
 */
public interface IRoleFinderAction {
	
	/**
	 * Restituisce la lista dei ruoli che deve essere erogata 
	 * dall'interfaccia di visualizzazione dei ruoli.
	 * @return La lista di ruoli.
	 */
	public List<Role> getRoles();
	
}