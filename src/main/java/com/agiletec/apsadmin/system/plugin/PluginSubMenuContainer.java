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
package com.agiletec.apsadmin.system.plugin;

/**
 * @author E.Santoboni
 */
public final class PluginSubMenuContainer {
	
	public String getSubMenuFilePath() {
		return _subMenuFilePath;
	}
	public void setSubMenuFilePath(String subMenuFilePath) {
		this._subMenuFilePath = subMenuFilePath;
	}
	
	public int getPriority() {
		return _priority;
	}
	public void setPriority(int priority) {
		this._priority = priority;
	}
	
	private String _subMenuFilePath;
	private int _priority;
	
}