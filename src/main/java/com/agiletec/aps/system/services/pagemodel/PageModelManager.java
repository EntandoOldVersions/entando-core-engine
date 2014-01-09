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
package com.agiletec.aps.system.services.pagemodel;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * Servizio di gestione dei modelli di pagina.
 * @author M.Diana - E.Santoboni
 */
public class PageModelManager extends AbstractService implements IPageModelManager {

	private static final Logger _logger = LoggerFactory.getLogger(PageModelManager.class);
	
	@Override
	public void init() throws Exception {
		this.loadPageModels();
		_logger.debug("{} ready. initialized {} page models", this.getClass().getName() ,this._models.size());
	}

	private void loadPageModels() throws ApsSystemException {
		try {
			this._models = this.getPageModelDAO().loadModels();
		} catch (Throwable t) {
			_logger.error("Error loading page models", t);
			throw new ApsSystemException("Error loading page models", t);
		}
	}

	/**
	 * Restituisce il modello di pagina con il codice dato
	 * @param name Il nome del modelo di pagina
	 * @return Il modello di pagina richiesto
	 */
	@Override
	public PageModel getPageModel(String name) {
		return (PageModel) _models.get(name);
	}

	/**
	 * Restituisce la Collection completa di modelli.
	 * @return la collection completa dei modelli disponibili in oggetti PageModel.
	 */
	@Override
	public Collection<PageModel> getPageModels() {
		return _models.values();
	}

	protected IPageModelDAO getPageModelDAO() {
		return _pageModelDao;
	}
	public void setPageModelDAO(IPageModelDAO pageModelDAO) {
		this._pageModelDao = pageModelDAO;
	}

	/**
	 * Map dei modelli di pagina configurati nel sistema
	 */
	private Map<String, PageModel> _models;

	private IPageModelDAO _pageModelDao;

}
