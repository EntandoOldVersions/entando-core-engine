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
package com.agiletec.plugins.jacms.aps.system.services.content.showlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.ListUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.searchengine.ISearchEngineManager;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.helper.BaseContentListHelper;
import com.agiletec.plugins.jacms.aps.system.services.content.helper.IContentListFilterBean;
import com.agiletec.plugins.jacms.aps.system.services.content.showlet.util.FilterUtils;

/**
 * Classe helper per la showlet di erogazione contenuti in lista.
 * @author E.Santoboni
 */
public class ContentListHelper extends BaseContentListHelper implements IContentListHelper {
	
	@Override
	public EntitySearchFilter[] getFilters(String contentType, String filtersShowletParam, RequestContext reqCtx) {
		Lang currentLang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
		return super.getFilters(contentType, filtersShowletParam, currentLang.getCode());
	}
	
	@Override
	public EntitySearchFilter getFilter(String contentType, IContentListFilterBean bean, RequestContext reqCtx) {
		Lang currentLang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
		return super.getFilter(contentType, bean, currentLang.getCode());
	}
	
	@Override
	public UserFilterOptionBean getUserFilterOption(String contentType, IContentListFilterBean bean, RequestContext reqCtx) {
		FilterUtils dom = new FilterUtils();
		return dom.getUserFilter(contentType, bean, this.getContentManager(), reqCtx);
	}
	
	@Override
	@Deprecated
	public String getShowletParam(EntitySearchFilter[] filters) {
		return super.getFilterParam(filters);
	}
	
	@Override
	public List<String> getContentsId(IContentListTagBean bean, RequestContext reqCtx) throws Throwable {
		List<String> contentsId = null;
		try {
			List<UserFilterOptionBean> userFilterOptions = bean.getUserFilterOptions();
			UserFilterOptionBean fullTextUserFilter = null;
			boolean isUserFilterExecuted = false;
			if (null != userFilterOptions) {
				for (int i = 0; i < userFilterOptions.size(); i++) {
					UserFilterOptionBean userFilter = userFilterOptions.get(i);
					if (null != userFilter.getFormFieldValues() && userFilter.getFormFieldValues().size() > 0) {
						if (userFilter.isAttributeFilter() || 
								(!userFilter.isAttributeFilter() 
										&& !userFilter.getKey().equals(UserFilterOptionBean.KEY_FULLTEXT))) {
							//if executed full-text search filter... it's not important here
							isUserFilterExecuted = true;
						} else if (!userFilter.isAttributeFilter() 
										&& userFilter.getKey().equals(UserFilterOptionBean.KEY_FULLTEXT)) {
							fullTextUserFilter = userFilter;
						}
					}
				}
			}
			if (!isUserFilterExecuted) {
				contentsId = this.searchInCache(bean.getListName(), reqCtx);
			}
			if (null == contentsId) {
				contentsId = this.extractContentsId(bean, userFilterOptions, reqCtx, isUserFilterExecuted);
			}
			contentsId = this.executeFullTextSearch(reqCtx, contentsId, fullTextUserFilter);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getContentsId");
			throw new ApsSystemException("Error extracting contents id", t);
		}
		return contentsId;
	}
	
	protected List<String> extractContentsId(IContentListTagBean bean, 
			List<UserFilterOptionBean> userFilters, RequestContext reqCtx, boolean isUserFilterExecuted) throws ApsSystemException {
		List<String> contentsId = null;
		try {
			Showlet showlet = (Showlet) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_SHOWLET);
			ApsProperties config = showlet.getConfig();
			if (null == bean.getContentType() && null != config) {
				bean.setContentType(config.getProperty(SHOWLET_PARAM_CONTENT_TYPE));
			}
			if (null == bean.getContentType()) {
				throw new ApsSystemException("Tipo contenuto non definito");
			}
			if (null == bean.getCategory() && null != config && null != config.getProperty(SHOWLET_PARAM_CATEGORY)) {
				bean.setCategory(config.getProperty(SHOWLET_PARAM_CATEGORY));
			}
			this.addShowletFilters(bean, config, SHOWLET_PARAM_FILTERS, reqCtx);
			if (null != userFilters && userFilters.size() > 0) {
				for (int i = 0; i < userFilters.size(); i++) {
					UserFilterOptionBean userFilter = userFilters.get(i);
					EntitySearchFilter filter = userFilter.getEntityFilter();
					if (null != filter) bean.addFilter(filter);
				}
			}
			String[] categories = this.getCategories(bean.getCategories(), config, userFilters);
			Collection<String> userGroupCodes = this.getAllowedGroups(reqCtx);
			contentsId = this.getContentManager().loadPublicContentsId(bean.getContentType(), categories, bean.getFilters(), userGroupCodes);
			if (!isUserFilterExecuted && bean.isCacheable()) {
				String cacheKey = this.buildCacheKey(bean.getListName(), userGroupCodes, reqCtx);
				this.putListInCache(bean.getContentType(), reqCtx, contentsId, cacheKey);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "extractContentsId");
			throw new ApsSystemException("Error extracting contents id", t);
		}
		return contentsId;
	}
	
	protected List<String> executeFullTextSearch(RequestContext reqCtx, 
			List<String> masterContentsId, UserFilterOptionBean fullTextUserFilter) throws ApsSystemException {
		if (fullTextUserFilter != null && null != fullTextUserFilter.getFormFieldValues()) {
			String word = fullTextUserFilter.getFormFieldValues().get(fullTextUserFilter.getFormFieldNames()[0]);
			Lang currentLang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
			List<String> fullTextResult = this.getSearchEngineManager().searchEntityId(currentLang.getCode(), word, this.getAllowedGroups(reqCtx));
			return ListUtils.intersection(fullTextResult, masterContentsId);
		} else {
			return masterContentsId;
		}
	}
	
	protected String[] getCategories(String[] categories, ApsProperties config, List<UserFilterOptionBean> userFilters) {
		Set<String> codes = new HashSet<String>();
		if (null != categories) {
			for (int i = 0; i < categories.length; i++) {
				codes.add(categories[i]);
			}
		}
		String categoriesParam = (null != config) ? config.getProperty(SHOWLET_PARAM_CATEGORIES) : null;
		if (null != categoriesParam && categoriesParam.trim().length() > 0) {
			List<String> categoryCodes = splitValues(categoriesParam, CATEGORIES_SEPARATOR);
			for (int j = 0; j < categoryCodes.size(); j++) {
				codes.add(categoryCodes.get(j));
			}
		}
		if (null != userFilters) {
			for (int i = 0; i < userFilters.size(); i++) {
				UserFilterOptionBean userFilterBean = userFilters.get(i);
				if (!userFilterBean.isAttributeFilter() 
						&& userFilterBean.getKey().equals(UserFilterOptionBean.KEY_CATEGORY) 
						&& null != userFilterBean.getFormFieldValues()) {
					codes.add(userFilterBean.getFormFieldValues().get(userFilterBean.getFormFieldNames()[0]));
				}
			}
		}
		if (codes.size() == 0) return null;
		String[] categoryCodes = new String[codes.size()];
		Iterator<String> iter = codes.iterator();
		int i = 0;
		while (iter.hasNext()) {
			categoryCodes[i++] = iter.next();
		}
		return categoryCodes;
	}
	
	protected void addShowletFilters(IContentListTagBean bean, ApsProperties showletParams, String showletParamName, RequestContext reqCtx) {
		if (null == showletParams) return; 
		String showletFilters = showletParams.getProperty(showletParamName);
		EntitySearchFilter[] filters = this.getFilters(bean.getContentType(), showletFilters, reqCtx);
		if (null == filters) return;
		for (int i=0; i<filters.length; i++) {
			bean.addFilter(filters[i]);
		}
	}
	
	@Deprecated
	protected List<String> getContentsId(IContentListTagBean bean, String[] categories, RequestContext reqCtx) throws Throwable {
		return this.getContentsId(bean, reqCtx);
	}
	
	protected void putListInCache(String contentType, RequestContext reqCtx, List<String> contentsId, String cacheKey) {
		if (this.getCacheManager() != null && contentsId != null) {
			IPage page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
			String pageCacheGroupName = SystemConstants.PAGES_CACHE_GROUP_PREFIX + page.getCode();
			String contentTypeCacheGroupName = JacmsSystemConstants.CONTENTS_ID_CACHE_GROUP_PREFIX + contentType;
			String[] groups = {contentTypeCacheGroupName, pageCacheGroupName};
			this.getCacheManager().putInCache(cacheKey, contentsId, groups);
		}
	}
	
	protected Collection<String> getAllowedGroups(RequestContext reqCtx) {
		UserDetails currentUser = (UserDetails) reqCtx.getRequest().getSession().getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER);
		/*
		IAuthorizationManager authManager = (IAuthorizationManager) ApsWebApplicationUtils.getBean(SystemConstants.AUTHORIZATION_SERVICE, reqCtx.getRequest());
		List<Group> groups = authManager.getGroupsOfUser(currentUser);
		Set<String> allowedGroup = new HashSet<String>();
		Iterator<Group> iter = groups.iterator();
    	while (iter.hasNext()) {
    		Group group = iter.next();
    		allowedGroup.add(group.getName());
    	}
		allowedGroup.add(Group.FREE_GROUP_NAME);
		return allowedGroup;
		*/
		return super.getAllowedGroups(currentUser);
	}
	
	@Override
	public List<String> searchInCache(String listName, RequestContext reqCtx) throws Throwable {
		Collection<String> userGroupCodes = this.getAllowedGroups(reqCtx);
		String cacheKey = this.buildCacheKey(listName, userGroupCodes, reqCtx);
		Object object = this.getCacheManager().getFromCache(cacheKey, 1800);//refresh ogni 30min
		if (null != object && (object instanceof List)) {
			return (List) object;
		}
		return null;
	}
	
	protected String buildCacheKey(String listName, Collection<String> userGroupCodes, RequestContext reqCtx) {
		IPage page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
		StringBuffer cacheKey = new StringBuffer(page.getCode());
		Showlet currentShowlet = (Showlet) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_SHOWLET);
		cacheKey.append("_").append(currentShowlet.getType().getCode());
		Integer frame = (Integer) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME);
		cacheKey.append("_").append(frame.intValue());
		Lang currentLang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
		cacheKey.append("_LANG").append(currentLang.getCode()).append("_");
		List<String> groupCodes = new ArrayList<String>(userGroupCodes);
		if (!groupCodes.contains(Group.FREE_GROUP_NAME)) groupCodes.add(Group.FREE_GROUP_NAME);
		Collections.sort(groupCodes);
		for (int i=0; i<groupCodes.size(); i++) {
			String code = (String) groupCodes.get(i);
			cacheKey.append("_").append(code);
		}
		if (null != currentShowlet.getConfig()) {
			List<String> paramKeys = new ArrayList(currentShowlet.getConfig().keySet());
			Collections.sort(paramKeys);
			for (int i=0; i<paramKeys.size(); i++) {
				if (i==0) {
					cacheKey.append("_SHOWLETPARAM");
				} else {
					cacheKey.append(",");
				}
				String paramkey = (String) paramKeys.get(i);
				cacheKey.append(paramkey).append("=").append(currentShowlet.getConfig().getProperty(paramkey));
			}
		}
		if (null != listName) {
			cacheKey.append("_LISTNAME").append(listName);
		}
		return cacheKey.toString();
	}
	/*
	public static String concatStrings(Collection<String> values, String separator) {
		StringBuffer concatedValues = new StringBuffer();
		if (null == values) return concatedValues.toString();
		boolean first = true;
		Iterator<String> valuesIter = values.iterator();
		while (valuesIter.hasNext()) {
			if (!first) {
				concatedValues.append(separator);
			}
			concatedValues.append(valuesIter.next());
			first = false;
		}
		return concatedValues.toString();
	}
	
	public static List<String> splitValues(String concatedValues, String separator) {
		List<String> values = new ArrayList<String>();
		if (concatedValues != null && concatedValues.trim().length() > 0) {
			 String[] codes = concatedValues.split(separator);
			 for (int i = 0; i < codes.length; i++) {
				 values.add(codes[i]);
			}
		}
		return values;
	}
	*/
	@Override
	public List<UserFilterOptionBean> getConfiguredUserFilters(IContentListTagBean bean, RequestContext reqCtx) throws ApsSystemException {
		List<UserFilterOptionBean> userEntityFilters = null;
		try {
			Showlet showlet = (Showlet) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_SHOWLET);
			ApsProperties config = showlet.getConfig();
			if (null == config || null == config.getProperty(SHOWLET_PARAM_CONTENT_TYPE)) {
				return null;
			}
			String contentTypeCode = config.getProperty(SHOWLET_PARAM_CONTENT_TYPE);
			IApsEntity prototype = this.getContentManager().getEntityPrototype(contentTypeCode);
			if (null == prototype) {
				ApsSystemUtils.getLogger().severe("Null content type by code '" + contentTypeCode + "'");
				return null;
			}
			Integer currentFrame = (Integer) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME);
			Lang currentLang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
			String userFilters = config.getProperty(SHOWLET_PARAM_USER_FILTERS);
			if (null != userFilters && userFilters.length() > 0) {
				userEntityFilters = FilterUtils.getUserFilters(userFilters, currentFrame, currentLang, prototype, reqCtx.getRequest());	
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getUserFilters");
			throw new ApsSystemException("Error extracting user filters", t);
		}
		return userEntityFilters;
	}
	
	protected ISearchEngineManager getSearchEngineManager() {
		return _searchEngineManager;
	}
	public void setSearchEngineManager(ISearchEngineManager searchEngineManager) {
		this._searchEngineManager = searchEngineManager;
	}
	
	private ISearchEngineManager _searchEngineManager;
	
}