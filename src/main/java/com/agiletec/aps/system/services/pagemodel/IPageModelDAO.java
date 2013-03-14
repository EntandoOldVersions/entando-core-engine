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
