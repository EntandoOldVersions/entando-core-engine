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
package com.agiletec.aps.system.services.showlettype;

import java.util.ArrayList;
import java.util.List;

import com.agiletec.aps.BaseTestCase;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.showlettype.ShowletTypeDOM;
import com.agiletec.aps.system.services.showlettype.ShowletTypeParameter;

/**
 * @author M.Diana
 */
public class TestShowletTypeDOM extends BaseTestCase {
	
    public void testParseConfig() throws ApsSystemException {
		String framesXml = "<config>" +
							"<parameter name=\"contentType\">" +
							"Tipo di contenuto (obbligatorio)" +
							"</parameter>" +
							"<parameter name=\"modelId\">" +
							"Modello di contenuto (obbligatorio)" +
							"</parameter>" +
							"<parameter name=\"filters\" />" +
							"<action name=\"listViewerConfig\"/>" +
							"</config>";
		ShowletTypeDOM showletTypeDOM = new ShowletTypeDOM(framesXml);
        String action = showletTypeDOM.getAction();
        assertTrue(action.equals("listViewerConfig"));
        List<ShowletTypeParameter> params = showletTypeDOM.getParameters();
        assertEquals(3, params.size());
	}
    
    public void testCreateConfig() throws ApsSystemException {
    	ShowletTypeParameter params1 = new ShowletTypeParameter();
    	params1.setName("param1");
    	params1.setDescr("Param1 Descr");
    	ShowletTypeParameter params2 = new ShowletTypeParameter();
    	params2.setName("param2");
    	params2.setDescr("Param2 Descr");
    	List<ShowletTypeParameter> params = new ArrayList<ShowletTypeParameter>();
    	params.add(params1);
    	params.add(params2);
    	ShowletTypeDOM showletTypeDOM = new ShowletTypeDOM(params, "customActionName");
    	String xml = showletTypeDOM.getXMLDocument();
    	
    	ShowletTypeDOM showletTypeDOM2 = new ShowletTypeDOM(xml);
    	assertEquals("customActionName", showletTypeDOM2.getAction());
    	List<ShowletTypeParameter> extractedParams = showletTypeDOM2.getParameters();
    	assertEquals(2, extractedParams.size());
    	assertEquals("param1", extractedParams.get(0).getName());
    	assertEquals("Param2 Descr", extractedParams.get(1).getDescr());
    }
			
}
