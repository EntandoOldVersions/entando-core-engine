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