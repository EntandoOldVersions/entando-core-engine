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
package com.agiletec.plugins.jacms.apsadmin.tags;

import com.agiletec.apsadmin.tags.EntityTypeInfoTag;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;

/**
 * Returns a content type (or one of its property) through the code.
 * You can choose whether to return the entire object (leaving the attribute "property" empty) or a single property.
 * The names of the available property of "Content Type": "typeCode", "typeDescr", 
 * "attributeMap" (map of attributes indexed by the name), "attributeList" (list of attributes).
 * @author E.Santoboni
 */
public class ContentTypeInfoTag extends EntityTypeInfoTag {
	
	@Override
	protected String getEntityManagerName() {
		return JacmsSystemConstants.CONTENT_MANAGER;
	}
	
}