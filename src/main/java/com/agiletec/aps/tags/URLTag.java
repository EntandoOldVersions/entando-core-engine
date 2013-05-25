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
package com.agiletec.aps.tags;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.aps.tags.util.IParameterParentTag;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Generates the URL to a portal page. The URL is either displayed or placed in a variable.
 * The URL depends on the page attributes and the given language otherwise the current values
 * are used.
 * Use the sub-tag "ParameterTag" to insert parameters in the query string.
 */
public class URLTag extends TagSupport implements IParameterParentTag {
	
	/**
	 * Prepares a PageURL object; this object may comprehend several sub-tags
	 */
	@Override
	public int doStartTag() throws JspException {
		ServletRequest request =  this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) request.getAttribute(RequestContext.REQCTX);
		try {
			IURLManager urlManager = 
				(IURLManager) ApsWebApplicationUtils.getBean(SystemConstants.URL_MANAGER, this.pageContext);
			this._pageUrl = urlManager.createURL(reqCtx);
			if (_pageCode != null) {
				_pageUrl.setPageCode(_pageCode);
			}
			if (_langCode != null) {
				_pageUrl.setLangCode(_langCode);
			}
			if (_paramRepeat) {
				_pageUrl.setParamRepeat();
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "doStartTag");
			throw new JspException("Error during tag initialization", t);
		}
		return EVAL_BODY_INCLUDE;
	}
	
	/**
	 * Completes the URL generation making it available for immediate
	 * output or placing it in a variable
	 */
	@Override
	public int doEndTag() throws JspException {
		String url = _pageUrl.getURL();
		if (this.getVar() != null) {
			this.pageContext.setAttribute(this.getVar(), url);
		} else {
			try {
				this.pageContext.getOut().print(url);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "doEndTag");
				throw new JspException("Error closing tag", t);
			}
		}
		return EVAL_PAGE;
	}
	
	@Override
	public void addParameter(String name, String value) {
		this._pageUrl.addParam(name, value);
	}
	
	/**
	 * Return the language code
	 * @return The literal code
	 */
	public String getLang() {
		return _langCode;
	}

	/**
	 * Set the language code
	 * @param lang the literal code
	 */
	public void setLang(String lang) {
		this._langCode = lang;
	}
	
	/**
	 * Return the page code
	 * @return The page code
	 */
	public String getPage() {
		return _pageCode;
	}
	
	/**
	 * Set the page code
	 * @param page The page code
	 */
	public void setPage(String page) {
		this._pageCode = page;
	}
	
	/**
	 * Return the name of the variable containing the generated URL.
	 * @return The name of the variable
	 */
	public String getVar() {
		return _varName;
	}
	
	/**
	 * Set the name of the variable containing the generated URL.
	 * @param var The name of the variable
	 */
	public void setVar(String var) {
		this._varName = var;
	}
	
	/**
	 * Repeats the parameters of the previous request when true, false otherwise.
	 * @return Returns the parRepeat.
	 */
	public boolean isParamRepeat() {
		return _paramRepeat;
	}
	
	/**
	 * Toggles the repetition of the previous query string parameters
	 * @param paramRepeat True enables the repetition, false otherwise.
	 */
	public void setParamRepeat(boolean paramRepeat) {
		this._paramRepeat = paramRepeat;
	}
	
	public void release() {
		_langCode = null;
		_pageCode = null;
		_varName = null;
		_paramRepeat = false;
		_pageUrl = null;
	}
	
	private String _langCode;
	private String _pageCode;
	private String _varName;
	private boolean _paramRepeat;
	private PageURL _pageUrl;

}
