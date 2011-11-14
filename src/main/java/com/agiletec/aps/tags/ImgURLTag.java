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
package com.agiletec.aps.tags;

/**
 * Return the URL of the static images 
 */
public class ImgURLTag extends ResourceURLTag {
	
	public int doStartTag() throws javax.servlet.jsp.JspException {
		this.setFolder("static/img/");
		return EVAL_BODY_INCLUDE;
	}
	
}
