/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model.report;

import java.util.HashMap;
import java.util.Map;

/**
 * @author E.Santoboni
 */
public abstract class AbstractReport {
	
	public SystemInstallation.Status getStatus() {
		if (null == this.getDatabaseStatus() || this.getDatabaseStatus().isEmpty()) {
			return SystemInstallation.Status.INIT;
		}
		if (this.getDatabaseStatus().containsValue(SystemInstallation.Status.INCOMPLETE)) {
			return SystemInstallation.Status.INCOMPLETE;
		}
		return SystemInstallation.Status.OK;
	}
	
	public Map<String, SystemInstallation.Status> getDatabaseStatus() {
		return _databaseStatus;
	}
	
	private Map<String, SystemInstallation.Status> _databaseStatus = new HashMap<String, SystemInstallation.Status>();
	
}
