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

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;

/**
 * @author E.Santoboni
 */
public interface IOAuthConsumerDAO {
    
    public OAuthConsumer getConsumer(String consumerKey);
    
    public void addAccessToken(OAuthAccessor accessor);
    
    public void deleteAccessToken(String username, String accessToken, String consumerKey);
    
    public OAuthAccessor getAccessor(String accessToken, OAuthConsumer consumer);
    
}
