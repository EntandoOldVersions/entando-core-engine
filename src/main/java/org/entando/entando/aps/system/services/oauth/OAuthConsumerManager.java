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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;
import net.oauth.server.OAuthServlet;

import org.apache.commons.codec.digest.DigestUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.services.user.UserDetails;

/**
 * Manager of consumers, access token (stored in database and in local cache) 
 * and request tokens (stored in local cache). 
 * Special thanks to Praveen Alavilli and OAuth examples.
 * http://oauth.googlecode.com/svn/code/java/example/oauth-provider/src/net/oauth/example/provider/core/SampleOAuthProvider.java
 * @author Praveen Alavilli - E.Santoboni
 */
public class OAuthConsumerManager extends AbstractService implements IOAuthConsumerManager {
    
    public void init() throws Exception {
        this.initMyEntandoConsumer();
        ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initialized ");
    }
    
    protected void release() {
        super.release();
        this.getConsumers().clear();
        this.getUnauthorizedTokensCache().clear();
        this.getAuthorizedTokensCache().clear();
    }
    
    private void initMyEntandoConsumer() throws Exception {
        InputStream stream = this.getClass().getResourceAsStream("myentando.properties");
        try {
            Properties prop = new Properties();
            prop.load(stream);
            String consumerKey = (String) prop.get("consumer_key");
            String consumerSecret = (String) prop.get("consumer_secret");
            String consumerDescription = (String) prop.get("description");
            OAuthConsumer consumer = 
                    new OAuthConsumer(null, consumerKey, consumerSecret, null);
            consumer.setProperty("name", consumerKey);
            consumer.setProperty("description", consumerDescription);
            this.getConsumers().put(consumerKey, consumer);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "initMyEntandoConsumer", "Error loading MyEntando Consumer");
            throw new Exception("Error loading MyEntando Consumer", t);
        } finally {
            stream.close();
        }
    }
    
    public OAuthConsumer getConsumer(OAuthMessage requestMessage) throws IOException, OAuthProblemException {
        String consumerKey = requestMessage.getConsumerKey();
        OAuthConsumer consumer = null;
        try {
            consumer = this.getConsumers().get(consumerKey);
            if (consumer == null) {
                consumer = this.getConsumerDAO().getConsumer(consumerKey);
                if (null != consumer) {
                    this.getConsumers().put(consumerKey, consumer);
                }
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getConsumer", "Error extracting consumer by key '" + consumerKey + "'");
            throw new RuntimeException("Error extracting consumer by key '" + consumerKey + "'");
        }
        if (consumer == null) {
            OAuthProblemException problem = new OAuthProblemException("Invalid Consumer - key '" + consumerKey + "'");
            throw problem;
        }
        return consumer;
    }
    
    public synchronized OAuthAccessor getAuthorizedAccessor(OAuthMessage requestMessage)
            throws IOException, OAuthProblemException {
        OAuthAccessor accessor = null;
        String consumerToken = requestMessage.getToken();
        try {
            OAuthConsumer consumer = this.getConsumer(requestMessage);
            accessor = this.getAuthorizedTokensCache().get(consumerToken);
            if (null == accessor) {
                accessor = this.getConsumerDAO().getAccessor(consumerToken, consumer);
                if (null != accessor) {
                    this.getAuthorizedTokensCache().put(consumerToken, accessor);
                }
            }
        } catch (OAuthProblemException t) {
            throw t;
        } catch (IOException io) {
            throw io;
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getAuthorizedAccessor", "Error extracting access token");
            throw new RuntimeException("Error extracting access token");
        }
        if (accessor == null) {
            throw new OAuthProblemException("token_expired");
        }
        return accessor;
    }
    
    public synchronized OAuthAccessor getAccessor(OAuthMessage requestMessage)
            throws IOException, OAuthProblemException {
        String consumerToken = requestMessage.getToken();
        OAuthAccessor accessor = this.getUnauthorizedTokensCache().get(consumerToken);
        if (accessor == null) {
            throw new OAuthProblemException("token_expired");
        }
        return accessor;
    }
    
    /**
     * Set the access token 
     */
    public synchronized void markAsAuthorized(OAuthAccessor accessor, String username) throws OAuthException {
        try {
            String requestToken = accessor.requestToken;
            OAuthAccessor unauthorizedAccessor = this.getUnauthorizedTokensCache().get(requestToken);
            if (null == unauthorizedAccessor) {
                throw new OAuthException("Invalid token for user '" + username + "'");
            }
            unauthorizedAccessor.setProperty("user", username);
            unauthorizedAccessor.setProperty("authorized", Boolean.TRUE);
        } catch (OAuthException oe) {
            throw oe;
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "markAsAuthorized", "Error while mark As Authorized request token");
        }
    }
    
    public synchronized void generateRequestToken(OAuthAccessor accessor) throws OAuthException {
        try {
            String consumerKey = (String) accessor.consumer.getProperty("name");
            String token_data = consumerKey + System.nanoTime();
            String token = DigestUtils.md5Hex(token_data);
            String secret_data = consumerKey + System.nanoTime() + token;
            String secret = DigestUtils.md5Hex(secret_data);
            accessor.requestToken = token;
            accessor.tokenSecret = secret;
            accessor.accessToken = null;
            this.getUnauthorizedTokensCache().put(token, accessor);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "generateRequestToken", "Error generating request token");
        }
    }
    
    public synchronized void generateAccessToken(OAuthAccessor accessor) throws OAuthException {
        try {
            String requestToken = accessor.requestToken;
            OAuthAccessor unauthorizedAccessor = this.getUnauthorizedTokensCache().get(requestToken);
            if (null == unauthorizedAccessor) {
                throw new OAuthException("Invalid token");
            }
            Object authorized = unauthorizedAccessor.getProperty("authorized");
            if (null == authorized || !authorized.equals(Boolean.TRUE)) {
                throw new OAuthException("Unauthorized token");
            }
            String username = (String) unauthorizedAccessor.getProperty("user");
            String consumerKey = (String) accessor.consumer.getProperty("name");
            String token_data = consumerKey + System.nanoTime();
            String token = DigestUtils.md5Hex(token_data);
            this.getUnauthorizedTokensCache().remove(requestToken);
            accessor.requestToken = null;
            accessor.accessToken = token;
            accessor.setProperty("user", username);
            accessor.setProperty("authorized", Boolean.TRUE);
            this.getConsumerDAO().addAccessToken(accessor);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "generateAccessToken", "Error generating access token");
        }
    }
    
    public void handleException(Exception e, HttpServletRequest request,
            HttpServletResponse response, boolean sendBody)
            throws IOException, ServletException {
        String realm = (request.isSecure()) ? "https://" : "http://";
        realm += request.getLocalName();
        OAuthServlet.handleException(response, e, realm, sendBody);
    }
    
    public OAuthValidator getOAuthValidator() {
        return new SimpleOAuthValidator();
    }
    
    public void deleteMyAccessToken(Properties properties) throws Throwable {
        try {
            UserDetails user = (UserDetails) properties.get(SystemConstants.API_USER_PARAMETER);
            if (null == user) {
                ApsSystemUtils.getLogger().info("Unable to delete access token form null user");
            }
            String username = user.getUsername();
            String accessToken = properties.getProperty("accessToken");
            OAuthConsumer consumer = (OAuthConsumer) properties.get(SystemConstants.API_OAUTH_CONSUMER_PARAMETER);
            String consumerKey = (null != consumer) ? consumer.consumerKey : null;
            this.getConsumerDAO().deleteAccessToken(username, accessToken, consumerKey);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "deleteMyAccessToken", "Error deleting access token");
        }
    }
    
    protected Map<String, OAuthConsumer> getConsumers() {
        return _consumers;
    }
    protected void setConsumers(Map<String, OAuthConsumer> consumers) {
        this._consumers = consumers;
    }
    
    protected Map<String, OAuthAccessor> getAuthorizedTokensCache() {
        return _authorizedTokensCache;
    }
    protected Map<String, OAuthAccessor> getUnauthorizedTokensCache() {
        return _unauthorizedTokensCache;
    }
    
    protected IOAuthConsumerDAO getConsumerDAO() {
        return _consumerDAO;
    }
    public void setConsumerDAO(IOAuthConsumerDAO consumerDAO) {
        this._consumerDAO = consumerDAO;
    }
    
    private Map<String, OAuthConsumer> _consumers = new HashMap<String, OAuthConsumer>();
    
    private Map<String, OAuthAccessor> _authorizedTokensCache = new HashMap<String, OAuthAccessor>();
    private Map<String, OAuthAccessor> _unauthorizedTokensCache = new HashMap<String, OAuthAccessor>();
    
    private IOAuthConsumerDAO _consumerDAO;
    
}