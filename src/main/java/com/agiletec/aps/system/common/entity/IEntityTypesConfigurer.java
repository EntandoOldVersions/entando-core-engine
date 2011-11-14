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
package com.agiletec.aps.system.common.entity;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * @author E.Santoboni
 */
public interface IEntityTypesConfigurer {
	
	/**
	 * Add a new entity prototype on the catalog.
	 * @param entityType The entity type to add.
	 * @throws ApsSystemException In case of error.
	 */
	public void addEntityPrototype(IApsEntity entityType) throws ApsSystemException;
	
	/**
	 * Update an entity prototype on the catalog.
	 * @param entityType The entity type to update
	 * @throws ApsSystemException In case of error.
	 */
	public void updateEntityPrototype(IApsEntity entityType) throws ApsSystemException;
	
	/**
	 * Remove an entity type from the catalog.
	 * @param entityTypeCode The code of the entity type to remove.
	 * @throws ApsSystemException In case of error.
	 */
	public void removeEntityPrototype(String entityTypeCode) throws ApsSystemException;
	
}
