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

/**
 * Interfaccia base per le classi Action delegate alla gestione utenti.
 * @author E.Santoboni
 */
public interface IUserAction {
	
	/**
	 * Esegue l'operazione di richiesta creazione di nuovo utente.
	 * @return Il codice del risultato dell'azione.
	 */
	public String newUser();
	
	/**
	 * Esegue l'operazione di richiesta di modifica utente.
	 * @return Il codice del risultato dell'azione.
	 */
	public String edit();
	
	/**
	 * Esegue l'operazione di salvataggio di un utente.
	 * @return Il codice del risultato dell'azione.
	 */
	public String save();
	
	/**
	 * Esegue le operazioni di richiesta di cancellazione utente.
	 * @return Il codice del risultato dell'azione.
	 */
	public String trash();
	
	/**
	 * Esegue l'operazione di cancellazione di un utente.
	 * @return Il codice del risultato dell'azione.
	 */
	public String delete();
	
}