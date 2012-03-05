/*
*
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
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
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.aps.system.services.page.events;

import com.agiletec.aps.system.common.IManager;
import com.agiletec.aps.system.common.notify.ApsEvent;
import com.agiletec.aps.system.services.page.IPage;

/**
 * Evento specifico da rilanciare in corispondenza 
 * di modifica di una pagina del portale.
 * @author E.Santoboni - M.Diana
 */
public class PageChangedEvent extends ApsEvent {
	
	public void notify(IManager srv) {
		((PageChangedObserver) srv).updateFromPageChanged(this);
	}
	
	public Class getObserverInterface() {
		return PageChangedObserver.class;
	}
	
	/**
	 * Restituisce la pagina modificata.
	 * @return La pagina modificata.
	 */
	public IPage getPage() {
		return _page;
	}

	/**
	 * Setta la pagina modificata.
	 * @param page La pagina modificata.
	 */
	public void setPage(IPage page) {
		this._page = page;
	}
	
	private IPage _page;
	
}
