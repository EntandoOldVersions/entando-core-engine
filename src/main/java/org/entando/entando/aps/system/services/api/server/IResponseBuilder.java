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
package org.entando.entando.aps.system.services.api.server;

import java.util.Properties;

import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.api.model.ApiMethod;

import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * @author E.Santoboni
 */
public interface IResponseBuilder {
    
    public ApiMethod extractApiMethod(ApiMethod.HttpMethod httpMethod, String namespace, String resourceName) throws ApiException;
    
    @Deprecated
    public Object invoke(String resourceName, Properties parameters) throws ApiException, ApsSystemException;
    
    @Deprecated
    public Object createResponse(String resourceName, Properties parameters) throws ApsSystemException;
    
    public Object createResponse(ApiMethod method, Properties parameters) throws ApsSystemException;
    
    public Object createResponse(ApiMethod method, Object bodyObject, Properties parameters) throws ApsSystemException;
    
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";
    
}