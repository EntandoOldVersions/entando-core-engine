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
package com.agiletec.plugins.jacms.aps.system.services.resource.parse;

import com.agiletec.aps.BaseTestCase;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jacms.aps.system.services.resource.parse.ResourceDOM;

/**
 * @version 1.0
 * @author M. Morini
 */
public class TestResourceDOM extends BaseTestCase {
	
    public void testGetXMLDocument() throws ApsSystemException {  
		ResourceDOM resourceDom = new ResourceDOM();
        resourceDom.addCategory("tempcategory");
        int index = resourceDom.getXMLDocument().indexOf("tempcategory");
        assertTrue(index != -1);
    }
    
}
