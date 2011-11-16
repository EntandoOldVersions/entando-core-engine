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
package com.agiletec.plugins.jacms.aps.system.services.dispenser;

import java.util.ArrayList;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeRole;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.cache.ICacheManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.cache.ICmsCacheWrapperManager;
import com.agiletec.plugins.jacms.aps.system.services.content.ContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.linkresolver.ILinkResolverManager;
import com.agiletec.plugins.jacms.aps.system.services.renderer.IContentRenderer;

/**
 * Fornisce i contenuti formattati.
 * Il compito del servizio, in fase di richiesta di un contenuto formattato, è quello di 
 * controllare preliminarmente le autorizzazzioni dell'utente corrente all'accesso al contenuto;
 * successivamente (in caso di autorizzazioni valide) restituisce il contenuto formattato.
 * @author M.Diana - E.Santoboni
 */
public class BaseContentDispenser extends AbstractService implements IContentDispenser {
	
	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initialized ");
	}
	
	@Override
	public String getRenderedContent(String contentId, long modelId, String langCode, RequestContext reqCtx) {
		ContentRenderizationInfo renderInfo = this.getRenderizationInfo(contentId, modelId, langCode, reqCtx);
		if (null == renderInfo) return "";
		return renderInfo.getRenderedContent();
	}
	
	@Override
	public ContentRenderizationInfo getRenderizationInfo(String contentId, long modelId, String langCode, RequestContext reqCtx) {
		Content contentToRender = null;
		ContentAuthorizationInfo authInfo = this.getAuthorizationInfo(contentToRender, contentId);
		if (authInfo == null) return null;
		return this.getRenderizationInfo(authInfo, contentToRender, contentId, modelId, langCode, reqCtx);
	}
	
	@Override
	public ContentAuthorizationInfo getAuthorizationInfo(String contentId) {
		return this.getAuthorizationInfo(null, contentId);
	}
	
	/**
	 * Carica le informazioni di autorizzazione sul contenuto.
	 * @param content Il contenuto del quale restituire le informazioni di autorizzazione. 
	 * @param contentId Il codice del contenuto del quale restituire le informazioni di autorizzazione. 
	 * Il parametro viene utilizzato nel caso il parametro contenuto sia nullo.
	 * @return Le informazioni di autorizzazione sul contenuto.
	 */
	protected ContentAuthorizationInfo getAuthorizationInfo(Content content, String contentId) {
		String authorizationCacheKey = ContentManager.getContentAuthInfoCacheKey(contentId);
		ContentAuthorizationInfo authInfo = null;
		if (null != this.getCacheManager()) {
			Object authInfoTemp = this.getCacheManager().getFromCache(authorizationCacheKey);
			if (authInfoTemp instanceof ContentAuthorizationInfo) {
				authInfo = (ContentAuthorizationInfo) authInfoTemp;
			}
		}
		if (null == authInfo) {
			try {
				if (null == content) {
					content = this.getPublicContent(contentId);
				}
				if (content != null) {
					authInfo = new ContentAuthorizationInfo(content);
				}
			} catch (Throwable t) {
				ApsSystemUtils.getLogger().throwing(this.getClass().getName(), "getAuthorizationInfo", t);
			}
			if (authInfo != null) {
				if (null != this.getCacheManager()) {
					String typeGroupId = JacmsSystemConstants.CONTENTS_TYPE_CACHE_GROUP_PREFIX + content.getTypeCode();
					String[] groups = { typeGroupId };
					this.getCacheManager().putInCache(authorizationCacheKey, authInfo, groups);
				}
			} else {
				ApsSystemUtils.getLogger().warning("No any Authorization Infos can be extracted for content " + contentId);
			}
		}
		return authInfo;
	}
	
	protected ContentRenderizationInfo getRenderizationInfo(ContentAuthorizationInfo authInfo, Content contentToRender,
			String contentId, long modelId, String langCode, RequestContext reqCtx) {
		ContentRenderizationInfo renderInfo = null;
		try {
			UserDetails currentUser = (null != reqCtx) ? (UserDetails) reqCtx.getRequest().getSession().getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER) : null;
			List<Group> userGroups = (null != currentUser) ? this.getAuthorizationManager().getGroupsOfUser(currentUser) : new ArrayList<Group>();
			if (authInfo.isUserAllowed(userGroups)) {
				String cacheKey = ContentManager.getRenderedContentCacheKey(contentId, modelId, langCode);
				if (null != this.getCacheManager()) {
					renderInfo = (ContentRenderizationInfo) this.getCacheManager().getFromCache(cacheKey);
				}
				if (null == renderInfo) {
					if (contentToRender == null) {
						contentToRender = this.getPublicContent(contentId);
					}
					String renderedContent = this.buildRenderedContent(contentToRender, modelId, langCode, reqCtx);
					if (null != renderedContent && renderedContent.trim().length() > 0 && null != this.getCacheManager()) {
						String modelGroupId = JacmsSystemConstants.CONTENT_MODEL_CACHE_GROUP_PREFIX + modelId;
						String typeGroupId = JacmsSystemConstants.CONTENTS_TYPE_CACHE_GROUP_PREFIX + authInfo.getContentType();
						String[] groups = { modelGroupId, typeGroupId };
						List<AttributeRole> roles = this.getContentManager().getAttributeRoles();
						renderInfo = new ContentRenderizationInfo(contentToRender, renderedContent, modelId, langCode, roles);
						this.getCacheManager().putInCache(cacheKey, renderInfo, groups);
					}
				}
				if (null == renderInfo) {
					return null;
				}
			} else {
				String renderedContent = "Current user '" + currentUser.getUsername() + "' can't view this content";
				renderInfo = new ContentRenderizationInfo(contentToRender, renderedContent, modelId, langCode, null);
				renderInfo.setRenderedContent(renderedContent);
				return renderInfo;
			}
			String finalRenderedContent = this._linkResolver.resolveLinks(renderInfo.getCachedRenderedContent(), reqCtx);
			renderInfo.setRenderedContent(finalRenderedContent);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getRenderedContent", "Error while rendering content " + contentId);
			return null;
		}
		return renderInfo;
	}
	
	/**
	 * Restituisce il contenuto renderizzato.
	 * @param authInfo Le informazioni di autorizzazione sul contenuto.
	 * @param contentToRender Il contenuto da renderizzare.
	 * @param contentId L'Identificativo del contenuto da renderizzare. 
	 * Il parametro viene utilizzato nel caso il parametro contenuto sia nullo.
	 * @param modelId Identificatore del modello di contenuto.
	 * @param langCode Codice della lingua di renderizzazione richiesta.
	 * @param reqCtx Il contesto della richiesta.
	 * @return Il contenuto renderizzato.
	 * @deprecated From jAPS 2.0 version 2.0.12, use getRenderizationInfo method
	 */
	protected String getRenderedContent(ContentAuthorizationInfo authInfo, 
			Content contentToRender, String contentId, long modelId, String langCode, RequestContext reqCtx) {
		String renderedContent = null;
		try {
			UserDetails currentUser = (null != reqCtx) ? (UserDetails) reqCtx.getRequest().getSession().getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER) : null;
			List<Group> userGroups = (null != currentUser) ? this.getAuthorizationManager().getGroupsOfUser(currentUser) : new ArrayList<Group>();
			//verifica autorizzazione
			if (authInfo.isUserAllowed(userGroups)) {
				String cacheKey = ContentManager.getRenderedContentCacheKey(contentId, modelId, langCode);
				if (null != this.getCacheManager()) {
					renderedContent = (String) this.getCacheManager().getFromCache(cacheKey);
				}
				if (null == renderedContent) {
					if (contentToRender == null) {
						contentToRender = this.getPublicContent(contentId);
					}
					renderedContent = this.buildRenderedContent(contentToRender, modelId, langCode, reqCtx);
					if (null != renderedContent && renderedContent.trim().length() > 0 && null != this.getCacheManager()) {
						String modelGroupId = JacmsSystemConstants.CONTENT_MODEL_CACHE_GROUP_PREFIX + modelId;
						String typeGroupId = JacmsSystemConstants.CONTENTS_TYPE_CACHE_GROUP_PREFIX + authInfo.getContentType();
						String[] groups = { modelGroupId, typeGroupId };
						this.getCacheManager().putInCache(cacheKey, renderedContent, groups);
					}
				}
				if (null == renderedContent) {
					return "";
				}
			} else {
				renderedContent = "Current user '" + currentUser.getUsername() + "' can't view this content";
			}
			renderedContent = _linkResolver.resolveLinks(renderedContent, reqCtx);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getRenderedContent", "Error while rendering content " + contentId);
			return "";
		}
		return renderedContent;
	}
	
	/**
	 * Costruisce il contenuto renderizzato.
	 * @param authInfo Le informazioni di autorizzazione sul contenuto.
	 * @param contentToRender Il contenuto da renderizzare.
	 * @param contentId L'Identificativo del contenuto da renderizzare. 
	 * Il parametro viene utilizzato nel caso il parametro contenuto sia nullo.
	 * @param modelId Identificatore del modello di contenuto.
	 * @param langCode Codice della lingua di renderizzazione richiesta.
	 * @param reqCtx Il contesto della richiesta.
	 * @return Il contenuto renderizzato.
	 * @deprecated From jAPS 2.0 version 2.0.12
	 */
	protected String buildRenderedContent(Content contentToRender, String contentId, long modelId, String langCode, RequestContext reqCtx) {
		String renderedContent = null;
		boolean ok = false;
		try {
			if (contentToRender == null) {
				contentToRender = this.getPublicContent(contentId);
			}
			if (contentToRender != null) {
				renderedContent = this.getContentRender().render(contentToRender, modelId, langCode, reqCtx);
				ok = true;
			}
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().throwing(this.getClass().getName(), "getRenderedContent", t);
		}
		if (!ok) {
			ApsSystemUtils.getLogger().warning("The content " + contentId + " can't be rendered");
		}
		return renderedContent;
	}

	protected String buildRenderedContent(Content content, long modelId, String langCode, RequestContext reqCtx) {
		if (null == content) {
			ApsSystemUtils.getLogger().warning("Null The content can't be rendered");
			return null;
		}
		String renderedContent = null;
		boolean ok = false;
		try {
			renderedContent = this.getContentRender().render(content, modelId, langCode, reqCtx);
			ok = true;
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().throwing(this.getClass().getName(), "getRenderedContent", t);
		}
		if (!ok) {
			ApsSystemUtils.getLogger().warning("The content " + content.getId() + " can't be rendered");
		}
		return renderedContent;
	}
	
	protected Content getPublicContent(String contentId) throws ApsSystemException {
		if (this.getCacheManager() instanceof ICmsCacheWrapperManager) {
			return ((ICmsCacheWrapperManager) this.getCacheManager()).getPublicContent(contentId);
		} else {
			return this.getContentManager().loadContent(contentId, true);
		}
	}
	
	protected ICacheManager getCacheManager() {
		return _cacheManager;
	}
	public void setCacheManager(ICacheManager manager) {
		this._cacheManager = manager;
	}
	
	protected IContentManager getContentManager() {
		return _contentManager;
	}
	public void setContentManager(IContentManager manager) {
		this._contentManager = manager;
	}
	
	protected IContentRenderer getContentRender() {
		return _contentRenderer;
	}
	public void setContentRenderer(IContentRenderer renderer) {
		this._contentRenderer = renderer;
	}
	
	protected ILinkResolverManager getLinkResolverManager() {
		return _linkResolver;
	}
	public void setLinkResolver(ILinkResolverManager resolver) {
		this._linkResolver = resolver;
	}
	
	protected IAuthorizationManager getAuthorizationManager() {
		return _authorizationManager;
	}
	public void setAuthorizationManager(IAuthorizationManager authorizationManager) {
		this._authorizationManager = authorizationManager;
	}
	
	private IContentRenderer _contentRenderer;
	private IContentManager _contentManager;
	private ILinkResolverManager _linkResolver;
	private ICacheManager _cacheManager;
	private IAuthorizationManager _authorizationManager;
	
}