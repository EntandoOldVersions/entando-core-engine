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
package org.entando.entando.aps.system.services.oauth;

import com.agiletec.aps.system.common.FieldSearchFilter;
import java.util.List;
import net.oauth.OAuthConsumer;
import org.entando.entando.aps.system.services.oauth.model.ConsumerRecordVO;

/**
 * @author E.Santoboni
 */
public interface IOAuthConsumerDAO {
    
    public List<String> getConsumerKeys(FieldSearchFilter[] filters);
    
    public ConsumerRecordVO getConsumerRecord(String consumerKey);
    
    public void addConsumer(ConsumerRecordVO consumer);
    
    public void updateConsumer(ConsumerRecordVO consumer);
    
    public void deleteConsumer(String consumerKey);
    
    public OAuthConsumer getConsumer(String consumerKey);
    
}
