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
package org.entando.entando.aps.system.services.oauth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;

import com.agiletec.aps.system.common.AbstractDAO;

/**
 * @author E.Santoboni
 */
public class OAuthTokenDAO extends AbstractDAO implements IOAuthTokenDAO {
    
    public void addAccessToken(OAuthAccessor accessor) {
        Connection conn = null;
        PreparedStatement stat = null;
        try {
            String consumer_key = (String) accessor.consumer.getProperty("name");
            String username = (String) accessor.getProperty("user");
            conn = this.getConnection();
            conn.setAutoCommit(false);
            stat = conn.prepareStatement(INSERT_TOKEN);
            stat.setString(1, accessor.accessToken);
            stat.setString(2, accessor.tokenSecret);
            stat.setString(3, consumer_key);
            stat.setString(4, username);
            stat.setDate(5, new java.sql.Date(new Date().getTime()));
            stat.executeUpdate();
            conn.commit();
        } catch (Throwable t) {
            this.executeRollback(conn);
            processDaoException(t, "Error while adding an access token", "addAccessToken");
        } finally {
            closeDaoResources(null, stat, conn);
        }
    }
    
    public OAuthAccessor getAccessor(String accessToken, OAuthConsumer consumer) {
        Connection conn = null;
        OAuthAccessor accessor = null;
        PreparedStatement stat = null;
        ResultSet res = null;
        try {
            String consumer_key = (String) consumer.getProperty("name");
            conn = this.getConnection();
            stat = conn.prepareStatement(SELECT_TOKEN);
            stat.setString(1, accessToken);
            stat.setString(2, consumer_key);
            res = stat.executeQuery();
            if (res.next()) {
                String tokensecret = res.getString(1);
                String username = res.getString(2);
                accessor = new OAuthAccessor(consumer);
                accessor.accessToken = accessToken;
                accessor.tokenSecret = tokensecret;
                accessor.setProperty("user", username);
                accessor.setProperty("authorized", Boolean.TRUE);
            }
        } catch (Throwable t) {
            processDaoException(t, "Error while loading accessor " + accessToken, "getAccessor");
        } finally {
            closeDaoResources(res, stat, conn);
        }
        return accessor;
    }
    
    public void deleteAccessToken(String username, String accessToken, String consumerKey) {
        Connection conn = null;
        PreparedStatement stat = null;
        try {
            conn = this.getConnection();
            conn.setAutoCommit(false);
            stat = conn.prepareStatement(DELETE_TOKEN);
            stat.setString(1, username);
            stat.setString(2, accessToken);
            stat.setString(3, consumerKey);
            stat.executeUpdate();
            conn.commit();
        } catch (Throwable t) {
            this.executeRollback(conn);
            processDaoException(t, "Error while deleting an access token", "deleteAccessToken");
        } finally {
            closeDaoResources(null, stat, conn);
        }
    }
    
    private String INSERT_TOKEN = 
            "INSERT INTO api_oauth_tokens (accesstoken, tokensecret, consumerkey, username, lastaccess) "
            + "VALUES (? , ? , ? , ? , ? )";
    
    private String SELECT_TOKEN = 
            "SELECT tokensecret, username "
            + "FROM api_oauth_tokens WHERE accesstoken = ? AND consumerkey = ?";
    
    private String DELETE_TOKEN = "DELETE FROM api_oauth_tokens WHERE username = ? AND accesstoken = ? AND consumerkey = ?";
    
}
