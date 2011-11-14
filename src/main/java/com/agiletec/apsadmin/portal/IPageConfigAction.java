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
package com.agiletec.apsadmin.portal;

/**
 * This interface is specific for those actions which handle the configuration of a single page.
 * @author E.Santoboni
 */
public interface IPageConfigAction {
	
	/**
	 * Configure the frames of portal page.
	 * @return The code describing the result of the operation.
	 */
	public String configure();
	
	/**
	 * Associate a showlet to a frame of the page on edit.
	 * @return The result code
	 */
	public String joinShowlet();

	/**
	 * Remove a showlet from those defined in the current page.
	 * @return The result code
	 * @deprecated use trashShowlet
	 */
	public String removeShowlet() ;
	
	/**
	 * Executes the specific action to trash a showlet from a page. This does NOT perform any operation.
	 * @return The result code
	 */
	public String trashShowlet() ;
	
	/**
	 * Forces the deletion of a showlet from a page.
	 * @return The result code
	 */
	public String deleteShowlet() ;
	
	/**
	 * Start the configuration of a single page frame. 
	 * @return The result code
	 */
	public String editFrame();
	
}
