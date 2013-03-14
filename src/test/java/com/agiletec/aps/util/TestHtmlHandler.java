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
package com.agiletec.aps.util;

import com.agiletec.aps.BaseTestCase;

import com.agiletec.aps.util.HtmlHandler;

/**
 * @version 1.0
 * @author W.Ambu
 */
public class TestHtmlHandler extends BaseTestCase {
	
    public void testGetParsedText() {
        String textToParse = "<title> This is the<b>first</b></title><body><b>this is</b>the next</body>";
        HtmlHandler htmlHandler = new HtmlHandler();
        String resultText = htmlHandler.getParsedText(textToParse);
        assertEquals("  This is the first    this is the next ", resultText);
    }
    
}

