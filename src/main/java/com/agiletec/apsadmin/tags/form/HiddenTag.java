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
package com.agiletec.apsadmin.tags.form;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * 
 * @version 1.0
 * @author M.Minnai
 */
public class HiddenTag extends org.apache.struts2.views.jsp.ui.HiddenTag {
	
	@Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new com.agiletec.apsadmin.tags.util.Hidden(stack, req, res);
    }
	
}
