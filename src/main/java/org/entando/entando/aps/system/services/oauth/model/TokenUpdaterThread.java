/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
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
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package org.entando.entando.aps.system.services.oauth.model;

import com.agiletec.aps.system.ApsSystemUtils;
import org.entando.entando.aps.system.services.oauth.IOAuthTokenDAO;

/**
 * Thread Class delegate to update OAuth token.
 * @author E.Santoboni
 */
public class TokenUpdaterThread extends Thread {
	
	public TokenUpdaterThread(String accessToken, int tokenTimeValidity,
			IOAuthTokenDAO tokenDao) {
		this._accessToken = accessToken;
		this._tokenTimeValidity = tokenTimeValidity;
		this._tokenDao = tokenDao;
	}
	
	public void run() {
		try {
			this._tokenDao.refreshAccessTokens(this._accessToken, this._tokenTimeValidity);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "run");
		}
	}
	
	private String _accessToken;
	private int _tokenTimeValidity;
	private IOAuthTokenDAO _tokenDao;
	
}