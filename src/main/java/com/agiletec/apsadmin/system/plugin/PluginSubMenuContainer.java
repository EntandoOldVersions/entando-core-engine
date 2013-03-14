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