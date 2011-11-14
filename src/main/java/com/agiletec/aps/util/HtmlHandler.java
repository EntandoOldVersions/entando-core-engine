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

