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

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.apsadmin.system.ApsAdminSystemConstants;
import com.agiletec.apsadmin.system.BaseAction;

import java.util.Date;
import org.entando.entando.aps.system.services.oauth.IOAuthConsumerManager;
import org.entando.entando.aps.system.services.oauth.model.Consumer;

/**
 * @author E.Santoboni
 */
public class ConsumerAction extends BaseAction {
    
    public String newConsumer() {
        this.setStrutsAction(ApsAdminSystemConstants.ADD);
        return SUCCESS;
    }
    
    public String edit() {
        try {
            Consumer consumer = this.getOauthConsumerManager().getConsumerRecord(this.getConsumerKey());
            if (null == consumer) {
                String[] args = {this.getConsumerKey()};
                this.addActionError(this.getText("error.consumer.notExist", args));
                return "list";
            }
            this.setCallbackUrl(consumer.getCallbackUrl());
            this.setDescription(consumer.getDescription());
            this.setExpirationDate(consumer.getExpirationDate());
            this.setSecret(consumer.getSecret());
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "edit");
            return FAILURE;
        }
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
    /*
    public Consumer getConsumer(String key) throws Throwable {
        return this.getOauthConsumerManager().getConsumerRecord(key);
    }
    */
    public String getConsumerKey() {
        return _consumerKey;
    }
    public void setConsumerKey(String consumerKey) {
        this._consumerKey = consumerKey;
    }
    
    public int getStrutsAction() {
        return _strutsAction;
    }
    public void setStrutsAction(int strutsAction) {
        this._strutsAction = strutsAction;
    }
    
    public String getSecret() {
        return _secret;
    }
    public void setSecret(String secret) {
        this._secret = secret;
    }
    
    public String getDescription() {
        return _description;
    }
    public void setDescription(String description) {
        this._description = description;
    }
    
    public String getCallbackUrl() {
        return _callbackUrl;
    }
    public void setCallbackUrl(String callbackUrl) {
        this._callbackUrl = callbackUrl;
    }
    
    public Date getExpirationDate() {
        return _expirationDate;
    }
    public void setExpirationDate(Date expirationDate) {
        this._expirationDate = expirationDate;
    }
    
    protected IOAuthConsumerManager getOauthConsumerManager() {
        return _oauthConsumerManager;
    }
    public void setOauthConsumerManager(IOAuthConsumerManager oauthConsumerManager) {
        this._oauthConsumerManager = oauthConsumerManager;
    }
    
    private String _consumerKey;
    private int _strutsAction;
    
    private String _secret;
    private String _description;
    private String _callbackUrl;
    private Date _expirationDate;
    
    private IOAuthConsumerManager _oauthConsumerManager;
    
}