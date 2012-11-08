/*
*
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* Entando is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.plugins.jacms.aps.tags;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.aps.util.ApsWebApplicationUtils;

import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.showlet.ContentListHelper;
import com.agiletec.plugins.jacms.aps.system.services.content.showlet.IContentListShowletHelper;
import com.agiletec.plugins.jacms.aps.system.services.content.showlet.IContentListTagBean;
import com.agiletec.plugins.jacms.aps.system.services.content.showlet.UserFilterOptionBean;

/**
 * Loads a list of contents IDs by applying the filters (if any).
 * @author E.Santoboni
 */
public class ContentListTag extends TagSupport implements IContentListTagBean {
	
	public ContentListTag() {
		super();
		this.release();
	}
	
	@Override
	public int doStartTag() throws JspException {
		return EVAL_BODY_INCLUDE;
	}
	
	@Override
	public int doEndTag() throws JspException {
		ServletRequest request =  this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) request.getAttribute(RequestContext.REQCTX);
		try {
			IContentListShowletHelper helper = (IContentListShowletHelper) ApsWebApplicationUtils.getBean(JacmsSystemConstants.CONTENT_LIST_HELPER, this.pageContext);
			List<UserFilterOptionBean> defaultUserFilterOptions = helper.getConfiguredUserFilters(this, reqCtx);
			this.addUserFilterOptions(defaultUserFilterOptions);
			this.extractExtraShowletParameters(reqCtx);
			if (null != this.getUserFilterOptions() && null != this.getUserFilterOptionsVar()) {
				this.pageContext.setAttribute(this.getUserFilterOptionsVar(), this.getUserFilterOptions());
			}
			List<String> contents = this.getContentsId(helper, reqCtx);
			this.pageContext.setAttribute(this.getListName(), contents);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "doEndTag");
			throw new JspException("Error detected while finalising the tag", t);
		}
		this.release();
		return EVAL_PAGE;
	}
	
	protected List<String> getContentsId(IContentListShowletHelper helper, RequestContext reqCtx) throws ApsSystemException {
		List<String> contents = null;
		try {
			contents = helper.getContentsId(this, reqCtx);
			Showlet currentShowlet = (Showlet) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_SHOWLET);
			Integer maxElements = null;
			if (null != currentShowlet.getConfig()) {
				ApsProperties properties = currentShowlet.getConfig();
				String maxElementsString = properties.getProperty("maxElements");
				try {
					maxElements = Integer.parseInt(maxElementsString);
				} catch (Exception e) {
					//nothing to catch
				}	
			}
			if (null != maxElements && contents != null && contents.size() > maxElements) {
				contents = contents.subList(0, maxElements);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getContentsId");
			throw new ApsSystemException("Error extracting content ids", t);
		}
		return contents;
	}
	
	private void extractExtraShowletParameters(RequestContext reqCtx) {
		try {
			Showlet showlet = (Showlet) reqCtx.getExtraParam((SystemConstants.EXTRAPAR_CURRENT_SHOWLET));
			ApsProperties config = showlet.getConfig();
			if (null != config) {
				Lang currentLang = (Lang) reqCtx.getExtraParam((SystemConstants.EXTRAPAR_CURRENT_LANG));
				this.addMultilanguageShowletParameter(config, IContentListShowletHelper.SHOWLET_PARAM_TITLE, currentLang, this.getTitleVar());
				this.addMultilanguageShowletParameter(config, IContentListShowletHelper.SHOWLET_PARAM_PAGE_LINK_DESCR, currentLang, this.getPageLinkDescriptionVar());
				if (null != this.getPageLinkVar()) {
					String pageLink = config.getProperty(ContentListHelper.SHOWLET_PARAM_PAGE_LINK);
					if (null != pageLink) {
						this.pageContext.setAttribute(this.getPageLinkVar(), pageLink);
					}
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "extractExtraShowletParameters", "Error extracting extra parameters");
		}
	}
	
	protected void addMultilanguageShowletParameter(ApsProperties config, String showletParamPrefix, Lang currentLang, String var) {
		if (null == var) return;
		String paramValue = config.getProperty(showletParamPrefix + "_" + currentLang.getCode());
		if (null == paramValue) {
			ILangManager langManager = (ILangManager) ApsWebApplicationUtils.getBean(SystemConstants.LANGUAGE_MANAGER, this.pageContext);
			Lang defaultLang = langManager.getDefaultLang();
			paramValue = config.getProperty(showletParamPrefix + "_" + defaultLang.getCode());
		}
		if (null != paramValue) {
			this.pageContext.setAttribute(var, paramValue);
		}
	}
	
	@Override
	public void release() {
		this._listName = null;
		this._contentType = null;
		this._categories = new String[0];
		this._filters = new EntitySearchFilter[0];
		this._listEvaluated = false;
		this._cacheable = true;
		this.setUserFilterOptions(null);
		this.setTitleVar(null);
		this.setPageLinkVar(null);
		this.setPageLinkDescriptionVar(null);
		this.setUserFilterOptionsVar(null);
	}
	
	@Override
	public void addFilter(EntitySearchFilter filter) {
		int len = this._filters.length;
		EntitySearchFilter[] newFilters = new EntitySearchFilter[len + 1];
		for(int i=0; i < len; i++){
			newFilters[i] = this._filters[i];
		}
		newFilters[len] = filter;
		this._filters = newFilters;
	}
	
	private void addUserFilterOptions(List<UserFilterOptionBean> userFilterOptions) {
		if (null == userFilterOptions) return;
		for (int i = 0; i < userFilterOptions.size(); i++) {
			this.addUserFilterOption(userFilterOptions.get(i));
		}
	}
	
	@Override
	public void addUserFilterOption(UserFilterOptionBean userFilterOption) {
		if (null == userFilterOption) return;
		if (null == this.getUserFilterOptions()) {
			this.setUserFilterOptions(new ArrayList<UserFilterOptionBean>());
		}
		this.getUserFilterOptions().add(userFilterOption);
	}
	
	@Override
	public EntitySearchFilter[] getFilters() {
		return this._filters;
	}
	
	/**
	 * Get the name of the variable in the page context that holds the list of the IDs found.
	 * @return Returns the name of the list.
	 */
	@Override
	public String getListName() {
		return _listName;
	}

	/**
	 * Set the name of the variable in the page context that holds the list of the IDs found.
	 * @param listName The listName to set.
	 */
	public void setListName(String listName) {
		this._listName = listName;
	}

	/**
	 * Get the code of the content types to search.
	 * @return The code of the content type.
	 */
	@Override
	public String getContentType() {
		return _contentType;
	}
	
	/**
	 * Set the code of the content types to search.
	 * @param contentType The code of the content type.
	 */
	@Override
	public void setContentType(String contentType) {
		this._contentType = contentType;
	}
	
	/**
	 * Return the identifier string of the category of the Content to search.
	 * @return The category code.
	 */
	@Override
	@Deprecated
	public String getCategory() {
		return null;
	}
	
	/**
	 * Set the identifier string of the category of the Content to search.
	 * @param category The category code.
	 */
	@Override
	public void setCategory(String category) {
		this.addCategory(category);
	}
	
	@Override
	public String[] getCategories() {
		return this._categories;
	}
	
	@Override
	public void addCategory(String category) {
		if (null == category) return;
		int len = this._categories.length;
		String[] newCategories = new String[len + 1];
		for(int i=0; i < len; i++){
			newCategories[i] = this._categories[i];
		}
		newCategories[len] = category;
		this._categories = newCategories;
	}

	/**
	 * Checks if the list if the list has been previously stored in the startTag method.
	 * @return true if the list wad evalued into start tag
	 * @deprecated the startTag method isn't extended
	 */
	protected boolean isListEvaluated() {
		return _listEvaluated;
	}
	
	/**
	 * Set if the list if the list has been previously stored in the startTag method.
	 * @param listEvaluated true if the list wad evalued into start tag
	 * @deprecated the startTag method isn't extended
	 */
	protected void setListEvaluated(boolean listEvaluated) {
		this._listEvaluated = listEvaluated;
	}
	
	/**
	 * Return true if the system caching must involved in the search process.
	 */
	@Override
	public boolean isCacheable() {
		return _cacheable;
	}
	
	/**
	 * Toggles the system caching usage when retrieving the list.
	 * Admitted values (true|false), default "true".
	 * @param cacheable
	 */
	public void setCacheable(boolean cacheable) {
		this._cacheable = cacheable;
	}
	
	@Override
	public List<UserFilterOptionBean> getUserFilterOptions() {
		return _userFilterOptions;
	}
	protected void setUserFilterOptions(List<UserFilterOptionBean> userFilterOptions) {
		this._userFilterOptions = userFilterOptions;
	}
	
	public String getTitleVar() {
		return _titleVar;
	}
	public void setTitleVar(String titleVar) {
		this._titleVar = titleVar;
	}
	
	public String getPageLinkVar() {
		return _pageLinkVar;
	}
	public void setPageLinkVar(String pageLinkVar) {
		this._pageLinkVar = pageLinkVar;
	}
	
	public String getPageLinkDescriptionVar() {
		return _pageLinkDescriptionVar;
	}
	public void setPageLinkDescriptionVar(String pageLinkDescriptionVar) {
		this._pageLinkDescriptionVar = pageLinkDescriptionVar;
	}
	
	public String getUserFilterOptionsVar() {
		return _userFilterOptionsVar;
	}
	public void setUserFilterOptionsVar(String userFilterOptionsVar) {
		this._userFilterOptionsVar = userFilterOptionsVar;
	}
	
	private String _listName;
	private String _contentType;
	private String[] _categories = new String[0];
	private EntitySearchFilter[] _filters = new EntitySearchFilter[0];
	
	private boolean _cacheable = true;
	
	private List<UserFilterOptionBean> _userFilterOptions;
	
	@Deprecated
	private boolean _listEvaluated;
	
	private String _titleVar;
	private String _pageLinkVar;
	private String _pageLinkDescriptionVar;
	
	private String _userFilterOptionsVar;
	
}