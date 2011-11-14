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
package com.agiletec.plugins.jacms.aps.system.services.renderer;

import com.agiletec.aps.system.common.renderer.EntityWrapper;
import com.agiletec.aps.util.DateConverter;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.SymbolicLink;

/**
 * Rappresenta un contenuto nella forma utilizzabile al servizio di renderizzazione. 
 * La classe estende HashMap per un agevole accesso agli attributi che
 * popolano il contenuto.
 * @author
 */
public class ContentWrapper extends EntityWrapper {
	
	/**
	 * Inizializzazione del Wrapper. 
	 * @param content Il contenuto da utilizzare 
	 * dal servizio di renderizzazione. 
	 */
	public ContentWrapper(Content content) {
		super(content);
	}
	
	/**
	 * Restituisce un URL simbolico che punta al contenuto stesso (link di 
	 * tipo SymbolicLink.CONTENT_TYPE).
	 * @return Un URL simbolico da utilizzare come href in un tag &lt;a&gt;
	 */
	public String getContentLink() {
		SymbolicLink link = new SymbolicLink();
		link.setDestinationToContent(this.getId());
		return link.getSymbolicDestination();
	}
	
	/**
	 * Restituisce un URL simbolico che punta al contenuto stesso su una pagina specficata 
	 * (link di tipo SymbolicLink.CONTENT_ON_PAGE_TYPE).
	 * @param pageCode Il codice della pagina su cui visualizzare il contenuto.
	 * @return Un URL simbolico da utilizzare come href in un tag &lt;a&gt;
	 */
	public String getContentOnPageLink(String pageCode) {
		SymbolicLink symbLink = new SymbolicLink();
		symbLink.setDestinationToContentOnPage(this.getId(), pageCode);
		return symbLink.getSymbolicDestination();
	}
	
	public String getCreated(String pattern) {
		Content content = (Content) super.getEntity();
		if (null != content.getCreated()) {
			return DateConverter.getFormattedDate(content.getCreated(), pattern, this.getRenderingLang());
		}
		return null;
	}
	
	public String getLastModified(String pattern) {
		Content content = (Content) super.getEntity();
		if (null != content.getLastModified()) {
			return DateConverter.getFormattedDate(content.getLastModified(), pattern, this.getRenderingLang());
		}
		return null;
	}
	
	public String getVersion() {
		return ((Content) super.getEntity()).getVersion();
	}
	
	public String getLastEditor() {
		return ((Content) super.getEntity()).getLastEditor();
	}
	
}