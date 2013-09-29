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
package org.entando.entando.aps.system.services.actionlog.model;

/**
 * @author E.Santoboni
 */
public class ManagerConfiguration {
	
	public Boolean getCleanOldActivities() {
		return _cleanOldActivities;
	}
	public void setCleanOldActivities(Boolean cleanOldActivities) {
		this._cleanOldActivities = cleanOldActivities;
	}
	
	public Integer getMaxActivitySizeByGroup() {
		return _maxActivitySizeByGroup;
	}
	public void setMaxActivitySizeByGroup(Integer maxActivitySizeByGroup) {
		this._maxActivitySizeByGroup = maxActivitySizeByGroup;
	}
	
	private Boolean _cleanOldActivities = false;
	private Integer _maxActivitySizeByGroup = 10;
	
}