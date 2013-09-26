/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando Enterprise Edition software.
* You can redistribute it and/or modify it
* under the terms of the Entando's EULA
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package org.entando.entando.apsadmin.common.currentuser;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.user.UserDetails;

import org.entando.entando.aps.system.services.userprofile.model.IUserProfile;
import org.entando.entando.apsadmin.common.UserAvatarAction;

/**
 * @author E.Santoboni
 */
public class CurrentAvatarAction extends UserAvatarAction {
	
	@Override
	protected IUserProfile getUserProfile() throws ApsSystemException {
		UserDetails currentUser = super.getCurrentUser();
		IUserProfile profile = (null != currentUser && null != currentUser.getProfile()) 
				? (IUserProfile) currentUser.getProfile() 
				: null;
		return profile;
	}
	
}