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
package com.agiletec.apsadmin.system.dispatcher;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.ng.ExecuteOperations;
import org.apache.struts2.dispatcher.ng.PrepareOperations;
import org.apache.struts2.dispatcher.ng.filter.FilterHostConfig;

/**
 * Extension of the Struts2 main filter.
 * Handles both the preparation and execution phases of the Struts dispatching process.
 * @author E.Santoboni
 */
public class StrutsPrepareAndExecuteFilter extends org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter {
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		InitOperations init = new InitOperations();
		try {
			FilterHostConfig config = new FilterHostConfig(filterConfig);
			init.initLogging(config);
			Dispatcher dispatcher = init.initDispatcher(config);
			init.initStaticContentLoader(config, dispatcher);
			this.prepare = new PrepareOperations(filterConfig.getServletContext(), dispatcher);
			this.execute = new ExecuteOperations(filterConfig.getServletContext(), dispatcher);
			this.excludedPatterns = init.buildExcludedPatternsList(dispatcher);
			this.postInit(dispatcher, filterConfig);
		} finally {
			init.cleanup();
		}
	}
	
}