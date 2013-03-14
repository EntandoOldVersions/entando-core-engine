/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando Enterprise Edition software.
* You can redistribute it and/or modify it
* under the terms of the Entando's EULA
*
* See the file License for the specific language governing permissions
* and limitations under the License
*
*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.aps.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * Init the system when the web application is started
 * @version 1.0
 * @author
 */
public class StartupListener extends org.springframework.web.context.ContextLoaderListener {

	/**
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {
		ServletContext svCtx = event.getServletContext();
		String msg = this.getClass().getName()+ ": INIT " + svCtx.getServletContextName();
		System.out.println(msg);
		super.contextInitialized(event);
		msg = this.getClass().getName() + ": INIT DONE "+ svCtx.getServletContextName();
		System.out.println(msg);
	}

}
