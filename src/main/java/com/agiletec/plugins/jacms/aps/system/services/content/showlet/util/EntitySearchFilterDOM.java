/*
*
* Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
* This file is part of jAPS software.
* jAPS is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
*/
package com.agiletec.plugins.jacms.aps.system.services.content.showlet.util;

import java.util.List;
import java.util.Properties;

import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.helper.IContentListFilterBean;

/**
 * Provides utility methods for content filters.
 * @author E.Santoboni
 * @deprecated use {@link FilterUtils} methods
 */
public class EntitySearchFilterDOM {
	
	/**
	 * Return the showlet parameters in the form of property list
	 * @param showletParam The string to convert into a property list
	 * @return The property list.
	 * @deprecated Use {@link FilterUtils}
	 */
	public static List<Properties> getPropertiesFilters(String showletParam) {
		return FilterUtils.getFiltersProperties(showletParam);
	}
	
	@Deprecated
	public EntitySearchFilter[] getFilters(String contentType, String showletParam, IContentManager contentManager, String langCode) {
		FilterUtils filterUtils = new FilterUtils();
		return filterUtils.getFilters(contentType, showletParam, contentManager, langCode);
	}
	
	@Deprecated
	public EntitySearchFilter getFilter(String contentType, IContentListFilterBean bean, IContentManager contentManager, String langCode) {
		FilterUtils filterUtils = new FilterUtils();
		return filterUtils.getFilter(contentType, bean, contentManager, langCode);
	}
	
	@Deprecated
	public String getShowletParam(EntitySearchFilter[] filters) {
		FilterUtils filterUtils = new FilterUtils();
		return filterUtils.getFilterParam(filters);
	}
	
	@Deprecated
	public static String getShowletParam(List<Properties> properties) {
		return FilterUtils.getShowletParam(properties);
	}
	
}