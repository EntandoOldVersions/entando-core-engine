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
package org.entando.entando.apsadmin.tags;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.apsadmin.tags.*;
import com.agiletec.aps.util.ApsWebApplicationUtils;

import org.entando.entando.aps.system.services.actionlogger.IActionLoggerManager;

/**
 * Returns a single record of Action Logger Manager (or one of its property) through the code.
 * You can choose whether to return the entire object (leaving the attribute "property" empty) or a single property.
 * The names of the available property of "ActivityStream": "id", "actionDate", "username", "namespace", "actionName", "parameters", "activityStreamInfo".
 * @author E.Santoboni
 */
public class ActionLogRecordTag extends AbstractObjectInfoTag {
	
	@Override
	protected Object getMasterObject(String keyValue) throws Throwable {
		IActionLoggerManager loggerManager = (IActionLoggerManager) ApsWebApplicationUtils.getBean(SystemConstants.ACTION_LOGGER_MANAGER, this.pageContext);
		return loggerManager.getActionRecord(Integer.parseInt(keyValue));
	}
	
}