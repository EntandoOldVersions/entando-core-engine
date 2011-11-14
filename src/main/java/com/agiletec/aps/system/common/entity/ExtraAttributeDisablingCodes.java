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
import com.agiletec.aps.system.common.entity.parse.AttributeDisablingCodesDOM;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * The Class of the extra attribute disabling codes.
 * @author E.Santoboni
 */
public final class ExtraAttributeDisablingCodes extends AbstractExtraAttributeSupportObject {
	
	protected void executeLoading(Map<String, String> collectionToFill, IEntityManager entityManager) throws ApsSystemException {
		if (!((IManager) entityManager).getName().equals(((IManager) this.getEntityManagerDest()).getName())) {
			return;
		}
		try {
			String xml = super.extractXml();
			AttributeDisablingCodesDOM dom = new AttributeDisablingCodesDOM();
			Map<String, String> codeMap = dom.extractDisablingCodes(xml, this.getDefsFilePath());
			List<String> codes = new ArrayList<String>(codeMap.keySet());
			for (int i = 0; i < codes.size(); i++) {
				String code = codes.get(i);
				if (collectionToFill.containsKey(code)) {
					ApsSystemUtils.getLogger().severe("You can't override existing disabling code : " + code + 
							" - " + collectionToFill.get(code));
				} else {
					collectionToFill.put(code, codeMap.get(code));
					ApsSystemUtils.getLogger().info("Added new disabling code : " + code + 
							" - " + collectionToFill.get(code));
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "executeLoading", "Error loading extra attribute disabling codes");
		}
	}
	
}