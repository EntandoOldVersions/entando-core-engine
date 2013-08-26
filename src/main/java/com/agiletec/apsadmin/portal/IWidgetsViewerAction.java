/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software. 
* Entando is a free software; 
* You can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
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
public interface IWidgetsViewerAction {
	
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
