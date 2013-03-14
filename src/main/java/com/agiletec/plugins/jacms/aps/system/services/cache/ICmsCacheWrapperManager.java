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
package com.agiletec.plugins.jacms.aps.system.services.cache;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.cache.ICacheManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;

/**
 * Cache Wrapper Manager for plugin jacms
 * @author E.Santoboni
 */
public interface ICmsCacheWrapperManager extends ICacheManager {
	
	/**
	 * Return a public content by id using the system cache.
	 * @param id The id of the public content to return.
	 * @return The content, if exist.
	 * @throws ApsSystemException In case of error.
	 */
	public Content getPublicContent(String id) throws ApsSystemException;
	
}