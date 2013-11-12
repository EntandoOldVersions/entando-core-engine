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
package com.agiletec.apsadmin.system.tiles.factory;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.TilesException;
import org.apache.tiles.factory.TilesContainerFactory;

import com.agiletec.apsadmin.system.tiles.impl.JapsBasicTilesContainer;

/**
* @see com.agiletec.plugins.struts2.tiles.JapsStrutsTilesListener
* @version 1.0
* @author zuanni G.Cocco
*/
public class JapsTilesContainerFactory extends TilesContainerFactory {
	 
	private static final Map<String, String> DEFAULTS = new HashMap<String, String>();

	static {
	        DEFAULTS.put(CONTAINER_FACTORY_INIT_PARAM, JapsTilesContainerFactory.class.getName());
	}
	
	public static JapsTilesContainerFactory getFactory(Object context) throws TilesException {
		return getMyFactory(context, DEFAULTS);
	}

	public static JapsTilesContainerFactory getMyFactory(Object context, Map<String, String> defaults) throws TilesException {
		Map<String, String> configuration = new HashMap<String, String>(defaults);
		configuration.putAll(getInitParameterMap(context));
		JapsTilesContainerFactory factory =
			(JapsTilesContainerFactory) JapsTilesContainerFactory.createFactory(configuration,
					CONTAINER_FACTORY_INIT_PARAM);
		factory.setDefaultConfiguration(defaults);
		return factory;
	}
	
	public TilesContainer createTilesContainer(Object context) throws TilesException {
		ServletContext servletContext = (ServletContext) context;
		JapsBasicTilesContainer container = new JapsBasicTilesContainer();
		container.setServletContext(servletContext);
		this.initializeContainer(context, container);
		return container;
	}
	
}
