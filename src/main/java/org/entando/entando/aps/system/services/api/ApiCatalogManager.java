/*
*
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* Entando is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package org.entando.entando.aps.system.services.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.ApiMethodRelatedShowlet;
import org.entando.entando.aps.system.services.api.model.ApiResource;
import org.entando.entando.aps.system.services.api.model.ApiService;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * @author E.Santoboni
 */
public class ApiCatalogManager extends AbstractService implements IApiCatalogManager {
    
	@Override
    public void init() throws Exception {
        ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initialized ");
    }
    
    @Override
    protected void release() {
        super.release();
        this.setMasterResources(null);
        this.setMasterServices(null);
    }
    
    protected void loadResources() throws ApsSystemException {
        try {
            ApiResourceLoader loader = new ApiResourceLoader(this.getLocationPatterns());
            Map<String, ApiResource> resources = loader.getResources();
            this.setMasterResources(resources);
            ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initialized Api Methods");
            this.getApiCatalogDAO().loadApiStatus(resources);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "loadResources", "Error loading Api Resources definitions");
            throw new ApsSystemException("Error loading Api Resources definitions", t);
        }
    }
    
    protected void loadServices() throws ApsSystemException {
        try {
            if (null == this.getMasterResources()) {
                this.loadResources();
            }
            List<ApiMethod> apiGETMethods = new ArrayList<ApiMethod>();
            List<ApiResource> resourceList = new ArrayList<ApiResource>(this.getMasterResources().values());
            for (int i = 0; i < resourceList.size(); i++) {
                ApiResource apiResource = resourceList.get(i);
                if (null != apiResource.getGetMethod()) {
                    apiGETMethods.add(apiResource.getGetMethod());
                }
            }
            this.setMasterServices(this.getApiCatalogDAO().loadServices(apiGETMethods));
        } catch (Throwable t) {
            this.setMasterServices(new HashMap<String, ApiService>());
            ApsSystemUtils.logThrowable(t, this, "loadServices", "Error loading Services definitions");
            throw new ApsSystemException("Error loading Services definitions", t);
        }
    }
    
    @Override
    public ApiMethod getRelatedMethod(String showletCode) throws ApsSystemException {
        List<ApiMethod> masterMethods = this.getMasterMethods(ApiMethod.HttpMethod.GET);
        for (int i = 0; i < masterMethods.size(); i++) {
            ApiMethod apiMethod = masterMethods.get(i);
            ApiMethodRelatedShowlet relatedShowlet = apiMethod.getRelatedShowlet();
            if (null != relatedShowlet && relatedShowlet.getShowletCode().equals(showletCode)) {
                return apiMethod.clone();
            }
        }
        return null;
    }
    
    @Override
    public Map<String, ApiMethod> getRelatedShowletMethods() throws ApsSystemException {
        Map<String, ApiMethod> mapping = new HashMap<String, ApiMethod>();
        try {
            List<ApiMethod> masterMethods = this.getMasterMethods(ApiMethod.HttpMethod.GET);
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
    
    @Override
    public void updateMethodConfig(ApiMethod apiMethod) throws ApsSystemException {
        try {
            ApiMethod masterMethod = this.checkMethod(apiMethod);
            this.getApiCatalogDAO().saveApiStatus(apiMethod);
            masterMethod.setStatus(apiMethod.getStatus());
			masterMethod.setHidden(apiMethod.getHidden());
            masterMethod.setRequiredAuth(apiMethod.getRequiredAuth());
            String requiredPermission = apiMethod.getRequiredPermission();
            if (null != requiredPermission && requiredPermission.trim().length() > 0) {
                masterMethod.setRequiredPermission(requiredPermission);
            } else {
                masterMethod.setRequiredPermission(null);
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "updateMethodConfig", "Error error updating api status : "
                    + "resource '" + apiMethod.getResourceName() + "' method '" + apiMethod.getHttpMethod() + "' ");
            throw new ApsSystemException("Error updating api status", t);
        }
    }
    
    @Override
    public void resetMethodConfig(ApiMethod apiMethod) throws ApsSystemException {
        try {
            ApiMethod masterMethod = this.checkMethod(apiMethod);
			String resourceCode = ApiResource.getCode(masterMethod.getNamespace(), masterMethod.getResourceName());
            this.getApiCatalogDAO().resetApiStatus(resourceCode, masterMethod.getHttpMethod());
            masterMethod.resetConfiguration();
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "resetApiStatus", "Error error resetting api status : "
                    + "resource '" + apiMethod.getResourceName() + "' method '" + apiMethod.getHttpMethod() + "' ");
            throw new ApsSystemException("Error resetting api status", t);
        }
    }
    
    private ApiMethod checkMethod(ApiMethod apiMethod) throws ApsSystemException {
        if (null == apiMethod) {
            throw new ApsSystemException("Null api method");
        }
        ApiMethod masterMethod = this.getMasterMethod(apiMethod.getHttpMethod(), apiMethod.getNamespace(), apiMethod.getResourceName());
        if (null == masterMethod) {
            throw new ApsSystemException("Api namespace '" + apiMethod.getNamespace() + "' "
					+ "resource '" + apiMethod.getResourceName() + "' "
                    + "method '" + apiMethod.getHttpMethod() + "' does not exist");
        }
        return masterMethod;
    }
    
    @Deprecated
    @Override
    public ApiMethod getMethod(String resourceName) throws ApsSystemException {
        return this.getMethod(ApiMethod.HttpMethod.GET, resourceName);
    }
    
    @Override
    public ApiMethod getMethod(ApiMethod.HttpMethod httpMethod, String resourceName) throws ApsSystemException {
        return this.getMethod(ApiMethod.HttpMethod.GET, null, resourceName);
    }
    
    @Override
    public ApiMethod getMethod(ApiMethod.HttpMethod httpMethod, String namespace, String resourceName) throws ApsSystemException {
        ApiMethod masterMethod = this.getMasterMethod(httpMethod, namespace, resourceName);
        if (null != masterMethod) {
            return masterMethod.clone();
        }
        return null;
    }
    
    protected ApiMethod getMasterMethod(ApiMethod.HttpMethod httpMethod, String namespace, String resourceName) throws ApsSystemException {
        try {
            if (null == this.getMasterResources()) {
                this.loadResources();
            }
			String resourceCode = ApiResource.getCode(namespace, resourceName);
            ApiResource resource = this.getMasterResources().get(resourceCode);
            if (null != resource) {
                return resource.getMethod(httpMethod);
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getMasterMethod", "Error extracting methods");
            throw new ApsSystemException("Error extracting methods", t);
        }
        return null;
    }
    
    @Deprecated
    @Override
    public Map<String, ApiMethod> getMethods() throws ApsSystemException {
        Map<String, ApiMethod> map = new HashMap<String, ApiMethod>();
        List<ApiMethod> list = this.getMethods(ApiMethod.HttpMethod.GET);
        for (int i = 0; i < list.size(); i++) {
            ApiMethod apiMethod = list.get(i);
            map.put(apiMethod.getResourceName(), apiMethod);
        }
        return map;
    }
    
    @Override
    public List<ApiMethod> getMethods(ApiMethod.HttpMethod httpMethod) throws ApsSystemException {
        List<ApiMethod> clonedMethods = new ArrayList<ApiMethod>();
        try {
            List<ApiMethod> masterMethods = this.getMasterMethods(httpMethod);
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
    
    protected List<ApiMethod> getMasterMethods(ApiMethod.HttpMethod httpMethod) throws ApsSystemException {
        List<ApiMethod> apiMethods = new ArrayList<ApiMethod>();
        try {
            if (null == this.getMasterResources()) {
                this.loadResources();
            }
            List<ApiResource> resourceList = new ArrayList<ApiResource>(this.getMasterResources().values());
            for (int i = 0; i < resourceList.size(); i++) {
                ApiResource apiResource = resourceList.get(i);
                if (null != apiResource.getMethod(httpMethod)) {
                    apiMethods.add(apiResource.getMethod(httpMethod));
                }
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getMasterMethods", "Error loading Master Methods definitions");
            throw new ApsSystemException("Error loading Master Methods definitions", t);
        }
        return apiMethods;
    }
    
    @Override
    public Map<String, ApiResource> getResources() throws ApsSystemException {
        Map<String, ApiResource> clonedApiResources = new HashMap<String, ApiResource>();
        try {
            if (null == this.getMasterResources()) {
                this.loadResources();
            }
            Iterator<String> iterator = this.getMasterResources().keySet().iterator();
            while (iterator.hasNext()) {
                String resourceFullCode = iterator.next();
                ApiResource resource = this.getMasterResources().get(resourceFullCode);
                clonedApiResources.put(resourceFullCode, resource.clone());
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getApiResources", "Error extracting resources");
            throw new ApsSystemException("Error extracting resources", t);
        }
        return clonedApiResources;
    }
	
    @Override
    public ApiResource getResource(String namespace, String resourceName) throws ApsSystemException {
        try {
            if (null == this.getMasterResources()) {
                this.loadResources();
            }
			String resourceCode = ApiResource.getCode(namespace, resourceName);
            ApiResource apiResource = this.getMasterResources().get(resourceCode);
            if (null != apiResource) {
                return apiResource.clone();
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getApiResource", 
                    "Error extracting resource by name '" + resourceName + "'");
            throw new ApsSystemException("Error extracting resource", t);
        }
        return null;
    }
    
    @Override
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
    
    @Override
    public Map<String, ApiService> getServices() throws ApsSystemException {
        Map<String, ApiService> clonedServices = new HashMap<String, ApiService>();
        try {
            if (null == this.getMasterResources()) {
                this.loadResources();
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
	
    @Override
    public Map<String, ApiService> getServices(String tag, Boolean myentando) throws ApsSystemException {
        Map<String, ApiService> services = this.getServices();
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

    @Override
    public void saveService(ApiService service) throws ApsSystemException {
        try {
            if (null == service) {
                throw new ApsSystemException("Null api service to save");
            }
            ApiMethod master = service.getMaster();
            if (null == master || null == this.getMethod(master.getHttpMethod(), master.getNamespace(), master.getResourceName())) {
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

    @Override
    public void deleteService(String key) throws ApsSystemException {
        try {
            this.getApiCatalogDAO().deleteService(key);
            this.getMasterServices().remove(key);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "deleteService", "Error deleting api service '" + key + "'");
            throw new ApsSystemException("Error deleting service '" + key + "'", t);
        }
    }
    
    @Override
    public void updateService(ApiService service) throws ApsSystemException {
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
	
    public Map<String, ApiResource> getMasterResources() {
        return _masterResources;
    }
    public void setMasterResources(Map<String, ApiResource> masterResources) {
        this._masterResources = masterResources;
    }
    
    protected Map<String, ApiService> getMasterServices() {
        return _masterServices;
    }
    protected void setMasterServices(Map<String, ApiService> masterServices) {
        this._masterServices = masterServices;
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
    
    private Map<String, ApiResource> _masterResources;
    
    private Map<String, ApiService> _masterServices;
    private String _locationPatterns;
    private IApiCatalogDAO _apiCatalogDAO;
    public static final String DEFAULT_LOCATION_PATTERN = "classpath*:/api/**/aps/apiMethods.xml";
    
}