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
package com.agiletec.plugins.jacms.apsadmin.portal.specialshowlet.viewer;

import com.agiletec.apsadmin.portal.specialshowlet.ISimpleShowletConfigAction;

/**
 * Interfaccia base per le action gestori della configurazione della showlet erogatore contenuto singolo.
 * @author E.Santoboni
 */
public interface IContentViewerShowletAction extends ISimpleShowletConfigAction {
	
	/**
	 * Esegue l'operazione di associazione di un contenuto alla showlet.
	 * L'operazione ha l'effetto di inserire il riferimento del contenuto desiderato 
	 * (ricavato dai parametri della richiesta) nei parametri di configurazione della showlet.
	 * @return Il codice del risultato dell'azione.
	 */
	public String joinContent();
	
}
