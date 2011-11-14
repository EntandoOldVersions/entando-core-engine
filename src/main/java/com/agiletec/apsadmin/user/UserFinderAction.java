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
package com.agiletec.apsadmin.user;

import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * Classe action delegate alla ricerca utenti.
 * @version 1.0
 * @author E.Santoboni
 */
public class UserFinderAction extends BaseAction implements IUserFinderAction {
	
	@Override
	public List<UserDetails> getUsers() {
		try {
			List<UserDetails> users = this.getUserManager().searchUsers(this.getText());
			BeanComparator comparator = new BeanComparator("username");
			Collections.sort(users, comparator);
			return users;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getUsers");
			throw new RuntimeException("Errore in ricerca utenti", t);
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
	
}