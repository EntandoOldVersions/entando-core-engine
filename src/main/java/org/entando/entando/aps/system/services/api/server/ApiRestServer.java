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
package org.entando.entando.aps.system.services.api.server;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.ApiError;
import org.entando.entando.aps.system.services.api.model.BaseApiResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;

/**
 * @author E.Santoboni
 */
public class ApiRestServer {
	
	@Context 
    private ServletContext _sc;
	
	@GET
	@Produces({"application/xml", "text/plain", "application/json"})
	@Path("/{langCode}/{method}")
	public Object doGet(@PathParam("langCode") String langCode, @PathParam("method") String method, @Context UriInfo ui) {
		return this.extractResponse(langCode, method, ui);
    }
	
	protected Object extractResponse(String langCode, String method, UriInfo ui) {
		Object response = null;
		try {
			WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(_sc);
			IResponseBuilder responseBuilder = (IResponseBuilder) wac.getBean(SystemConstants.API_RESPONSE_BUILDER);
			MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
			Properties properties = new Properties();
			if (null != queryParams) {
				Set<Entry<String, List<String>>> entries = queryParams.entrySet();
				Iterator<Entry<String, List<String>>> iter = entries.iterator();
				while (iter.hasNext()) {
					Map.Entry<String, List<String>> entry = (Entry<String, List<String>>) iter.next();
					//extract only the first value
					properties.put(entry.getKey(), entry.getValue().get(0));
				}
			}
			properties.put(SystemConstants.API_LANG_CODE_PARAMETER, langCode);
			response = responseBuilder.createResponse(method, properties);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "doGet", "Error building api response");
			response = new BaseApiResponse();
			ApiError error = new ApiError(IApiErrorCodes.SERVER_ERROR, "Error building response for method '" + method + "'");
			((BaseApiResponse) response).addError(error);
		}
        return response;
    }
	
}
