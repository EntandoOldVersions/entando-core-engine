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
package org.entando.entando.apsadmin.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.entando.entando.aps.system.services.api.IApiCatalogManager;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.ApiMethod.HttpMethod;
import org.entando.entando.aps.system.services.api.model.ApiResource;
import org.entando.entando.apsadmin.api.helper.SchemaGeneratorActionHelper;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.role.IRoleManager;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.util.SelectItem;
import com.agiletec.apsadmin.system.BaseAction;

import org.apache.commons.beanutils.BeanComparator;

/**
 * @author E.Santoboni
 */
public class ApiResourceAction extends BaseAction implements IApiResourceAction {
    
    public void validate() {
        try {
            super.validate();
            String resourceName = this.getResourceName();
            String namespace = this.getNamespace();
            if (null == resourceName) {
                this.addActionError(this.getText("error.resource.invalidName"));
            } else if (null == this.getApiCatalogManager().getApiResource(namespace, resourceName)) {
                this.addActionError(this.getText("error.resource.invalid", new String[]{resourceName}));
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "validate", "Error validating request");
        }
    }
    
    public String generateRequestBodySchema() {
        try {
            ApiMethod method = this.checkAndReturnMethod();
            if (method.getHttpMethod().equals(ApiMethod.HttpMethod.GET) 
                    || method.getHttpMethod().equals(ApiMethod.HttpMethod.DELETE)) {
                String[] args = {method.getResourceName(), method.getHttpMethod().toString()};
                this.addActionError(this.getText("error.resource.method.request.schemaNotAvailable", args));
                return INPUT;
            }
            if (null == method.getExpectedType()) {
                throw new ApsSystemException("Null expectedType for Method " + method.getHttpMethod() + " for resource " + method.getResourceName());
            }
            String result = this.generateAndCheckSchema(method.getExpectedType());
            if (INPUT.equals(result)) {
                String[] args = {method.getResourceName(), method.getHttpMethod().toString()};
                this.addActionError(this.getText("error.resource.method.request.schemaNotAvailable", args));
                return INPUT;
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "generateRequestBodySchema", "Error extracting request body Schema");
            return FAILURE;
        }
        return SUCCESS;
    }
    
    public String generateResponseBodySchema() {
        try {
            ApiMethod method = this.checkAndReturnMethod();
            Class responseClass = this.getSchemaGeneratorHelper().extractResponseClass(method, this.getRequest());
            String result = this.generateAndCheckSchema(responseClass);
            if (INPUT.equals(result)) {
                String[] args = {method.getResourceName(), method.getHttpMethod().toString()};
                this.addActionError(this.getText("error.resource.method.response.schemaNotAvailable", args));
                return INPUT;
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "generateResponseBodySchema", "Error extracting response body Schema");
            return FAILURE;
        }
        return SUCCESS;
    }
    
    private String generateAndCheckSchema(Class jaxbObject) throws IOException {
        InputStream stream = null;
        try {
            String text = this.getSchemaGeneratorHelper().generateSchema(jaxbObject);
            if (null == text || text.trim().length() == 0) {
                return INPUT;
            } else {
                stream = new ByteArrayInputStream(text.getBytes());
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "generateSchema", "Error extracting generating schema from class " + jaxbObject);
            throw new RuntimeException("Error extracting generating schema", t);
        }
        this.setSchemaStream(stream);
        return SUCCESS;
    }
    
    public String updateMethodStatus() {
        try {
            ApiMethod method = this.checkAndReturnMethod();
            String requiredAuthority = this.getRequest().getParameter(method.getHttpMethod().toString() + "_methodAuthority");
            String active = this.getRequest().getParameter(method.getHttpMethod().toString() + "_active");
            if (null == requiredAuthority) {
                //TODO MANAGE
                return SUCCESS;
            }
            this.updateMethodStatus(method, requiredAuthority, active);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "updateMethodStatus", "Error updating method status");
            return FAILURE;
        }
        return SUCCESS;
    }
    
    public String resetMethodStatus() {
        try {
            ApiMethod method = this.checkAndReturnMethod();
            this.resetMethodStatus(method);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "updateMethodStatus", "Error resetting method status");
            return FAILURE;
        }
        return SUCCESS;
    }
    
    private ApiMethod checkAndReturnMethod() throws Throwable {
        ApiResource resource = this.getApiCatalogManager().getApiResource(this.getNamespace(), this.getResourceName());
        ApiMethod method = resource.getMethod(this.getHttpMethod());
        if (null == method) {
            throw new ApsSystemException("Null Method " + this.getHttpMethod() + " for resource " + this.getResourceName());
        }
        return method;
    }
    
    public String updateAllMethodStatus() {
        try {
            ApiResource resource = this.getApiCatalogManager().getApiResource(this.getNamespace(), this.getResourceName());
            String requiredAuthority = this.getRequest().getParameter("methodAuthority");
            String active = this.getRequest().getParameter("active");
            if (null == requiredAuthority) {
                //TODO MANAGE
                return SUCCESS;
            }
            this.updateMethodStatus(resource.getGetMethod(), requiredAuthority, active);
            this.updateMethodStatus(resource.getPostMethod(), requiredAuthority, active);
            this.updateMethodStatus(resource.getPutMethod(), requiredAuthority, active);
            this.updateMethodStatus(resource.getDeleteMethod(), requiredAuthority, active);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "updateAllMethodStatus", "Error updating all method status");
            return FAILURE;
        }
        return SUCCESS;
    }
    
    private void updateMethodStatus(ApiMethod method, String requiredAuthority, String active) {
        if (null == method) return;
        try {
            if (null != requiredAuthority && requiredAuthority.equals("0")) {
                method.setRequiredAuth(true);
                method.setRequiredPermission(null);
            } else if (null != requiredAuthority && null != this.getRoleManager().getPermission(requiredAuthority)) {
                method.setRequiredAuth(true);
                method.setRequiredPermission(requiredAuthority);
            } else {
                method.setRequiredAuth(false);
                method.setRequiredPermission(null);
            }
            method.setStatus(active != null);
            this.getApiCatalogManager().updateMethodConfig(method);
            String[] args = {method.getHttpMethod().toString()};
            this.addActionMessage(this.getText("message.resource.method.configUpdated", args));
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "updateMethodStatus", 
                    "Error updating method status - " + method.getResourceName() + " - " + method.getHttpMethod());
            throw new RuntimeException("Error updating method status", t);
        }
    }
    
    public String resetAllMethodStatus() {
        try {
            ApiResource resource = this.getApiCatalogManager().getApiResource(this.getNamespace(), this.getResourceName());
            this.resetMethodStatus(resource.getGetMethod());
            this.resetMethodStatus(resource.getPostMethod());
            this.resetMethodStatus(resource.getPutMethod());
            this.resetMethodStatus(resource.getDeleteMethod());
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "resetAllMethodStatus", "Error resetting all method status");
            return FAILURE;
        }
        return SUCCESS;
    }
    
    public void resetMethodStatus(ApiMethod method) {
        if (null == method) return;
        try {
            this.getApiCatalogManager().resetMethodConfig(method);
            String[] args = {method.getHttpMethod().toString()};
            this.addActionMessage(this.getText("message.resource.method.configResetted", args));
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "resetMethodStatus", 
                    "Error resetting method status - " + method.getResourceName() + " - " + method.getHttpMethod());
            throw new RuntimeException("Error resetting method status", t);
        }
    }
    
    public List<SelectItem> getMethodAuthorityOptions() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        try {
            items.add(new SelectItem("", this.getText("label.none")));
            items.add(new SelectItem("0", this.getText("label.method.authority.autenticationRequired")));
            List<Permission> permissions = new ArrayList<Permission>();
            permissions.addAll(this.getRoleManager().getPermissions());
            BeanComparator comparator = new BeanComparator("description");
            Collections.sort(permissions, comparator);
            for (int i = 0; i < permissions.size(); i++) {
                Permission permission = permissions.get(i);
                items.add(new SelectItem(permission.getName(), 
                        this.getText("label.method.authority.permission") + " " + permission.getDescription()));
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getMethodAuthorityOptions", "Error extracting autority options");
        }
        return items;
    }
    
    public ApiResource getApiResource() {
        try {
            return this.getApiCatalogManager().getApiResource(this.getNamespace(), this.getResourceName());
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getApiResource", "Error extracting "
					+ "resource '" + this.getResourceName() + "' namespace'" + this.getNamespace()+ "'");
            throw new RuntimeException("Error extracting resource '" + this.getResourceName() + "' namespace'" + this.getNamespace()+ "'", t);
        }
    }
    
    public String getResourceName() {
        return _resourceName;
    }
    public void setResourceName(String resourceName) {
        this._resourceName = resourceName;
    }
	
	public String getNamespace() {
		return _namespace;
	}
	public void setNamespace(String namespace) {
		this._namespace = namespace;
	}
    
    public HttpMethod getHttpMethod() {
        return _httpMethod;
    }
    public void setHttpMethod(HttpMethod httpMethod) {
        this._httpMethod = httpMethod;
    }
    
    public Boolean getMethodStatus() {
        return _methodStatus;
    }
    public void setMethodStatus(Boolean methodStatus) {
        this._methodStatus = methodStatus;
    }
    
    public InputStream getSchemaStream() {
        return _schemaStream;
    }
    public void setSchemaStream(InputStream schemaStream) {
        this._schemaStream = schemaStream;
    }
    
    protected IApiCatalogManager getApiCatalogManager() {
        return _apiCatalogManager;
    }
    public void setApiCatalogManager(IApiCatalogManager apiCatalogManager) {
        this._apiCatalogManager = apiCatalogManager;
    }
    
    protected IRoleManager getRoleManager() {
        return _roleManager;
    }
    public void setRoleManager(IRoleManager roleManager) {
        this._roleManager = roleManager;
    }
    
    protected SchemaGeneratorActionHelper getSchemaGeneratorHelper() {
        return new SchemaGeneratorActionHelper();
    }
    
    private String _resourceName;
	private String _namespace;
    private ApiMethod.HttpMethod _httpMethod;
    private Boolean _methodStatus;
    
    private InputStream _schemaStream;
    
    private IApiCatalogManager _apiCatalogManager;
    private IRoleManager _roleManager;
    
}
