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
package com.agiletec.aps.system.common.entity.loader;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.IManager;
import com.agiletec.aps.system.common.entity.IEntityManager;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;

/**
 * The Class loader of the extra attribute.
 * @author E.Santoboni
 */
public class ExtraAttributeLoader {
	
	public Map<String, AttributeInterface> extractAttributes(BeanFactory beanFactory, String entityManagerName) {
		Map<String, AttributeInterface> attributes = null;
		try {
			attributes = this.loadExtraAttributes(beanFactory, entityManagerName);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "extractAttributes", "Error loading extra attributes");
		}
		return attributes;
	}
	
	private Map<String, AttributeInterface> loadExtraAttributes(BeanFactory beanFactory, String entityManagerName) {
		Map<String, AttributeInterface> extraAttributes = new HashMap<String, AttributeInterface>();
		try {
			ListableBeanFactory factory = (ListableBeanFactory) beanFactory;
			String[] defNames = factory.getBeanNamesForType(ExtraAttributeWrapper.class);
			for (int i=0; i<defNames.length; i++) {
				try {
					Object wrapperObject = beanFactory.getBean(defNames[i]);
					if (wrapperObject != null) {
						ExtraAttributeWrapper wrapper = (ExtraAttributeWrapper) wrapperObject;
						String destEntityManagerName = ((IManager) wrapper.getEntityManagerDest()).getName();
						if (entityManagerName.equals(destEntityManagerName) && null != wrapper.getAttribute()) {
							extraAttributes.put(wrapper.getAttribute().getType(), wrapper.getAttribute());
						}
					}
				} catch (Throwable t) {
					ApsSystemUtils.logThrowable(t, this, "loadExtraAttributes", "Error extracting attribute : wrapper bean " + defNames[i]);
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "loadExtraAttributes", "Error loading extra attributes");
		}
		return extraAttributes;
	}
	
}
