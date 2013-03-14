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
package com.agiletec.apsadmin.portal;

/**
 * @author E.Santoboni
 */
public interface IShowletsViewerAction {
	
	/**
	 * Show the showlet catalog.
	 * @return The code describing the result of the operation.
	 */
	public String viewShowlets();
	
	/**
	 * Show the list of pages where a single showlet is published.
	 * @return The code describing the result of the operation.
	 */
	public String viewShowletUtilizers();
	
}
