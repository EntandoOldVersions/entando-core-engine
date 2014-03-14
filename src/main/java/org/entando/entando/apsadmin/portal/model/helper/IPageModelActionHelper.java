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
package org.entando.entando.apsadmin.portal.model.helper;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.pagemodel.PageModel;

/**
 * Helper interface for PageModel Actions.
 * @author E.Santoboni
 */
public interface IPageModelActionHelper {
	
	public Map<String, List<Object>> getReferencingObjects(PageModel pageModel, HttpServletRequest request) throws ApsSystemException;
	
}
