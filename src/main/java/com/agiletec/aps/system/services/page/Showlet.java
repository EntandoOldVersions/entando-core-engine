/*
*
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* Entando is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.aps.system.services.page;

import java.io.Serializable;

import com.agiletec.aps.system.services.showlettype.ShowletType;
import com.agiletec.aps.util.ApsProperties;

/**
 * This class represent an instance of a showlet configured in a page frame. 
 * @author
 */
public class Showlet implements Serializable {

	/**
	 * Return the configuration of the showlet
	 * @return The configuration properties
	 */
	public ApsProperties getConfig() {
		if (null == this._config && null != this.getType()) {
			return this.getType().getConfig();
		}
		return _config;
	}

	/**
	 * Set the configuration of the showlet.
	 * @param config The configuration properties to set
	 */
	public void setConfig(ApsProperties config) {
		this._config = config;
	}

	/**
	 * Return the type of the showlet
	 * @param The type of the showlet
	 */
	public ShowletType getType() {
		return _type;
	}

	/**
	 * Set the showlet type
	 * @param type The of the showlet 
	 */
	public void setType(ShowletType type) {
		this._type = type;
	}
	
	/**
     * Return the id of the content published in the showlet, if any.
     * @rtuen The id of the published content 
     */
    public String getPublishedContent() {
        return _publishedContent;
    }
    
    /**
     * Set the id of the content to publish in the current showlet.
     * @param publishedContent The id of the content to publish
     */
    public void setPublishedContent(String publishedContent) {
        this._publishedContent = publishedContent;
    }
	
	/**
	 * The type of the showlet
	 */
	private ShowletType _type;
	
	/**
	 * The configuration properties; the configuration may be null
	 */
	private ApsProperties _config;
	
	/**
	 * id of the content published in this showlet
	 */
	private String _publishedContent;
	
}
