/*
*
* Copyright 2008 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
* This file is part of Entando software.
* Entando is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2008 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
*/
package com.agiletec.apsadmin.system.tiles;

import javax.servlet.ServletContext;

import org.apache.struts2.tiles.StrutsTilesListener;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.TilesException;

import com.agiletec.apsadmin.system.tiles.factory.JapsTilesContainerFactory;

/**
 * Listener for loading during init struts2 tiles configuration files.
 * Accepting for web.xml context param org.apache.tiles.impl.BasicTilesContainer.DEFINITIONS_CONFIG
 * coma separared entries with jolly characters **.
 * 
 *  <listener>
 *	  <listener-class> com.agiletec.plugins.struts2.tiles.JapsStrutsTilesListener</listener-class>
 *  </listener>
 * 
 * 
 * @see org.apache.struts2.tiles.StrutsTilesListener
 * @version 1.0
 * @author zuanni G.Cocco
 */
public class JapsStrutsTilesListener extends StrutsTilesListener {

	@Override
	protected TilesContainer createContainer(ServletContext context) throws TilesException {
		JapsTilesContainerFactory factory = JapsTilesContainerFactory.getFactory(context);
		return factory.createContainer(context);
	}
	
}
