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
package org.entando.entando.aps.system.orm;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;

/**
 * @author E.Santoboni
 */
public class EntandoComponentConfiguration implements Comparable<EntandoComponentConfiguration> {
	
	public String getCode() {
		return _code;
	}
	public void setCode(String code) {
		this._code = code;
	}
	
	public List<String> getDependencies() {
		return _dependencies;
	}
	public void setDependencies(List<String> dependencies) {
		this._dependencies = dependencies;
	}
	
	public Map<String, Resource> getDefaultSqlResources() {
		return _defaultSqlResources;
	}
	public void setDefaultSqlResources(Map<String, Resource> defaultSqlResources) {
		this._defaultSqlResources = defaultSqlResources;
	}
	/*
	@Deprecated
	public Map<String, Resource> getSqlResources() {
		return this.getDefaultSqlResources();
	}
	@Deprecated
	public void setSqlResources(Map<String, Resource> sqlResources) {
		this.setDefaultSqlResources(sqlResources);
	}
	*/
	public Map<String, List<String>> getTableMapping() {
		return _tableMapping;
	}
	public void setTableMapping(Map<String, List<String>> tableMapping) {
		this._tableMapping = tableMapping;
	}
	
	@Override
	public int compareTo(EntandoComponentConfiguration other) {
		List<String> deps = this.getDependencies();
		List<String> otherDeps = other.getDependencies();
		if (null != otherDeps && otherDeps.contains(this.getCode())) {
			return -1;
		} else if (null != deps && deps.contains(other.getCode())) {
			return 1;
		}
		return 0;
	}
	
	private String _code;
	private List<String> _dependencies;
	private Map<String, List<String>> _tableMapping;
	private Map<String, Resource> _defaultSqlResources;
	
}