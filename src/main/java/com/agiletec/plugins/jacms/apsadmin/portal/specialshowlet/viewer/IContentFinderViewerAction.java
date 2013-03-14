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
package com.agiletec.plugins.jacms.apsadmin.portal.specialshowlet.viewer;

import com.agiletec.plugins.jacms.apsadmin.content.IContentFinderAction;

/**
 * Interfaccia base per la classe Action che cerca i contenuti per 
 * la configurazione delle showlet di tipo "Pubblica contenuto singolo".
 * @author E.Santoboni
 */
public interface IContentFinderViewerAction extends IContentFinderAction {
	
	/**
	 * Esegue l'operazione di richiesta associazione di un contenuto alla showlet.
	 * La richiesta viene effettuata nell'interfaccia di ricerca risorse e viene redirezionata 
	 * alla action che gestisce la configurazione della showlet di pubblicazione contenuto.
	 * @return Il codice del risultato dell'azione.
	 */
	public String joinContent();
	
}
