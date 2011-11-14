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
package com.agiletec.apsadmin.portal.specialshowlet;

import com.agiletec.aps.system.services.page.Showlet;

/**
 * Basic interface for the action classes which configure the showlets with parameters. 
 * @author E.Santoboni
 */
public interface ISimpleShowletConfigAction {
	
	/**
	 * Initialize the interface used for configuration management.
	 * @return The code resulting from the operation.
	 */
	public String init();
	
	/**
	 * Save the configuration of the current showlet.
	 * @return The result code.
	 */
	public String save();
	
	/**
	 * Return the configuration of the showlet currently on edit.   
	 * @return The showlet currently on edit.
	 */
	public Showlet getShowlet();
	
}
