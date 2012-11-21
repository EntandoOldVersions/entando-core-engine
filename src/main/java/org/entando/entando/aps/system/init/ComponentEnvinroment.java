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
package org.entando.entando.aps.system.init;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * @author E.Santoboni
 */
public class ComponentEnvinroment {
	
	public ComponentEnvinroment(String code) {
		this.setCode(code);
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
	public void setDefaultSqlResourcesPaths(Map<String, String> defaultSqlResourcesPaths) {
		this._defaultSqlResourcesPaths = defaultSqlResourcesPaths;
	}
	
	private String _code;
	private Map<String, String> _defaultSqlResourcesPaths = new HashMap<String, String>();
	
}