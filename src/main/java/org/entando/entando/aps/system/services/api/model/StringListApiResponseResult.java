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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author E.Santoboni
 */
public class StringListApiResponseResult extends AbstractApiResponseResult {
    
    @XmlElement(name = "items", required = false)
    public ListResponse<String> getResult() {
        if (null != this.getMainResult()) {
            List<String> strings = new ArrayList<String>();
            strings.addAll((Collection<String>) this.getMainResult());
            ListResponse<String> entity = new ListResponse<String>(strings) {};
            return entity;
        }
        return null;
    }
    
}