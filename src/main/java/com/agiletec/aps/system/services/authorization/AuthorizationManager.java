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
package com.agiletec.aps.system.services.authorization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.IGroupManager;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.role.IRoleManager;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.role.Role;
import com.agiletec.aps.system.services.user.UserDetails;

/**
 * Servizio di autorizzazione.
 * Il servizio espone tutti i metodi necessari per la verifica verifica delle autorizzazioni utente, 
 * qualsiasi sia la sua provenienza e definizione.
 * @author E.Santoboni
 */
public class AuthorizationManager extends AbstractService implements IAuthorizationManager {
	
	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initialized");
	}
	
	@Override
	public boolean isAuth(UserDetails user, IApsAuthority auth) {
		return this.checkAuth(user, auth);
	}
	
	@Override
	public boolean isAuth(UserDetails user, Group group) {
		return this.isAuthOnGroup(user, group.getName());
	}
	
	@Override
	public boolean isAuth(UserDetails user, IApsEntity entity) {
		if (null == entity) return false;
		String mainGroupName = entity.getMainGroup();
		Group group = this.getGroupManager().getGroup(mainGroupName);
		boolean check = this.isAuth(user, group);
		if (check || mainGroupName.equals(Group.FREE_GROUP_NAME)) return true;
		Set<String> groups = entity.getGroups();
		Iterator<String> iter = groups.iterator();
		while (iter.hasNext()) {
			String groupName = iter.next();
			group = this.getGroupManager().getGroup(groupName);
			check = this.isAuth(user, group);
			if (check || groupName.equals(Group.FREE_GROUP_NAME)) return true;
		}
		return false;
	}
	
	@Override
	public boolean isAuth(UserDetails user, Permission permission) {
		return this.isAuthOnPermission(user, permission.getName());
	}
	
	@Override
	public boolean isAuth(UserDetails user, IPage page) {
		if (this.isAuthOnGroup(user, Group.ADMINS_GROUP_NAME)) return true;
		String pageGroup = page.getGroup();
		if (Group.FREE_GROUP_NAME.equals(pageGroup)) return true;
		boolean isAuthorized = this.isAuthOnGroup(user, pageGroup);
		if (isAuthorized) return true;
		Collection<String> extraGroups = page.getExtraGroups();
		if (null != extraGroups && !extraGroups.isEmpty()) {
			if (extraGroups.contains(Group.FREE_GROUP_NAME)) return true;
			Iterator<String> iter = extraGroups.iterator();
			while (iter.hasNext()) {
				String extraGroupName = iter.next();
				if (this.isAuthOnGroup(user, extraGroupName)) return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isAuthOnGroup(UserDetails user, String groupName) {
		Group group = this.getGroupManager().getGroup(groupName);
		Group adminGroup = this.getGroupManager().getGroup(Group.ADMINS_GROUP_NAME);
		return (this.checkAuth(user, group) || this.checkAuth(user, adminGroup));
	}
	
	@Override
	public boolean isAuthOnRole(UserDetails user, String roleName) {
		Role role = this.getRoleManager().getRole(roleName);
		return (this.isAuthOnPermission(user, Permission.SUPERUSER) || this.checkAuth(user, role));
	}
	
	@Override
	public boolean isAuthOnPermission(UserDetails user, String permissionName) {
		boolean check = this.isAuthOnSinglePermission(user, permissionName);
		if (check) return true;
		return this.isAuthOnSinglePermission(user, Permission.SUPERUSER);
	}
	
	private boolean isAuthOnSinglePermission(UserDetails user, String permissionName) {
		List<Role> rolesWithPermission = this.getRoleManager().getRolesWithPermission(permissionName);
		for (int i=0; i<rolesWithPermission.size(); i++) {
			Role role = rolesWithPermission.get(i);
			boolean check = this.checkAuth(user, role);
			if (check) return true;
		}
		return false;
	}
        
        @Deprecated
        public List<Group> getGroupsOfUser(UserDetails user) {
            return this.getUserGroups(user);
        }
	
	@Override
	public List<Group> getUserGroups(UserDetails user) {
		if (null == user) return null;
                List<Group> groups = new ArrayList<Group>();
		IApsAuthority[] auths = user.getAuthorities();
		if (null != auths) {
			for (int i=0; i<auths.length; i++) {
				IApsAuthority auth = auths[i];
				if (null == auth) continue;
				String authName = auth.getAuthority();
				Group group = this.getGroupManager().getGroup(authName);
				if (null != group) groups.add(group);
			}
		}
		return groups;
	}
	
	@Override
	public List<Role> getUserRoles(UserDetails user) {
		if (null == user) return null;
                List<Role> roles = new ArrayList<Role>();
		IApsAuthority[] auths = user.getAuthorities();
		if (null != auths) {
			for (int i=0; i<auths.length; i++) {
				IApsAuthority auth = auths[i];
				if (null == auth) continue;
				if (auth instanceof Role) {
                                    String authName = auth.getAuthority();
                                    Role role = this.getRoleManager().getRole(authName);
                                    if (null != role) roles.add(role);
				}
			}
		}
		return roles;
	}
	
	private boolean checkAuth(UserDetails user, IApsAuthority requiredAuth) {
		if (null == requiredAuth) return false;
		IApsAuthority[] auths = user.getAuthorities();
		if (null != auths) {
			for (int i=0; i<auths.length; i++) {
				IApsAuthority auth = auths[i];
				if (null == auth) continue;
				String authName = auth.getAuthority();
				if (requiredAuth.getAuthority().equals(authName)) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected IGroupManager getGroupManager() {
		return _groupManager;
	}
	public void setGroupManager(IGroupManager groupManager) {
		this._groupManager = groupManager;
	}
	
	protected IRoleManager getRoleManager() {
		return _roleManager;
	}
	public void setRoleManager(IRoleManager roleManager) {
		this._roleManager = roleManager;
	}
    
	private IGroupManager _groupManager;
	private IRoleManager _roleManager;
	
}