/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
