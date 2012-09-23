/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm;

/**
 * @author E.Santoboni
 */
public interface ExtendedColumnDefinition {
	
	public String[] extensions(IDbCreatorManager.DatabaseType type);
	
}
