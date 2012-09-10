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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.entando.entando.aps.system.services.api.IApiCatalogManager;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.role.IRoleManager;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.util.SelectItem;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * @author E.Santoboni
 */
public abstract class AbstractApiAction extends BaseAction {
    
    public List<SelectItem> getPermissionAutorityOptions() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        try {
            List<Permission> permissions = new ArrayList<Permission>();
            permissions.addAll(this.getRoleManager().getPermissions());
            BeanComparator comparator = new BeanComparator("description");
            Collections.sort(permissions, comparator);
            for (int i = 0; i < permissions.size(); i++) {
                Permission permission = permissions.get(i);
                items.add(new SelectItem(permission.getName(), 
                        this.getText("label.api.authority.permission") + " " + permission.getDescription()));
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getPermissionAutorityOptions", "Error extracting autority options");
        }
        return items;
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
    
    private IApiCatalogManager _apiCatalogManager;
    private IRoleManager _roleManager;
    
}