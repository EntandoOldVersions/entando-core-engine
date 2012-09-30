/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.util;

import com.j256.ormlite.db.DerbyEmbeddedDatabaseType;

/**
 * @author E.Santoboni
 */
public class ApsDerbyEmbeddedDatabaseType extends DerbyEmbeddedDatabaseType {
	
	@Override
	public void appendEscapedEntityName(StringBuilder sb, String name) {
		sb.append(" ").append(name.toUpperCase()).append(" ");
	}
	
}
