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
package org.entando.entando.apsadmin.portal.guifragment.helper;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.entando.entando.aps.system.services.guifragment.GuiFragment;

import com.agiletec.aps.system.exception.ApsSystemException;

public interface IGuiFragmentActionHelper {

	public Map<String, List<Object>> getReferencingObjects(GuiFragment fragment, HttpServletRequest request) throws ApsSystemException;
}
