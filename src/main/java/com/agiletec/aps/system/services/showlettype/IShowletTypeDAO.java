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

import java.util.Map;

import com.agiletec.aps.util.ApsProperties;

/**
 * Interfaccia base per Data Access Object dei tipi di showlet (ShowletType).
 * @author E.Santoboni
 */
public interface IShowletTypeDAO {
	
	/**
	 * Carica e restituisce il Map dei tipi di showlet.
	 * @return Il map dei tipi di showlet
	 */
	public Map<String, ShowletType> loadShowletTypes();
	
	public void addShowletType(ShowletType showletType);
	
	public void deleteShowletType(String showletTypeCode);
	
	@Deprecated
	public void updateShowletTypeTitles(String showletTypeCode, ApsProperties titles);
	
	@Deprecated
	public void updateShowletType(String showletTypeCode, ApsProperties titles, ApsProperties defaultConfig);
	
	public void updateShowletType(String showletTypeCode, ApsProperties titles, ApsProperties defaultConfig, String mainGroup);
	
}