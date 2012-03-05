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
package com.agiletec.plugins.jacms.aps.system.services.content.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.helper.BaseFilterUtils;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.cache.ICacheManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;

/**
 * @author E.Santoboni
 */
public class BaseContentListHelper implements IContentListHelper {
    
    public EntitySearchFilter[] getFilters(String contentType, String filtersShowletParam, String langCode) {
        Content contentPrototype = this.getContentManager().createContentType(contentType);
        if (null == filtersShowletParam || filtersShowletParam.trim().length() == 0 || null == contentPrototype) {
            return null;
        }
        BaseFilterUtils dom = new BaseFilterUtils();
        return dom.getFilters(contentPrototype, filtersShowletParam, langCode);
    }
    
    public EntitySearchFilter getFilter(String contentType, IContentListFilterBean bean, String langCode) {
        BaseFilterUtils dom = new BaseFilterUtils();
        Content contentPrototype = this.getContentManager().createContentType(contentType);
        if (null == contentPrototype) {
            return null;
        }
        return dom.getFilter(contentPrototype, bean, langCode);
    }
    
    public String getFilterParam(EntitySearchFilter[] filters) {
        BaseFilterUtils dom = new BaseFilterUtils();
        return dom.getFilterParam(filters);
    }
    
    public List<String> getContentsId(IContentListBean bean, UserDetails user) throws Throwable {
        List<String> contentsId = null;
        try {
            contentsId = this.searchInCache(bean, user);
            if (null == contentsId) {
                contentsId = this.extractContentsId(bean, user);
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getContentsId");
            throw new ApsSystemException("Error extracting contents id", t);
        }
        return contentsId;
    }

    protected List<String> extractContentsId(IContentListBean bean, UserDetails user) throws ApsSystemException {
        List<String> contentsId = null;
        try {
            if (null == bean.getContentType()) {
                throw new ApsSystemException("Tipo contenuto non definito");
            }
            Collection<String> userGroupCodes = this.getAllowedGroups(user);
            contentsId = this.getContentManager().loadPublicContentsId(bean.getContentType(), bean.getCategories(), bean.getFilters(), userGroupCodes);
            if (bean.isCacheable()) {
                String cacheKey = this.buildCacheKey(bean, userGroupCodes);
                this.putListInCache(bean.getContentType(), contentsId, cacheKey);
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "extractContentsId");
            throw new ApsSystemException("Error extracting contents id", t);
        }
        return contentsId;
    }

    private void putListInCache(String contentType, List<String> contentsId, String cacheKey) {
        if (this.getCacheManager() != null && contentsId != null) {
            String contentTypeCacheGroupName = JacmsSystemConstants.CONTENTS_ID_CACHE_GROUP_PREFIX + contentType;
            String[] groups = {contentTypeCacheGroupName};
            this.getCacheManager().putInCache(cacheKey, contentsId, groups);
        }
    }

    /**
     * Return the groups to witch execute the filter to contents.
     * The User object is non null, extract the groups from the user, else 
     * return a collection with only the "free" group. 
     * @param user The user. Can be null.
     * @return The groups to witch execute the filter to contents.
     */
    protected Collection<String> getAllowedGroups(UserDetails user) {
        Set<String> allowedGroup = new HashSet<String>();
        allowedGroup.add(Group.FREE_GROUP_NAME);
        if (null != user) {
            List<Group> groups = this.getAuthorizationManager().getUserGroups(user);
            Iterator<Group> iter = groups.iterator();
            while (iter.hasNext()) {
                Group group = iter.next();
                allowedGroup.add(group.getName());
            }
        }
        return allowedGroup;
    }

    protected List<String> searchInCache(IContentListBean bean, UserDetails user) throws Throwable {
        Collection<String> userGroupCodes = this.getAllowedGroups(user);
        String cacheKey = this.buildCacheKey(bean, userGroupCodes);
        Object object = this.getCacheManager().getFromCache(cacheKey, 1800);//refresh ogni 30min
        if (null != object && (object instanceof List)) {
            return (List) object;
        }
        return null;
    }

    protected String buildCacheKey(IContentListBean bean, Collection<String> userGroupCodes) {
        StringBuffer cacheKey = new StringBuffer();
        if (null != bean.getListName()) {
            cacheKey.append("LISTNAME_").append(bean.getListName());
        }
        List<String> groupCodes = new ArrayList<String>(userGroupCodes);
        if (!groupCodes.contains(Group.FREE_GROUP_NAME)) {
            groupCodes.add(Group.FREE_GROUP_NAME);
        }
        Collections.sort(groupCodes);
        for (int i = 0; i < groupCodes.size(); i++) {
            if (i == 0) {
                cacheKey.append("-GROUPS_");
            }
            String code = groupCodes.get(i);
            cacheKey.append("_").append(code);
        }
        if (null != bean.getCategories()) {
            List<String> categoryCodes = Arrays.asList(bean.getCategories());
            Collections.sort(categoryCodes);
            for (int j = 0; j < categoryCodes.size(); j++) {
                if (j == 0) {
                    cacheKey.append("-CATEGORIES_");
                }
                String code = categoryCodes.get(j);
                cacheKey.append("_").append(code);
            }
        }
        if (null != bean.getFilters()) {
            for (int k = 0; k < bean.getFilters().length; k++) {
                if (k == 0) {
                    cacheKey.append("-FILTERS_");
                }
                EntitySearchFilter filter = bean.getFilters()[k];
                cacheKey.append("_").append(filter.toString());
            }
        }
        return cacheKey.toString();
    }

    public static String concatStrings(Collection<String> values, String separator) {
        StringBuffer concatedValues = new StringBuffer();
        if (null == values) {
            return concatedValues.toString();
        }
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

    protected ICacheManager getCacheManager() {
        return _cacheManager;
    }
    public void setCacheManager(ICacheManager cacheManager) {
        this._cacheManager = cacheManager;
    }

    protected IContentManager getContentManager() {
        return _contentManager;
    }
    public void setContentManager(IContentManager contentManager) {
        this._contentManager = contentManager;
    }

    protected IAuthorizationManager getAuthorizationManager() {
        return _authorizationManager;
    }
    public void setAuthorizationManager(IAuthorizationManager authorizationManager) {
        this._authorizationManager = authorizationManager;
    }
    
    private ICacheManager _cacheManager;
    private IContentManager _contentManager;
    private IAuthorizationManager _authorizationManager;
    
}