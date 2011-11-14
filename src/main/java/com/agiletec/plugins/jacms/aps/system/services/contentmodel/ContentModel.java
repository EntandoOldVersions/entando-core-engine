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
package com.agiletec.plugins.jacms.aps.system.services.contentmodel;

import java.io.Serializable;

/**
 * Rappresenta un modello di contenuto. 
 * L'attributo contentShape rappresenta come il contenuto è formattato (il template di velocity).
 * @author 
 */
public class ContentModel implements Comparable, Serializable {
	
	/**
	 * Restituisce l'identificativo del modello.
	 * @return L'identificativo del modello.
	 */
	public long getId() {
		return _id;
	}

	/**
	 * Setta l'identificativo del modello.
	 * @param id L'identificativo del modello.
	 */
	public void setId(long id) {
		this._id = id;
	}
	
	/**
	 * Restituisce il tipo di contenuto a cui si applica il modello.
	 * @return Il tipo di contenuto a cui si applica il modello.
	 */
	public String getContentType() {
		return _contentType;
	}

	/**
	 * Setta il tipo di contenuto.
	 * @param contentType Il tipo di contenuto da settare
	 */
	public void setContentType(String contentType) {
	    _contentType = contentType;
	}
	
	/**
	 * Restituisce la descrizione del modello.
	 * @return La descrizione del modello.
	 */
	public String getDescription() {
		return _description;
	}

	/**
	 * Setta la descrizione del modello.
	 * @param descr La descrizione del modello.
	 */
	public void setDescription(String descr) {
		this._description = descr;
	}
	
	/**
	 * @return Returns the _contentShape.
	 */
	public String getContentShape() {
		return _contentShape;
	}
	
	/**
	 * @param shape The _contentShape to set.
	 */
	public void setContentShape(String shape) {
		_contentShape = shape;
	}

	/**
	 * Restituisce il nome del foglio di stile particolare per questo modello.
	 * @return Il nome del foglio di stile. Può essere null.
	 */
	public String getStylesheet() {
		return _stylesheet;
	}

	/**
	 * Imposta il nome del foglio di stile particolare per questo modello.
	 * @param stylesheet Il nome del foglio di stile
	 */
	public void setStylesheet(String stylesheet) {
		this._stylesheet = stylesheet;
	}
	
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object model) {
		int result = this.getContentType().compareTo(((ContentModel) model).getContentType());
		if (result == 0) {
			if (this.getId()>(((ContentModel) model).getId())) {
				return 1;
			} else return -1;
		}
		return result;
	}
	
	private long _id;
	private String _contentType;
	private String _description;
	private String _contentShape;
	private String _stylesheet;
	
}
