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
package com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute;

import java.io.Serializable;

/**
 * @author E.Santoboni
 */
public class JAXBResourceValue implements Serializable {
	
	public String getText() {
		return _text;
	}
	public void setText(String text) {
		this._text = text;
	}
	
	public String getPath() {
		return _path;
	}
	public void setPath(String path) {
		this._path = path;
	}
	
	private String _text;
	private String _path;
	
}