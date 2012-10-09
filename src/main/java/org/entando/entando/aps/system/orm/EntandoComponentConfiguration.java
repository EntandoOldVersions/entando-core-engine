/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
	
	@Deprecated
	public Map<String, Resource> getSqlResources() {
		return this.getDefaultSqlResources();
	}
	@Deprecated
	public void setSqlResources(Map<String, Resource> sqlResources) {
		this.setDefaultSqlResources(sqlResources);
	}
	
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