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
package com.agiletec.aps.tags.util;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.services.page.Showlet;

/**
 * @author E.Santoboni
 */
public interface IFrameDecoratorContainer {
	
	public boolean needsDecoration(Showlet showlet, RequestContext reqCtx);
	
	public boolean isShowletDecorator();
	
	public String getHeaderPath();

	public String getFooterPath();

	public int getOrder();
}
