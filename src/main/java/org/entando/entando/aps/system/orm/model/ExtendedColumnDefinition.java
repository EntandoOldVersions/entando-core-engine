/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.model;

import org.entando.entando.aps.system.orm.IDbInstallerManager;

/**
 * @author E.Santoboni
 */
public interface ExtendedColumnDefinition {
	
	public String[] extensions(IDbInstallerManager.DatabaseType type);
	
}
