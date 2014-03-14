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
package org.entando.entando.apsadmin.portal.model;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.pagemodel.PageModel;

import java.util.List;
import java.util.Map;

import org.entando.entando.apsadmin.portal.model.helper.IPageModelActionHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author E.Santoboni
 */
public class PageModelAction extends AbstractPageModelAction {
	
	private static final Logger _logger = LoggerFactory.getLogger(PageModelAction.class);
	
	public String trash() {
		try {
			String check = this.checkModelForDelete();
			if (null != check) return check;
		} catch (Throwable t) {
			_logger.error("error in trash", t);
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public String delete() {
		try {
			String check = this.checkModelForDelete();
			if (null != check) return check;
			this.getPageModelManager().deletePageModel(this.getCode());
		} catch (Throwable t) {
			_logger.error("error in delete", t);
			return FAILURE;
		}
		return SUCCESS;
	}
	
	protected String checkModelForDelete() throws ApsSystemException {
		PageModel model = super.getPageModel(this.getCode());
		if (null == model) {
			this.addActionError(this.getText("error.pageModel.notExist"));
			return "pageModelList";
		}
		Map<String, List<Object>> references = this.getPageModelActionHelper().getReferencingObjects(model, this.getRequest());
		if (null != references && references.size() > 0) {
			this.setReferences(references);
	        return "references";
		}
		return null;
	}
	
	public String getCode() {
		return _code;
	}
	public void setCode(String code) {
		this._code = code;
	}
	
	public Map<String, List<Object>> getReferences() {
		return _references;
	}
	protected void setReferences(Map<String, List<Object>> references) {
		this._references = references;
	}
	
	protected IPageModelActionHelper getPageModelActionHelper() {
		return _pageModelActionHelper;
	}
	public void setPageModelActionHelper(IPageModelActionHelper pageModelActionHelper) {
		this._pageModelActionHelper = pageModelActionHelper;
	}
	
	private String _code;
	
	private Map<String, List<Object>> _references;
	
	private IPageModelActionHelper _pageModelActionHelper;
	
}