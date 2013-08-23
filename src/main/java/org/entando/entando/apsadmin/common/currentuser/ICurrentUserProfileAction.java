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
package org.entando.entando.apsadmin.common.currentuser;

import com.agiletec.apsadmin.system.entity.IApsEntityAction;

/**
 * Interfaccia delle classi action per la gestione del Profilo Utente corrente
 * @author E.Santoboni
 */
public interface ICurrentUserProfileAction extends IApsEntityAction {
	
	public static final String SESSION_PARAM_NAME_CURRENT_PROFILE = "currentProfileOnSession";
	
}
