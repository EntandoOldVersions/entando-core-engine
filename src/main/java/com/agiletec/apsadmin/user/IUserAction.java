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