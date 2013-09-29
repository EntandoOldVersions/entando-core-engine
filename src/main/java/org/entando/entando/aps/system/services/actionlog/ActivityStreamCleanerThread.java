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
package org.entando.entando.aps.system.services.actionlog;

import com.agiletec.aps.system.ApsSystemUtils;

/**
 * @author E.Santoboni
 */
public class ActivityStreamCleanerThread extends Thread {
	
	public ActivityStreamCleanerThread(Integer maxActivitySizeByGroup, 
			IActionLogDAO actionLogDAO) {
		this._maxActivitySizeByGroup = maxActivitySizeByGroup;
		this._actionLogDAO = actionLogDAO;
	}
	
	@Override
	public void run() {
		try {
			this._actionLogDAO.cleanOldActivityStreamLogs(this._maxActivitySizeByGroup);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "run");
		}
	}
	
	private Integer _maxActivitySizeByGroup;
	private IActionLogDAO _actionLogDAO;
	
}