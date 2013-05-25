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

import org.apache.taglibs.standard.tag.common.core.OutSupport;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.system.services.showlettype.ShowletType;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Returns informations about the showlet where the tag resides.
 * The "param" attribute acceptes the following values:
 * - "code" returns the code of the associated showlet type (empty if none associated)<br/>
 * - "title" returns the name of the associated showlet type (empty if none associated)<br/>
 * - "config" returns the value of the configuration parameter declared in the "configParam" attribute<br/>
 * To obtain information about a showlet placed in a frame other than the current, use the "frame" attribute.
 * @author E.Santoboni - E.Mezzano
 */
@SuppressWarnings("serial")
public class CurrentShowletTag extends OutSupport {
	
	@Override
	public int doStartTag() throws JspException {
		try {
			Showlet showlet = this.extractShowlet();
			if (null == showlet) return super.doStartTag();
			String value = null;
			if ("code".equals(this.getParam())) {
				value = showlet.getType().getCode();
			} else if ("title".equals(this.getParam())) {
				value = this.extractTitle(showlet);
			} else if ("config".equals(this.getParam())) {
				ApsProperties config = showlet.getConfig();
				if (null != config) {
					value = config.getProperty(this.getConfigParam());
				}
			}
			if (null != value) {
				String var = this.getVar();
				if (null == var || "".equals(var)) {
					if (this.getEscapeXml()) {
						out(this.pageContext, this.getEscapeXml(), value);
					} else {
						this.pageContext.getOut().print(value);
					}
				} else {
					this.pageContext.setAttribute(this.getVar(), value);
				}
			}
		} catch (Throwable t) {
			String msg = "Error detected during showlet preprocessing";
			ApsSystemUtils.logThrowable(t, this, "doEndTag", msg);
			throw new JspException(msg, t);
		}
		return super.doStartTag();
	}
	
	private String extractTitle(Showlet showlet) {
		ServletRequest request = this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) request.getAttribute(RequestContext.REQCTX);
		Lang currentLang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
		ShowletType type = showlet.getType();
		String value = type.getTitles().getProperty(currentLang.getCode());
		if (null == value || value.trim().length() == 0) {
			ILangManager langManager = 
				(ILangManager) ApsWebApplicationUtils.getBean(SystemConstants.LANGUAGE_MANAGER, this.pageContext);
			Lang defaultLang = langManager.getDefaultLang();
			value = type.getTitles().getProperty(defaultLang.getCode());
		}
		return value;
	}
	
	private Showlet extractShowlet() {
		ServletRequest req =  this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) req.getAttribute(RequestContext.REQCTX);
		Showlet showlet = null;
		if (this.getFrame() < 0) {
			showlet = (Showlet) reqCtx.getExtraParam((SystemConstants.EXTRAPAR_CURRENT_SHOWLET));
		} else {
			IPage currentPage = (IPage) reqCtx.getExtraParam((SystemConstants.EXTRAPAR_CURRENT_PAGE));
			Showlet[] showlets = currentPage.getShowlets();
			if (showlets.length > this.getFrame()) {
				showlet = showlets[this.getFrame()];
			}
		}
		return showlet;
	}
	
	@Override
	public void release() {
		super.release();
		this._param = null;
		this._configParam = null;
		this._var = null;
		this._frame = -1;
		super.escapeXml = true;
	}
	
	public String getParam() {
		return _param;
	}
	public void setParam(String param) {
		this._param = param;
	}
	
	public String getConfigParam() {
		return _configParam;
	}
	public void setConfigParam(String configParam) {
		this._configParam = configParam;
	}
	
	public String getVar() {
		return _var;
	}
	public void setVar(String var) {
		this._var = var;
	}
	
	public int getFrame() {
		return _frame;
	}
	public void setFrame(int frame) {
		this._frame = frame;
	}
	
	/**
	 * Checks if the special characters must be escaped
	 * @return True if the special characters must be escaped
	 */
	public boolean getEscapeXml() {
		return super.escapeXml;
	}
	
	/**
	 * Toggles the escape of the special characters of the result.
	 * @param escapeXml True to perform the escaping, false otherwise.
	 */
	public void setEscapeXml(boolean escapeXml) {
		super.escapeXml = escapeXml;
	}
	
	private String _param;
	private String _configParam;
	private String _var;
	
	private int _frame = -1;
	
}