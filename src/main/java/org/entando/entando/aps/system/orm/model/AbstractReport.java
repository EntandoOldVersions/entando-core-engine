/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author E.Santoboni
 */
public abstract class AbstractReport {
	
	public InstallationReport.Status getStatus() {
		return _status;
	}
	public void setStatus(InstallationReport.Status status) {
		this._status = status;
	}
	
	public Map<String, InstallationReport.Status> getDatabaseStatus() {
		return _databaseStatus;
	}
	
	private InstallationReport.Status _status;
	private Map<String, InstallationReport.Status> _databaseStatus = new HashMap<String, InstallationReport.Status>();
	
}
