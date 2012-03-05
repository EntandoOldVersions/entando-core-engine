/*
*
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* Entando is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.plugins.jacms.aps.system.services.renderer;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.renderer.BaseEntityRenderer;
import com.agiletec.aps.system.common.renderer.EntityWrapper;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.ContentModel;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.IContentModelManager;

/**
 * Servizio di renderizzazione contenuti.
 * @author M.Diana - W.Ambu - E.Santoboni
 */
public class BaseContentRenderer extends BaseEntityRenderer implements IContentRenderer {
	
	@Override
	public String render(Content content, long modelId, String langCode, RequestContext reqCtx) {
		String contentModel = this.getModelShape(modelId);
		return super.render(content, contentModel, langCode, true);
	}
	
	@Override
	protected EntityWrapper getEntityWrapper(IApsEntity entity) {
		return new ContentWrapper((Content)entity);
	}
	
	protected String getModelShape(long modelId) {
		ContentModel model = this.getContentModelManager().getContentModel(modelId);
		String shape = null;
		if (model != null) {
			shape = model.getContentShape();
		} 
		if (shape == null) {
			shape = "Content model " + modelId + " undefined";
			ApsSystemUtils.getLogger().severe(shape);
		}
		return shape;
	}
	
	@Override
	protected String getEntityWrapperContextName() {
		return "content";
	}
	
	protected IContentModelManager getContentModelManager() {
		return _contentModelManager;
	}
	public void setContentModelManager(IContentModelManager contentModelManager) {
		this._contentModelManager = contentModelManager;
	}
	
	private IContentModelManager _contentModelManager;
	
}