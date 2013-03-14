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

/**
 * A Menu Section Object
 * @author E.Santoboni
 */
public class MenuSection extends AbstractBaseBean {
	
	public MenuSection(String id) {
		super(id);
	}
	
	@Override
	public MenuSection clone() {
		MenuSection clone = new MenuSection(this.getId());
		clone.setDescription(this.getDescription());
		clone.setDescriptionKey(this.getDescriptionKey());
		clone.setLongDescription(this.getLongDescription());
		clone.setLongDescriptionKey(this.getLongDescriptionKey());
		return clone;
	}
	
}