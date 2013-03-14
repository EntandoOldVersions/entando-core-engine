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
package com.agiletec.aps.system.common.entity.event;

import com.agiletec.aps.system.common.notify.ObserverService;

/**
 * Interface for observers of events of Entity Types.
 * @author E.Santoboni
 */
public interface EntityTypesChangingObserver extends ObserverService {
	
	/**
	 * The method is invoked by the service observers.
	 * @param event The event of entity type changing.
	 */
	public void updateFromEntityTypesChanging(EntityTypesChangingEvent event);
	
}