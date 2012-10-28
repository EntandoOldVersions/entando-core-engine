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
	
	public SystemInstallationReport.Status getStatus() {
		if (null == this.getDatabaseStatus() || this.getDatabaseStatus().isEmpty()) {
			return SystemInstallationReport.Status.INIT;
		}
		if (this.getDatabaseStatus().containsValue(SystemInstallationReport.Status.INCOMPLETE)) {
			return SystemInstallationReport.Status.INCOMPLETE;
		}
		return SystemInstallationReport.Status.OK;
	}
	
	public Map<String, SystemInstallationReport.Status> getDatabaseStatus() {
		return _databaseStatus;
	}
	
	private Map<String, SystemInstallationReport.Status> _databaseStatus = new HashMap<String, SystemInstallationReport.Status>();
	
}
