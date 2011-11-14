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

import java.util.Map;

/**
 * Interfaccia base per Data Access Object deii modelli di pagina (PageModel)
 * @author E.Santoboni
 */
public interface IPageModelDAO {
	
	/**
	 * Carica e restituisce la mappa dei modelli di pagina.
	 * @return la mappa dei modelli.
	 */
	public Map<String, PageModel> loadModels();
	
}
