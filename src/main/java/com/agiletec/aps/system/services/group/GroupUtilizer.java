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
package com.agiletec.aps.system.services.group;

import java.util.List;

import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * Basic interface for those services whose handled elements are based on groups.
 * @author E.Santoboni
 */
public interface GroupUtilizer {
	
	/**
	 * Return the id of the utilizing service.
	 * @return The id of the utilizer.
	 */
	public String getName();
	
	/**
	 * Return the list of the objects members of the group identified by the given name.
	 * @param groupName The name of the group
	 * @return The list of the objects members of the given group.
	 * @throws ApsSystemException In case of error
	 */
	public List getGroupUtilizers(String groupName) throws ApsSystemException;
	
}
