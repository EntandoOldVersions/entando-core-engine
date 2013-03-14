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
package com.agiletec.apsadmin.user.role;

/**
 * Interfaccia base per le classi action della gestione Ruoli.
 * @version 1.0
 * @author E.Santoboni
 */
public interface IRoleAction {
	
	/**
	 * Esegue l'operazione di richiesta creazione di nuovo ruolo.
	 * @return Il codice del risultato dell'azione.
	 */
	public String newRole();
	
	/**
	 * Esegue l'operazione di richiesta di modifica ruolo.
	 * @return Il codice del risultato dell'azione.
	 */
	public String edit();
	
	/**
	 * Esegue l'operazione di salvataggio di un ruolo.
	 * @return Il codice del risultato dell'azione.
	 */
	public String save();
	
	/**
	 * Esegue l'operazione di visualizzazione di un ruolo.
	 * @return Il codice del risultato dell'azione.
	 */
	public String view();
	
	/**
	 * Esegue le operazioni di richiesta di cancellazione ruolo.
	 * @return Il codice del risultato dell'azione.
	 */
	public String trash();
	
	/**
	 * Esegue l'operazione di cancellazione di un ruolo.
	 * @return Il codice del risultato dell'azione.
	 */
	public String delete();
	
}