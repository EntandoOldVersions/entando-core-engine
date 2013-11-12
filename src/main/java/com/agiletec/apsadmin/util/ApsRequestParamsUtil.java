/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* Entando is a free software;
* You can redistribute it and/or modify it
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
package com.agiletec.apsadmin.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.ServletRequest;

/**
 * 
 * @version 1.0
 * @author E.Santoboni
 */
public class ApsRequestParamsUtil {
	
	public static String[] splitParam(String pname, String separator) {
        if (pname.endsWith(".x") || pname.endsWith(".y")) {
        	return pname.substring(0, pname.length()-2).split(separator);
        }
        return pname.split(separator);
	}
	
	public static String[] getApsParams(String paramPrefix, String separator, ServletRequest request) {
		String[] apsParams = null;
		Enumeration params = request.getParameterNames();
        while (params.hasMoreElements()) {
        	String pname = (String) params.nextElement();
        	if (pname.startsWith("action:")) {
        		pname = pname.substring("action:".length());
        	}
        	if (pname.startsWith(paramPrefix)) {
        		apsParams = splitParam(pname, separator);
        		break;
        	}
        }
        return apsParams;
	}
	
	public static String createApsActionParam(String action, Properties params) {
		StringBuffer buffer = new StringBuffer(action);
		if (params.size()>0) {
			buffer.append("?");
			Iterator keys = params.keySet().iterator();
			boolean first = true;
			while (keys.hasNext()) {
				String key = (String) keys.next();
				String value = params.getProperty(key);
				if (!first) buffer.append(";");
				buffer.append(key).append("=").append(value);
				first = false;
			}
		}
		return buffer.toString();
	}
	
}
