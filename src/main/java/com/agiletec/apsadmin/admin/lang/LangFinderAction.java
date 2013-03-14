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
package com.agiletec.apsadmin.admin.lang;

import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * This action class implements the default actions to display and search among the system languages.
 * @author E.Santoboni
 */
public class LangFinderAction extends BaseAction implements ILangFinderAction {
	
	public List<Lang> getLangs() {
		List<Lang> langs = null;
		try {
			langs = this.getLangManager().getLangs();
		} catch(Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getLangs");
			throw new RuntimeException("Error extracting system lang", t);
		}
		return langs;
	}
	
	/**
	 * Return the list of available languages for the system, sorted by description. 
	 * @return The list of available languages.
	 */
	public List<Lang> getAssignableLangs() {
		List<Lang> assignableLangs = null;
		try {
			assignableLangs = this.getLangManager().getAssignableLangs();
		} catch(Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getAssignableLangs");
			throw new RuntimeException("Error extracting assignable langs", t);
		}
		return assignableLangs;
	}
	
}