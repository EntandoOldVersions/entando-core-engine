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
package com.agiletec.plugins.jacms.aps.system.services.resource.event;

import com.agiletec.aps.system.common.notify.ObserverService;

/**
 * Interface of the observer services of resource changing events
 * @author E.Santoboni - M.Diana
 */
public interface ResourceChangedObserver extends ObserverService {
	
	/**
	 * Refresh the service by a changing resource event.
	 * @param event The event
	 */
	public void updateFromResourceChanged(ResourceChangedEvent event);
	
}
