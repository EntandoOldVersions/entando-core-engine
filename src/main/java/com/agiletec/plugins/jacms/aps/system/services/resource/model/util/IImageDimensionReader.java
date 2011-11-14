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
package com.agiletec.plugins.jacms.aps.system.services.resource.model.util;

import java.util.Map;

import com.agiletec.plugins.jacms.aps.system.services.resource.model.ImageResourceDimension;

/**
 * Interfaccia base per le classi delegate al caricamento 
 * delle dimensioni per il redimensionamento delle immagini.
 * @author E.Santoboni
 */
public interface IImageDimensionReader {
	
	/**
     * Restituisce la mappa delle dimensioni di resize delle immagini, 
     * indicizzate in base all'id della dimensione.
     * @return La mappa delle dimensioni di resize delle immagini.
     */
    public Map<Integer, ImageResourceDimension> getImageDimensions();
	
}
