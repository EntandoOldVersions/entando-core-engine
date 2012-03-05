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
package com.agiletec.aps.system.services.pagemodel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.agiletec.aps.system.common.AbstractDAO;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.showlettype.IShowletTypeManager;

/**
 * Data Access Object per i modelli di pagina (PageModel)0
 * @author 
 */
public class PageModelDAO extends AbstractDAO implements IPageModelDAO {
	
	/**
	 * Carica e restituisce la mappa dei modelli di pagina.
	 * @return La mappa dei modelli.
	 */
	public Map<String, PageModel> loadModels() {
		Connection conn = null;
		Statement stat = null;
		ResultSet res = null;
		Map<String, PageModel> models = new HashMap<String, PageModel>();
		try {
			conn = this.getConnection();
			stat = conn.createStatement();
			res = stat.executeQuery(ALL_PAGEMODEL);
			while (res.next()) {
				PageModel pageModel = this.getPageModelFromResultSet(res);
				models.put(pageModel.getCode(), pageModel);
			}
		} catch (Throwable t) {
			processDaoException(t, "Error loading the page models", "loadModels");
		} finally{
			closeDaoResources(res, stat, conn);
		}
		return models;
	}
	
	/**
	 * Costruisce e restituisce un modello di pagina 
	 * leggendo una riga di resultset.
	 * @param res Il resultset da leggere
	 * @return Il modello di pagina generato
	 * @throws ApsSystemException In caso di errore.
	 */
	protected PageModel getPageModelFromResultSet(ResultSet res) throws ApsSystemException {
		PageModel pageModel = new PageModel();
		String code = null;
		try {
			code = res.getString(1);
			pageModel.setCode(code);
			pageModel.setDescr(res.getString(2));
			String xmlFrames = res.getString(3);
			if (null != xmlFrames && xmlFrames.length() > 0) {
				PageModelDOM pageModelDOM = new PageModelDOM(xmlFrames, this.getShowletTypeManager());
				pageModel.setFrames(pageModelDOM.getFrames());
				pageModel.setMainFrame(pageModelDOM.getMainFrame());
				pageModel.setDefaultShowlet(pageModelDOM.getDefaultShowlet());
			}
			pageModel.setPluginCode(res.getString(4));
		} catch (Throwable t) {
			processDaoException(t, "Error building the page model code '" + code + "'", "getPageModelFromResultSet");
		}
		return pageModel;
	}
	
	protected IShowletTypeManager getShowletTypeManager() {
		return _showletTypeManager;
	}
	public void setShowletTypeManager(IShowletTypeManager showletTypeManager) {
		this._showletTypeManager = showletTypeManager;
	}

	private IShowletTypeManager _showletTypeManager;
	
	private final String ALL_PAGEMODEL = 
		"SELECT code, descr, frames, plugincode FROM pagemodels";
	
}
