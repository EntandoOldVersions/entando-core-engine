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
package com.agiletec.plugins.jacms.apsadmin.content.preview;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.widget.ContentViewerHelper;
import com.agiletec.plugins.jacms.apsadmin.content.ContentActionConstants;

import javax.servlet.http.HttpServletRequest;

/**
 * Classe helper per la showlet di erogazione contenuti per la funzione preview da redazione contenuti.
 * La classe deriva direttamente dalla classe helper {@link ContentViewerHelper} utilizzata nel front-end di portale per le funzioni di renderizzazione contenuti.
 * @author E.Santoboni
 */
public class ContentPreviewViewerHelper extends ContentViewerHelper {
	
	@Override
	public String getRenderedContent(String contentId, String modelId, RequestContext reqCtx) throws ApsSystemException {
		String renderedContent = "";
		HttpServletRequest request = reqCtx.getRequest();
		try {
			String contentOnSessionMarker = (String) request.getAttribute("contentOnSessionMarker");
			if (null == contentOnSessionMarker || contentOnSessionMarker.trim().length() == 0) {
				contentOnSessionMarker = request.getParameter("contentOnSessionMarker");
			}
			Lang currentLang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
			String langCode = currentLang.getCode();
			Widget widget = (Widget) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_WIDGET);
			Content contentOnSession = (Content) request.getSession()
					.getAttribute(ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT_PREXIX + contentOnSessionMarker);
			contentId = (contentOnSession.getId() == null ? contentOnSession.getTypeCode()+"123" : contentOnSession.getId());
			ApsProperties showletConfig = widget.getConfig();
			modelId = this.extractModelId(contentId, modelId, showletConfig);
			if (null != contentId && null != modelId) {
				long longModelId = new Long(modelId).longValue();
				this.setStylesheet(longModelId, reqCtx);
				renderedContent = this.getContentDispenser().getRenderedContent(contentId, longModelId, langCode, reqCtx);
			} else {
				ApsSystemUtils.getLogger().warn(
						"Parametri visualizzazione contenuto incompleti: contenuto=" + contentId + " modello="
								+ modelId);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getRenderedContent");
			throw new ApsSystemException(
					"Errore in fase di preparazione del contenuto per preview", t);
		}
		return renderedContent;
	}
	
}
