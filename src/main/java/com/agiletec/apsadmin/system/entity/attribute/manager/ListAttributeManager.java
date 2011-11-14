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
package com.agiletec.apsadmin.system.entity.attribute.manager;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.ListAttribute;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.apsadmin.system.entity.attribute.AttributeTracer;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Manager class for the 'Multi-Language List' Attributes
 * @author E.Santoboni
 */
public class ListAttributeManager extends AbstractAttributeManager {
	
	@Override
	protected void updateAttribute(AttributeInterface attribute, AttributeTracer tracer, HttpServletRequest request) {
		this.manageListAttribute(false, true, null, attribute, tracer, request, null);
	}
	
	@Override
	protected void checkAttribute(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		super.checkAttribute(action, attribute, tracer, entity);
		this.manageListAttribute(true, false, action, attribute, tracer, null, entity);
	}
	
	private void manageListAttribute(boolean isCheck, boolean isUpdate, ActionSupport action, 
			AttributeInterface attribute, AttributeTracer tracer, HttpServletRequest request, IApsEntity entity) {
		List<Lang> langs = this.getLangManager().getLangs();
		for (int i=0;i<langs.size(); i++) {
			Lang lang = langs.get(i);
			List<AttributeInterface> attributeList = ((ListAttribute)attribute).getAttributeList(lang.getCode());
			for (int j=0; j<attributeList.size(); j++) {
				AttributeInterface attributeElement = attributeList.get(j);
				AttributeTracer elementTracer = (AttributeTracer) tracer.clone();
				elementTracer.setListElement(true);
				elementTracer.setListLang(lang);
				elementTracer.setListIndex(j);
				AbstractAttributeManager elementManager = (AbstractAttributeManager) this.getManager(attributeElement.getType());
				if (elementManager != null) {
					if (isCheck && !isUpdate) {
						elementManager.checkAttribute(action, attributeElement, elementTracer, entity);
					}
					if (!isCheck && isUpdate) {
						elementManager.updateAttribute(attributeElement, elementTracer, request);
					}
				}
			}
		}
	}
	
	@Override
	protected int getState(AttributeInterface attribute, AttributeTracer tracer) {
		boolean valued = true;
		List<Lang> langs = this.getLangManager().getLangs();
		for (int i=0;i<langs.size(); i++) {
			Lang lang = langs.get(i);
			List<AttributeInterface> attributeList = ((ListAttribute)attribute).getAttributeList(lang.getCode());
			if (attributeList == null || attributeList.size()==0) {
				valued = false;
				break;
			}
		}
		if (valued) {
			return VALUED_ATTRIBUTE_STATE;
		} else return EMPTY_ATTRIBUTE_STATE;
	}
	
	@Override
	protected void setExtraPropertyTo(AttributeManagerInterface manager) {
		super.setExtraPropertyTo(manager);
		((ListAttributeManager) manager).setLangManager(this.getLangManager());
	}
	
}