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
package org.entando.entando.aps.system.services.api.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author E.Santoboni
 */
@XmlRootElement(name = "response")
public class StringApiResponse extends AbstractApiResponse {
    
    @XmlElement(name = "result", required = false)
    public String getResult() {
        if (null != super.getResult()) {
            return super.getResult().toString();
        }
        return null;
    }
    
    public void setResult(Object result, String html) {
        super.setResult(result);
    }
    
    public void setResult(String result) {
        super.setResult(result);
    }
    
    protected AbstractApiResponseResult createResponseResultInstance() {
        throw new UnsupportedOperationException("Unsupported method");
    }
    
}