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
package org.entando.entando.plugins.jacms.aps.system.services.api.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.plugins.jacms.aps.system.services.content.helper.BaseFilterUtils;
import com.agiletec.plugins.jacms.aps.system.services.content.helper.IContentListBean;

/**
 * @author E.Santoboni
 */
public class ApiContentListBean implements IContentListBean {
	
	public ApiContentListBean(String contentType, EntitySearchFilter[] filters, String[] categories) {
		this.setContentType(contentType);
		this.setCategories(categories);
		this.setFilters(filters);
	}
	
	@Override
	public String getListName() {
		StringBuffer buffer = new StringBuffer("listName_api");
		buffer.append("-TYPE:" + this.getContentType());
		buffer.append("_FILTERS:");
		if (null != this.getFilters() && this.getFilters().length > 0) {
			BaseFilterUtils filterUtils = new BaseFilterUtils();
			buffer.append(filterUtils.getFilterParam(this.getFilters()));
		} else {
			buffer.append("NULL");
		}
		buffer.append("_CATEGORIES:");
		if (null != this.getCategories() && this.getCategories().length > 0) {
			List<String> categories = Arrays.asList(this.getCategories());
			Collections.sort(categories);
			for (int i = 0; i < categories.size(); i++) {
				if (i>0) buffer.append("+");
				buffer.append(categories.get(i));
			}
		} else {
			buffer.append("NULL");
		}
		return buffer.toString();
	}
	
	@Override
	public String getContentType() {
		return _contentType;
	}
	protected void setContentType(String contentType) {
		this._contentType = contentType;
	}
	
	@Override
	public String[] getCategories() {
		return this._categories;
	}
	protected void setCategories(String[] categories) {
		this._categories = categories;
	}
	
	@Override
	public EntitySearchFilter[] getFilters() {
		return this._filters;
	}
	protected void setFilters(EntitySearchFilter[] filters) {
		this._filters = filters;
	}
	
	@Override
	public boolean isCacheable() {
		return true;
	}
	
	private String _contentType;
	private EntitySearchFilter[] _filters;
	private String[] _categories;
	
}