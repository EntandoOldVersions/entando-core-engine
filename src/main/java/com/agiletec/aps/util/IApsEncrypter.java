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
package com.agiletec.aps.util;

import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * Encrypter Interface.
 * @author E.Santoboni
 */
public interface IApsEncrypter {
	
	/**
	 * Encrypt the given plain text using the default algorithm
	 * @param plainText the string to encrypt
	 * @return the given string encrypted
	 * @throws ApsSystemException in caso d'errore
	 */
	public String encrypt(String plainText) throws ApsSystemException;
	
}