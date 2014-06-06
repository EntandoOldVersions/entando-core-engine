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
package com.agiletec.aps.system.services.pagemodel.events;

import com.agiletec.aps.system.common.IManager;
import com.agiletec.aps.system.common.notify.ApsEvent;
import com.agiletec.aps.system.services.pagemodel.PageModel;

/**
 * @author E.Santoboni
 */
public class PageModelChangedEvent extends ApsEvent {
	
	@Override
	public void notify(IManager srv) {
		((PageModelChangedObserver) srv).updateFromPageModelChanged(this);
	}
	
	@Override
	public Class getObserverInterface() {
		return PageModelChangedObserver.class;
	}
	
	public PageModel getPageModel() {
		return _pageModel;
	}
	public void setPageModel(PageModel pageModel) {
		this._pageModel = pageModel;
	}
	
	public int getOperationCode() {
		return _operationCode;
	}
	public void setOperationCode(int operationCode) {
		this._operationCode = operationCode;
	}
	
	private PageModel _pageModel;
	
	private int _operationCode;
	
	public static final int INSERT_OPERATION_CODE = 1;
	public static final int REMOVE_OPERATION_CODE = 2;
	public static final int UPDATE_OPERATION_CODE = 3;
	
}
