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
package com.agiletec.apsadmin.user.group;

/**
 * Interface defining the actions needed in order to handle groups.
 * @author E.Santoboni
 */
public interface IGroupAction {
	
	/**
	 * Create a new group.
	 * @return The result code.
	 */
	public String newGroup();
	
	/**
	 * Edit a group
	 * @return The result code.
	 */
	public String edit();
	
	/**
	 * Show the detail of a group.
	 * @return The result code.
	 */
	public String showDetail();
	
	/**
	 * Save a group.
	 * @return The result code.
	 */
	public String save();
	
	/**
	 *  Start the deletion process of a group.
	 * @return The result code.
	 */
	public String trash();
	
	/**
	 * Delete a group permanently.
	 * @return The result code.
	 */
	public String delete();
	
}