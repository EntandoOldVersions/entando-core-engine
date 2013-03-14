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