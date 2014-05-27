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
public class LinkedListApiResponseResult extends AbstractApiResponseResult {
    
	@Override
    @XmlElement(name = "items", required = false)
    public ListResponse<LinkedListItem> getResult() {
        if (null != this.getMainResult()) {
            List<LinkedListItem> items = new ArrayList<LinkedListItem>();
            items.addAll((Collection<LinkedListItem>) this.getMainResult());
            ListResponse<LinkedListItem> entity = new ListResponse<LinkedListItem>(items) {};
            return entity;
        }
        return null;
    }
    
}