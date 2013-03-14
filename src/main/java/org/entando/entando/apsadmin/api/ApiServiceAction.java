/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
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
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package org.entando.entando.apsadmin.api;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.ApiMethodParameter;
import org.entando.entando.aps.system.services.api.model.ApiMethodRelatedShowlet;
import org.entando.entando.aps.system.services.api.model.ApiService;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.IGroupManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.system.services.showlettype.IShowletTypeManager;
import com.agiletec.aps.system.services.showlettype.ShowletType;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.apsadmin.system.ApsAdminSystemConstants;

/**
 * @author E.Santoboni
 */
public class ApiServiceAction extends AbstractApiAction {
	
	@Override
	public void validate() {
		super.validate();
		try {
			this.checkMasterMethod(this.getNamespace(), this.getResourceName());
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
			if ((this.getStrutsAction() == ApsAdminSystemConstants.ADD
					|| this.getStrutsAction() == ApsAdminSystemConstants.PASTE)
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
			ApiMethod masterMethod = this.getMethod(this.getNamespace(), this.getResourceName());
			List<ApiMethodParameter> apiParameters = masterMethod.getParameters();
			this.extractFreeParameters(apiParameters);
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
	
	private void extractFreeParameters(List<ApiMethodParameter> apiParameters) {
		if (null == apiParameters) {
			return;
		}
		for (int i = 0; i < apiParameters.size(); i++) {
			ApiMethodParameter apiMethodParameter = apiParameters.get(i);
			String requestParamName = "freeParameter_" + apiMethodParameter.getKey();
			String value = this.getRequest().getParameter(requestParamName);
			if (null != value && Boolean.parseBoolean(value)) {
				this.getFreeParameters().add(apiMethodParameter.getKey());
			}
		}
	}
	
	/**
	 * Create of new api service.
	 * @return The result code.
	 */
	public String newService() {
		try {
			if (null != this.getResourceCode()) {
				String[] sections = this.getResourceCode().split(":");
				if (sections.length == 2) {
					this.setNamespace(sections[0]);
					this.setResourceName(sections[1]);
				} else {
					this.setResourceName(sections[0]);
				}
			}
			String check = this.checkMasterMethod(this.getNamespace(), this.getResourceName());
			if (null != check) {
				return check;
			}
			ApiMethod masterMethod = this.getMethod(this.getNamespace(), this.getResourceName());
			if (null != this.getShowletTypeCode() && null != masterMethod.getRelatedShowlet()) {
				ShowletType type = this.getShowletTypeManager().getShowletType(this.getShowletTypeCode());
				if (null != type && type.isLogic()) {
					ApsProperties parameters =
							this.extractParametersFromShowletProperties(masterMethod.getRelatedShowlet(), type.getConfig());
					this.setApiParameterValues(parameters);
				}
			}
			this.setApiParameters(masterMethod.getParameters());
			this.setStrutsAction(ApsAdminSystemConstants.ADD);
			this.setServiceKey(this.buildTempKey(masterMethod.getResourceName()));
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "newService");
			return FAILURE;
		}
		return SUCCESS;
	}

	public ApiMethod getMethod(String namespace, String resourceName) {
		try {
			return this.getApiCatalogManager().getMethod(ApiMethod.HttpMethod.GET, namespace, resourceName);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getMethod", 
					"Error extracting GET method of resource '" + resourceName + "' namespace '" + namespace + "'");
		}
		return null;
	}

	public List<Lang> getSystemLangs() {
		return this.getLangManager().getLangs();
	}
	
	/**
	 * Copy an exist showlet (physic and with parameters, joined with a exist api method) 
	 * and value the form of creation of new api service.
	 * @return The result code.
	 */
	public String copyFromShowlet() {
		try {
			String check = this.checkMasterMethod(this.getNamespace(), this.getResourceName());
			if (null != check) {
				return check;
			}
			ApiMethod masterMethod = this.getMethod(this.getNamespace(), this.getResourceName());
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
						new String[]{masterShowlet.getType().getCode(), masterMethod.getResourceName()}));
				return INPUT;
			}
			ApsProperties parameters = this.extractParametersFromShowlet(masterMethod.getRelatedShowlet(), masterShowlet);
			this.setApiParameterValues(parameters);
			this.setApiParameters(masterMethod.getParameters());
			this.setStrutsAction(ApsAdminSystemConstants.PASTE);
			this.setServiceKey(this.buildTempKey(masterMethod.getResourceName()));
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "copyFromShowlet");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	private ApsProperties extractParametersFromShowlet(ApiMethodRelatedShowlet relatedShowlet, Showlet masterShowlet) {
		ApsProperties showletProperties = (masterShowlet.getType().isLogic())
				? masterShowlet.getType().getConfig() : masterShowlet.getConfig();
		return this.extractParametersFromShowletProperties(relatedShowlet, showletProperties);
	}

	private ApsProperties extractParametersFromShowletProperties(ApiMethodRelatedShowlet relatedShowlet, ApsProperties showletProperties) {
		ApsProperties parameters = new ApsProperties();
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
		} while (null != this.getApiService(currentCode));
		return currentCode;
	}

	/**
	 * Edit an exist api service.
	 * @return The result code.
	 */
	public String edit() {
		try {
			String check = this.checkService();
			if (null != check) {
				return check;
			}
			ApiService apiService = this.getApiService(this.getServiceKey());
			this.setApiParameters(apiService.getMaster().getParameters());
			this.setResourceName(apiService.getMaster().getResourceName());
			this.setNamespace(apiService.getMaster().getNamespace());
			this.setApiParameterValues(apiService.getParameters());
			this.setDescriptions(apiService.getDescription());
			this.setHiddenService(apiService.isHidden());
			this.setActiveService(apiService.isActive());
			this.setMyEntandoService(apiService.isMyEntando());
			this.setServiceKey(apiService.getKey());
			if (null != apiService.getFreeParameters()) {
				List<String> freeParams = Arrays.asList(apiService.getFreeParameters());
				this.setFreeParameters(freeParams);
			}
			this.setTag(apiService.getTag());
			this.setRequiredAuth(apiService.getRequiredAuth());
			this.setRequiredGroup(apiService.getRequiredGroup());
			this.setRequiredPermission(apiService.getRequiredPermission());
			this.setStrutsAction(ApsAdminSystemConstants.EDIT);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "edit");
			return FAILURE;
		}
		return SUCCESS;
	}

	/**
	 * Save an api service.
	 * @return The result code.
	 */
	public String save() {
		try {
			String key = this.getServiceKey().trim();
			ApiMethod masterMethod = this.getMethod(this.getNamespace(), this.getResourceName());
			String[] freeParams = null;
			if (null != this.getFreeParameters()) {
				freeParams = new String[this.getFreeParameters().size()];
				for (int i = 0; i < this.getFreeParameters().size(); i++) {
					freeParams[i] = this.getFreeParameters().get(i);
				}
			}
			ApiService service = new ApiService(key, this.getDescriptions(), masterMethod, this.getApiParameterValues(),
					freeParams, this.getTag(), !this.isHiddenService(), this.isActiveService(), this.isMyEntandoService());
			service.setRequiredAuth(this.getRequiredAuth());
			if (null != this.getRequiredGroup() && this.getRequiredGroup().trim().length() > 0) {
				service.setRequiredGroup(this.getRequiredGroup());
			}
			if (null != this.getRequiredPermission() && this.getRequiredPermission().trim().length() > 0) {
				service.setRequiredPermission(this.getRequiredPermission());
			}
			this.getApiCatalogManager().saveService(service);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "save");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	/**
	 * Start the deletion operations for the given api service.
	 * @return The result code.
	 */
	public String trash() {
		try {
			String check = this.checkService();
			if (null != check) {
				return check;
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "trash");
			return FAILURE;
		}
		return SUCCESS;
	}

	/**
	 * Delete an api service from the system.
	 * @return The result code.
	 */
	public String delete() {
		try {
			String check = this.checkService();
			if (null != check) {
				return check;
			}
			this.getApiCatalogManager().deleteService(this.getServiceKey());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "delete");
			return FAILURE;
		}
		return SUCCESS;
	}

	protected String checkMasterMethod(String namespace, String resourceName) throws Throwable {
		if (resourceName == null) {
			this.addActionError(this.getText("error.service.new.masterApiMethod.required"));
			return INPUT;
		}
		ApiMethod masterMethod = this.getMethod(namespace, resourceName);
		if (masterMethod == null) {
			this.addActionError(this.getText("error.service.new.masterApiMethod.invalid"));
			return INPUT;
		}
		if (!masterMethod.isCanSpawnOthers()) {
			if (null != namespace) {
				String[] args = {masterMethod.getResourceName(), masterMethod.getNamespace()};
				this.addActionError(this.getText("error.service.new.masterApiMethod.unspawnable2", args));
			} else {
				String[] args = {masterMethod.getResourceName()};
				this.addActionError(this.getText("error.service.new.masterApiMethod.unspawnable", args));
			}
			return INPUT;
		}
		return null;
	}
	
    public String generateResponseBodySchema() {
        try {
            String result = this.checkService();
			if (null != result) return result;
			ApiService apiService = this.getApiService(this.getServiceKey());
            return super.generateResponseBodySchema(apiService.getMaster());
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "generateResponseBodySchema", "Error extracting response body Schema");
            return FAILURE;
        }
    }
	
	protected String checkService() throws Throwable {
		ApiService apiService = this.getApiService(this.getServiceKey());
		if (apiService == null) {
			this.addActionError(this.getText("error.service.invalid", new String[]{this.getServiceKey()}));
			return INPUT;
		}
		return null;
	}
	
	/**
	 * Return the list of system groups.
	 * @return The list of system groups.
	 */
	public List<Group> getGroups() {
		return this.getGroupManager().getGroups();
	}
	
	public Group getGroup(String name) {
		return this.getGroupManager().getGroup(name);
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
	
	public String getResourceCode() {
		return _resourceCode;
	}
	public void setResourceCode(String resourceCode) {
		this._resourceCode = resourceCode;
	}
	
	public String getNamespace() {
		return _namespace;
	}
	public void setNamespace(String namespace) {
		this._namespace = namespace;
	}
	
	public String getResourceName() {
		return _resourceName;
	}
	public void setResourceName(String resourceName) {
		this._resourceName = resourceName;
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
	
	public boolean isHiddenService() {
		return _hiddenService;
	}
	public void setHiddenService(boolean hiddenService) {
		this._hiddenService = hiddenService;
	}
	
	public boolean isMyEntandoService() {
		return _myEntandoService;
	}
	public void setMyEntandoService(boolean myEntandoService) {
		this._myEntandoService = myEntandoService;
	}
	
    public Boolean getRequiredAuth() {
		return _requiredAuth;
    }
    public void setRequiredAuth(Boolean requiredAuth) {
        this._requiredAuth = requiredAuth;
    }
    
	public String getRequiredGroup() {
		return _requiredGroup;
	}
	public void setRequiredGroup(String requiredGroup) {
		this._requiredGroup = requiredGroup;
	}
	
	public String getRequiredPermission() {
		return _requiredPermission;
	}
	public void setRequiredPermission(String requiredPermission) {
		this._requiredPermission = requiredPermission;
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

	public String getShowletTypeCode() {
		return _showletTypeCode;
	}
	public void setShowletTypeCode(String showletTypeCode) {
		this._showletTypeCode = showletTypeCode;
	}
	
	protected IPageManager getPageManager() {
		return _pageManager;
	}
	public void setPageManager(IPageManager pageManager) {
		this._pageManager = pageManager;
	}

	protected IShowletTypeManager getShowletTypeManager() {
		return _showletTypeManager;
	}
	public void setShowletTypeManager(IShowletTypeManager showletTypeManager) {
		this._showletTypeManager = showletTypeManager;
	}
	
	protected IGroupManager getGroupManager() {
		return _groupManager;
	}
	public void setGroupManager(IGroupManager groupManager) {
		this._groupManager = groupManager;
	}
	
	private String _serviceGroup;
	private int _strutsAction;
	
	private String _resourceCode;
	
	private String _resourceName;
	private String _namespace;
	
	private String _serviceKey;
	private ApsProperties _descriptions;
	private boolean _activeService;
	private boolean _hiddenService;
	private boolean _myEntandoService;
	
	private Boolean _requiredAuth;
	private String _requiredPermission;
	private String _requiredGroup;
	
	private List<ApiMethodParameter> _apiParameters;
	private ApsProperties _apiParameterValues;
	private List<String> _freeParameters = new ArrayList<String>();
	private String _tag;
	private String _pageCode;
	private Integer _framePos;
	private String _showletTypeCode;
	
	private IPageManager _pageManager;
	private IShowletTypeManager _showletTypeManager;
	private IGroupManager _groupManager;
	
}