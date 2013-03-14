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
package com.agiletec.aps.system.services.authorization;

import java.io.Serializable;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.role.Role;

/**
 * Rappresentazione di una autorizzazione.
 * Nelle implementazioni concrete viene utilizzata per le implementazioni 
 * degli oggetti Gruppi (classe {@link Group}) e Ruoli (classe {@link Role}).
 * @author E.Santoboni
 */
public interface IApsAuthority extends Serializable {
	
	/**
	 * Restituisce il codice dell'autorizzazione.
	 * @return Il codice deell'autorizzazione.
	 */
	public String getAuthority();
	
}