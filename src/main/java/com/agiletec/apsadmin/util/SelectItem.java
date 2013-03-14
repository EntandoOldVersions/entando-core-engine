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
package com.agiletec.apsadmin.util;

/**
 * @author E.Santoboni
 * @deprecated Use {@link com.agiletec.aps.util.SelectItem}
 */
public class SelectItem extends com.agiletec.aps.util.SelectItem {
	
	public SelectItem(String key, String value) {
		super(key, value);
	}
	
	public SelectItem(String key, String value, String optgroup) {
		super(key, value, optgroup);
	}
	
}