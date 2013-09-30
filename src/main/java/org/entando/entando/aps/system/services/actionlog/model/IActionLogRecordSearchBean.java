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

import java.util.Date;

/**
 * @author E.Santoboni - S.Puddu
 */
public interface IActionLogRecordSearchBean {
	
	public Date getStart();
	
	public Date getEnd();
	
	public String getUsername();
	
	public String getNamespace();
	
	public String getActionName();
	
	public String getParams();
	
}