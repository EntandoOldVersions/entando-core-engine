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
package com.agiletec.apsadmin.user;

/**
 * Interfaccia base per le classi Action delegate 
 * alla gestione delle associazioni utente/autorizzazioni.
 * @author E.Santoboni
 */
public interface IUserToAuthoritiesAction {
	
	/**
	 * Esegue l'operazione di richiesta di modifica autorizzazioni utente.
	 * @return Il codice del risultato dell'azione.
	 */
	public String edit();
	
	/**
	 * Esegue l'operazione di aggiunta gruppo ad un utente.
	 * @return Il codice del risultato dell'azione.
	 */
	public String addGroup();
	
	/**
	 * Esegue l'operazione di rimozione gruppo ad un utente.
	 * @return Il codice del risultato dell'azione.
	 */
	public String removeGroup();
	
	/**
	 * Esegue l'operazione di aggiunta ruolo ad un utente.
	 * @return Il codice del risultato dell'azione.
	 */
	public String addRole();
	
	/**
	 * Esegue l'operazione di rimozione ruolo ad un utente.
	 * @return Il codice del risultato dell'azione.
	 */
	public String removeRole();
	
	/**
	 * Esegue l'operazione di salvataggio di un utente.
	 * @return Il codice del risultato dell'azione.
	 */
	public String save();
	
	public static final String CURRENT_FORM_USER_AUTHS_PARAM_NAME = "currentUserOnForm";
	
}
