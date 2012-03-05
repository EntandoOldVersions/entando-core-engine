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
package com.agiletec.plugins.jacms.aps.system.services.cache;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.common.entity.event.EntityTypesChangingEvent;
import com.agiletec.aps.system.common.entity.event.EntityTypesChangingObserver;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.cache.ICacheManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.ContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.event.PublicContentChangedEvent;
import com.agiletec.plugins.jacms.aps.system.services.content.event.PublicContentChangedObserver;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.ContentModel;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.IContentModelManager;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.event.ContentModelChangedEvent;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.event.ContentModelChangedObserver;
import com.agiletec.plugins.jacms.aps.system.services.resource.ResourceUtilizer;
import com.agiletec.plugins.jacms.aps.system.services.resource.event.ResourceChangedEvent;
import com.agiletec.plugins.jacms.aps.system.services.resource.event.ResourceChangedObserver;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInterface;

/**
 * Cache Wrapper Manager for plugin jacms
 * @author E.Santoboni
 */
public class CmsCacheWrapperManager extends AbstractService 
		implements ICmsCacheWrapperManager, PublicContentChangedObserver, ContentModelChangedObserver, EntityTypesChangingObserver, ResourceChangedObserver {
	
	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initialized");
	}
	
	@Override
	public void updateFromPublicContentChanged(PublicContentChangedEvent event) {
		try {
			Content content = event.getContent();
			Logger log = ApsSystemUtils.getLogger();
			if (log.isLoggable(Level.FINEST)) {
				log.info("Notified public content update : type " + content.getId());
			}
			this.releaseRelatedItems(content);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateFromPublicContentChanged", 
					"Error notifing event " + PublicContentChangedEvent.class.getName());
		}
	}
	
	@Override
	public void updateFromContentModelChanged(ContentModelChangedEvent event) {
		try {
			ContentModel model = event.getContentModel();
			Logger log = ApsSystemUtils.getLogger();
			if (log.isLoggable(Level.FINEST)) {
				log.info("Notified content model update : type " + model.getId());
			}
			String cacheGroupKey = JacmsSystemConstants.CONTENT_MODEL_CACHE_GROUP_PREFIX + model.getId();
			this.getCacheManager().flushGroup(cacheGroupKey);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateFromContentModelChanged", 
					"Error notifing event " + ContentModelChangedEvent.class.getName());
		}
	}
	
	@Override
	public void updateFromEntityTypesChanging(EntityTypesChangingEvent event) {
		try {
			String entityManagerName = event.getEntityManagerName();
			if (!entityManagerName.equals(JacmsSystemConstants.CONTENT_MANAGER)) return;
			if (event.getOperationCode() == EntityTypesChangingEvent.INSERT_OPERATION_CODE) return;
			IApsEntity oldEntityType = event.getOldEntityType();
			Logger log = ApsSystemUtils.getLogger();
			if (log.isLoggable(Level.FINEST)) {
				log.info("Notified content type modify : type " + oldEntityType.getTypeCode());
			}
			String typeGroupKey = JacmsSystemConstants.CONTENTS_TYPE_CACHE_GROUP_PREFIX + oldEntityType.getTypeCode();
			this.getCacheManager().flushGroup(typeGroupKey);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateFromEntityTypesChanging", 
					"Error notifing event " + EntityTypesChangingEvent.class.getName());
		}
	}
	
	@Override
	public void updateFromResourceChanged(ResourceChangedEvent event) {
		try {
			ResourceInterface resource = event.getResource();
			if (null == resource) return;
			List<String> utilizers = ((ResourceUtilizer) this.getContentManager()).getResourceUtilizers(resource.getId());
			for (int i = 0; i < utilizers.size(); i++) {
				String contentId = utilizers.get(i);
				Content content = this.getContentManager().loadContent(contentId, true);
				if (null != content) {
					this.releaseRelatedItems(content);
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateFromResourceChanged", 
					"Error notifing event " + ResourceChangedEvent.class.getName());
		}
	}
	
	private void releaseRelatedItems(Content content) {
		String authInfokey = ContentManager.getContentAuthInfoCacheKey(content.getId());
		this.getCacheManager().flushEntry(authInfokey);
		this.getCacheManager().flushGroup(JacmsSystemConstants.CONTENTS_ID_CACHE_GROUP_PREFIX + content.getTypeCode());
		this.getCacheManager().flushEntry(JacmsSystemConstants.CONTENT_CACHE_PREFIX + content.getId());
		List<Lang> langs = this.getLangManager().getLangs();
		List<ContentModel> models = this.getContentModelManager().getModelsForContentType(content.getTypeCode());
		for (int i = 0; i < langs.size(); i++) {
			Lang lang = langs.get(i);
			for (int j = 0; j < models.size(); j++) {
				ContentModel contentModel = models.get(j);
				String key = ContentManager.getRenderedContentCacheKey(content.getId(), contentModel.getId(), lang.getCode());
				this.getCacheManager().flushEntry(key);
			}
		}
	}
	
	@Override
	public Content getPublicContent(String id) throws ApsSystemException {
		Content content = null;
		try {
			String cacheKey = JacmsSystemConstants.CONTENT_CACHE_PREFIX + id;
			content = (Content) this.getCacheManager().getFromCache(cacheKey);
			if (null == content) {
				content = this.getContentManager().loadContent(id, true);
				if (null != content) {
					String typeGroupId = JacmsSystemConstants.CONTENTS_TYPE_CACHE_GROUP_PREFIX + content.getTypeCode();
					String[] groups = {typeGroupId};
					this.getCacheManager().putInCache(cacheKey, content, groups);
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getPublicContent");
			throw new ApsSystemException("Error extracting content by id '" + id + "'", t);
		}
		return content;
	}
	
	@Override
	public void flushAll() {
		this.getCacheManager().flushAll();
	}
	
	@Override
	public void flushEntry(String key) {
		this.getCacheManager().flushEntry(key);
	}
	
	@Override
	public void flushGroup(String group) {
		this.getCacheManager().flushGroup(group);
	}
	
	@Override
	public void putInCache(String key, Object obj) {
		this.getCacheManager().putInCache(key, obj);
	}
	
	@Override
	public void putInCache(String key, Object obj, String[] groups) {
		this.getCacheManager().putInCache(key, obj, groups);
	}
	
	@Override
	public Object getFromCache(String key) {
		return this.getCacheManager().getFromCache(key);
	}
	
	@Override
	public Object getFromCache(String key, int myRefreshPeriod) {
		return this.getCacheManager().getFromCache(key, myRefreshPeriod);
	}
	
	protected IContentManager getContentManager() {
		return _contentManager;
	}
	public void setContentManager(IContentManager contentManager) {
		this._contentManager = contentManager;
	}
	
	protected ICacheManager getCacheManager() {
		return _cacheManager;
	}
	public void setCacheManager(ICacheManager cacheManager) {
		this._cacheManager = cacheManager;
	}
	
	protected ILangManager getLangManager() {
		return _langManager;
	}
	public void setLangManager(ILangManager langManager) {
		this._langManager = langManager;
	}
	
	protected IContentModelManager getContentModelManager() {
		return _contentModelManager;
	}
	public void setContentModelManager(IContentModelManager contentModelManager) {
		this._contentModelManager = contentModelManager;
	}
	
	private IContentManager _contentManager;
	private ICacheManager _cacheManager;
	private ILangManager _langManager;
	private IContentModelManager _contentModelManager;
	
}