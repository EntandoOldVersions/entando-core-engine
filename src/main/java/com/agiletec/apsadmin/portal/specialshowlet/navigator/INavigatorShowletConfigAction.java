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
package com.agiletec.apsadmin.portal.specialshowlet.navigator;

import java.util.List;

import com.agiletec.aps.system.services.page.showlet.NavigatorExpression;
import com.agiletec.apsadmin.portal.specialshowlet.ISimpleShowletConfigAction;

/**
 * Interfaccia per la classe action dell'interfaccia 
 * di gestione configurazione Showlet tipo Navigator
 * @version 1.0
 * @author E.Santoboni
 */
public interface INavigatorShowletConfigAction extends ISimpleShowletConfigAction {
	
	/**
	 * Esegue l'operazione di aggiunta di una espressione 
	 * in base ai parametri di richiesta corrente.
	 * @return Il codice del risultato dell'azione.
	 */
	public String addExpression();
	
	/**
	 * Esegue l'operazione di rimozione di una espressione 
	 * in base ai parametri di richiesta corrente.
	 * @return Il codice del risultato dell'azione.
	 */
	public String removeExpression();
	
	/**
	 * Esegue l'operazione di spostamento di una espressione 
	 * in base ai parametri di richiesta corrente.
	 * @return Il codice del risultato dell'azione.
	 */
	public String moveExpression();
	
	/**
	 * Restituisce la lista di espressioni associata alla showlet corrente.
	 * @return La lista di espressioni associata alla showlet corrente.
	 */
	public List<NavigatorExpression> getExpressions();
	
	public static final String MOVEMENT_UP_CODE = "UP";
	public static final String MOVEMENT_DOWN_CODE = "DOWN";
	
}