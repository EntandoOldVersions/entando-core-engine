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
package com.agiletec.aps.system.common.entity.loader;

import com.agiletec.aps.system.common.entity.IEntityManager;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;

/**
 * The Wrapper Class of the extra attribute.
 * @author E.Santoboni
 */
public class ExtraAttributeWrapper {
	
	public AttributeInterface getAttribute() {
		return _attribute;
	}
	public void setAttribute(AttributeInterface attribute) {
		this._attribute = attribute;
	}
	
	protected IEntityManager getEntityManagerDest() {
		return _entityManagerDest;
	}
	public void setEntityManagerDest(IEntityManager entityManagerDest) {
		this._entityManagerDest = entityManagerDest;
	}
	
	private AttributeInterface _attribute;
	private IEntityManager _entityManagerDest;
	
}
