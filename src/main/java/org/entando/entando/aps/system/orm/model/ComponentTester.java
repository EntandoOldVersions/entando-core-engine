/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model;

/**
 * @author E.Santoboni
 */
public class ComponentTester {
	
	public String getScript() {
		return _script;
	}
	public void setScript(String script) {
		this._script = script;
	}
	
	public Integer getMinResult() {
		return _minResult;
	}
	public void setMinResult(Integer minResult) {
		this._minResult = minResult;
	}
	
	public Integer getMaxResult() {
		return _maxResult;
	}
	public void setMaxResult(Integer maxResult) {
		this._maxResult = maxResult;
	}
	
	private String _script;
	private Integer _minResult;
	private Integer _maxResult;
	
}