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
package com.agiletec.aps.system.services.showlettype;

import java.util.Map;

import javax.sql.DataSource;

import com.agiletec.aps.BaseTestCase;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.showlettype.ShowletType;
import com.agiletec.aps.system.services.showlettype.ShowletTypeDAO;

/**
 * @author M.Diana
 */
public class TestShowletTypeDAO extends BaseTestCase {
	
    public void testLoadShowletTypes() throws Throwable {
    	DataSource dataSource = (DataSource) this.getApplicationContext().getBean("portDataSource");
    	ShowletTypeDAO showletTypeDao = new ShowletTypeDAO();
    	showletTypeDao.setDataSource(dataSource);
    	ILangManager langManager = (ILangManager) this.getService(SystemConstants.LANGUAGE_MANAGER);
    	showletTypeDao.setLangManager(langManager);
    	Map<String, ShowletType> types = null;
		try {
			types = showletTypeDao.loadShowletTypes();
		} catch (Throwable t) {
            throw t;
        }
		ShowletType showletType = (ShowletType) types.get("content_viewer");
		assertNotNull(showletType);
		showletType = (ShowletType) types.get("content_viewer_list");
		assertNotNull(showletType);
	}    
	
}
