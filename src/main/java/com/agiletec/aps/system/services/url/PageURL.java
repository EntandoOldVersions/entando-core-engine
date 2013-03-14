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
package com.agiletec.aps.system.services.url;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;

/**
 * Rappresenta un URL ad una pagina del sistema. Oggetti di questa classe
 * devono avere un ciclo di vita limitato ad una richiesta; non devono
 * essere memorizzati in modo permanente.
 * @author
 */
public class PageURL {
	
	/**
	 * Costruttore utilizzato dalla factory di questa classe 
	 * (IURLManager). Non deve essere utilizzato direttamente.
	 * @param urlManager The Url Manager
	 * @param reqCtx The Request Context
	 */
	public PageURL(IURLManager urlManager, RequestContext reqCtx) {
		this._urlManager = urlManager;
		this._reqCtx = reqCtx;
	}
	
	/**
	 * Imposta il codice della lingua richiesta.
	 * @param langCode Il codice della lingua da impostare.
	 */
	public void setLangCode(String langCode) {
		this._langCode = langCode;
	}

	/**
	 * Imposta la lingua richiesta. L'effetto è equivalente
	 * alla chiamata setLangCode(lang.getCode()) .
	 * @param lang Il codice della lingua da impostare.
	 */
	public void setLang(Lang lang) {
		this._langCode = lang.getCode();
	}
	/**
	 * Imposta il codice della pagina richiesta.
	 * @param pageCode Il codice della pagina da impostare.
	 */
	public void setPageCode(String pageCode) {
		this._pageCode = pageCode;
	}

	/**
	 * Imposta la pagina richiesta. L'effetto è equivalente
	 * alla chiamata setPageCode(page.getCode()) .
	 * @param page La pagina da impostare.
	 */
	public void setPage(IPage page) {
		this._pageCode = page.getCode();
	}
	
	/**
	 * Restituisce la lingua precedentemente impostata.
	 * @return Il codice lingua, o null se non è stata impostata.
	 */
	public String getLangCode() {
		return _langCode;
	}

	/**
	 * @return Il codice pagina, o null se non è stata impostata.
	 */
	public String getPageCode() {
		return _pageCode;
	}

	/**
	 * Aggiunge un parametro.
	 * @param name Il nome del parametro.
	 * @param value Il valore del parametro.
	 */
	public void addParam(String name, String value) {
		if (name != null) {
			if (this._params == null) {
				this._params = new HashMap();
			}
			String val = (value == null ? "" : value);
			this._params.put(name, val);
		}
	}
	
	/**
	 * Restituisce la mappa dei parametri, indicizzati in base al nome.
	 * @return La mappa dei parametri.
	 */
	public Map getParams() {
		return _params;
	}
	
	/**
	 * Restituisce l'URL utilizzabile. La costruzione dell'URL
	 * è delegata all'implementazione della classe AbstractURLManager.
	 * @return L'URL generato.
	 */
	public String getURL() {
		return this._urlManager.getURLString(this, _reqCtx);
	}
	
	/**
	 * Ripete i parametri della request precedente settandoli nella mappa dei parametri.
	 */
	public void setParamRepeat() {
		if (null == this._reqCtx) return;
		HttpServletRequest req = this._reqCtx.getRequest();
		Map params = req.getParameterMap();
		if (null != params && !params.isEmpty()) {
			Iterator keyIter = params.keySet().iterator();
			while (keyIter.hasNext()) {
				String key = (String) keyIter.next();
				this.addParam(key, req.getParameter(key));
			}
		}
	}
	
	public boolean isEscapeAmp() {
		return _escapeAmp;
	}
	public void setEscapeAmp(boolean escapeAmp) {
		this._escapeAmp = escapeAmp;
	}
	
	private IURLManager _urlManager;
	private RequestContext _reqCtx;
	private String _pageCode;
	private String _langCode;
	private Map _params;
	
	private boolean _escapeAmp = true;
	
}
