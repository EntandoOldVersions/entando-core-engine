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
package com.agiletec.apsadmin.system.entity.type;

/**
 * @author E.Santoboni
 */
public final class EntityTypeNamespaceInfoBean {
	
	public String getEntityManagerName() {
		return _entityManagerName;
	}
	public void setEntityManagerName(String entityManagerName) {
		this._entityManagerName = entityManagerName;
	}
	public String getNamespacePrefix() {
		return _namespacePrefix;
	}
	public void setNamespacePrefix(String namespacePrefix) {
		this._namespacePrefix = namespacePrefix;
	}
	
	private String _entityManagerName;
	private String _namespacePrefix;
	
}
