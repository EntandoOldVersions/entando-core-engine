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
package com.agiletec.aps.system.services.baseconfig;

import com.agiletec.aps.BaseTestCase;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;

/**
 * @author M.Casari
 */
public class TestBaseConfigService extends BaseTestCase {
	
    public void testGetParam() throws ApsSystemException {
    	ConfigInterface baseConfigManager = (ConfigInterface) this.getService(SystemConstants.BASE_CONFIG_MANAGER);
		String param = baseConfigManager.getParam(SystemConstants.CONFIG_PARAM_NOT_FOUND_PAGE_CODE);
		assertEquals(param, "notfound");
	}
    
}