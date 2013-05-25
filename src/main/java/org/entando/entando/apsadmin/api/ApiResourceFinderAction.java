/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* You can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package org.entando.entando.apsadmin.api;

import org.entando.entando.aps.system.services.api.model.ApiResource;

/**
 * @author E.Santoboni
 */
public class ApiResourceFinderAction extends AbstractApiFinderAction {
    
	protected boolean includeIntoMapping(ApiResource apiResource) {
		return true;
	}
	
}
