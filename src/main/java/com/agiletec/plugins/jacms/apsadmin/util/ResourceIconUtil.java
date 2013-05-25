/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
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
package com.agiletec.plugins.jacms.apsadmin.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author E.Santoboni
 */
public class ResourceIconUtil {
	
	public String getIconFile(String fileName) {
		String extension = fileName.substring(fileName.lastIndexOf('.')+1).trim();
		if (null != extension && extension.length()>0) {
			Iterator<String> iter = this.getResourceTypesIconFiles().keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				List<String> extensions = Arrays.asList(key.split(","));
				if (extensions.contains(extension)) {
					return this.getResourceTypesIconFiles().get(key);
				}
			}
		}
		return this.getDefaultResourceIcon();
	}
	
	protected Map<String, String> getResourceTypesIconFiles() {
		return _resourceTypesIconFiles;
	}
	public void setResourceTypesIconFiles(Map<String, String> resourceTypesIconFiles) {
		this._resourceTypesIconFiles = resourceTypesIconFiles;
	}
	
	protected String getDefaultResourceIcon() {
		return _defaultResourceIcon;
	}
	public void setDefaultResourceIcon(String defaultResourceIcon) {
		this._defaultResourceIcon = defaultResourceIcon;
	}
	
	private Map<String, String> _resourceTypesIconFiles;
	private String _defaultResourceIcon;
	
}