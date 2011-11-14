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
package com.agiletec.plugins.jacms.apsadmin.portal.specialshowlet.listviewer;

import java.util.HashMap;
import java.util.Map;

import com.agiletec.apsadmin.ApsAdminBaseTestCase;

/**
 * @author E.Santoboni
 */
public abstract class TestAbstractAttributeFilterAction extends ApsAdminBaseTestCase {
	
	protected Map<String, String> getBaseParams(String contentTypeCode) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("pageCode", "homepage");
		params.put("frame", "1");
		params.put("showletTypeCode", "advancedListViewer");
		params.put("contentType", contentTypeCode);
		params.put("filters", "");
		return params;
	}
	
	protected String executeAddFilter(String username, Map<String, String> params, String actionName) throws Throwable {
		this.setUserOnSession(username);
		this.initAction("/do/jacms/Page/SpecialShowlet/ListViewer", actionName);
		this.addParameters(params);
		return this.executeAction();
	}
	
}