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
package org.entando.entando.apsadmin.tags;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.IEntityManager;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.util.ApsWebApplicationUtils;

import com.agiletec.apsadmin.tags.AbstractObjectInfoTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;

/**
 * Returns the list of entity types through the code and the entity service name.
 * @author E.Santoboni
 */
public class EntityTypesInfoTag extends AbstractObjectInfoTag {
	
	@Override
	protected Object getMasterObject(String keyValue) throws Throwable {
		String managerNameValue = (String) super.findValue(keyValue, String.class);
		try {
			IEntityManager entityManager = (IEntityManager) ApsWebApplicationUtils.getBean(managerNameValue, this.pageContext);
			if (null != entityManager) {
				List<IApsEntity> entityTypes = new ArrayList<IApsEntity>();
				Map<String, IApsEntity> typeMap = entityManager.getEntityPrototypes();
				if (null != typeMap) {
					BeanComparator c = new BeanComparator(this.getOrderBy());
					entityTypes.addAll(typeMap.values());
					Collections.sort(entityTypes, c);
				}
				return entityTypes;
			} else {
				ApsSystemUtils.getLogger().finest("Null entity manager : service name '" + managerNameValue + "'");
			}
		} catch (Throwable t) {
			String message = "Error extracting entity types : entity manager '" + managerNameValue + "'";
			ApsSystemUtils.logThrowable(t, this, "getMasterObject", message);
			throw new ApsSystemException(message, t);
		}
		return null;
	}
	
	protected String getEntityManagerName() {
		return super.getKey();
	}
	public void setEntityManagerName(String entityManagerName) {
		super.setKey(entityManagerName);
	}
	
	public String getOrderBy() {
		return _orderBy;
	}
	public void setOrderBy(String orderBy) {
		this._orderBy = orderBy;
	}
	
	private String _orderBy = "typeDescr";
	
}