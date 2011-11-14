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
package com.agiletec.apsadmin.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.apsadmin.system.ApsAdminSystemConstants;

/**
 * Print the current value of the counter.
 * The counter should be used inside the tabindex attribute in HTML tags that allow its use.
 * @author E.Santoboni
 */
public class BackendGuiClientTag extends TagSupport {
	
	@Override
	public int doEndTag() throws JspException {
		try {
			HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
			String client = (String) request.getSession().getAttribute(ApsAdminSystemConstants.SESSION_PARAM_BACKEND_GUI_CLIENT);
			if (null == client || client.trim().length() == 0) {
				client = ApsAdminSystemConstants.BACKEND_GUI_CLIENT_NORMAL;
			}
			if (null != this.getVar() && this.getVar().trim().length() > 0) {
				this.pageContext.getRequest().setAttribute(this.getVar(), client);
			} else {
				pageContext.getOut().print(client);
			}
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "doEndTag");
			throw new JspException("Error on ClientTag", e);
		}
		return super.doEndTag();
	}
	
	/**
	 * Set the name used to reference the value of the gui client code pushed into the Value Stack.
	 * @return The name of the variable
	 */
	public void setVar(String var) {
		this._var = var;
	}
	
	/**
	 * Get the name used to reference the value of the gui client code pushed into the Value Stack.
	 * @return The name of the variable
	 */
	public String getVar() {
		return _var;
	}
	
	private String _var;
	
}