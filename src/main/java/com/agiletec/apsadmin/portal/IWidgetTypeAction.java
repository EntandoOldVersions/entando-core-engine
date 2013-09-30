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
public interface IWidgetTypeAction {
	
	/**
	 * @deprecated Use {@link #newUserWidget()} instead
	 */
	public String newUserShowlet();

	/**
	 * Create of new user widget.
	 * @return The result code.
	 */
	public String newUserWidget();
	
	/**
	 * Copy an existing widget (physic and with parameters) and value the form 
	 * of creation of new user widget.
	 * @return The result code.
	 */
	public String copy();
	
	/**
	 * Edit an exist widget type.
	 * @return The result code.
	 */
	public String edit();
	
	/**
	 * Update an exist widget type.
	 * @return The result code.
	 */
	public String save();
	
	/**
	 * Start the deletion operations for the given widget type.
	 * @return The result code.
	 */
	public String trash();
	
	/**
	 * Delete a widget type from the system.
	 * @return The result code.
	 */
	public String delete();
	
}