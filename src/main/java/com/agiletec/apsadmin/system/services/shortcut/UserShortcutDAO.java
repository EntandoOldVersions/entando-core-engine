/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando Enterprise Edition software.
* You can redistribute it and/or modify it
* under the terms of the Entando's EULA
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.apsadmin.system.services.shortcut;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.agiletec.aps.system.common.AbstractDAO;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * Data Access Object for the configuration of user shortcut.
 * @author E.Santoboni
 */
public class UserShortcutDAO extends AbstractDAO implements IUserShortcutDAO {
	
	@Override
	public void saveUserConfig(String username, String config) {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.deleteUserConfigRecord(username, conn);
			this.addUserConfigRecord(username, config, conn);
			conn.commit();
		} catch (Throwable t) {
			this.processDaoException(t, "Error deleting user config by user " + username, "deleteUserConfig");
		} finally {
			this.closeConnection(conn);
		}
	}
	
	private void addUserConfigRecord(String username, String config, Connection conn) throws ApsSystemException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(ADD_CONFIG);
			stat.setString(1, username);
			stat.setString(2, config);
			stat.executeUpdate();
		} catch (Throwable t) {
			this.processDaoException(t, "Error adding user config record", "addUserConfigRecord");
		} finally {
			this.closeDaoResources(null, stat);
		}
	}
	
	@Override
	public void deleteUserConfig(String username) {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.deleteUserConfigRecord(username, conn);
			conn.commit();
		} catch (Throwable t) {
			this.processDaoException(t, "Error deleting user config by user " + username, "deleteUserConfig");
		} finally {
			this.closeConnection(conn);
		}
	}
	
	private void deleteUserConfigRecord(String username, Connection conn) {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(DELETE_CONFIG);
			stat.setString(1, username);
			stat.executeUpdate();
		} catch (Throwable t) {
			this.processDaoException(t, "Error deleting user config record by id " + username, 
					"deleteUserConfigRecord");
		} finally {
			this.closeDaoResources(null, stat);
		}
	}

	@Override
	public String loadUserConfig(String username) {
		Connection conn = null;
		String config = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(LOAD_CONFIG);
			stat.setString(1, username);
			res = stat.executeQuery();
			if (res.next()) {
				config = res.getString(1);
			}
		} catch (Throwable t) {
			this.processDaoException(t, "Error loading short cut config by user " + username, "loadUserConfig");
		} finally {
			this.closeDaoResources(res, stat, conn);
		}
		return config;
	}
	
	private final String LOAD_CONFIG = 
		"SELECT config FROM authusershortcuts WHERE username = ?";
	
	private final String DELETE_CONFIG = 
		"DELETE FROM authusershortcuts WHERE username = ?";
	
	private final String ADD_CONFIG = 
		"INSERT INTO authusershortcuts(username, config) VALUES (?, ?)";
	
}