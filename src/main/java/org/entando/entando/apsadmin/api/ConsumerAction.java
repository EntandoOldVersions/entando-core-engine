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
package org.entando.entando.apsadmin.api;

import com.agiletec.apsadmin.system.BaseAction;

import org.entando.entando.aps.system.services.oauth.IOAuthConsumerManager;
import org.entando.entando.aps.system.services.oauth.model.Consumer;

/**
 * @author E.Santoboni
 */
public class ConsumerAction extends BaseAction {
    
    public String newConsumer() {
        return SUCCESS;
    }
    
    public String edit() {
        return SUCCESS;
    }
    
    public String save() {
        return SUCCESS;
    }
    
    public String trash() {
        return SUCCESS;
    }
    
    public String delete() {
        return SUCCESS;
    }
    
    public Consumer getConsumer(String key) throws Throwable {
        return this.getOauthConsumerManager().getConsumerRecord(key);
    }
    
    protected IOAuthConsumerManager getOauthConsumerManager() {
        return _oauthConsumerManager;
    }
    public void setOauthConsumerManager(IOAuthConsumerManager oauthConsumerManager) {
        this._oauthConsumerManager = oauthConsumerManager;
    }
    
    private IOAuthConsumerManager _oauthConsumerManager;
    
}