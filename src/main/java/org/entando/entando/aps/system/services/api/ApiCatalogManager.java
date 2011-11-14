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

import java.util.HashMap;
import java.util.Iterator;
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
	
	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initialized ");
	}
	
	@Override
	protected void release() {
		super.release();
		this.setMasterMethods(null);
		this.setMasterServices(null);
	}
	
	protected void loadMethods() throws ApsSystemException {
		try {
			ApiMethodLoader loader = new ApiMethodLoader(this.getLocationPatterns(), this.getServletContext());
			this.setMasterMethods(loader.getMethods());
			ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initialized - Api Methods " + this.getMasterMethods().size());
			this.getApiCatalogDAO().loadApiStatus(this.getMasterMethods());
		} catch (Throwable t) {
			this.setMasterMethods(new HashMap<String, ApiMethod>());
			ApsSystemUtils.logThrowable(t, this, "loadApiMethods", "Error loading Api Methods definitions");
			throw new ApsSystemException("Error loading Api Methods definitions", t);
		}
	}
	
	protected void loadServices() throws ApsSystemException {
		try {
			if (null == this.getMasterMethods()) {
				this.loadMethods();
			}
			this.setMasterServices(this.getApiCatalogDAO().loadServices(this.getMasterMethods()));
		} catch (Throwable t) {
			this.setMasterServices(new HashMap<String, ApiService>());
			ApsSystemUtils.logThrowable(t, this, "loadServices", "Error loading Services definitions");
			throw new ApsSystemException("Error loading Services definitions", t);
		}
	}
	
	@Override
	public ApiMethod getRelatedMethod(String showletCode) throws ApsSystemException {
		Iterator<ApiMethod> methodIter = this.getMasterMethods().values().iterator();
		while (methodIter.hasNext()) {
			ApiMethod apiMethod = methodIter.next();
			ApiMethodRelatedShowlet relatedShowlet = apiMethod.getRelatedShowlet();
			if (null != relatedShowlet && relatedShowlet.getShowletCode().equals(showletCode)) {
				return this.getMethod(apiMethod.getMethodName());
			}
		}
		return null;
	}
	
	@Override
	public Map<String, ApiMethod> getRelatedShowletMethods() throws ApsSystemException {
		Map<String, ApiMethod> mapping = new HashMap<String, ApiMethod>();
		try {
			if (null == this.getMasterMethods()) {
				this.loadMethods();
			}
			Iterator<ApiMethod> methodIter = this.getMasterMethods().values().iterator();
			while (methodIter.hasNext()) {
				ApiMethod apiMethod = methodIter.next();
				ApiMethodRelatedShowlet relatedShowlet = apiMethod.getRelatedShowlet();
				if (null != relatedShowlet) {
					String showletCode = relatedShowlet.getShowletCode();
					if (mapping.containsKey(showletCode)) {						
						ApiMethod alreadyMapped = mapping.get(showletCode);
						String alertMessage = "There is more than one method related whith showlet '" + showletCode + "' - " +
								"Actual mapped '" + alreadyMapped.getMethodName() + "'; other method '" + apiMethod.getMethodName() + "'";
						ApsSystemUtils.getLogger().severe(alertMessage);
					} else {
						mapping.put(showletCode, this.getMethod(apiMethod.getMethodName()));
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
	public void updateApiStatus(ApiMethod apiMethod) throws ApsSystemException {
		try {
			if (null == apiMethod) {
				throw new ApsSystemException("Null api method");
			}
			ApiMethod masterMethod = this.getMasterMethods().get(apiMethod.getMethodName());
			if (null == masterMethod) {
				throw new ApsSystemException("Api '" + apiMethod.getMethodName() + "' does not exist");
			}
			this.getApiCatalogDAO().saveApiStatus(apiMethod);
			masterMethod.setActive(apiMethod.isActive());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateApiStatus", "Error error updating api status : method '" + apiMethod.getMethodName() + "'");
			throw new ApsSystemException("Error loading Services definitions", t);
		}
	}
	
	@Override
	public ApiMethod getMethod(String methodName) throws ApsSystemException {
		try {
			if (null == this.getMasterMethods()) {
				this.loadMethods();
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getMethod", "Error extracting methods");
			throw new ApsSystemException("Error extracting methods", t);
		}
		ApiMethod method = this.getMasterMethods().get(methodName);
		if (null == method) return null;
		return method.clone();
	}
	
	@Override
	public Map<String, ApiMethod> getMethods() throws ApsSystemException {
		Map<String, ApiMethod> clonedMethods = new HashMap<String, ApiMethod>();
		try {
			if (null == this.getMasterMethods()) {
				this.loadMethods();
			}
			Iterator<String> methodsIter = this.getMasterMethods().keySet().iterator();
			while (methodsIter.hasNext()) {
				String method = methodsIter.next();
				clonedMethods.put(method, this.getMasterMethods().get(method).clone());
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getMethods", "Error extracting methods");
			throw new ApsSystemException("Error extracting methods", t);
		}
		return clonedMethods;
	}
	
	protected Map<String, ApiMethod> getMasterMethods() throws ApsSystemException {
		return this._masterMethods;
	}
	protected void setMasterMethods(Map<String, ApiMethod> masterMethods) {
		this._masterMethods = masterMethods;
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
		if (null == service) return null;
		return service.clone();
	}
	
	@Override
	public Map<String, ApiService> getApiServices() throws ApsSystemException {
		Map<String, ApiService> clonedServices = new HashMap<String, ApiService>();
		try {
			if (null == this.getMasterMethods()) {
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
	
	@Override
	public Map<String, ApiService> getApiServices(String tag, Boolean myentando) throws ApsSystemException {
		Map<String, ApiService> services = this.getApiServices();
		if ((null == tag || tag.trim().length() == 0) && null == myentando) return services;
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
			if (null == master || null == this.getMasterMethods().get(master.getMethodName())) {
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
	
	protected Map<String, ApiService> getMasterServices() {
		return _masterServices;
	}
	protected void setMasterServices(Map<String, ApiService> masterServices) {
		this._masterServices = masterServices;
	}
	
	protected ServletContext getServletContext() {
		return this._servletContext;
	}
	@Override
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
	
	private Map<String, ApiMethod> _masterMethods;
	private Map<String, ApiService> _masterServices;
	
	private ServletContext _servletContext;
	private String _locationPatterns;
	
	private IApiCatalogDAO _apiCatalogDAO;
	
	public static final String DEFAULT_LOCATION_PATTERN = "classpath*:/api/**/aps/apiMethods.xml";
	
}