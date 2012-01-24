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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.ApiMethodRelatedShowlet;
import org.entando.entando.aps.system.services.api.model.ApiService;
import org.springframework.web.context.ServletContextAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * @author E.Santoboni
 */
public class ApiCatalogManager extends AbstractService implements IApiCatalogManager, ServletContextAware {
    
    public void init() throws Exception {
        ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initialized ");
    }
    
    protected void release() {
        super.release();
        this.setMasterRestFulMethods(null);
        this.setMasterServices(null);
    }
    
    protected void loadMethods() throws ApsSystemException {
        try {
            ApiMethodLoader loader = new ApiMethodLoader(this.getLocationPatterns(), this.getServletContext());
            Map<ApiMethod.HttpMethod, List<ApiMethod>> apiMethods = loader.getMethods();
            List<ApiMethod> apiGETMethods = apiMethods.get(ApiMethod.HttpMethod.GET);
            this.setMasterRestFulMethods(apiMethods);
            ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initialized Api Methods");
            this.getApiCatalogDAO().loadApiStatus(apiGETMethods);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "loadApiMethods", "Error loading Api Methods definitions");
            throw new ApsSystemException("Error loading Api Methods definitions", t);
        }
    }
    
    protected void loadServices() throws ApsSystemException {
        try {
            if (null == this.getMasterRestFulMethods()) {
                this.loadMethods();
            }
            List<ApiMethod> masterMethods = this.getMasterRestFulMethods().get(ApiMethod.HttpMethod.GET);
            this.setMasterServices(this.getApiCatalogDAO().loadServices(masterMethods));
        } catch (Throwable t) {
            this.setMasterServices(new HashMap<String, ApiService>());
            ApsSystemUtils.logThrowable(t, this, "loadServices", "Error loading Services definitions");
            throw new ApsSystemException("Error loading Services definitions", t);
        }
    }
    
    public ApiMethod getRelatedMethod(String showletCode) throws ApsSystemException {
        List<ApiMethod> masterMethods = this.getMasterRestFulMethods().get(ApiMethod.HttpMethod.GET);
        for (int i = 0; i < masterMethods.size(); i++) {
            ApiMethod apiMethod = masterMethods.get(i);
            ApiMethodRelatedShowlet relatedShowlet = apiMethod.getRelatedShowlet();
            if (null != relatedShowlet && relatedShowlet.getShowletCode().equals(showletCode)) {
                return apiMethod.clone();
            }
        }
        return null;
    }
    
    public Map<String, ApiMethod> getRelatedShowletMethods() throws ApsSystemException {
        Map<String, ApiMethod> mapping = new HashMap<String, ApiMethod>();
        try {
            if (null == this.getMasterRestFulMethods()) {
                this.loadMethods();
            }
            List<ApiMethod> masterMethods = this.getMasterRestFulMethods().get(ApiMethod.HttpMethod.GET);
            for (int i = 0; i < masterMethods.size(); i++) {
                ApiMethod apiMethod = masterMethods.get(i);
                ApiMethodRelatedShowlet relatedShowlet = apiMethod.getRelatedShowlet();
                if (null != relatedShowlet) {
                    String showletCode = relatedShowlet.getShowletCode();
                    if (mapping.containsKey(showletCode)) {
                        ApiMethod alreadyMapped = mapping.get(showletCode);
                        String alertMessage = "There is more than one method related whith showlet '" + showletCode + "' - "
                                + "Actual mapped '" + alreadyMapped.getResourceName() + "'; other method '" + apiMethod.getResourceName() + "'";
                        ApsSystemUtils.getLogger().severe(alertMessage);
                    } else {
                        mapping.put(showletCode, apiMethod.clone());
                    }
                }
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getRelatedShowletMethods", "Error loading related showlet methods");
            throw new ApsSystemException("Error loading related showlet methods", t);
        }
        return mapping;
    }
    
    public void updateApiStatus(ApiMethod apiMethod) throws ApsSystemException {
        try {
            if (null == apiMethod) {
                throw new ApsSystemException("Null api method");
            }
            ApiMethod masterMethod = this.getMasterMethod(ApiMethod.HttpMethod.GET, apiMethod.getResourceName());
            if (null == masterMethod) {
                throw new ApsSystemException("Api '" + apiMethod.getResourceName() + "' does not exist");
            }
            this.getApiCatalogDAO().saveApiStatus(apiMethod);
            masterMethod.setActive(apiMethod.isActive());
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "updateApiStatus", "Error error updating api status : resource '" + apiMethod.getResourceName() + "'");
            throw new ApsSystemException("Error updating api status", t);
        }
    }
    
    @Deprecated
    public ApiMethod getMethod(String resourceName) throws ApsSystemException {
        return this.getMethod(ApiMethod.HttpMethod.GET, resourceName);
    }
    
    public ApiMethod getMethod(ApiMethod.HttpMethod httpMethod, String resourceName) throws ApsSystemException {
        ApiMethod masterMethod = this.getMasterMethod(httpMethod, resourceName);
        if (null != masterMethod) {
            return masterMethod.clone();
        }
        return null;
    }
    
    protected ApiMethod getMasterMethod(ApiMethod.HttpMethod httpMethod, String resourceName) throws ApsSystemException {
        try {
            if (null == this.getMasterRestFulMethods()) {
                this.loadMethods();
            }
            List<ApiMethod> masterMethods = this.getMasterRestFulMethods().get(httpMethod);
            for (int i = 0; i < masterMethods.size(); i++) {
                ApiMethod extracted = masterMethods.get(i);
                if (resourceName.equals(extracted.getResourceName())) return extracted;
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getMasterMethod", "Error extracting methods");
            throw new ApsSystemException("Error extracting methods", t);
        }
        return null;
    }
    
    @Deprecated
    public Map<String, ApiMethod> getMethods() throws ApsSystemException {
        Map<String, ApiMethod> map = new HashMap<String, ApiMethod>();
        List<ApiMethod> list = this.getMethods(ApiMethod.HttpMethod.GET);
        for (int i = 0; i < list.size(); i++) {
            ApiMethod apiMethod = list.get(i);
            map.put(apiMethod.getResourceName(), apiMethod);
        }
        return map;
    }
    
    public List<ApiMethod> getMethods(ApiMethod.HttpMethod httpMethod) throws ApsSystemException {
        List<ApiMethod> clonedMethods = new ArrayList<ApiMethod>();
        try {
            if (null == this.getMasterRestFulMethods()) {
                this.loadMethods();
            }
            List<ApiMethod> masterMethods = this.getMasterRestFulMethods().get(httpMethod);
            for (int i = 0; i < masterMethods.size(); i++) {
                ApiMethod apiMethod = masterMethods.get(i);
                clonedMethods.add(apiMethod.clone());
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getMethods", "Error extracting methods");
            throw new ApsSystemException("Error extracting methods", t);
        }
        return clonedMethods;
    }
    
    public Map<ApiMethod.HttpMethod, List<ApiMethod>> getApiRestFulMethods() throws ApsSystemException {
        Map<ApiMethod.HttpMethod, List<ApiMethod>> clonedRestfulMethods = new HashMap<ApiMethod.HttpMethod, List<ApiMethod>>();
        try {
            if (null == this.getMasterRestFulMethods()) {
                this.loadMethods();
            }
            Iterator<ApiMethod.HttpMethod> iterator = this.getMasterRestFulMethods().keySet().iterator();
            while (iterator.hasNext()) {
                ApiMethod.HttpMethod httpMethod = iterator.next();
                List<ApiMethod> masterMethods = this.getMasterRestFulMethods().get(httpMethod);
                List<ApiMethod> clonedMethods = new ArrayList<ApiMethod>();
                for (int i = 0; i < masterMethods.size(); i++) {
                    ApiMethod apiMethod = masterMethods.get(i);
                    clonedMethods.add(apiMethod.clone());
                }
                clonedRestfulMethods.put(httpMethod, clonedMethods);
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getApiRestFulMethods", "Error extracting methods");
            throw new ApsSystemException("Error extracting methods", t);
        }
        return clonedRestfulMethods;
    }
    
    public ApiService getApiService(String key) throws ApsSystemException {
        try {
            if (null == this.getMasterServices()) {
                this.loadServices();
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getApiService", "Error extracting services");
            throw new ApsSystemException("Error extracting services", t);
        }
        ApiService service = this.getMasterServices().get(key);
        if (null == service) {
            return null;
        }
        return service.clone();
    }

    public Map<String, ApiService> getApiServices() throws ApsSystemException {
        Map<String, ApiService> clonedServices = new HashMap<String, ApiService>();
        try {
            if (null == this.getMasterRestFulMethods()) {
                this.loadMethods();
            }
            if (null == this.getMasterServices()) {
                this.loadServices();
            }
            if (null != this.getMasterServices()) {
                Iterator<String> servicesIter = this.getMasterServices().keySet().iterator();
                while (servicesIter.hasNext()) {
                    String serviceKey = servicesIter.next();
                    clonedServices.put(serviceKey, this.getMasterServices().get(serviceKey).clone());
                }
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getServices", "Error extracting services");
            throw new ApsSystemException("Error extracting services", t);
        }
        return clonedServices;
    }

    public Map<String, ApiService> getApiServices(String tag, Boolean myentando) throws ApsSystemException {
        Map<String, ApiService> services = this.getApiServices();
        if ((null == tag || tag.trim().length() == 0) && null == myentando) {
            return services;
        }
        Map<String, ApiService> servicesToReturn = new HashMap<String, ApiService>();
        try {
            Iterator<ApiService> iter = services.values().iterator();
            while (iter.hasNext()) {
                ApiService apiService = iter.next();
                String serviceTag = apiService.getTag();
                boolean tagCheck = (null == tag || (null != serviceTag && serviceTag.toLowerCase().indexOf(tag.trim().toLowerCase()) > -1));
                boolean myentandoCheck = (null == myentando || (myentando.booleanValue() == apiService.isMyEntando()));
                if (tagCheck && myentandoCheck) {
                    servicesToReturn.put(apiService.getKey(), apiService);
                }
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getServices", "Error extracting services");
            throw new ApsSystemException("Error extracting services", t);
        }
        return servicesToReturn;
    }

    public void saveService(ApiService service) throws ApsSystemException {
        try {
            if (null == service) {
                throw new ApsSystemException("Null api service to save");
            }
            ApiMethod master = service.getMaster();
            if (null == master || null == this.getMethod(master.getHttpMethod(), master.getResourceName())) {
                throw new ApsSystemException("null or invalid master method of service to save");
            }
            if (null != this.getMasterServices().get(service.getKey())) {
                this.getApiCatalogDAO().updateService(service);
            } else {
                this.getApiCatalogDAO().addService(service);
            }
            this.getMasterServices().put(service.getKey(), service);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "saveService", "Error saving service");
            throw new ApsSystemException("Error saving service", t);
        }
    }

    public void deleteService(String key) throws ApsSystemException {
        try {
            this.getApiCatalogDAO().deleteService(key);
            this.getMasterServices().remove(key);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "deleteService", "Error deleting api service '" + key + "'");
            throw new ApsSystemException("Error deleting service '" + key + "'", t);
        }
    }
    
    public void updateApiServiceStatus(ApiService service) throws ApsSystemException {
        try {
            if (null == service) {
                throw new ApsSystemException("Null api service to update");
            }
            ApiService masterService = this.getMasterServices().get(service.getKey());
            if (null == masterService) {
                throw new ApsSystemException("Api service '" + service.getKey() + "' does not exist");
            }
            masterService.setActive(service.isActive());
            masterService.setPublicService(service.isPublicService());
            this.getApiCatalogDAO().updateService(masterService);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "updateApiServiceStatus", "Error updating api service '" + service.getKey() + "'");
            throw new ApsSystemException("Error updating service '" + service.getKey() + "'", t);
        }
    }
    
    protected Map<ApiMethod.HttpMethod, List<ApiMethod>> getMasterRestFulMethods() {
        return _masterRestFulMethods;
    }
    protected void setMasterRestFulMethods(Map<ApiMethod.HttpMethod, List<ApiMethod>> masterRestFulMethods) {
        this._masterRestFulMethods = masterRestFulMethods;
    }
    
    protected Map<String, ApiService> getMasterServices() {
        return _masterServices;
    }
    protected void setMasterServices(Map<String, ApiService> masterServices) {
        this._masterServices = masterServices;
    }
    
    protected ServletContext getServletContext() {
        return this._servletContext;
    }
    
    public void setServletContext(ServletContext servletContext) {
        this._servletContext = servletContext;
    }

    protected String getLocationPatterns() {
        if (null == this._locationPatterns) {
            return DEFAULT_LOCATION_PATTERN;
        }
        return _locationPatterns;
    }

    public void setLocationPatterns(String locationPatterns) {
        this._locationPatterns = locationPatterns;
    }

    protected IApiCatalogDAO getApiCatalogDAO() {
        return _apiCatalogDAO;
    }
    public void setApiCatalogDAO(IApiCatalogDAO apiCatalogDAO) {
        this._apiCatalogDAO = apiCatalogDAO;
    }
    
    private Map<ApiMethod.HttpMethod, List<ApiMethod>> _masterRestFulMethods;
    
    private Map<String, ApiService> _masterServices;
    private ServletContext _servletContext;
    private String _locationPatterns;
    private IApiCatalogDAO _apiCatalogDAO;
    public static final String DEFAULT_LOCATION_PATTERN = "classpath*:/api/**/aps/apiMethods.xml";
    
}