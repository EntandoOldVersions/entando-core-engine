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
package com.agiletec.plugins.jacms.aps.system.services.content.helper;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.dispenser.ContentAuthorizationInfo;

/**
 * Return informations of content authorization
 * @author E.Santoboni
 */
public interface IContentAuthorizationHelper {
	
	/**
	 * Return true if the given user can view the given content.
	 * @param user The user
	 * @param content The content.
	 * @return True if the given user can view the content.
	 * @throws ApsSystemException In case of error
	 */
	public boolean isAuth(UserDetails user, Content content) throws ApsSystemException;
	
	/**
	 * Return true if the given user can view the given content.
	 * @param user The user
	 * @param contentId The content id.
	 * @param publicVersion true if the control should be carried out in the public version
	 * @return True if the given user can view the content.
	 * @throws ApsSystemException In case of error
	 */
	public boolean isAuth(UserDetails user, String contentId, boolean publicVersion) throws ApsSystemException;
	
	/**
	 * Return true if the given user can view the given content.
	 * @param user The user
	 * @param info The content authorization info.
	 * @return True if the given user can view the content.
	 * @throws ApsSystemException In case of error
	 */
	public boolean isAuth(UserDetails user, ContentAuthorizationInfo info) throws ApsSystemException;
	
	/**
	 * Return true if the given user can edit the given content.
	 * @param user The user
	 * @param content The content.
	 * @return True if the given user can edit the content.
	 * @throws ApsSystemException In case of error
	 */
	public boolean isAuthToEdit(UserDetails user, Content content) throws ApsSystemException;
	
	/**
	 * Return true if the given user can edit the given content.
	 * @param user The user
	 * @param contentId The content id.
	 * @param publicVersion true if the control should be carried out in the public version
	 * @return True if the given user can edit the content.
	 * @throws ApsSystemException In case of error
	 */
	public boolean isAuthToEdit(UserDetails user, String contentId, boolean publicVersion) throws ApsSystemException;
	
	/**
	 * Return true if the given user can edit the given content.
	 * @param user The user
	 * @param info The content authorization info.
	 * @return True if the given user can edit the content.
	 * @throws ApsSystemException In case of error
	 */
	public boolean isAuthToEdit(UserDetails user, ContentAuthorizationInfo info) throws ApsSystemException;
	
}