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
package com.agiletec.apsadmin.user;

/**
 * Classe action delegate alla ricerca utenti.
 * @author E.Santoboni
 * @deprecated From Entando 3.3.1, use org.entando.entando.apsadmin.user.UserProfileFinderAction
 */
public class UserFinderAction extends org.entando.entando.apsadmin.user.UserProfileFinderAction implements IUserFinderAction {
	/*
	public List<UserDetails> getUsers() {
		try {
			List<UserDetails> users = this.getUserManager().searchUsers(this.getText());
			BeanComparator comparator = new BeanComparator("username");
			Collections.sort(users, comparator);
			return users;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getUsers");
			throw new RuntimeException("Error searching users", t);
		}
	}
	
	public String getText() {
		return _text;
	}
	public void setText(String text) {
		this._text = text;
	}
	
	protected IUserManager getUserManager() {
		return _userManager;
	}
	public void setUserManager(IUserManager userManager) {
		this._userManager = userManager;
	}
	
	private String _text;
	private IUserManager _userManager;
	*/
}