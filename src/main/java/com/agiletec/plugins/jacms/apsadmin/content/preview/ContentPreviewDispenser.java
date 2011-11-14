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
package com.agiletec.plugins.jacms.apsadmin.content.preview;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.dispenser.BaseContentDispenser;
import com.agiletec.plugins.jacms.apsadmin.content.ContentActionConstants;

/**
 * Fornisce i contenuti formattati per la funzione preview da redazione contenuti.
 * La classe deriva direttamente dalla classe utilizzata nel front-end {@link BaseContentDispenser} di portale per le funzioni di renderizzazione contenuti.
 * @author E.Santoboni
 */
public class ContentPreviewDispenser extends BaseContentDispenser {
	
	@Override
	public String getRenderedContent(String contentId, long modelId, String langCode, RequestContext reqCtx) {
		Content contentOnSession = (Content) reqCtx.getRequest().getSession().getAttribute(ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT);
		String renderedContent = this.getRenderedContent(contentOnSession, modelId, langCode, reqCtx);
		return renderedContent;
	}
	
	protected String getRenderedContent(Content content, long modelId, String langCode, RequestContext reqCtx) {
		String renderedContent = null;
		boolean ok = false;
		try {
			renderedContent = this.getContentRender().render(content, modelId, langCode, reqCtx);
			ok = true;
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().throwing(this.getClass().getName(), "getRenderedContent", t);
		}
		if (!ok) {
			ApsSystemUtils.getLogger().warning("Impossibile fornire preview per il contenuto " + content.getId());
			return "";
		}
		renderedContent = this.getLinkResolverManager().resolveLinks(renderedContent, reqCtx);
		return renderedContent;
	}
	
}