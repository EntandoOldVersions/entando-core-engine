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
public final class HookPointElementContainer {
	
	public String getHookPointKey() {
		return _hookPointKey;
	}
	public void setHookPointKey(String hookPointKey) {
		this._hookPointKey = hookPointKey;
	}
	
	public int getPriority() {
		return _priority;
	}
	public void setPriority(int priority) {
		this._priority = priority;
	}
	
	public String getFilePath() {
		return _filePath;
	}
	public void setFilePath(String filePath) {
		this._filePath = filePath;
	}
	
	private String _hookPointKey;
	private int _priority;
	private String _filePath;
	
}