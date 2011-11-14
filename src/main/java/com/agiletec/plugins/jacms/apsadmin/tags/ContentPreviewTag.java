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
package com.agiletec.plugins.jacms.apsadmin.tags;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.showlet.IContentViewerHelper;
import com.agiletec.plugins.jacms.aps.tags.ContentTag;

/**
 * This returns the content ready for the preview functionality
 * This tags comes from the ContentTag class used in the front-end to render a content.
 * @author E.Santoboni
 */
public class ContentPreviewTag extends ContentTag {
	
	@Override
	public int doStartTag() throws JspException {
		ServletRequest request =  this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) request.getAttribute(RequestContext.REQCTX);
		try {
			IContentViewerHelper helper = (IContentViewerHelper) ApsWebApplicationUtils.getBean(JacmsSystemConstants.CONTENT_PREVIEW_VIEWER_HELPER, this.pageContext);
			String renderedContent = helper.getRenderedContent(null, null, reqCtx);
			this.pageContext.getOut().print(renderedContent);
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "doStartTag");
			throw new JspException("Error detected during tag initialisation", e);
		}
		return EVAL_PAGE;
	}
	
}
