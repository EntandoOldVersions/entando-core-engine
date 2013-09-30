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
package org.entando.entando.apsadmin.tags;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsWebApplicationUtils;

import com.opensymphony.xwork2.util.ValueStack;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.struts2.views.jsp.StrutsBodyTagSupport;
import org.entando.entando.aps.system.services.actionlog.IActionLogManager;

/**
 * Return the list (of integer) for the activity stream of the current user.
 * @author E.Santoboni
 */
public class ActivityStreamTag extends StrutsBodyTagSupport {
	
	@Override
	public int doEndTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
		try {
			IActionLogManager loggerManager = (IActionLogManager) ApsWebApplicationUtils.getBean(SystemConstants.ACTION_LOGGER_MANAGER, this.pageContext);
			UserDetails currentUser = (UserDetails) request.getSession().getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER);
			List<Integer> ids = loggerManager.getActivityStream(currentUser);
			if (null != this.getVar()) {
				ValueStack stack = this.getStack();
				stack.getContext().put(this.getVar(), ids);
	            stack.setValue("#attr['" + this.getVar() + "']", ids, false);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "doStartTag");
			throw new JspException("Error on doStartTag", t);
		}
		return super.doEndTag();
	}
	
	public String getVar() {
		return _var;
	}
	public void setVar(String var) {
		this._var = var;
	}
	
	private String _var;
	
}