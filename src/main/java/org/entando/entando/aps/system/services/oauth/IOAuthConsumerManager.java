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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthValidator;

/**
 * @author E.Santoboni
 */
public interface IOAuthConsumerManager {
    
    public OAuthConsumer getConsumer(OAuthMessage requestMessage) throws IOException, OAuthProblemException;
    
    public OAuthAccessor getAuthorizedAccessor(OAuthMessage requestMessage) throws IOException, OAuthProblemException;
    
    public OAuthAccessor getAccessor(OAuthMessage requestMessage) throws IOException, OAuthProblemException;
    
    public OAuthValidator getOAuthValidator();
    
    /**
     * Generate a fresh request token and secret for a consumer.
     * @param accessor The consumer
     * @throws OAuthException In case of error
     */
    public void generateRequestToken(OAuthAccessor accessor) throws OAuthException;
    
    /**
     * Generate an access token and secret for a consumer.
     * @param accessor The consumer
     * @throws OAuthException In case of error
     */
    public void generateAccessToken(OAuthAccessor accessor) throws OAuthException;
    
    public void markAsAuthorized(OAuthAccessor accessor, String username) throws OAuthException;
    
    public void handleException(Exception e, HttpServletRequest request,
            HttpServletResponse response, boolean sendBody) throws IOException, ServletException;
    
}