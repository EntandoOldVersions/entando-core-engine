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
package com.agiletec.plugins.jacms.aps.system.services.content;

import java.util.List;

import com.agiletec.aps.BaseTestCase;

import com.agiletec.aps.system.services.group.GroupUtilizer;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;

public class TestGroupUtilizer extends BaseTestCase {
	public void testGetGroupsUtilizers() throws Throwable {
    	String[] names = this.getApplicationContext().getBeanNamesForType(GroupUtilizer.class);
    	assertTrue(names.length>=4);
    	for (int i=0; i<names.length; i++) {
			GroupUtilizer service = (GroupUtilizer) this.getApplicationContext().getBean(names[i]);
			List utilizers = service.getGroupUtilizers("coach");
			if (names[i].equals(JacmsSystemConstants.CONTENT_MANAGER)) {
				assertEquals(6, utilizers.size());
			} else if (names[i].equals(JacmsSystemConstants.RESOURCE_MANAGER)) {
				assertEquals(0, utilizers.size());
			}
		}
    }
}
