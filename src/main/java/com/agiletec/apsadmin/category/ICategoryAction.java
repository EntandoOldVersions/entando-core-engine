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
package com.agiletec.apsadmin.category;

/**
 * Interface defining the actions needed in order to handle categories.  
 * @author E.Santoboni
 */
public interface ICategoryAction {
	
	/**
	 * Create a new category.
	 * @return The result code.
	 */
	public String add();
	
	/**
	 * Edit an existing category.
	 * @return The result code.
	 */
	public String edit();
	
	/**
	 * Show the detail of a category.
	 * @return The result code.
	 */
	public String showDetail();
	
	/**
	 * Start the deletion process of a category.
	 * @return The result code.
	 */
	public String trash();
	
	/**
	 * Delete a category permanently.
	 * @return The result code.
	 */
	public String delete();
	
	/**
	 * Save a category.
	 * @return The result code.
	 */
	public String save();
	
}
