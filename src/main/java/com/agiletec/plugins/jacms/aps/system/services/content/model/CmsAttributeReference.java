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
package com.agiletec.plugins.jacms.aps.system.services.content.model;

/**
 * Rappresentazione delle referenze interne di un'attributo specifico per il cms.
 * @author E.Santoboni
 */
public class CmsAttributeReference {
	
	public CmsAttributeReference(String page, String content, String resource) {
		this._refPage = page;
		this._refContent = content;
		this._refResource = resource;
	}
	
	public String getRefContent() {
		return _refContent;
	}
	public String getRefPage() {
		return _refPage;
	}
	public String getRefResource() {
		return _refResource;
	}
	
	private String _refPage;
	private String _refContent;
	private String _refResource;
	
}
