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
package org.entando.entando.apsadmin.api;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.entando.entando.aps.system.services.api.IApiCatalogManager;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.ApiMethodParameter;
import org.entando.entando.aps.system.services.api.model.ApiMethodRelatedShowlet;
import org.entando.entando.aps.system.services.api.model.ApiService;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.system.services.showlettype.ShowletType;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.apsadmin.system.ApsAdminSystemConstants;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * @author E.Santoboni
 */
public class ApiServiceAction extends BaseAction implements IApiServiceAction {
	
	@Override
	public void validate() {
		super.validate();
		try {
			this.checkMasterMethod(this.getApiMethodName());
			this.checkCode();
			this.checkDescriptions();
			this.checkParameters();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "validate");
			throw new RuntimeException("Error validating service", t);
		}
	}
	
	private void checkDescriptions() {
		this.setDescriptions(new ApsProperties());
		Iterator<Lang> langsIter = this.getLangManager().getLangs().iterator();
		while (langsIter.hasNext()) {
			Lang lang = (Lang) langsIter.next();
			String titleKey = "lang_" + lang.getCode();
			String title = this.getRequest().getParameter(titleKey);
			if (null == title || title.trim().length() == 0) {
				String[] args = {lang.getDescr()};
				this.addFieldError(titleKey, this.getText("error.service.new.insertDescription", args));
			} else {
				this.getDescriptions().put(lang.getCode(), title.trim());
			}
		}
	}
	
	private void checkCode() {
		String key = this.getServiceKey();
		try {
			if ((this.getStrutsAction() == ApsAdminSystemConstants.ADD || 
					this.getStrutsAction() == ApsAdminSystemConstants.PASTE) 
					&& null != key && key.trim().length() > 0) {
				if (null != this.getApiCatalogManager().getApiService(key)) {
					String[] args = {key};
					this.addFieldError("serviceKey", this.getText("error.service.new.duplicateKey", args));
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "checkCode");
			throw new RuntimeException("Error checking service key", t);
		}
	}
	
	private void checkParameters() {
		try {
			this.setApiParameterValues(new ApsProperties());
			ApiMethod masterMethod = this.getMethod(this.getApiMethodName());
			List<ApiMethodParameter> apiParameters = masterMethod.getParameters();
			this.setApiParameters(apiParameters);
			for (int i = 0; i < apiParameters.size(); i++) {
				ApiMethodParameter apiParameter = apiParameters.get(i);
				String fieldName = apiParameter.getKey() + "_apiParam";
				String value = this.getRequest().getParameter(fieldName);
				if (null != value && value.trim().length() > 0) {
					this.getApiParameterValues().put(apiParameter.getKey(), value);
				}
				boolean isFreeParameter = (null != this.getFreeParameters()) ? this.getFreeParameters().contains(apiParameter.getKey()) : false;
				if (apiParameter.isRequired() && (null == value || value.trim().length() == 0) && !isFreeParameter) {
					this.addFieldError(fieldName, this.getText("error.service.parameter.invalidSetting", new String[]{apiParameter.getKey(), apiParameter.getDescription()}));
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "checkParameters");
			throw new RuntimeException("Error checking parameters", t);
		}
	}
	
	@Override
	public String newService() {
		try {
			String check = this.checkMasterMethod(this.getApiMethodName());
			if (null != check) return check;
			ApiMethod masterMethod = this.getMethod(this.getApiMethodName());
			this.setApiParameters(masterMethod.getParameters());
			this.setStrutsAction(ApsAdminSystemConstants.ADD);
			this.setServiceKey(this.buildTempKey(masterMethod.getMethodName()));
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "newService");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public ApiMethod getMethod(String methodName) {
		try {
			return this.getApiCatalogManager().getMethod(methodName);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getMethod", "Error extracting method '" + methodName + "'");
		}
		return null;
	}
	
	public List<Lang> getSystemLangs() {
		return this.getLangManager().getLangs();
	}
	
	@Override
	public String copyFromShowlet() {
		try {
			String check = this.checkMasterMethod(this.getApiMethodName());
			if (null != check) return check;
			ApiMethod masterMethod = this.getApiCatalogManager().getMethod(this.getApiMethodName());
			IPage page = this.getPageManager().getPage(this.getPageCode());
			if (null == page) {
				this.addFieldError("pageCode", this.getText("error.service.paste.invalidPageCode", new String[]{this.getPageCode()}));
				return INPUT;
			}
			Showlet[] showlets = page.getShowlets();
			if (null == this.getFramePos() || this.getFramePos() > showlets.length || null == showlets[this.getFramePos()]) {
				String framePosString = (null != this.getFramePos()) ? this.getFramePos().toString() : "null";
				this.addFieldError("framePos", this.getText("error.service.paste.invalidFramePos", new String[]{this.getPageCode(), framePosString}));
				return INPUT;
			}
			Showlet masterShowlet = showlets[this.getFramePos()];
			ShowletType type = (masterShowlet.getType().isLogic()) ? masterShowlet.getType().getParentType() : masterShowlet.getType();
			if (null == masterMethod.getRelatedShowlet() 
					|| !masterMethod.getRelatedShowlet().getShowletCode().equals(type.getCode())) {
				this.addFieldError("framePos", this.getText("error.service.paste.invalidShowlet", 
						new String[]{masterShowlet.getType().getCode(), masterMethod.getMethodName()}));
				return INPUT;
			}
			ApsProperties parameters = this.extractParametersFromShowlet(masterMethod.getRelatedShowlet(), masterShowlet);
			this.setApiParameterValues(parameters);
			this.setApiParameters(masterMethod.getParameters());
			this.setStrutsAction(ApsAdminSystemConstants.PASTE);
			this.setServiceKey(this.buildTempKey(masterMethod.getMethodName()));
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "copyFromShowlet");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	private ApsProperties extractParametersFromShowlet(ApiMethodRelatedShowlet relatedShowlet, Showlet masterShowlet) {
		ApsProperties parameters = new ApsProperties();
		ApsProperties showletProperties = (masterShowlet.getType().isLogic()) 
				? masterShowlet.getType().getConfig() : masterShowlet.getConfig();
		ApsProperties mapping = relatedShowlet.getMapping();
		if (null != showletProperties && null != mapping) {
			Iterator<Object> keyIter = showletProperties.keySet().iterator();
			while (keyIter.hasNext()) {
				Object key = keyIter.next();
				if (null != mapping.get(key)) {
					parameters.put(mapping.get(key), showletProperties.get(key));
				}
			}
		}
		return parameters;
	}
	
	private String buildTempKey(String masterMethodName) throws Throwable {
		int index = 0;
		String currentCode = null;
		do {
			index++;
			currentCode = masterMethodName + "_" + index;
		} while (null != this.getApiCatalogManager().getApiService(currentCode));
		return currentCode;
	}
	
	@Override
	public String edit() {
		try {
			String check = this.checkService(this.getServiceKey());
			if (null != check) return check;
			ApiService apiService = this.getApiCatalogManager().getApiService(this.getServiceKey());
			this.setApiParameters(apiService.getMaster().getParameters());
			this.setApiMethodName(apiService.getMaster().getMethodName());
			this.setApiParameterValues(apiService.getParameters());
			this.setDescriptions(apiService.getDescription());
			this.setPublicService(apiService.isPublicService());
			this.setActiveService(apiService.isActive());
			this.setMyEntandoService(apiService.isMyEntando());
			this.setServiceKey(apiService.getKey());
			if (null != apiService.getFreeParameters()) {
				List<String> freeParams = Arrays.asList(apiService.getFreeParameters());
				this.setFreeParameters(freeParams);
			}
			this.setTag(apiService.getTag());
			this.setStrutsAction(ApsAdminSystemConstants.EDIT);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "edit");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String save() {
		try {
			String key = this.getServiceKey().trim();
			ApiMethod masterMethod = this.getMethod(this.getApiMethodName());
			String[] freeParams = null;
			if (null != this.getFreeParameters()) {
				freeParams = new String[this.getFreeParameters().size()];
				for (int i = 0; i < this.getFreeParameters().size(); i++) {
					freeParams[i] = this.getFreeParameters().get(i);
				}
			}
			ApiService service = new ApiService(key, this.getDescriptions(), masterMethod, this.getApiParameterValues(), 
                                freeParams, this.getTag(), this.isPublicService(), this.isActiveService(), this.isMyEntandoService());
			this.getApiCatalogManager().saveService(service);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "save");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String trash() {
		try {
			String check = this.checkService(this.getServiceKey());
			if (null != check) return check;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "trash");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String delete() {
		try {
			String check = this.checkService(this.getServiceKey());
			if (null != check) return check;
			this.getApiCatalogManager().deleteService(this.getServiceKey());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "delete");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	protected String checkMasterMethod(String methodName) throws Throwable {
		if (methodName == null) {
			this.addActionError(this.getText("error.service.new.masterApiMethod.required"));
			return INPUT;
		}
		ApiMethod masterMethod = this.getMethod(methodName);
		if (masterMethod == null) {
			this.addActionError(this.getText("error.service.new.masterApiMethod.invalid"));
			return INPUT;
		}
		if (!masterMethod.isCanSpawnOthers()) {
                    String[] args = {masterMethod.getMethodName()};
                    this.addActionError(this.getText("error.service.new.masterApiMethod.unspawnable", args));
                    return INPUT;
		}
		return null;
	}
	
	private String checkService(String serviceKey) throws Throwable {
		ApiService apiService = this.getApiCatalogManager().getApiService(this.getServiceKey());
		if (apiService == null) {
			this.addActionError(this.getText("error.service.invalid", new String[]{this.getServiceKey()}));
			return INPUT;
		}
		return null;
	}
	
	public String getServiceGroup() {
		return _serviceGroup;
	}
	public void setServiceGroup(String serviceGroup) {
		this._serviceGroup = serviceGroup;
	}
	
	public int getStrutsAction() {
		return _strutsAction;
	}
	public void setStrutsAction(int strutsAction) {
		this._strutsAction = strutsAction;
	}
	
	public String getApiMethodName() {
		return _apiMethodName;
	}
	public void setApiMethodName(String apiMethodName) {
		this._apiMethodName = apiMethodName;
	}
	
	public String getServiceKey() {
		return _serviceKey;
	}
	public void setServiceKey(String serviceKey) {
		this._serviceKey = serviceKey;
	}
	
	public ApsProperties getDescriptions() {
		return _descriptions;
	}
	public void setDescriptions(ApsProperties descriptions) {
		this._descriptions = descriptions;
	}
	
	public boolean isActiveService() {
		return _activeService;
	}
	public void setActiveService(boolean activeService) {
		this._activeService = activeService;
	}
	
	public boolean isPublicService() {
		return _publicService;
	}
	public void setPublicService(boolean publicService) {
		this._publicService = publicService;
	}
	
	public boolean isMyEntandoService() {
		return _myEntandoService;
	}
	public void setMyEntandoService(boolean myEntandoService) {
		this._myEntandoService = myEntandoService;
	}
	
	public List<ApiMethodParameter> getApiParameters() {
		return _apiParameters;
	}
	public void setApiParameters(List<ApiMethodParameter> apiParameters) {
		this._apiParameters = apiParameters;
	}
	
	public ApsProperties getApiParameterValues() {
		return _apiParameterValues;
	}
	public void setApiParameterValues(ApsProperties apiParameterValues) {
		this._apiParameterValues = apiParameterValues;
	}

	public List<String> getFreeParameters() {
		return _freeParameters;
	}
	public void setFreeParameters(List<String> freeParameters) {
		this._freeParameters = freeParameters;
	}
	
	public String getTag() {
		return _tag;
	}
	public void setTag(String tag) {
		this._tag = tag;
	}
	
	public String getPageCode() {
		return _pageCode;
	}
	public void setPageCode(String pageCode) {
		this._pageCode = pageCode;
	}
	
	public Integer getFramePos() {
		return _framePos;
	}
	public void setFramePos(Integer framePos) {
		this._framePos = framePos;
	}
	
	protected IApiCatalogManager getApiCatalogManager() {
		return _apiCatalogManager;
	}
	public void setApiCatalogManager(IApiCatalogManager apiCatalogManager) {
		this._apiCatalogManager = apiCatalogManager;
	}
	
	protected IPageManager getPageManager() {
		return _pageManager;
	}
	public void setPageManager(IPageManager pageManager) {
		this._pageManager = pageManager;
	}
	
	private String _serviceGroup;
	
	private int _strutsAction;
	
	private String _apiMethodName;
	
	private String _serviceKey;
	
	private ApsProperties _descriptions;
	
	private boolean _activeService;
	private boolean _publicService;
        private boolean _myEntandoService;
	
	private List<ApiMethodParameter> _apiParameters;
	private ApsProperties _apiParameterValues;
	
	private List<String> _freeParameters;
	
	private String _tag;
	
	private String _pageCode;
	private Integer _framePos;
	
	private IApiCatalogManager _apiCatalogManager;
	private IPageManager _pageManager;
	
}