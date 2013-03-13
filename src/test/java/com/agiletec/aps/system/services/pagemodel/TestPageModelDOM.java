/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* Entando is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.aps.system.services.pagemodel;

import com.agiletec.aps.BaseTestCase;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.pagemodel.PageModelDOM;
import com.agiletec.aps.system.services.showlettype.IShowletTypeManager;

/**
 * @version 1.0
 * @author M.Diana
 */
public class TestPageModelDOM extends BaseTestCase {
	
    public void testGetFrames() throws Throwable {
		String framesXml = "<frames>" 
				+ "<frame pos=\"0\"><descr>Box sinistra alto</descr></frame>"
				+ "<frame pos=\"1\"><descr>Box sinistra basso</descr></frame>"
				+ "<frame pos=\"2\" main=\"true\"><descr>Box centrale 1</descr></frame>"
				+ "<frame pos=\"3\"><descr>Box centrale 2</descr></frame>"
				+ "<frame pos=\"4\"><descr>Box destra alto</descr></frame>"
				+ "<frame pos=\"5\"><descr>Box destra basso</descr></frame>"
				+ "</frames>";
		IShowletTypeManager showletTypeManager = 
        	(IShowletTypeManager) this.getService(SystemConstants.SHOWLET_TYPE_MANAGER);
        PageModelDOM pageModelDOM = new PageModelDOM(framesXml, showletTypeManager);
        String[] frames = pageModelDOM.getFrames();
        assertEquals(frames[0].equals("Box sinistra alto"), true);
	}   
			
}
