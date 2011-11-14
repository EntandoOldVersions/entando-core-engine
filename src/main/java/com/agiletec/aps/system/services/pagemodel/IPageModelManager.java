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
package com.agiletec.aps.system.services.pagemodel;

import java.util.Collection;

/**
 * Interfaccia base per i servizi di gestione dei modelli di pagina.
 * @author 
 */
public interface IPageModelManager {
	
	/**
	 * Restituisce il modello di pagina con il codice dato
	 * @param name Il nome del modelo di pagina
	 * @return Il modello di pagina richiesto
	 */
	public PageModel getPageModel(String name);
	
	/**
	 * Restituisce la Collection completa di modelli.
	 * @return la collection completa dei modelli disponibili in oggetti PageModel.
	 */
	public Collection<PageModel> getPageModels();
	
}
