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
package com.agiletec.apsadmin.system.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.BooleanAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.DateAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.ITextAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.NumberAttribute;
import com.agiletec.aps.util.DateConverter;
import com.agiletec.apsadmin.system.BaseActionHelper;
import com.agiletec.apsadmin.system.entity.attribute.manager.AttributeManagerInterface;
import com.agiletec.apsadmin.util.CheckFormatUtil;
import com.opensymphony.xwork2.ActionSupport;

/**
 * This abstract class supports all the helper classes that, in turn, support those
 * classes which handle elements built with the "ApsEntity' entries.
 * @author E.Santoboni
 */
public class EntityActionHelper extends BaseActionHelper implements IEntityActionHelper {
	
	@Override
	public void updateEntity(IApsEntity currentEntity, HttpServletRequest request) {
		this.updateEntityAttributes(currentEntity, request);
	}
	
	@Override
	public void updateEntityAttributes(IApsEntity currentEntity, HttpServletRequest request) {
		try {
			List<AttributeInterface> attributes = currentEntity.getAttributeList();
			for (int i=0; i<attributes.size(); i++) {
				AttributeInterface attribute = attributes.get(i);
				if (attribute.isActive()) {
					String attributeType = attribute.getType();
					AttributeManagerInterface attributeManager = this.getManager(attributeType);
					if (attributeManager != null) {
						attributeManager.updateEntityAttribute(attribute, this.getAttributeManagers(), request);
					}
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().throwing("EntityActionHelper", "updateEntityAttributes", t);
			throw new RuntimeException("Errore in updateEntityAttributes", t);
		}
	}
	
	@Override
	public void scanEntity(IApsEntity currentEntity, ActionSupport action) {
		this.scanEntityAttributes(currentEntity, action);
	}
	
	@Override
	public void scanEntityAttributes(IApsEntity currentEntity, ActionSupport action) {
		List<AttributeInterface> attributes = currentEntity.getAttributeList();
		for (int i=0; i<attributes.size(); i++) {
			AttributeInterface attribute = attributes.get(i);
			if (attribute.isActive()) {
				String attributeType = attribute.getType();
				AttributeManagerInterface attributeManager = this.getManager(attributeType);
				if (attributeManager != null) {
					attributeManager.checkEntityAttribute(action, this.getAttributeManagers(), attribute, currentEntity);
				}
			}
		}
	}
	
	public AttributeManagerInterface getManager(String typeCode) {
		return this.getAttributeManagers().get(typeCode);
	}
	
	@Override
	public EntitySearchFilter[] getSearchFilters(AbstractApsEntityFinderAction entityFinderAction, IApsEntity prototype) {
		EntitySearchFilter[] filters = new EntitySearchFilter[0];
		List<AttributeInterface> contentAttributes = prototype.getAttributeList();
		for (int i=0; i<contentAttributes.size(); i++) {
			AttributeInterface attribute = contentAttributes.get(i);
			if (attribute.isActive() && attribute.isSearcheable()) {
				if (attribute instanceof ITextAttribute) {
					String insertedText = entityFinderAction.getSearchFormFieldValue(attribute.getName()+"_textFieldName");
					if (null != insertedText && insertedText.trim().length() > 0) {
						EntitySearchFilter filterToAdd = new EntitySearchFilter(attribute.getName(), true, insertedText.trim(), true);
						filters = this.addFilter(filters, filterToAdd);
					}
				} else if (attribute instanceof DateAttribute) {
					Date dateStart = this.getDateSearchFormValue(entityFinderAction, attribute, "_dateStartFieldName", true);
					Date dateEnd = this.getDateSearchFormValue(entityFinderAction, attribute, "_dateEndFieldName", false);
					if (null != dateStart || null != dateEnd) {
						EntitySearchFilter filterToAdd = new EntitySearchFilter(attribute.getName(), true, dateStart, dateEnd);
						filters = this.addFilter(filters, filterToAdd);
					}
				} else if (attribute instanceof BooleanAttribute) {
					String booleanValue = entityFinderAction.getSearchFormFieldValue(attribute.getName()+"_booleanFieldName");
					if (null != booleanValue && booleanValue.trim().length() > 0) {
						EntitySearchFilter filterToAdd = new EntitySearchFilter(attribute.getName(), true, booleanValue, false);
						filters = this.addFilter(filters, filterToAdd);
					}
				} else if (attribute instanceof NumberAttribute) {
					BigDecimal numberStart = this.getNumberSearchFormValue(entityFinderAction, attribute, "_numberStartFieldName", true);
					BigDecimal numberEnd = this.getNumberSearchFormValue(entityFinderAction, attribute, "_numberEndFieldName", false);
					if (null != numberStart || null != numberEnd) {
						EntitySearchFilter filterToAdd = new EntitySearchFilter(attribute.getName(), true, numberStart, numberEnd);
						filters = this.addFilter(filters, filterToAdd);
					}
				}
			}
		}
		return filters;
	}
	
	private Date getDateSearchFormValue(AbstractApsEntityFinderAction entityFinderAction, 
			AttributeInterface attribute, String dateFieldNameSuffix, boolean start) {
		String inputFormName = attribute.getName()+dateFieldNameSuffix;
		String insertedDate = entityFinderAction.getSearchFormFieldValue(inputFormName);
		Date date = null;
		if (insertedDate != null && insertedDate.trim().length() > 0) {
			if (CheckFormatUtil.isValidDate(insertedDate.trim())) {
				date = DateConverter.parseDate(insertedDate.trim(), "dd/MM/yyyy");
			} else {
				String[] args = {attribute.getName()};
				if (start) {
					entityFinderAction.addFieldError(inputFormName, entityFinderAction.getText("error.attribute.startDate.invalid", args));
				} else {
					entityFinderAction.addFieldError(inputFormName, entityFinderAction.getText("error.attribute.endDate.invalid",args));
				}
			}
		}
		return date;
	}
	
	private BigDecimal getNumberSearchFormValue(AbstractApsEntityFinderAction entityFinderAction, 
			AttributeInterface attribute, String numberFieldNameSuffix, boolean start) {
		String inputFormName = attribute.getName()+numberFieldNameSuffix;
		String insertedNumberString = entityFinderAction.getSearchFormFieldValue(inputFormName);
		BigDecimal bigdecimal = null;
		if (insertedNumberString != null && insertedNumberString.trim().length() > 0) {
			if (CheckFormatUtil.isValidNumber(insertedNumberString.trim())) {
				bigdecimal = new BigDecimal(Integer.parseInt(insertedNumberString.trim()));
			} else {
				String[] args = {attribute.getName()};
				if (start) {
					entityFinderAction.addFieldError(inputFormName, entityFinderAction.getText("error.attribute.startNumber.invalid", args));
				} else {
					entityFinderAction.addFieldError(inputFormName, entityFinderAction.getText("error.attribute.endNumber.invalid", args));
				}
			}
		}
		return bigdecimal;
	}
	
	private EntitySearchFilter[] addFilter(EntitySearchFilter[] filters, EntitySearchFilter filterToAdd) {
		int len = filters.length;
		EntitySearchFilter[] newFilters = new EntitySearchFilter[len + 1];
		for(int i=0; i < len; i++){
			newFilters[i] = filters[i];
		}
		newFilters[len] = filterToAdd;
		return newFilters;
	}
	
	protected Map<String, AttributeManagerInterface> getAttributeManagers() {
		return _attributeManagers;
	}
	
	public void setAttributeManagers(Map<String, AttributeManagerInterface> attributeManagers) {
		this._attributeManagers = attributeManagers;
	}
	
	public void setExtraAttributeManagers(Map<String, AttributeManagerInterface> extraAttributeManagers) {
		this.getAttributeManagers().putAll(extraAttributeManagers);
	}
	
	private Map<String, AttributeManagerInterface> _attributeManagers;
	
}
