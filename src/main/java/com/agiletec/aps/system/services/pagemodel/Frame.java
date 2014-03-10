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
package com.agiletec.aps.system.services.pagemodel;

import com.agiletec.aps.system.services.page.Widget;

/**
 * Representation of a frame of page model
 * @author E.Santoboni
 */
public class Frame {
	
	public int getPos() {
		return _pos;
	}
	public void setPos(int pos) {
		this._pos = pos;
	}
	
	public String getDescription() {
		return _description;
	}
	public void setDescription(String description) {
		this._description = description;
	}
	
	public boolean isMainFrame() {
		return _mainFrame;
	}
	public void setMainFrame(boolean mainFrame) {
		this._mainFrame = mainFrame;
	}
	
	public Widget getDefaultWidget() {
		return _defaultWidget;
	}
	public void setDefaultWidget(Widget defaultWidget) {
		this._defaultWidget = defaultWidget;
	}
	
	private int _pos;
	private String _description;
	private boolean _mainFrame;
	private Widget _defaultWidget;
	
}