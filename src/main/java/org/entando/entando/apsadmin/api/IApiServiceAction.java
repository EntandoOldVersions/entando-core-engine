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
package org.entando.entando.apsadmin.api;

/**
 * @author E.Santoboni
 */
public interface IApiServiceAction {
	
	/**
	 * Create of new api service.
	 * @return The result code.
	 */
	public String newService();
	
	/**
	 * Copy an exist showlet (physic and with parameters, joined with a exist api method) 
	 * and value the form of creation of new api service.
	 * @return The result code.
	 */
	public String copyFromShowlet();
	
	/**
	 * Edit an exist api service.
	 * @return The result code.
	 */
	public String edit();
	
	/**
	 * Save an api service.
	 * @return The result code.
	 */
	public String save();
	
	/**
	 * Start the deletion operations for the given api service.
	 * @return The result code.
	 */
	public String trash();
	
	/**
	 * Delete an api service from the system.
	 * @return The result code.
	 */
	public String delete();
}
