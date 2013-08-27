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
package org.entando.entando.aps.system.services.userprofile.api.model;

import com.agiletec.aps.system.common.entity.model.IApsEntity;

import javax.xml.bind.annotation.XmlRootElement;

import org.entando.entando.aps.system.common.entity.api.JAXBEntityType;

/**
 * @author E.Santoboni
 */
@XmlRootElement(name = "userProfileType")
public class JAXBUserProfileType extends JAXBEntityType {
    
    public JAXBUserProfileType() {}
    
    public JAXBUserProfileType(IApsEntity entityType) {
        super(entityType);
    }
    
}