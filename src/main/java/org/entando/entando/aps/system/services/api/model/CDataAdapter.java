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
package org.entando.entando.aps.system.services.api.model;

/**
 * @author Frédéric Barmes - E.Santoboni
 */
public class CDataAdapter  {
	
	/**
	 * Check whether a string is a CDATA string
	 * @param s the string to check
	 * @return
	 */
	public static boolean isCdata(String s) {
		s = s.trim();
		return (s.startsWith(CDATA_START) && s.endsWith(CDATA_STOP));
	}
	
	/**
	 * Parse a CDATA String.
	 * If is a CDATA, removes leading and trailing string
	 * Otherwise does nothing
	 * @param s the string to parse
	 * @return the parsed string
	 */
	public static String parse(String s)  {
		if (isCdata(s)) {
			StringBuilder sb = new StringBuilder(s.trim());
			sb.replace(0, CDATA_START.length(), "");
			int stopIndex = sb.lastIndexOf(CDATA_STOP);
			sb.replace(stopIndex, (stopIndex + CDATA_STOP.length()),"");
			s = sb.toString();
		}
		return s;
	}
	
	/**
	 * Add CDATA leading and trailing to a string if not already a CDATA
	 * @param string
	 * @return
	 */
	public static String print(String string) {
		if (isCdata(string)) {
			return string;
		} else {
			if (null != string && string.trim().length() > 0) {
				return CDATA_START + string + CDATA_STOP;
			}
			return string;
		}
	}
	
	private static final String CDATA_START = "<![CDATA[";
	private static final String CDATA_STOP = "]]>";
	
}