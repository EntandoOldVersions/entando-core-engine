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
package com.agiletec.plugins.jacms.apsadmin.content;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletResponseAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;

/**
 * Classe action delegate alla gestione della funzione di preview contenuti.
 * @author E.Santoboni
 */
public class ContentPreviewAction extends AbstractContentAction implements IContentPreviewAction, ServletResponseAware {
	
	@Override
	public String preview() {
		Content content = this.getContent();
		this.getContentActionHelper().updateEntity(content, this.getRequest());
		try {
			String previewLangCode = this.extractPreviewLangCode();
			this.setPreviewLangCode(previewLangCode);
			String previewPageCode = this.getRequest().getParameter(PAGE_CODE_PARAM_PREFIX + "_" + previewLangCode);
			if (null == previewPageCode) {
				previewPageCode = this.getRequest().getParameter(PAGE_CODE_PARAM_PREFIX);
			}
			this.setPreviewPageCode(previewPageCode);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "preview", "Error");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	private String extractPreviewLangCode() {
		String previewLangCode = null;
		Enumeration<String> attributeEnum = this.getRequest().getAttributeNames();
		if (null != attributeEnum) {
			while (attributeEnum.hasMoreElements()) {
				String attributeName = attributeEnum.nextElement();
				if (attributeName.startsWith(LANG_CODE_PARAM_PREFIX + "_")) {
					previewLangCode = (String) this.getRequest().getAttribute(attributeName);
					break;
				}
			}
		}
		if (null == previewLangCode || previewLangCode.trim().length() == 0) {
			previewLangCode = this.getLangManager().getDefaultLang().getCode();
		}
		return previewLangCode;
	}
	
	@Override
	public String executePreview() {
		try {
			String pageDestCode = this.getCheckPageDestinationCode();
			if (null == pageDestCode) return INPUT;
			this.prepareForwardParams(pageDestCode);
			this.getRequest().setCharacterEncoding("UTF-8");
		} catch (Throwable t) {
			String message = "Error";
			ApsSystemUtils.logThrowable(t, this, "executePreview", message);
			throw new RuntimeException(message, t);
		}
		return SUCCESS;
	}
	
	protected String getCheckPageDestinationCode() {
		IPageManager pageManager = this.getPageManager();
		String pageDestCode = this.getPreviewPageCode();
		if (null == pageDestCode || pageDestCode.trim().length() == 0) {
			pageDestCode = this.getContent().getViewPage();
			if (null == pageDestCode || null == pageManager.getPage(pageDestCode)) {
				String[] args = {pageDestCode};
				this.addFieldError("previewPageCode", this.getText("error.content.preview.pageNotValid", args));
				return null;
			}
		}
		if (null == pageManager.getPage(pageDestCode)) {
			String[] args = {pageDestCode};
			this.addFieldError("previewPageCode", this.getText("error.content.preview.pageNotFound", args));
			return null;
		}
		return pageDestCode;
	}
	
	private void prepareForwardParams(String pageDestCode) {
		HttpServletRequest request = this.getRequest();
		RequestContext reqCtx = new RequestContext();
		reqCtx.setRequest(request);
		reqCtx.setResponse(this.getServletResponse());
		Lang currentLang = this.getLangManager().getLang(this.getPreviewLangCode());
		if (null == currentLang) {
			currentLang = this.getLangManager().getDefaultLang();
		}
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, currentLang);
		IPageManager pageManager = this.getPageManager();
		IPage pageDest = pageManager.getPage(pageDestCode);
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		request.setAttribute(RequestContext.REQCTX, reqCtx);
	}
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this._response = response;
	}
	public HttpServletResponse getServletResponse() {
		return _response;
	}
	
	public String getPreviewPageCode() {
		return _previewPageCode;
	}
	public void setPreviewPageCode(String previewPageCode) {
		this._previewPageCode = previewPageCode;
	}
	
	public String getPreviewLangCode() {
		return _previewLangCode;
	}
	public void setPreviewLangCode(String previewLangCode) {
		this._previewLangCode = previewLangCode;
	}
	
	protected IPageManager getPageManager() {
		return _pageManager;
	}
	public void setPageManager(IPageManager pageManager) {
		this._pageManager = pageManager;
	}
	
	private HttpServletResponse _response;
	
	private String _previewPageCode;
	private String _previewLangCode;
	
	private IPageManager _pageManager;
	
}