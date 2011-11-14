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

import org.springframework.context.ApplicationEvent;

/**
 * Interfaccia base per i servizi notificatore eventi.
 * @author M.Diana - E.Santoboni
 */
public interface INotifyManager {
	
	/**
	 * Notifica un'evento a tutti i listener definiti nel sistema.
	 * @param event L'evento da notificare.
	 */
	public void publishEvent(ApplicationEvent event);
	
}