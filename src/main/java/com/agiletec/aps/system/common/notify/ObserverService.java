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
package com.agiletec.aps.system.common.notify;

/**
 * Interfaccia base per l'estensione delle interfaccie base 
 * per l'implementazione dei servizi destinatari alla notificazione 
 * di eventi particolari. L'interfaccia non deve essere implementata 
 * direttamente dai servizi ascoltatori.
 * @author E.Santoboni - M.Diana
 */
public interface ObserverService {
	
	/**
	 * Aggiorna il servizio in base all'evento notificato.
	 * @param event L'evento notificato.
	 */
	public void update(ApsEvent event);
	
}
