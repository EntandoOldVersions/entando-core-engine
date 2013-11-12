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

import java.util.Map;

import com.agiletec.aps.util.ApsProperties;

/**
 * Interfaccia base per Data Access Object dei tipi di showlet (WidgetType).
 * @author E.Santoboni
 */
public interface IWidgetTypeDAO {
	
	/**
	 * @deprecated Use {@link #loadWidgetTypes()} instead
	 */
	public Map<String, WidgetType> loadShowletTypes();

	/**
	 * Carica e restituisce il Map dei tipi di showlet.
	 * @return Il map dei tipi di showlet
	 */
	public Map<String, WidgetType> loadWidgetTypes();
	
	public void addShowletType(WidgetType showletType);
	
	/**
	 * @deprecated Use {@link #deleteWidgetType(String)} instead
	 */
	public void deleteShowletType(String showletTypeCode);

	public void deleteWidgetType(String showletTypeCode);
	
	@Deprecated
	public void updateShowletTypeTitles(String showletTypeCode, ApsProperties titles);
	
	@Deprecated
	public void updateShowletType(String showletTypeCode, ApsProperties titles, ApsProperties defaultConfig);
	
	/**
	 * @deprecated Use {@link #updateWidgetType(String,ApsProperties,ApsProperties,String)} instead
	 */
	public void updateShowletType(String showletTypeCode, ApsProperties titles, ApsProperties defaultConfig, String mainGroup);

	public void updateWidgetType(String showletTypeCode, ApsProperties titles, ApsProperties defaultConfig, String mainGroup);
	
}