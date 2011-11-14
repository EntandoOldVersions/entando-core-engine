/*
 *
 * Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
 *
 * This file is part of jAPS software.
 * jAPS is a free software; 
 * you can redistribute it and/or modify it
 * under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
 * 
 * See the file License for the specific language governing permissions   
 * and limitations under the License
 * 
 * 
 * 
 * Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
 *
 */
package com.agiletec.aps.system.services.keygenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.agiletec.aps.system.common.AbstractDAO;

/**
 * Data Access Object per la generazione di chiavi univoche.
 * @author S.Didaci - E.Santoboni
 */
public class KeyGeneratorDAO extends AbstractDAO implements IKeyGeneratorDAO {
	
	/**
	 * Estrae la chiave presente nel db.
	 * Il metodo viene chiamato solo in fase di inizializzazione.
	 * @return La chiave estratta.
	 */
	public int getUniqueKey() {
		Connection conn = null;
		int currentKey = 0;
		Statement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			stat = conn.createStatement();
			res = stat.executeQuery(EXTRACT_KEY);
			if (res.next()) {
				currentKey = res.getInt(1);
			}
		} catch (Throwable e) {
			processDaoException(e, "Error while getting the unique key",
			"getUniqueKey");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return currentKey;
	}

	/**
	 * Aggiorna la chiave univoca nel db.
	 * @param currentKey Il valore della chiave corrente.
	 */
	public synchronized void updateKey(int currentKey) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(UPDATE_KEY);
			stat.setInt(1, currentKey);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable e) {
			this.executeRollback(conn);
			processDaoException(e, "Error while updating a key", "getUpdateKey");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	private final String EXTRACT_KEY = "SELECT keyvalue FROM uniquekeys";

	private final String UPDATE_KEY = "UPDATE uniquekeys SET keyvalue = ? ";
}
