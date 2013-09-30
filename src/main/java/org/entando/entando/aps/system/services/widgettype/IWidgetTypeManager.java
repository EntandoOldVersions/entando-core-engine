/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software. 
* Entando is a free software; 
* You can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package org.entando.entando.aps.system.services.widgettype;

import java.util.List;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.util.ApsProperties;

/**
 * Interfaccia base per i Servizi gestiori dei tipi di 
 * showlet (WidgetType) definiti nel sistema.
 * @author E.Santoboni
 */
public interface IWidgetTypeManager {
	
	/**
	 * @deprecated Use {@link #getWidgetType(String)} instead
	 */
	public WidgetType getShowletType(String code);

	/**
	 * Restituisce la definizione di un tipo di showlet in base al codice.
	 * @param code Il codice univoco del tipo
	 * @return La definizione del tipo di showlet
	 */
	public WidgetType getWidgetType(String code);
	
	/**
	 * @deprecated Use {@link #getWidgetTypes()} instead
	 */
	public List<WidgetType> getShowletTypes();

	/**
	 * Restituisce la lista completa (ordinata per descrizione) dei tipi di showlet.
	 * @return la lista completa dei widget (ordinata per la descrizione del tipo) 
	 * disponibili in oggetti WidgetType.
	 */
	public List<WidgetType> getWidgetTypes();
	
	/**
	 * @deprecated Use {@link #addWidgetType(WidgetType)} instead
	 */
	public void addShowletType(WidgetType showletType) throws ApsSystemException;

	public void addWidgetType(WidgetType widgetType) throws ApsSystemException;
	
	/**
	 * @deprecated Use {@link #deleteWidgetType(String)} instead
	 */
	public void deleteShowletType(String showletTypeCode) throws ApsSystemException;

	public void deleteWidgetType(String widgetTypeCode) throws ApsSystemException;
	
	@Deprecated
	public void updateShowletTypeTitles(String showletTypeCode, ApsProperties titles) throws ApsSystemException;
	
	@Deprecated
	public void updateShowletType(String showletTypeCode, ApsProperties titles, ApsProperties defaultConfig) throws ApsSystemException;

	/**
	 * @deprecated Use {@link #updateWidgetType(String,ApsProperties,ApsProperties,String)} instead
	 */
	public void updateShowletType(String showletTypeCode, ApsProperties titles, ApsProperties defaultConfig, String mainGroup) throws ApsSystemException;

	public void updateWidgetType(String showletTypeCode, ApsProperties titles, ApsProperties defaultConfig, String mainGroup) throws ApsSystemException;
	
}