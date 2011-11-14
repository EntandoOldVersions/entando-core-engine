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
package com.agiletec.aps.system.services.showlettype;

import java.util.List;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.util.ApsProperties;

/**
 * Interfaccia base per i Servizi gestiori dei tipi di 
 * showlet (ShowletType) definiti nel sistema.
 * @author 
 */
public interface IShowletTypeManager {
	
	/**
	 * Restituisce la definizione di un tipo di showlet in base al codice.
	 * @param code Il codice univoco del tipo
	 * @return La definizione del tipo di showlet
	 */
	public ShowletType getShowletType(String code);
	
	/**
	 * Restituisce la lista completa (ordinata per descrizione) dei tipi di showlet.
	 * @return la lista completa delle showlet (ordinata per la descrizione del tipo) 
	 * disponibili in oggetti ShowletType.
	 */
	public List<ShowletType> getShowletTypes();
	
	public void addShowletType(ShowletType showletType) throws ApsSystemException;
	
	public void deleteShowletType(String showletTypeCode) throws ApsSystemException;
	
	@Deprecated
	public void updateShowletTypeTitles(String showletTypeCode, ApsProperties titles) throws ApsSystemException;
	
	@Deprecated
	public void updateShowletType(String showletTypeCode, ApsProperties titles, ApsProperties defaultConfig) throws ApsSystemException;

	public void updateShowletType(String showletTypeCode, ApsProperties titles, ApsProperties defaultConfig, String mainGroup) throws ApsSystemException;
	
}