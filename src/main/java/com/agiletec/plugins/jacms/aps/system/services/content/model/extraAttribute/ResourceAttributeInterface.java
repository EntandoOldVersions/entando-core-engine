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

import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInterface;

/**
 * Interfaccia per gli attributi di entità di tipo risorsa.
 * @author W.Ambu
 */
public interface ResourceAttributeInterface extends AttributeInterface {
	
	/**
	 * Restituisce la risorsa associata all'attributo.
	 * @return la risorsa associata all'attributo.
	 */
    public ResourceInterface getResource();
    
    /**
	 * Restituisce la risorsa associata all'attributo.
	 * @param langCode il codice della lingua.
	 * @return la risorsa associata all'attributo.
	 */
    public ResourceInterface getResource(String langCode);
    
	/**
	 * Setta una risorsa sull'attributo.
	 * @param resource La risorsa da associare all'attributo.
	 * @param langCode il codice della lingua.
	 */
    public void setResource(ResourceInterface resource, String langCode);

}
