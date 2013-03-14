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
package com.agiletec.apsadmin.admin.lang;

/**
 * This base interface declares the default actions for the system Languages.
 * @author E.Santoboni
 */
public interface ILangAction {
	
	/**
	 * Executes the specific action to add a lang to the system languages.
	 * @return The result code.
	 */
	public String add();
	
	/**
	 * Executes the specific action to remove a lang from the system languages.
	 * @return The result code.
	 */
	public String remove();
	
}