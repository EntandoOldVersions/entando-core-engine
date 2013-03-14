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
package com.agiletec.apsadmin.system.services.shortcut.model;

import java.io.Serializable;

/**
 * @author E.Santoboni
 */
public abstract class AbstractBaseBean implements Serializable {
	
	public AbstractBaseBean(String id) {
		this.setId(id);
	}
	
	public String getId() {
		return _id;
	}
	protected void setId(String id) {
		this._id = id;
	}
	
	public String getDescriptionKey() {
		return _descriptionKey;
	}
	public void setDescriptionKey(String descriptionKey) {
		this._descriptionKey = descriptionKey;
	}
	
	public String getDescription() {
		return _description;
	}
	public void setDescription(String description) {
		this._description = description;
	}
	
	public String getLongDescription() {
		return _longDescription;
	}
	public void setLongDescription(String longDescription) {
		this._longDescription = longDescription;
	}
	
	public String getLongDescriptionKey() {
		return _longDescriptionKey;
	}
	public void setLongDescriptionKey(String longDescriptionKey) {
		this._longDescriptionKey = longDescriptionKey;
	}
	
	private String _id;
	private String _descriptionKey;
	private String _description;
	
	private String _longDescription;
	private String _longDescriptionKey;
	
}