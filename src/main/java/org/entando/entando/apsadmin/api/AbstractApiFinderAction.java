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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.entando.entando.aps.system.services.api.IApiCatalogManager;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.ApiResource;
import org.entando.entando.apsadmin.api.model.ApiSelectItem;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * @author E.Santoboni
 */
public abstract class AbstractApiFinderAction extends BaseAction {
    
    public List<List<ApiResource>> getResourceFlavours() {
        List<String> pluginCodes = new ArrayList<String>();
        List<List<ApiResource>> group = new ArrayList<List<ApiResource>>();
        try {
            Map<String, List<ApiResource>> mapping = this.getResourceFlavoursMapping(pluginCodes);
            this.addResourceGroup("core", mapping, group);
            for (int i = 0; i < pluginCodes.size(); i++) {
                String pluginCode = pluginCodes.get(i);
                this.addResourceGroup(pluginCode, mapping, group);
            }
            this.addResourceGroup("custom", mapping, group);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getResourceFlavours");
            throw new RuntimeException("Error extracting Flavours resources", t);
        }
        return group;
    }
    
    private void addResourceGroup(String groupCode, Map<String, List<ApiResource>> mapping, List<List<ApiResource>> group) {
        List<ApiResource> singleGroup = mapping.get(groupCode);
        if (null != singleGroup) {
            BeanComparator comparator = new BeanComparator("resourceName");
            Collections.sort(singleGroup, comparator);
            group.add(singleGroup);
        }
    }
    
    private Map<String, List<ApiResource>> getResourceFlavoursMapping(List<String> pluginCodes) throws Throwable {
        Map<String, List<ApiResource>> finalMapping = new HashMap<String, List<ApiResource>>();
        try {
            Map<String, ApiResource> masterResources = this.getApiCatalogManager().getApiResources();
            Iterator<ApiResource> resourcesIter = masterResources.values().iterator();
            while (resourcesIter.hasNext()) {
                ApiResource apiResource = resourcesIter.next();
                List<ApiResource> resources = finalMapping.get(apiResource.getSectionCode());
                if (null == resources) {
                    resources = new ArrayList<ApiResource>();
                    finalMapping.put(apiResource.getSectionCode(), resources);
                }
                resources.add(apiResource);
                String pluginCode = apiResource.getPluginCode();
                if (null != pluginCode && !pluginCodes.contains(pluginCode)) {
                    pluginCodes.add(pluginCode);
                }
            }
            Collections.sort(pluginCodes);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getResourceFlavoursMapping");
            throw new RuntimeException("Error extracting resource Flavours mapping", t);
        }
        return finalMapping;
    }
    
    @Deprecated
    public List<List<ApiSelectItem>> getMethodFlavours() {
        List<String> pluginCodes = new ArrayList<String>();
        List<List<ApiSelectItem>> group = new ArrayList<List<ApiSelectItem>>();
        try {
            Map<String, List<ApiSelectItem>> mapping = this.getMethodFlavoursMapping(pluginCodes);
            this.addGroup("core", mapping, group);
            for (int i = 0; i < pluginCodes.size(); i++) {
                String pluginCode = pluginCodes.get(i);
                this.addGroup(pluginCode, mapping, group);
            }
            this.addGroup("custom", mapping, group);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getMethodFlavours");
            throw new RuntimeException("Error extracting Flavours methods", t);
        }
        return group;
    }
    
    @Deprecated
    protected Map<String, List<ApiSelectItem>> getMethodFlavoursMapping(List<String> pluginCodes) throws Throwable {
        Map<String, List<ApiSelectItem>> mapping = new HashMap<String, List<ApiSelectItem>>();
        try {
            Map<String, ApiMethod> methodMap = this.getApiCatalogManager().getMethods();
            List<ApiMethod> methods = new ArrayList<ApiMethod>(methodMap.values());
            for (int i = 0; i < methods.size(); i++) {
                ApiMethod method = methods.get(i);
                if (this.includeIntoMapping(method)) {
                    String pluginCode = method.getPluginCode();
                    if (null != pluginCode && pluginCode.trim().length() > 0) {
                        if (!pluginCodes.contains(pluginCode)) {
                            pluginCodes.add(pluginCode);
                        }
                        this.addMethod(pluginCode, method, mapping);
                    } else if (method.getSource().equals("core")) {
                        this.addMethod(method.getSource(), method, mapping);
                    } else {
                        this.addMethod("custom", method, mapping);
                    }
                }
            }
            Collections.sort(pluginCodes);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getMethodFlavoursMapping");
            throw new RuntimeException("Error extracting Flavours mapping", t);
        }
        return mapping;
    }

    protected boolean includeIntoMapping(ApiMethod method) {
        return true;
    }

    protected void addMethod(String mapCode, ApiMethod method, Map<String, List<ApiSelectItem>> mapping) {
        List<ApiSelectItem> methods = mapping.get(mapCode);
        if (null == methods) {
            methods = new ArrayList<ApiSelectItem>();
            mapping.put(mapCode, methods);
        }
        ApiSelectItem item = new ApiSelectItem(method.getMethodName(), method.getDescription(), mapCode);
        item.setActiveItem(method.isActive());
        methods.add(item);
    }

    protected void addGroup(String code, Map<String, List<ApiSelectItem>> mapping, List<List<ApiSelectItem>> group) {
        List<ApiSelectItem> singleGroup = mapping.get(code);
        if (null != singleGroup) {
            BeanComparator comparator = new BeanComparator("value");
            Collections.sort(singleGroup, comparator);
            group.add(singleGroup);
        }
    }

    public ApiMethod getMethod(String methodName) {
        try {
            return this.getApiCatalogManager().getMethod(methodName);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getMethod", "Error extracting method '" + methodName + "'");
        }
        return null;
    }
    
    protected IApiCatalogManager getApiCatalogManager() {
        return _apiCatalogManager;
    }
    public void setApiCatalogManager(IApiCatalogManager apiCatalogManager) {
        this._apiCatalogManager = apiCatalogManager;
    }
    
    private IApiCatalogManager _apiCatalogManager;
    
}