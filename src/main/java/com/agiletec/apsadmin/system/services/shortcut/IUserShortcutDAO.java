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
package com.agiletec.apsadmin.system.services.shortcut;

/**
 * Interface for the Data Access Object for the configuration of user shortcut.
 * @author E.Santoboni
 */
public interface IUserShortcutDAO {
	
	/**
	 * Load the configuration by user.
	 * The returned string will be a xml 
	 * (if the specificated user has a configuration) or null;
	 * @param username The username.
	 * @return The config of the given username.
	 */
	public String loadUserConfig(String username);
	
	/**
	 * Save a user configuration.
	 * @param username The username.
	 * @param config The xml string to save
	 */
	public void saveUserConfig(String username, String config);
	
	/**
	 * Delete a user configuration
	 * @param username The username of the config to delete
	 */
	public void deleteUserConfig(String username);
	
}