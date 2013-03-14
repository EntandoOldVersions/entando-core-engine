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
package com.agiletec.apsadmin.system.entity.type;

/**
 * @author E.Santoboni
 */
public interface IEntityTypeConfigAction {
	
	public String addEntityType();
	
	public String editEntityType();
	
	public String addAttribute();
	
	public String editAttribute();
	
	public String moveAttribute();
	
	public String removeAttribute();
	
	public String saveEntityType();
	
	public static final String ENTITY_TYPE_OPERATION_ID_SESSION_PARAM = "operationId_sessionParam";
	public static final String ENTITY_TYPE_MANAGER_SESSION_PARAM = "entityTypeManager_sessionParam";
	public static final String ENTITY_TYPE_ON_EDIT_SESSION_PARAM = "entityTypeOnEdit_sessionParam";
	
}