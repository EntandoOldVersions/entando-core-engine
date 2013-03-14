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
package com.agiletec.plugins.jacms.apsadmin.content.attribute.action.list;

import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.ListAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.ListAttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.MonoListAttribute;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.apsadmin.content.ContentActionConstants;
import com.agiletec.plugins.jacms.apsadmin.content.helper.IContentActionHelper;

/**
 * Classi action base delegata 
 * alla gestione delle operazione sugli attributi di contenuto tipo lista.
 * @author E.Santoboni
 */
public class ListAttributeAction extends com.agiletec.apsadmin.system.entity.attribute.action.list.ListAttributeAction {
	
	@Override
	public String addListElement() {
		try {
			super.addListElement();
			Content content = this.getContent();
			int index = -1;
			ListAttributeInterface currentAttribute = (ListAttributeInterface) content.getAttribute(this.getAttributeName());
			String nestedType = currentAttribute.getNestedAttributeTypeCode();
			if (!nestedType.equals("Attach") && !nestedType.equals("Image") && !nestedType.equals("Link")) {
				return SUCCESS;
			}
			if (currentAttribute instanceof MonoListAttribute) {
				List<AttributeInterface> attributes = ((MonoListAttribute) currentAttribute).getAttributes();
				index = attributes.size() - 1;
			} else if (currentAttribute instanceof ListAttribute) {
				List<AttributeInterface> attributes = ((ListAttribute) currentAttribute).getAttributeList(this.getListLangCode());
				index = attributes.size() - 1;
			}
			this.setElementIndex(index);
			if (nestedType.equals("Attach") || nestedType.equals("Image")) {
				this.setResourceTypeCode(nestedType);
				return "chooseResource";
			} else {
				return "chooseLink";
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "addListElement");
			return FAILURE;
		}
	}
	
	@Override
	protected IApsEntity getCurrentApsEntity() {
		Content content = this.updateContentOnSession();
		return content;
	}
	
	public Content getContent() {
		return (Content) this.getRequest().getSession().getAttribute(ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT);
	}
	
	protected Content updateContentOnSession() {
		Content content = this.getContent();
		this.getContentActionHelper().updateEntity(content, this.getRequest());
		return content;
	}
	
	public String getEntryContentAnchorDest() {
		return "contentedit_" + this.getListLangCode() + "_" + this.getAttributeName();
	}
	
	public String getResourceTypeCode() {
		return _resourceTypeCode;
	}
	protected void setResourceTypeCode(String resourceTypeCode) {
		this._resourceTypeCode = resourceTypeCode;
	}
	
	/**
	 * Restituisce la classe helper della gestione contenuti.
	 * @return La classe helper della gestione contenuti.
	 */
	protected IContentActionHelper getContentActionHelper() {
		return _contentActionHelper;
	}
	
	/**
	 * Setta la classe helper della gestione contenuti.
	 * @param contentActionHelper La classe helper della gestione contenuti.
	 */
	public void setContentActionHelper(IContentActionHelper contentActionHelper) {
		this._contentActionHelper = contentActionHelper;
	}
	
	private String _resourceTypeCode;
	
	private IContentActionHelper _contentActionHelper;
	
}