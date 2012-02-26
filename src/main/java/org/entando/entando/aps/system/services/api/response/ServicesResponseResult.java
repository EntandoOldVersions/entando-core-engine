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
package org.entando.entando.aps.system.services.api.response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.entando.entando.aps.system.services.api.model.AbstractApiResponseResult;
import org.entando.entando.aps.system.services.api.model.ListResponse;
import org.entando.entando.aps.system.services.api.model.ServiceInfo;

/**
 * @author E.Santoboni
 */
public class ServicesResponseResult extends AbstractApiResponseResult {
    
    @XmlElement(name = "service", required = false)
    public ServiceInfo getResult() {
        if (this.getMainResult() instanceof ServiceInfo) {
            return (ServiceInfo) this.getMainResult();
        }
        return null;
    }
    
    @XmlElement(name = "services", required = false)
    public ListResponse getResults() {
        if (this.getMainResult() instanceof Collection) {
            List<ServiceInfo> services = new ArrayList<ServiceInfo>();
            services.addAll((Collection<ServiceInfo>) this.getMainResult());
            ListResponse listResponse = new ListResponse(services) {};
            return listResponse;
        }
        return null;
    }
    
}