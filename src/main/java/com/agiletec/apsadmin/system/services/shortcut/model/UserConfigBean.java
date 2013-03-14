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
package com.agiletec.apsadmin.system.services.shortcut.model;

import java.io.Serializable;

/**
 * The shortcut user config class.
 * @author E.Santoboni
 */
public class UserConfigBean implements Serializable {
	
	public UserConfigBean(String username, String[] config) {
		this.setUsername(username);
		this.setConfig(config);
	}
	
	public String getUsername() {
		return _username;
	}
	protected void setUsername(String username) {
		this._username = username;
	}
	
	public String[] getConfig() {
		return _config;
	}
	protected void setConfig(String[] config) {
		this._config = config;
	}
	
	private String _username;
	private String[] _config;
	
}