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
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.beanutils.BeanComparator;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.api.model.ApiMethodParameter;
import org.entando.entando.aps.system.services.api.model.ApiService;
import org.entando.entando.aps.system.services.api.model.ServiceInfo;
import org.entando.entando.aps.system.services.api.model.ServiceParameterInfo;
import org.entando.entando.aps.system.services.api.server.IResponseBuilder;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.util.ApsProperties;

/**
 * @author E.Santoboni
 */
public class MasterApiMethodsInterface implements IMasterApiMethodsInterface {
	
	@Override
	public ArrayList<ServiceInfo> getServices(Properties properties) throws ApiException {
		ArrayList<ServiceInfo> services = new ArrayList<ServiceInfo>();
		try {
			String defaultLangCode = this.getLangManager().getDefaultLang().getCode();
			String langCode = properties.getProperty(SystemConstants.API_LANG_CODE_PARAMETER);
			String tagParamValue = properties.getProperty("tag");
                        String myentandoParamValue = properties.getProperty("myentando");
                        Boolean myentando = (null != myentandoParamValue && myentandoParamValue.trim().length() > 0) ? new Boolean(myentandoParamValue) : null;
			langCode = (null != langCode && null != this.getLangManager().getLang(langCode)) ? langCode : defaultLangCode;
			Iterator<ApiService> iter = this.getApiCatalogManager().getApiServices(tagParamValue, myentando).values().iterator();
			while (iter.hasNext()) {
				ApiService service = (ApiService) iter.next();
				if (service.isActive() && service.isPublicService()) {
					ServiceInfo smallService = this.createServiceInfo(service, langCode, defaultLangCode);
					services.add(smallService);
				}
			}
			BeanComparator comparator = new BeanComparator("description");
			Collections.sort(services, comparator);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getServices", "Error extracting services");
			throw new ApiException(IApiErrorCodes.SERVER_ERROR, "Internal error");
		}
		return services;
	}
        
        protected ServiceInfo createServiceInfo(ApiService service, String langCode, String defaultLangCode) {
            String description = service.getDescription().getProperty(langCode);
            if (null == description || description.trim().length() == 0) {
                    description = service.getDescription().getProperty(defaultLangCode);
            }
            ServiceInfo smallService = new ServiceInfo(service.getKey(), description, service.getTag(), service.isMyEntando());
            String[] freeParameters = service.getFreeParameters();
            if (null != freeParameters && freeParameters.length > 0) {
                    for (int i = 0; i < freeParameters.length; i++) {
                            String freeParameter = freeParameters[i];
                            ApiMethodParameter apiParameter = service.getMaster().getParameter(freeParameter);
                            if (null != apiParameter) {
                                    ServiceParameterInfo spi = new ServiceParameterInfo(apiParameter);
                                    ApsProperties serviceParameters = service.getParameters();
                                    String defaultValue = (null != serviceParameters) ? serviceParameters.getProperty(freeParameter) : null;
                                    if (null != defaultValue) {
                                            spi.setDefaultValue(defaultValue);
                                            spi.setRequired(false);
                                    }
                                    smallService.addParameter(spi);
                            }
                    }
            }
            return smallService;
        }
	
	@Override
	public Object getService(Properties properties) throws ApiException {
		Object response = null;
		String key = (String) properties.get("key");
		try {
			ApiService service = this.getApiCatalogManager().getApiService(key);
			if (null == service) {
				throw new ApiException(IApiErrorCodes.API_SERVICE_INVALID, "Service '" + key + "' does not exist");
			}
			if (!service.isActive()) {
				throw new ApiException(IApiErrorCodes.API_SERVICE_ACTIVE_FALSE, "Service '" + key + "' is not active");
			}
			Properties serviceParameters = new Properties();
			serviceParameters.putAll(service.getParameters());
			Iterator<Object> paramIter = properties.keySet().iterator();
			while (paramIter.hasNext()) {
				String paramName = (String) paramIter.next();
				if (paramName.equals(SystemConstants.API_LANG_CODE_PARAMETER) || service.isFreeParameter(paramName)) {
					serviceParameters.put(paramName, properties.getProperty(paramName));
				}
			}
			response = this.getResponseBuilder().createResponse(service.getMaster(), serviceParameters);
		} catch (ApiException e) {
			throw e;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getService", "Error invocating service - key '" + key + "'");
			throw new ApiException(IApiErrorCodes.SERVER_ERROR, "Internal error");
		}
		return response;
	}
	
	protected IApiCatalogManager getApiCatalogManager() {
		return _apiCatalogManager;
	}
	public void setApiCatalogManager(IApiCatalogManager apiCatalogManager) {
		this._apiCatalogManager = apiCatalogManager;
	}
	
	protected ILangManager getLangManager() {
		return _langManager;
	}
	public void setLangManager(ILangManager langManager) {
		this._langManager = langManager;
	}
	
	protected IResponseBuilder getResponseBuilder() {
		return _responseBuilder;
	}
	public void setResponseBuilder(IResponseBuilder responseBuilder) {
		this._responseBuilder = responseBuilder;
	}
	
	private IApiCatalogManager _apiCatalogManager;
	private ILangManager _langManager;
	private IResponseBuilder _responseBuilder;
	
}