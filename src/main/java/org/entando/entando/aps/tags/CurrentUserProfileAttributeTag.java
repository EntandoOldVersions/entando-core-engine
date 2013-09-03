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
package org.entando.entando.aps.tags;

import javax.servlet.http.HttpSession;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.UserDetails;
import org.entando.entando.aps.system.services.userprofile.model.IUserProfile;

/**
 * Current User Profile tag.
 * Return an attribute value of the current user profile.
 * @author E.Santoboni
 */
public class CurrentUserProfileAttributeTag extends UserProfileAttributeTag {
    
	@Override
    protected IUserProfile getUserProfile() throws Throwable {
        HttpSession session = this.pageContext.getSession();
        UserDetails currentUser = (UserDetails) session.getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER);
        if (currentUser == null || currentUser.getUsername().equals(SystemConstants.GUEST_USER_NAME) || null == currentUser.getProfile()) {
            ApsSystemUtils.getLogger().severe("User '" + currentUser + "' : Null user, or guest user or user without profile");
            return null;
        }
        return (IUserProfile) currentUser.getProfile();
    }
    
}