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
package com.agiletec.aps.tags;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Search and return the page (or the list of pages) with the given widget type.
 */
public class PageWithWidgetTag extends TagSupport {

	@Override
	public int doStartTag() throws JspException {
		IPageManager pageManager = (IPageManager) ApsWebApplicationUtils.getBean(SystemConstants.PAGE_MANAGER, this.pageContext); 
		try {
			List<IPage> pages = pageManager.getShowletUtilizers(this.getWidgetTypeCode());
			if (StringUtils.isNotBlank(this.getFilterParamName()) && StringUtils.isNotBlank(this.getFilterParamValue())) {
				pages = this.filterByConfigParamValue(pages);
			}
			if (this.isListResult()) {
				this.pageContext.setAttribute(this.getVar(), pages);
			} else if (null != pages && pages.size() > 0) {
				this.pageContext.setAttribute(this.getVar(), pages.get(0));
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "doStartTag");
			throw new JspException("Errore tag", t);
		}
		return super.doStartTag();
	}

	protected List<IPage> filterByConfigParamValue(List<IPage> pages) {
		List<IPage> filteredPages = new ArrayList<IPage>();
		Iterator<IPage> it = pages.iterator();
		while (it.hasNext()) {
			IPage currentPage = it.next();
			Showlet[] showlets = currentPage.getShowlets();
			for (int i = 0; i < showlets.length; i++) {
				Showlet currentWidget = showlets[i];
				if (null != currentWidget && currentWidget.getType().getCode().equals(this.getWidgetTypeCode())) {
					ApsProperties config = currentWidget.getConfig();
					if (null != config) {
						String value = config.getProperty(this.getFilterParamName());
						if (StringUtils.isNotBlank(value) && value.equals(this.getFilterParamValue())) {
							filteredPages.add(currentPage);
						}
					}
				}
			}
		}
		return filteredPages;
	}

	@Override
	public void release() {
		super.release();
		this.setWidgetTypeCode(null);
		this.setVar(null);
		this.setListResult(false);
		this.setFilterParamValue(null);
		this.setFilterParamName(null);
	}

	public String getVar() {
		return _var;
	}
	public void setVar(String var) {
		this._var = var;
	}

	public boolean isListResult() {
		return _listResult;
	}
	public void setListResult(boolean listResult) {
		this._listResult = listResult;
	}

	public String getFilterParamName() {
		return _filterParamName;
	}
	public void setFilterParamName(String filterParamName) {
		this._filterParamName = filterParamName;
	}

	public String getFilterParamValue() {
		return _filterParamValue;
	}
	public void setFilterParamValue(String filterParamValue) {
		this._filterParamValue = filterParamValue;
	}

	public String getWidgetTypeCode() {
		return _widgetTypeCode;
	}
	public void setWidgetTypeCode(String widgetTypeCode) {
		this._widgetTypeCode = widgetTypeCode;
	}

	private String _widgetTypeCode;
	private String _var;
	private boolean _listResult;
	private String _filterParamName;
	private String _filterParamValue;

}