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
