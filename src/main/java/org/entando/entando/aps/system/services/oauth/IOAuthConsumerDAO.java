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

import com.agiletec.aps.system.common.FieldSearchFilter;
import java.util.List;
import net.oauth.OAuthConsumer;
import org.entando.entando.aps.system.services.oauth.model.Consumer;

/**
 * @author E.Santoboni
 */
public interface IOAuthConsumerDAO {
    
    public List<String> getConsumerKeys(FieldSearchFilter[] filters);
    
    public void deleteConsumer(String consumerKey);
    
    public Consumer getConsumerRecord(String consumerKey);
    
    public void updateConsumer(Consumer consumer);
    
    public OAuthConsumer getConsumer(String consumerKey);
    
}
