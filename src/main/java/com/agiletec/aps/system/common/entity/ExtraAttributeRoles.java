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
package com.agiletec.aps.system.common.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.IManager;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeRole;
import com.agiletec.aps.system.common.entity.parse.AttributeRoleDOM;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * The Class of the extra attribute roles.
 * @author E.Santoboni
 */
public final class ExtraAttributeRoles extends AbstractExtraAttributeSupportObject {
	
	protected void executeLoading(Map<String, AttributeRole> collectionToFill, IEntityManager entityManager) throws ApsSystemException {
		if (!((IManager) entityManager).getName().equals(((IManager) this.getEntityManagerDest()).getName())) {
			return;
		}
		AttributeRoleDOM dom = new AttributeRoleDOM();
		try {
			String xml = super.extractXml();
			Map<String, AttributeRole> attributeRoles = dom.extractRoles(xml, this.getDefsFilePath());
			List<AttributeRole> roles = new ArrayList<AttributeRole>(attributeRoles.values());
			for (int i = 0; i < roles.size(); i++) {
				AttributeRole role = roles.get(i);
				if (collectionToFill.containsKey(role.getName())) {
					ApsSystemUtils.getLogger().severe("You can't override existing attribute role : " 
							+ role.getName() + " - " + role.getDescription());
				} else {
					collectionToFill.put(role.getName(), role);
					ApsSystemUtils.getLogger().info("Added new attribute role : " 
							+ role.getName() + " - " + role.getDescription());
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "executeLoading", "Error loading extra attribute Roles");
		}
	}
	
}