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
package com.agiletec.aps.system.common;

import java.io.Serializable;

/**
 * Base interface for implementing Services.
 * @author E.Santoboni
 */
public interface IManager extends Serializable {
	
	/**
	 * Service initialization.
	 * @throws Exception In the case of error when service is initialized.
	 */
	public void init() throws Exception;
	
	/**
	 * Destroy method invoked on bean factory shutdown.
	 */
	public void destroy();
	
	/**
	 * Method to invoke when service restart is needed.
	 * @throws Throwable In the case of error when service is initialized.
	 */
	public void refresh() throws Throwable;
	
	
	/** 
	 * Return the service name.
	 * @return the service name.
	 */
	public String getName();
	
}
