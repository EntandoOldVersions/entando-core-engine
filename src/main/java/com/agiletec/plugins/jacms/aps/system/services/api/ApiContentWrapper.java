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
package com.agiletec.plugins.jacms.aps.system.services.api;

import java.util.List;
import java.util.Properties;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.plugins.jacms.aps.system.services.api.model.ApiContentListBean;
import com.agiletec.plugins.jacms.aps.system.services.api.model.JAXBContent;
import com.agiletec.plugins.jacms.aps.system.services.cache.ICmsCacheWrapperManager;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.helper.IContentAuthorizationHelper;
import com.agiletec.plugins.jacms.aps.system.services.content.helper.IContentListHelper;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.ContentModel;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.IContentModelManager;
import com.agiletec.plugins.jacms.aps.system.services.dispenser.IContentDispenser;
import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.ApiException;

/**
 * @author E.Santoboni
 * @deprecated 
 */
public class ApiContentWrapper {
	
	public Object getContents(Properties properties) throws Throwable {
		return this.extractContents(properties);
	}
	
	protected List<String> extractContents(Properties properties) throws Throwable {
		List<String> contentsId = null;
		try {
			ApiContentListBean bean = this.buildSearchBean(properties);
			contentsId = this.getContentListHelper().getContentsId(bean, null);
		} catch (ApiException ae) {
			throw ae;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getContents");
			throw new ApsSystemException("Error into API method", t);
		}
        return contentsId;
	}
	
	protected ApiContentListBean buildSearchBean(Properties properties) throws ApiException, Throwable {
		ApiContentListBean bean = null;
		try {
			String contentType = properties.getProperty("contentType");
			if (null == this.getContentManager().getSmallContentTypesMap().get(contentType)) {
				throw new ApiException(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, "Content Type '" + contentType + "' does not exist");
			}
			String langCode = properties.getProperty(SystemConstants.API_LANG_CODE_PARAMETER);
			String filtersParam = properties.getProperty("filters");
			EntitySearchFilter[] filters = this.getContentListHelper().getFilters(contentType, filtersParam, langCode);
			String[] categoryCodes = null;
			String categoriesParam = properties.getProperty("categories");
			if (null != categoriesParam && categoriesParam.trim().length() > 0) {
				categoryCodes = categoriesParam.split(IContentListHelper.CATEGORIES_SEPARATOR);
			}
			bean = new ApiContentListBean(contentType, filters, categoryCodes);
		} catch (ApiException ae) {
			throw ae;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "buildSearchBean");
			throw new ApsSystemException("Error into API method", t);
		}
        return bean;
	}
	
	public String getContentsToHtml(Properties properties) throws Throwable {
		StringBuffer render = new StringBuffer();
		try {
			String modelId = properties.getProperty("modelId");
			if (null == modelId || modelId.trim().length() == 0) return null;
			String contentType = properties.getProperty("contentType");
			Content prototype = (Content) this.getContentManager().getEntityPrototype(contentType);
			Integer modelIdInteger = this.checkModel(modelId, prototype);
			if (null == modelIdInteger) return null;
			List<String> contentsId = this.extractContents(properties);
			String langCode = properties.getProperty(SystemConstants.API_LANG_CODE_PARAMETER);
			render.append(this.getItemsStartElement());
			for (int i = 0; i < contentsId.size(); i++) {
				render.append(this.getItemStartElement());
				render.append(this.getContentDispenser().getRenderedContent(contentsId.get(i), modelIdInteger, langCode, null));
				render.append(this.getItemEndElement());
			}
			render.append(this.getItemsEndElement());
		} catch (ApiException ae) {
			throw ae;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getContentsToHtml");
			throw new ApsSystemException("Error into API method", t);
		}
        return render.toString();
	}
	
	public Object getContent(Properties properties) throws ApiException, Throwable {
		JAXBContent apiContent = null;
		String id = properties.getProperty("id");
		try {
			String langCode = properties.getProperty(SystemConstants.API_LANG_CODE_PARAMETER);
			Content mainContent = this.getPublicContent(id);
			apiContent = new JAXBContent(mainContent, langCode);
			UserDetails guestUser = this.getUserManager().getGuestUser();
			if (!this.getContentAuthorizationHelper().isAuth(guestUser, mainContent)) {
				throw new ApiException("Required content '" + id + "' does not free");
			}
		} catch (ApiException ae) {
			throw ae;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getContent");
			throw new ApsSystemException("Error into API method", t);
		}
        return apiContent;
	}
	
	public String getContentToHtml(Properties properties) throws ApiException, Throwable {
		String render = null;
		String id = properties.getProperty("id");
		String modelId = properties.getProperty("modelId");
		try {
			if (null == modelId || modelId.trim().length() == 0) return null;
			Content mainContent = this.getPublicContent(id);
			Integer modelIdInteger = this.checkModel(modelId, mainContent);
			if (null == modelIdInteger) return null;
			String langCode = properties.getProperty(SystemConstants.API_LANG_CODE_PARAMETER);
			render = this.getContentDispenser().getRenderedContent(id, modelIdInteger, langCode, null);
		} catch (ApiException ae) {
			throw ae;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getContent");
			throw new ApsSystemException("Error into API method", t);
		}
        return render;
	}
	
	protected Content getPublicContent(String id) throws ApiException, Throwable {
		Content content = null;
		try {
			content = this.getCmsCacheWrapperManager().getPublicContent(id);
			if (null == content) {
				throw new ApiException(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, "Null content by id '" + id + "'");
			}
		} catch (ApiException ae) {
			throw ae;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getPublicContent");
			throw new ApsSystemException("Error extracting content by id '" + id + "'", t);
		}
		return content;
	}
	
	protected Integer checkModel(String modelId, Content content) throws ApiException, Throwable {
		Integer modelIdInteger = null;
		try {
			if (null == modelId || modelId.trim().length() == 0) return null;
			if (modelId.equals("default")) {
				if (null == content.getDefaultModel()) {
					throw new ApiException(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, 
							"Invalid 'default' system model for content type '" + content.getTypeCode() + "' - Contact the administrators");
				}
				modelIdInteger = Integer.parseInt(content.getDefaultModel());
			} else if (modelId.equals("list")) {
				if (null == content.getListModel()) {
					throw new ApiException(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, 
							"Invalid 'list' system model for content type '" + content.getTypeCode() + "' - Contact the administrators");
				}
				modelIdInteger = Integer.parseInt(content.getListModel());
			} else {				
				try {
					modelIdInteger = Integer.parseInt(modelId);
				} catch (Throwable t) {
					throw new ApiException(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, "The model id must be an integer or 'default' or 'list' - '" + modelId + "'");
				}
			}
			ContentModel model = this.getContentModelManager().getContentModel(modelIdInteger);
			if (model == null) {
				throw new ApiException(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, "The content model with id '" + modelId + "' does not exist");
			} else if (!content.getTypeCode().equals(model.getContentType())) {
				throw new ApiException(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, "The content model with id '" + modelId + "' does not match with content of type '" + content.getTypeDescr() + "' ");
			}
		} catch (ApiException ae) {
			throw ae;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "checkModel");
			throw new ApsSystemException("Error checking model id '" + modelId + "'", t);
		}
		return modelIdInteger;
	}
	
	protected IContentManager getContentManager() {
		return _contentManager;
	}
	public void setContentManager(IContentManager contentManager) {
		this._contentManager = contentManager;
	}
	
	protected IContentListHelper getContentListHelper() {
		return _contentListHelper;
	}
	public void setContentListHelper(IContentListHelper contentListHelper) {
		this._contentListHelper = contentListHelper;
	}
	
	protected IUserManager getUserManager() {
		return _userManager;
	}
	public void setUserManager(IUserManager userManager) {
		this._userManager = userManager;
	}
	
	protected IContentAuthorizationHelper getContentAuthorizationHelper() {
		return _contentAuthorizationHelper;
	}
	public void setContentAuthorizationHelper(IContentAuthorizationHelper contentAuthorizationHelper) {
		this._contentAuthorizationHelper = contentAuthorizationHelper;
	}
	
	protected ICmsCacheWrapperManager getCmsCacheWrapperManager() {
		return _cmsCacheWrapperManager;
	}
	public void setCmsCacheWrapperManager(ICmsCacheWrapperManager cmsCacheWrapperManager) {
		this._cmsCacheWrapperManager = cmsCacheWrapperManager;
	}
	
	protected IContentModelManager getContentModelManager() {
		return _contentModelManager;
	}
	public void setContentModelManager(IContentModelManager contentModelManager) {
		this._contentModelManager = contentModelManager;
	}
	
	protected IContentDispenser getContentDispenser() {
		return _contentDispenser;
	}
	public void setContentDispenser(IContentDispenser contentDispenser) {
		this._contentDispenser = contentDispenser;
	}
        
        public String getItemsStartElement() {
		return _itemsStartElement;
	}
	public void setItemsStartElement(String itemsStartElement) {
		this._itemsStartElement = itemsStartElement;
	}

	public String getItemStartElement() {
		return _itemStartElement;
	}
	public void setItemStartElement(String itemStartElement) {
		this._itemStartElement = itemStartElement;
	}

	public String getItemEndElement() {
		return _itemEndElement;
	}
	public void setItemEndElement(String itemEndElement) {
		this._itemEndElement = itemEndElement;
	}

	public String getItemsEndElement() {
		return _itemsEndElement;
	}
	public void setItemsEndElement(String itemsEndElement) {
		this._itemsEndElement = itemsEndElement;
	}
	
	private IContentManager _contentManager;
	private IContentListHelper _contentListHelper;
	private IUserManager _userManager;
	private IContentAuthorizationHelper _contentAuthorizationHelper;
	private ICmsCacheWrapperManager _cmsCacheWrapperManager;
	private IContentModelManager _contentModelManager;
	private IContentDispenser _contentDispenser;

	private String _itemsStartElement = "<ul>";
	private String _itemStartElement = "<li>";
	private String _itemEndElement = "</li>";
	private String _itemsEndElement = "</ul>";
        
}