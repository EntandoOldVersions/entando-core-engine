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
package com.agiletec.aps.system.common.entity.helper;

/**
 * 
 * @author E.Santoboni
 */
public interface IEntityFilterBean {
    
    public String getKey();
    
    public boolean isAttributeFilter();
    
    public boolean getLikeOption();
    
    public String getValue();
    
    public String getStart();
    
    public String getEnd();
    
    public String getOrder();
    
}
