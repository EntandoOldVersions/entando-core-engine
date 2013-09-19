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
public class PrepareOperations extends org.apache.struts2.dispatcher.ng.PrepareOperations {
	
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
				String entandoActionName = EntandoActionUtils.extractEntandoActionName(request);
				mapping = mapper.getMapping(request, dispatcher.getConfigurationManager());
				if (null != entandoActionName) {
					mapping.setName(entandoActionName);
				}
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
