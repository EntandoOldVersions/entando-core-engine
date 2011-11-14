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
package com.agiletec.apsadmin.admin.localestring;

/**
 * This base interface declares the default operations to handle the localization strings 
 * @author E.Santoboni
 */
public interface ILocaleStringAction {
	
	public String newLabel();
	
	public String edit();
	
	public String delete();
	
	public String save();
	
}