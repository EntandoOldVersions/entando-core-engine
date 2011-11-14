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
