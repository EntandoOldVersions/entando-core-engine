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
package com.agiletec.aps.system.common;

/**
 * This interface is for beans which support the refresh method.
 * @author E.Santoboni
 */
public interface RefreshableBean {
	
	/**
	 * Method to invoke when bean refresh is needed.
	 * @throws Throwable In the case of error when service is initialized.
	 */
	public void refresh() throws Throwable;
	
}
