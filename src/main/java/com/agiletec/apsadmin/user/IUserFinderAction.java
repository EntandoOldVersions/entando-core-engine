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
package com.agiletec.apsadmin.user;

import java.util.List;

import com.agiletec.aps.system.services.user.UserDetails;

/**
 * Interfaccia base per le classi acion delegate alla ricerca e visualizzazione utenti in lista.
 * @author E.Santoboni
 */
public interface IUserFinderAction {
	
	/**
	 * Restituisce la lista degli utenti che deve essere erogata dall'interfaccia di 
	 * visualizzazione degli utenti.
	 * @return La lista di utenti che deve essere erogata dall'interfaccia di 
	 * visualizzazione degli utenti.
	 */
	public List<UserDetails> getUsers();
	
}
