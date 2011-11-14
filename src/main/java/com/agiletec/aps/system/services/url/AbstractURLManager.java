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
package com.agiletec.aps.system.services.url;

import java.util.Iterator;
import java.util.Map;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.common.AbstractService;

/**
 * Servizio di creazione di URL alle risorse del sistema. Per ora è previsto
 * solo l'URL da utilizzare come link ad una pagina del portale.<br>
 * Costituisce anche la factory per gli oggetti PageURL.
 * La classe è astratta in quanto la mappatura tra url e risorse può
 * essere differente a seconda delle esigenze. Ad esempio, in un portale
 * multilingua può essere utile introdurre il codice lingua come parte
 * dell'URL delle pagine. Alcuni metodi di validità generale sono implementati.
 * @author M.Diana
 */
public abstract class AbstractURLManager extends AbstractService implements IURLManager {

	/**
	 * Crea e restituisce un oggetto PageURL.<br>
	 * N.B.: l'oggetto restituito deve essere utilizzato nell'ambito
	 * della richiesta corrente (non memorizzarlo in modo più persistente, ad
	 * esempio in sessione) in quanto contiene riferimenti ad altri servizi.
	 * @param reqCtx Il contesto della richiesta.
	 * @return L'oggetto creato.
	 */
	@Override
	public PageURL createURL(RequestContext reqCtx){
		PageURL pageUrl = new PageURL(this, reqCtx);
		return pageUrl;
	}
	
	/**
	 * Costruisce la query string a partire dai parametri passati.
	 * @param params Una mappa di parametri, indicizzata in base al nome.
	 * @return La query string; se la mappa passata è nulla o vuota restituisce
	 * una stringa vuota, se la mappa non è vuota la stringa restituita comprende
	 * il carattere ? di introduzione e il separatore & se ci sono più parametri.
	 */
	protected String createQueryString(Map<String, String> params) {
		String queryString = "";
		if (params != null && !params.isEmpty()) {
			StringBuffer buf = new StringBuffer();
			buf.append("?");
			Iterator<String> keyIter = params.keySet().iterator();
			int index = 1;
			while (keyIter.hasNext()) {
				String name = keyIter.next();
				buf.append(name + '=' + params.get(name));
				if (index != params.size()) {
					buf.append("&amp;");
					index++;
				}
			}
			queryString = buf.toString();
		}
		return queryString;
	}
	
}
