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

import com.agiletec.aps.system.services.authorization.IApsAuthority;

/**
 * Interfaccia base per le classi Action delegate 
 * alla gestione delle associazioni autorizzazione/utenti.
 * @author E.Santoboni
 */
public interface IAuthorityToUsersAction {
	
	/**
	 * Esegue l'operazione di associazione di un utente 
	 * al gruppo di utenti aventi l'autorizzazione gestita.
	 * @return Il codice del risultato dell'azione.
	 */
	public String addUser();
	
	/**
	 * Esegue l'operazione di rimozione di un utente 
	 * dal gruppo di utenti aventi l'autorizzazione gestita.
	 * @return Il codice del risultato dell'azione.
	 */
	public String removeUser();
	
	/**
	 * Restituisce l'autorizzazione gestita.
	 * @return L'autorizzazione gestita.
	 */
	public IApsAuthority getApsAuthority();
	
}
