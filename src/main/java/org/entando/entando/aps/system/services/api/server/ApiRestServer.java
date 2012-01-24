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

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.ApiError;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.BaseApiResponse;
import org.entando.entando.aps.system.services.api.provider.json.JSONProvider;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.user.IAuthenticationProviderManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * @author E.Santoboni
 */
public class ApiRestServer {
    
    @GET
    @Produces({"application/xml", "text/plain", "application/json"})
    @Path("/{langCode}/{resourceName}")
    public Object doGet(@PathParam("langCode") String langCode, @PathParam("resourceName") String resourceName, 
            @Context HttpServletRequest request, @Context HttpServletResponse response, @Context UriInfo ui) {
        return this.buildGetDeleteResponse(langCode, ApiMethod.HttpMethod.GET, resourceName, request, response, ui);
    }
    
    @POST
    @Consumes({"application/xml"})
    @Produces({"application/json", "application/xml"})
    @Path("/{langCode}/{resourceName}")
    public Object doPostFromXmlBody(@PathParam("langCode") String langCode, @PathParam("resourceName") String resourceName, 
            @Context HttpServletRequest request, @Context HttpServletResponse response, @Context UriInfo ui) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.POST, resourceName, request, response, ui, MediaType.APPLICATION_XML_TYPE);
    }
    
    @POST
    @Consumes({"application/json"})
    @Produces({"application/json", "application/xml"})
    @Path("/{langCode}/{resourceName}")
    public Object doPostFromJsonBody(@PathParam("langCode") String langCode, @PathParam("resourceName") String resourceName, 
            @Context HttpServletRequest request, @Context HttpServletResponse response, @Context UriInfo ui) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.POST, resourceName, request, response, ui, MediaType.APPLICATION_JSON_TYPE);
    }
    
    @PUT
    @Consumes({"application/xml"})
    @Produces({"application/json", "application/xml"})
    @Path("/{langCode}/{resourceName}")
    public Object doPutFromXmlBody(@PathParam("langCode") String langCode, @PathParam("resourceName") String resourceName, 
            @Context HttpServletRequest request, @Context HttpServletResponse response, @Context UriInfo ui) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.PUT, resourceName, request, response, ui, MediaType.APPLICATION_XML_TYPE);
    }
    
    @PUT
    @Consumes({"application/json"})
    @Produces({"application/json", "application/xml"})
    @Path("/{langCode}/{resourceName}")
    public Object doPutFromJsonBody(@PathParam("langCode") String langCode, @PathParam("resourceName") String resourceName, 
            @Context HttpServletRequest request, @Context HttpServletResponse response, @Context UriInfo ui) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.PUT, resourceName, request, response, ui, MediaType.APPLICATION_JSON_TYPE);
    }
    
    @DELETE
    @Produces({"application/json", "application/xml"})
    @Path("/{langCode}/{resourceName}")
    public Object doDelete(@PathParam("langCode") String langCode, @PathParam("resourceName") String resourceName, 
            @Context HttpServletRequest request, @Context HttpServletResponse response, @Context UriInfo ui) {
        return this.buildGetDeleteResponse(langCode, ApiMethod.HttpMethod.DELETE, resourceName, request, response, ui);
    }
    
    protected Object buildGetDeleteResponse(String langCode, ApiMethod.HttpMethod httpMethod, 
            String resourceName, HttpServletRequest request, HttpServletResponse response, UriInfo ui) {
        Object responseObject = null;
        try {
            IResponseBuilder responseBuilder = (IResponseBuilder) ApsWebApplicationUtils.getBean(SystemConstants.API_RESPONSE_BUILDER, request);
            Properties properties = this.extractRequestParameters(ui);
            properties.put(SystemConstants.API_LANG_CODE_PARAMETER, langCode);
            ApiMethod apiMethod = responseBuilder.extractApiMethod(httpMethod, resourceName);
            UserDetails user = this.extractUser(apiMethod, request, response);
            if (null != user) {
                properties.put(SystemConstants.API_USER_PARAMETER, user);
            }
            responseObject = responseBuilder.createResponse(apiMethod, properties);
        } catch (ApiException ae) {
            return this.buildErrorResponse(httpMethod, resourceName, ae);
        } catch (Throwable t) {
            return this.buildErrorResponse(httpMethod, resourceName, t);
        }
        return responseObject;
    }
    
    protected Object buildPostPutResponse(String langCode, ApiMethod.HttpMethod httpMethod, 
            String resourceName, HttpServletRequest request, HttpServletResponse response, UriInfo ui, MediaType mediaType) {
        Object responseObject = null;
        try {
            IResponseBuilder responseBuilder = (IResponseBuilder) ApsWebApplicationUtils.getBean(SystemConstants.API_RESPONSE_BUILDER, request);
            Properties properties = this.extractRequestParameters(ui);
            properties.put(SystemConstants.API_LANG_CODE_PARAMETER, langCode);
            ApiMethod apiMethod = responseBuilder.extractApiMethod(httpMethod, resourceName);
            UserDetails user = this.extractUser(apiMethod, request, response);
            if (null != user) {
                properties.put(SystemConstants.API_USER_PARAMETER, user);
            }
            Class expectedType = apiMethod.getExpectedType();
            Object bodyObject = null;
            if (MediaType.APPLICATION_JSON_TYPE.equals(mediaType)) {
                JSONProvider jsonProvider = new JSONProvider();
                bodyObject = jsonProvider.readFrom(expectedType, expectedType.getGenericSuperclass(), 
                        expectedType.getAnnotations(), mediaType, null, request.getInputStream());
            } else {
                JAXBContext context = JAXBContext.newInstance(expectedType);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                bodyObject = (Object) unmarshaller.unmarshal(request.getInputStream());
            }
            responseObject = responseBuilder.createResponse(apiMethod, bodyObject, properties);
        } catch (ApiException ae) {
            return this.buildErrorResponse(httpMethod, resourceName, ae);
        } catch (Throwable t) {
            return this.buildErrorResponse(httpMethod, resourceName, t);
        }
        return responseObject;
    }

    protected Properties extractRequestParameters(UriInfo ui) {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
        Properties properties = new Properties();
        if (null != queryParams) {
            List<String> reservedParameters = Arrays.asList(SystemConstants.API_RESERVED_PARAMETERS);
            Set<Entry<String, List<String>>> entries = queryParams.entrySet();
            Iterator<Entry<String, List<String>>> iter = entries.iterator();
            while (iter.hasNext()) {
                Map.Entry<String, List<String>> entry = (Entry<String, List<String>>) iter.next();
                String key = entry.getKey();
                if (!reservedParameters.contains(key)) {
                    //extract only the first value
                    properties.put(key, entry.getValue().get(0));
                }
            }
        }
        return properties;
    }
    
    private Object buildErrorResponse(ApiMethod.HttpMethod httpMethod, String resourceName, Throwable t) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Method '").append(httpMethod).append("' Resource '").append(resourceName).append("'");
        ApsSystemUtils.logThrowable(t, this, "buildErrorResponse", "Error building api response  - " + buffer.toString());
        BaseApiResponse response = new BaseApiResponse();
        if (t instanceof ApiException) {
            response.addErrors(((ApiException) t).getErrors());
        } else {
            ApiError error = new ApiError(IApiErrorCodes.SERVER_ERROR, "Error building response - " + buffer.toString());
            response.addError(error);
        }
        response.setResult(IResponseBuilder.FAILURE, null);
        return response;
    }
    
    protected UserDetails extractUser(ApiMethod apiMethod, 
            HttpServletRequest request, HttpServletResponse response) throws ApiException, IOException, ServletException {
        UserDetails user = null;
        //extract user
        return user;
    }
    
}