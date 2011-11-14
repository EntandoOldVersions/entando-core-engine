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