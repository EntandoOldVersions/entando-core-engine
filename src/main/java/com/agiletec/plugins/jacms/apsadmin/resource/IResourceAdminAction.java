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
package com.agiletec.plugins.jacms.apsadmin.resource;

/**
 * Interface of action delegated to execute administrative tasks on resources
 * @author E.Santoboni
 */
public interface IResourceAdminAction {
	
	/**
	 * Refresh all the resources instance (not the "main" instance)
	 * @return The code describing the result of the operation.
	 */
	public String refreshResourcesInstances();
	
}