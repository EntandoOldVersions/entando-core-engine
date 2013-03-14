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
package com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute;

import java.util.List;

import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.plugins.jacms.aps.system.services.content.model.CmsAttributeReference;

/**
 * Interfaccia base per gli attributi specifici per il cms.
 * @author E.Santoboni
 */
public interface IReferenceableAttribute {
	
	/**
	 * Restituisce la lista di referenze (in oggetti 
	 * tipo CmsAttributeReference) generati dall'attributo.
	 * @param systemLangs The system langs.
	 * @return La lista di referenze.
	 */
	public List<CmsAttributeReference> getReferences(List<Lang> systemLangs);
	
}
