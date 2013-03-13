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
package com.agiletec.aps.tags.util;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.services.page.Showlet;

/**
 * @author E.Santoboni
 */
public class BaseFrameDecoratorContainer implements IFrameDecoratorContainer {
	
	@Override
	public boolean needsDecoration(Showlet showlet, RequestContext reqCtx) {
		return true;
	}
	
	@Override
	public boolean isShowletDecorator() {
		return false;
	}
	
	@Override
	public String getHeaderPath() {
		return _headerPath;
	}
	public void setHeaderPath(String headerPath) {
		this._headerPath = headerPath;
	}
	
	@Override
	public String getFooterPath() {
		return _footerPath;
	}
	public void setFooterPath(String footerPath) {
		this._footerPath = footerPath;
	}
	
	@Override
	public int getOrder() {
		return _order;
	}
	public void setOrder(int order) {
		this._order = order;
	}
	
	private String _headerPath;
	private String _footerPath;
	private int _order;
	
}
