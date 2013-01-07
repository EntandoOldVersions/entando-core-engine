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
package org.entando.entando.aps.system.init.model;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.entando.entando.aps.system.init.IPostProcessor;
import org.jdom.Element;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * @author E.Santoboni
 */
public class ComponentEnvinroment {
	
	public ComponentEnvinroment(Element environmentElement, Map<String, String> postProcessClasses) throws Throwable {
		try {
			String environmentCode = environmentElement.getAttributeValue("code");
			this.setCode(environmentCode);
			Element defaultSqlResourcesElement = environmentElement.getChild("defaultSqlResources");
			if (null != defaultSqlResourcesElement) {
				List<Element> datasourceElements = defaultSqlResourcesElement.getChildren("datasource");
				for (int j = 0; j < datasourceElements.size(); j++) {
					Element datasourceElement = datasourceElements.get(j);
					String datasourceName = datasourceElement.getAttributeValue("name");
					String path = datasourceElement.getText().trim();
					this.getDefaultSqlResourcesPaths().put(datasourceName, path);
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "ComponentEnvinroment");
			throw new ApsSystemException("Error creating ComponentEnvinroment", t);
		}
	}
	
	public String getCode() {
		return _code;
	}
	protected void setCode(String code) {
		this._code = code;
	}
	
	public Resource getSqlResources(String datasourceName) {
		String path = this.getDefaultSqlResourcesPaths().get(datasourceName);
		if (null == path) {
			return null;
		}
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		return resolver.getResource(path);
	}
	
	public Map<String, String> getDefaultSqlResourcesPaths() {
		return _defaultSqlResourcesPaths;
	}
	protected void setDefaultSqlResourcesPaths(Map<String, String> defaultSqlResourcesPaths) {
		this._defaultSqlResourcesPaths = defaultSqlResourcesPaths;
	}
	
	public List<IPostProcessor> getPostProcesses() {
		return _postProcesses;
	}
	protected void setPostProcesses(List<IPostProcessor> postProcesses) {
		this._postProcesses = postProcesses;
	}
	
	private String _code;
	private Map<String, String> _defaultSqlResourcesPaths = new HashMap<String, String>();
	
	private List<IPostProcessor> _postProcesses;
	
}