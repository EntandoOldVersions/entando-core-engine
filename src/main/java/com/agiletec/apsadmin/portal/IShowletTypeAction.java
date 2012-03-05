/*
*
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* Entando is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.apsadmin.portal;

/**
 * @author E.Santoboni
 */
public interface IShowletTypeAction {
	
	/**
	 * Create of new user showlet.
	 * @return The result code.
	 */
	public String newUserShowlet();
	
	/**
	 * Copy an exist showlet (physic and with parameters) and value the form 
	 * of creation of new user showlet.
	 * @return The result code.
	 */
	public String copy();
	
	/**
	 * Edit an exist showlet type.
	 * @return The result code.
	 */
	public String edit();
	
	/**
	 * Update an exist showlet type.
	 * @return The result code.
	 */
	public String save();
	
	/**
	 * Start the deletion operations for the given showlet type.
	 * @return The result code.
	 */
	public String trash();
	
	/**
	 * Delete a showlet type from the system.
	 * @return The result code.
	 */
	public String delete();
	
}