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
package org.entando.entando.aps.system.services.api;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.springframework.core.io.Resource;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.aps.util.FileTextReader;

/**
 * Shortcut Loader Class.
 * @author E.Santoboni
 */
public class ApiMethodLoader {
    
    protected ApiMethodLoader(String locationPatterns, ServletContext servletContext) throws ApsSystemException {
        try {
            StringTokenizer tokenizer = new StringTokenizer(locationPatterns, ",");
            while (tokenizer.hasMoreTokens()) {
                String locationPattern = tokenizer.nextToken().trim();
                this.loadApiMethodsObjects(locationPattern, servletContext);
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "ApiMethodLoader", "Error loading Api Method definitions");
            throw new ApsSystemException("Error loading Api Method definitions", t);
        }
    }
    
    private void loadApiMethodsObjects(String locationPattern, ServletContext servletContext) throws Exception {
        Logger log = ApsSystemUtils.getLogger();
        Resource[] resources = ApsWebApplicationUtils.getResources(locationPattern, servletContext);
        ApiMethodsDefDOM dom = null;
        for (int i = 0; i < resources.length; i++) {
            Resource resource = resources[i];
            InputStream is = null;
            try {
                String path = resource.getFilename();
                is = resource.getInputStream();
                String xml = FileTextReader.getText(is);
                dom = new ApiMethodsDefDOM(xml, path);
                Map<ApiMethod.HttpMethod, List<ApiMethod>> extractedMethods = dom.getRestFulMethods();
                if (null != extractedMethods) {
                    Iterator<ApiMethod.HttpMethod> httpMethodIter = extractedMethods.keySet().iterator();
                    while (httpMethodIter.hasNext()) {
                        ApiMethod.HttpMethod httpMethod = httpMethodIter.next();
                        List<ApiMethod> extractedHttpMethods = extractedMethods.get(httpMethod);
                        List<ApiMethod> httpMethods = this.getMethods().get(httpMethod);
                        if (null != httpMethods) {
                            for (int j = 0; j < extractedHttpMethods.size(); j++) {
                                ApiMethod extractedHttpMethod = extractedHttpMethods.get(j);
                                if (this.hasConflict(extractedHttpMethod, httpMethods)) {
                                    String alertMessage = "ALERT: Into definition file '" + path + "' "
                                            + "there is an API method '" + extractedHttpMethod.getHttpMethod() 
                                            + "' resource '" + extractedHttpMethod.getResourceName() + "' and there is just one already present - "
                                            + "This definition will be ignored!!!";
                                    ApsSystemUtils.getLogger().severe(alertMessage);
                                } else {
                                    httpMethods.add(extractedHttpMethod);
                                }
                            }
                        } else {
                            this.getMethods().put(httpMethod, extractedHttpMethods);
                        }
                    }
                }
                log.info("Loaded Shortcut definition by file " + path);
            } catch (Throwable t) {
                ApsSystemUtils.logThrowable(t, this, "loadShortcuts", "Error loading Shortcut definition by location Pattern '" + locationPattern + "'");
            } finally {
                if (null != is) {
                    is.close();
                }
            }
        }
    }
    
    private boolean hasConflict(ApiMethod newMethod, List<ApiMethod> extractedMethods) {
        for (int i = 0; i < extractedMethods.size(); i++) {
            ApiMethod apiMethod = extractedMethods.get(i);
            if (newMethod.getResourceName().equals(apiMethod.getResourceName())) return true;
        }
        return false;
    }
    
    public Map<ApiMethod.HttpMethod, List<ApiMethod>> getMethods() {
        return _apiMethods;
    }
    
    private Map<ApiMethod.HttpMethod, List<ApiMethod>> _apiMethods = new HashMap<ApiMethod.HttpMethod, List<ApiMethod>>();
    
}