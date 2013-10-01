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
package com.agiletec.apsadmin.system.dispatcher;

import com.opensymphony.xwork2.inject.Container;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

/**
 * @author E.Santoboni
 */
public class PrepareOperations  extends org.apache.struts2.dispatcher.ng.PrepareOperations {
	
	public PrepareOperations(ServletContext servletContext, Dispatcher dispatcher) {
        super(servletContext, dispatcher);
		this.dispatcher = dispatcher;
		this.servletContext = servletContext;
    }
	
	@Override
	public ActionMapping findActionMapping(HttpServletRequest request, HttpServletResponse response, boolean forceLookup) {
        ActionMapping mapping = (ActionMapping) request.getAttribute(STRUTS_ACTION_MAPPING_KEY);
        if (mapping == null || forceLookup) {
            try {
				Container container = dispatcher.getContainer();
				
				ActionMapper mapper = container.getInstance(ActionMapper.class);
				/*
				String entandoActionName = null;
				Enumeration enumeration = request.getParameterNames();
				if (null != enumeration) {
					while (enumeration.hasMoreElements()) {
						String paramName = enumeration.nextElement().toString();
						//System.out.println("attribute names - " + paramName);
						if (paramName.startsWith("entandoaction:")) {
							entandoActionName = paramName.substring("entandoaction:".length());
							// Strip off the image button location info, if found
							System.out.println("entandoAction names - " + entandoActionName);
							if (entandoActionName.endsWith(".x") || entandoActionName.endsWith(".y")) {
								entandoActionName = entandoActionName.substring(0, entandoActionName.length() - 2);
							}
							break;
						}
					}
				}
				*/
				String entandoActionName = EntandoActionUtils.extractEntandoActionName(request);
				mapping = mapper.getMapping(request, dispatcher.getConfigurationManager());
				if (null != entandoActionName) {
					//String actionName = mapping.getNamespace() + "/" + entandoActionName;
					//mapping = mapper.getMappingFromActionName(entandoActionName);
				//} else {
					mapping.setName(entandoActionName);
				}
                
				
				//System.out.println("------------------------------------------------");
				
				//System.out.println("name - " + mapping.getName());
				//System.out.println("method - " + mapping.getMethod());
				
				//System.out.println("namespace - " + mapping.getNamespace());
				//System.out.println("name - " + mapping.getName());
				//System.out.println("method - " + mapping.getMethod());
				//System.out.println("------------------------------------------------");
			
                if (mapping != null) {
                    request.setAttribute(STRUTS_ACTION_MAPPING_KEY, mapping);
                }
            } catch (Exception ex) {
                dispatcher.sendError(request, response, servletContext, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex);
            }
        }

        return mapping;
    }
	
	private Dispatcher dispatcher;
	private ServletContext servletContext;
	
	private static final String STRUTS_ACTION_MAPPING_KEY = "struts.actionMapping";
	
}
