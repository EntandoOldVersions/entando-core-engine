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

/**
 * 
 * @author W.Ambu
 */
public class HtmlHandler {

	/**
	 * Extracts text without html tags
	 * @param textToParse
	 * @return the text value of the node
	 */
	public String getParsedText(String textToParse) {
		String parsedText;
		parsedText = textToParse.replaceAll("<[^<>]+>", " ");
		return parsedText;
	}

}

