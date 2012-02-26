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
package org.entando.entando.plugins.jacms.aps.system.services.api.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.entando.entando.aps.system.services.api.model.AbstractApiResponse;

/**
 * @author E.Santoboni
 */
@XmlRootElement(name = "response")
public class ContentTypeResponse extends AbstractApiResponse {
    
    public void setResult(Object result, String html) {
        ContentTypeResponseResult responseResult = new ContentTypeResponseResult();
        responseResult.setMainResult(result);
        responseResult.setHtml(html);
        this._result = responseResult;
    }
    
    @XmlElement(name = "result", required = true)
    private ContentTypeResponseResult _result;
    
}