/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software. 
* Entando is a free software; 
* You can redistribute it and/or modify it
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
package com.agiletec.plugins.jacms.aps.system.services.content.helper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import org.entando.entando.aps.system.services.cache.CacheableInfo;
import org.entando.entando.aps.system.services.cache.ICacheInfoManager;
import org.springframework.cache.annotation.Cacheable;

/**
 * Return informations of content authorization
 * @author E.Santoboni
 */
public class ContentAuthorizationHelper implements IContentAuthorizationHelper {
	
	@Override
	public boolean isAuth(UserDetails user, Content content) throws ApsSystemException {
		if (null == content) {
			ApsSystemUtils.getLogger().severe("Null content");
			return false;
		}
		Set<String> groupCodes = this.getContentGroups(content);
		return this.isAuth(user, groupCodes);
	}
	
	@Override
	public boolean isAuth(UserDetails user, PublicContentAuthorizationInfo info) throws ApsSystemException {
		List<Group> userGroups = this.getAuthorizationManager().getUserGroups(user);
		return info.isUserAllowed(userGroups);
	}
	
	@Override
	public boolean isAuth(UserDetails user, String contentId, boolean publicVersion) throws ApsSystemException {
		if (publicVersion) {
			PublicContentAuthorizationInfo authorizationInfo = this.getAuthorizationInfo(contentId);
			return this.isAuth(user, authorizationInfo);
		}
		Content content = this.getContentManager().loadContent(contentId, publicVersion);
		return this.isAuth(user, content);
	}
	
	private Set<String> getContentGroups(Content content) {
		Set<String> groupCodes = new HashSet<String>();
		groupCodes.add(content.getMainGroup());
		Set<String> extraGroupCodes = content.getGroups();
		if (null != extraGroupCodes) {
			groupCodes.addAll(extraGroupCodes);
		}
		return groupCodes;
	}
	
	protected boolean isAuth(UserDetails user, Set<String> groupCodes) throws ApsSystemException {
		if (null == user) {
			ApsSystemUtils.getLogger().severe("Null user");
			return false;
		}
		if (groupCodes.contains(Group.FREE_GROUP_NAME)) return true;
		List<Group> userGroups = this.getAuthorizationManager().getUserGroups(user);
		if (null != userGroups) {
			for (int i = 0; i < userGroups.size(); i++) {
				Group group = userGroups.get(i);
				if (groupCodes.contains(group.getName())) return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isAuthToEdit(UserDetails user, Content content) throws ApsSystemException {
		if (null == content) {
			ApsSystemUtils.getLogger().severe("Null content");
			return false;
		}
		String mainGroupName = content.getMainGroup();
		return this.isAuthToEdit(user, mainGroupName);
	}
	
	@Override
	public boolean isAuthToEdit(UserDetails user, PublicContentAuthorizationInfo info) throws ApsSystemException {
		String mainGroupName = info.getMainGroup();
		return this.isAuthToEdit(user, mainGroupName);
	}
	
	private boolean isAuthToEdit(UserDetails user, String mainGroupName) throws ApsSystemException {
		if (null == user) {
			ApsSystemUtils.getLogger().severe("Null user");
			return false;
		}
		return (this.getAuthorizationManager().isAuthOnPermission(user, JacmsSystemConstants.PERMISSION_EDIT_CONTENTS) 
				&& this.getAuthorizationManager().isAuthOnGroup(user, mainGroupName));
	}
	
	@Override
	public boolean isAuthToEdit(UserDetails user, String contentId, boolean publicVersion) throws ApsSystemException {
		Content content = this.getContentManager().loadContent(contentId, publicVersion);
		return this.isAuth(user, content);
	}
	
	@Override
	@Cacheable(value = ICacheInfoManager.CACHE_NAME, 
			key = "T(com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants).CONTENT_AUTH_INFO_CACHE_PREFIX.concat(#contentId)")
	@CacheableInfo(groups = "T(com.agiletec.plugins.jacms.aps.system.services.cache.CmsCacheWrapperManager).getContentCacheGroupsCsv(#contentId)")
	public PublicContentAuthorizationInfo getAuthorizationInfo(String contentId) {
		PublicContentAuthorizationInfo authInfo = null;
		try {
			Content content = this.getContentManager().loadContent(contentId, true);
			authInfo = new PublicContentAuthorizationInfo(content);
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().throwing(this.getClass().getName(), "getAuthorizationInfo", t);
		}
		return authInfo;
	}
	
	protected IContentManager getContentManager() {
		return _contentManager;
	}
	public void setContentManager(IContentManager contentManager) {
		this._contentManager = contentManager;
	}
	
	protected IAuthorizationManager getAuthorizationManager() {
		return _authorizationManager;
	}
	public void setAuthorizationManager(IAuthorizationManager authorizationManager) {
		this._authorizationManager = authorizationManager;
	}
	
	private IContentManager _contentManager;
	private IAuthorizationManager _authorizationManager;
	
}