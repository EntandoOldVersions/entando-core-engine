/*
 *
 * Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
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
 * Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
 *
 */
package com.agiletec.aps.system.common.entity.loader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.IEntityManager;
import com.agiletec.aps.system.common.entity.parse.AttributeDisablingCodesDOM;
import com.agiletec.aps.util.FileTextReader;

/**
 * The Class Loader of the extra attribute disabling codes.
 * @author E.Santoboni
 */
public final class AttributeDisablingCodesLoader {
	
	public Map<String, String> extractDisablingCodes(String attributeDisablingCodesFileName, BeanFactory beanFactory, IEntityManager entityManager) {
		Map<String, String> disablingCodes = new HashMap<String, String>();
		try {
			this.setEntityManager(entityManager);
			this.setBeanFactory(beanFactory);
			this.loadDefaultDisablingCodes(attributeDisablingCodesFileName, disablingCodes);
			this.loadExtraDisablingCodes(disablingCodes);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "extractDisablingCodes", "Error loading disabling codes");
		}
		return disablingCodes;
	}
	
	private void loadDefaultDisablingCodes(String disablingCodesFileName, Map<String, String> disablingCodes) {
		try {
			String xml = this.extractConfigFile(disablingCodesFileName);
			if (null != xml) {
				AttributeDisablingCodesDOM dom = new AttributeDisablingCodesDOM();
				disablingCodes.putAll(dom.extractDisablingCodes(xml, disablingCodesFileName));
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "extractDisablingCodes", "Error loading disabling codes : file " + disablingCodesFileName);
		}
	}
	
	private void loadExtraDisablingCodes(Map<String, String> disablingCodes) {
		try {
			ListableBeanFactory factory = (ListableBeanFactory) this.getBeanFactory();
			String[] defNames = factory.getBeanNamesForType(ExtraAttributeDisablingCodesWrapper.class);
			for (int i=0; i<defNames.length; i++) {
				try {
					Object loader = this.getBeanFactory().getBean(defNames[i]);
					if (loader != null) {
						((ExtraAttributeDisablingCodesWrapper) loader).executeLoading(disablingCodes, this.getEntityManager());
					}
				} catch (Throwable t) {
					ApsSystemUtils.logThrowable(t, this, "loadExtraDisablingCodes", 
							"Error extracting attribute support object : bean " + defNames[i]);
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "loadExtraDisablingCodes", 
					"Error loading attribute support object");
		}
	}
	
	private String extractConfigFile(String fileName) throws Throwable {
		InputStream is = this.getEntityManager().getClass().getResourceAsStream(fileName);
		if (null == is) {
			ApsSystemUtils.getLogger().config(this.getEntityManager().getClass().getName() + ": there isn't any object to load : file " + fileName);
			return null;
		}
		return FileTextReader.getText(is);
	}
	
	protected IEntityManager getEntityManager() {
		return _entityManager;
	}
	protected void setEntityManager(IEntityManager entityManager) {
		this._entityManager = entityManager;
	}
	
	protected BeanFactory getBeanFactory() {
		return _beanFactory;
	}
	protected void setBeanFactory(BeanFactory beanFactory) {
		this._beanFactory = beanFactory;
	}
	
	private IEntityManager _entityManager;
	private BeanFactory _beanFactory;
	
}