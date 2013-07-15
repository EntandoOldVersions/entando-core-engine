/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software. 
* Entando is a free software; 
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
package com.agiletec.apsadmin.portal;

import java.util.List;

import org.entando.entando.aps.system.services.widgettype.WidgetType;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;

/**
 * @author E.Santoboni
 */
public class ShowletsViewerAction extends AbstractPortalAction implements IShowletsViewerAction {
	
	@Override
	public String viewShowlets() {
		return SUCCESS;
	}
	
	public List<IPage> getShowletUtilizers(String showletTypeCode) {
		List<IPage> utilizers = null;
		try {
			utilizers = this.getPageManager().getShowletUtilizers(showletTypeCode);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getShowletUtilizers");
			throw new RuntimeException("Error on extracting showletUtilizers : showlet type code " + showletTypeCode, t);
		}
		return utilizers;
	}
	
	public Group getGroup(String groupCode) {
		Group group = super.getGroupManager().getGroup(groupCode);
		if (null == group) {
			group = super.getGroupManager().getGroup(Group.FREE_GROUP_NAME);
		}
		return group;
	}
	
	@Override
	public String viewShowletUtilizers() {
		return SUCCESS;
	}
	
	public List<IPage> getShowletUtilizers() {
		return this.getShowletUtilizers(this.getShowletTypeCode());
	}
	
	public WidgetType getShowletType(String typeCode) {
		return this.getWidgetTypeManager().getShowletType(typeCode);
	}
	
	public String getShowletTypeCode() {
		return _showletTypeCode;
	}
	public void setShowletTypeCode(String showletTypeCode) {
		this._showletTypeCode = showletTypeCode;
	}
	
	private String _showletTypeCode;
	
}