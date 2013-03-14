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
package com.agiletec.apsadmin.admin;

import java.util.Map;

/**
 * This interface declares all the base actions available for the system administration.
 * @author E.Santoboni
 */
public interface IBaseAdminAction {
	
	/**
	 * Get the system parameters in order to edit them.
	 * @return the result code.
	 */
	public String configSystemParams();
	
	/**
	 * Update the system params.
	 * @return the result code.
	 */
	public String updateSystemParams();
	
	public Map<String, String> getSystemParams();
	
	/**
	 * Reload the system configuration.
	 * @return the result code.
	 */
	public String reloadConfig();
	
	/**
	 * Reload the references of all the existing entities.
	 * @return the result code.
	 */
	public String reloadEntitiesReferences();
	
}
