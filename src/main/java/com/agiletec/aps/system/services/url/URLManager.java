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
package com.agiletec.aps.system.services.url;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.PageUtils;

/**
 * Servizio di gestione degli url; crea un URL completo ad una pagina del portale 
 * a partire da informazioni essenziali.
 * @author M.Diana - E.Santoboni
 */
public class URLManager extends AbstractURLManager {
	
	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initialized");
	}

	/**
	 * Crea un URL completo ad una pagina del portale a partire dalle informazioni
	 * essenziali contenute nell'oggetto pageUrl passato come parametro.<br>
	 * In questa implementazione, l'URL è costruito come concatenazione dei seguenti elementi:
	 * <ul>
	 * <li> parametro di configurazione PAR_APPL_BASE_URL, che rappresenta l'URL
	 * base della web application così come viene visto dall'esterno; deve comprendere
	 * la stringa "http://" e deve terminare con "/";
	 * </li>
	 * <li> codice della lingua impostata nell'oggetto pageUrl, oppure la lingua corrente, 
	 * oppure la lingua di default;
	 * </li>
	 * <li> se il parametro "urlStyle" è settato a "classic", codice della pagina corrente impostata nell'oggetto pageUrl 
	 * seguito dal suffisso ".wp", altrimenti, se il parametro "urlStyle" è settato a "breadcrumbs", 
	 * "/pages/" seguito dal'insieme del codici pagina dalla root alla pagina corrente separati da "/";
	 * </li>
	 * <li> eventuale query string se sull'oggetto pageUrl sono stati impostati parametri.
	 * </li>
	 * </ul>
	 * @param pageUrl L'oggetto contenente le informazioni da tradurre in URL.
	 * @param Il contesto della richiesta.
	 * @return La Stringa contenente l'URL.
	 * @see com.agiletec.aps.system.services.url.AbstractURLManager#getURLString(com.agiletec.aps.system.services.url.PageURL, com.agiletec.aps.system.RequestContext)
	 */
	@Override
	public String getURLString(PageURL pageUrl, RequestContext reqCtx) {
		String langCode = pageUrl.getLangCode();
		Lang lang = this.getLangManager().getLang(langCode);
		if (lang == null && null != reqCtx) {
			lang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
		}
		if (lang == null) {
			lang = this.getLangManager().getDefaultLang();
		}
		String pageCode = pageUrl.getPageCode();
		IPage page = this.getPageManager().getPage(pageCode);
		if (page == null && null != reqCtx) {
			page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
		}
		if (page == null) {
			page = this.getPageManager().getRoot();
		}
		String url = this.createUrl(page, lang, pageUrl.getParams());
		if (null != reqCtx) {
			HttpServletResponse resp = reqCtx.getResponse();
			String encUrl = resp.encodeURL(url.toString());  
			return encUrl;
		} else {
			return url;
		}
	}

	/**
	 * Create and return url by required page, lang and request params.
	 * @param requiredPage The required page.
	 * @param requiredLang The required lang.
	 * @param params A map of params. It can be null.
	 * @return The url.
	 */
	@Override
	public String createUrl(IPage requiredPage, Lang requiredLang, Map<String, String> params) {
		StringBuffer url = new StringBuffer();
		url.append(this.getConfigManager().getParam(SystemConstants.PAR_APPL_BASE_URL));
		if (!this.isUrlStyleBreadcrumbs()) {
			url.append(requiredLang.getCode()).append('/');
			url.append(requiredPage.getCode()).append(".page");
		} else {
			url.append("pages/");
			url.append(requiredLang.getCode()).append('/');
			StringBuffer fullPath = PageUtils.getFullPath(requiredPage, "/");
			url.append(fullPath.append("/"));
		}
		String queryString = this.createQueryString(params);
		url.append(queryString);
		return url.toString();
	}

	protected boolean isUrlStyleBreadcrumbs() {
		String param = this.getConfigManager().getParam(SystemConstants.CONFIG_PARAM_URL_STYLE);
		return (param != null && param.trim().equalsIgnoreCase(SystemConstants.CONFIG_PARAM_URL_STYLE_BREADCRUMBS));
	}

	protected ConfigInterface getConfigManager() {
		return _configManager;
	}
	public void setConfigManager(ConfigInterface configManager) {
		this._configManager = configManager;
	}

	protected ILangManager getLangManager() {
		return _langManager;
	}
	public void setLangManager(ILangManager langManager) {
		this._langManager = langManager;
	}

	protected IPageManager getPageManager() {
		return _pageManager;
	}
	public void setPageManager(IPageManager pageManager) {
		this._pageManager = pageManager;
	}

	private ConfigInterface _configManager;
	private IPageManager _pageManager;
	private ILangManager _langManager;

}
