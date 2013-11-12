/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* Entando is a free software;
* You can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package org.entando.entando.aps.system.services.api;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.agiletec.aps.system.common.AbstractDAO;

/**
 * @author E.Santoboni
 */
public class ApiTestHelperDAO extends AbstractDAO {
	
	public void cleanApiStatus() {
		String sql = "DELETE FROM apicatalog_methods";
		this.clean(sql);
	}

	public void cleanServices() {
		String sql = "DELETE FROM apicatalog_services";
		this.clean(sql);
	}
	
	private void clean(String sql) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(sql);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error while cleaning api/methods", "clean");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
}