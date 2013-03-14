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
