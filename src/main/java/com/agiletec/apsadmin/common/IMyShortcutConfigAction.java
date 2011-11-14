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
package com.agiletec.apsadmin.common;

/**
 * Interface of the action that manage the shortcut configuration of the current user.
 * @author E.Santoboni
 */
public interface IMyShortcutConfigAction {
	
	/**
	 * Join a shortcut in the user configuration.
	 * @return The result code.
	 */
	public String joinMyShortcut();

	/**
	 * Remove a shortcut from the user configuration.
	 * @return The result code.
	 */
	public String removeMyShortcut();
	
	/**
	 * Swap a shortcut whith other one in the user configuration.
	 * @return The result code.
	 */
	public String swapMyShortcut();
	
	public static final String SESSION_PARAM_MY_SHORTCUTS = "myShortcuts_sessionParam";
	
}