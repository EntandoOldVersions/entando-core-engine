/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
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
package com.agiletec.plugins.jacms.apsadmin.tags;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.tags.ExecShowletTag;
import com.agiletec.aps.tags.util.IFrameDecoratorContainer;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.apsadmin.content.ContentActionConstants;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.entando.entando.aps.system.services.page.IPage;
import org.entando.entando.aps.system.services.page.Widget;

/**
 * This tag allows the preliminary execution of the showlet so to show the preview of the contents
 * within the content administration pages in the backend.
 * This tag class extends the {@link ExecShowletTag} class used in the front-end to build the pages of the portal.
 * @author E.Santoboni
 */
public class ContentPreviewExecShowletTag extends ExecShowletTag {
	
	@Override
	protected void includeShowlet(RequestContext reqCtx, Widget widget, List<IFrameDecoratorContainer> decorators) throws Throwable {
		HttpServletRequest request = reqCtx.getRequest();
		String contentOnSessionMarker = (String) request.getAttribute("contentOnSessionMarker");
		if (null == contentOnSessionMarker || contentOnSessionMarker.trim().length() == 0) {
			contentOnSessionMarker = request.getParameter("contentOnSessionMarker");
		}
		Content contentOnSession = (Content) request.getSession()
				.getAttribute(ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT_PREXIX + contentOnSessionMarker);
		if (contentOnSession!=null && widget != null 
				&& "viewerConfig".equals(widget.getType().getAction())) {
			IPage currentPage = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
			if ((currentPage.getCode().equals(contentOnSession.getViewPage()) && (widget.getConfig() == null || widget.getConfig().size() == 0)) 
					|| (widget.getConfig() != null && widget.getConfig().get("contentId") != null && widget.getConfig().get("contentId").equals(contentOnSession.getId()))) {
				String path = CONTENT_VIEWER_JSP;
				reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_SHOWLET, widget);
				this.pageContext.include(path.toString());
				return;
			}
		}
		super.includeShowlet(reqCtx, widget, decorators);
	}
	
	private final String CONTENT_VIEWER_JSP="/WEB-INF/plugins/jacms/apsadmin/jsp/content/preview/content_viewer.jsp";
	
}