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
package com.agiletec.aps.system.common.entity.event;

import com.agiletec.aps.system.common.IManager;
import com.agiletec.aps.system.common.notify.ApsEvent;

/**
 * Event request that triggers the reload of the entity reference.
 * @author E.Santoboni
 */
public class ReloadingEntitiesReferencesEvent extends ApsEvent {
	
	/**
	 * Return the interface of the observer needed to reload entitie reverences
	 * @return Class The observer interface
	 */
	public Class getObserverInterface() {
		return ReloadingEntitiesReferencesObserver.class;
	}
	
	@Override
	public void notify(IManager srv) {
		((ReloadingEntitiesReferencesObserver) srv).reloadEntitiesReferences(this);
	}
	
}