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
package org.entando.entando.aps.system.services.userprofile.api.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.entando.entando.aps.system.services.api.model.AbstractApiResponse;

/**
 * @author E.Santoboni
 */
@XmlRootElement(name = "response")
public class UserProfileResponse extends AbstractApiResponse {
    
	@Override
    @XmlElement(name = "result", required = true)
    public UserProfileResponseResult getResult() {
        return (UserProfileResponseResult) super.getResult();
    }
	
	public void setResult(UserProfileResponseResult result) {
		super.setResult(result);
	}
	
    @Override
    protected UserProfileResponseResult createResponseResultInstance() {
        return new UserProfileResponseResult();
    }
    
}