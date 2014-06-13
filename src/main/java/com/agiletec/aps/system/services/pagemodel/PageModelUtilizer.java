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
package com.agiletec.aps.system.services.pagemodel;

import java.util.List;

import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * Base interface for the services whose elements can directly reference the page models
 * @author E.Santoboni
 */
public interface PageModelUtilizer {
	
	/**
	 * Return the id of the utilizer service.
	 * @return The id of the utilizer
	 */
	public String getName();
	
	/**
	 * Return the list of the objects which reference the given page model.
	 * @param pageModelCode The code of the page
	 * @return The list of the object referencing the given page model
	 * @throws ApsSystemException In case of error
	 */
	public List getPageModelUtilizers(String pageModelCode) throws ApsSystemException;
	
}
