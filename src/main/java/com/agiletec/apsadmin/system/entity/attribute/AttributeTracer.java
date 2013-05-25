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
package com.agiletec.apsadmin.system.entity.attribute;

/**
 * This class implements the 'tracer' for the jAPS Attributes. This class is
 * used, with the singles attributes, to trace the position inside 'composite'
 * attributes. This class is involved during the update and validation process
 * of the Attribute and, furthermore, it guarantees the correct construction of
 * the form in the content edit interface.
 * @author E.Santoboni
 * @deprecated As of version 2.4.1 of Entando, use com.agiletec.aps.system.common.entity.model.AttributeTracer class.
 * Attribute validation moved inside single attribute,
 */
public class AttributeTracer extends com.agiletec.aps.system.common.entity.model.AttributeTracer {
	
}
