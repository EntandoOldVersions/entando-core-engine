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
package org.entando.entando.aps.system.services.actionlog.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author E.Santoboni
 */
public class ActivityStreamLikeInfos extends ArrayList<ActivityStreamLikeInfo> implements Serializable {
	
	public boolean containsUser(String username) {
		Iterator<ActivityStreamLikeInfo> iter = this.iterator();
		while (iter.hasNext()) {
			ActivityStreamLikeInfo asli = iter.next();
			if (asli.getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}
	
}