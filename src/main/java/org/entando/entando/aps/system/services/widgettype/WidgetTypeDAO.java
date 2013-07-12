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
package org.entando.entando.aps.system.services.widgettype;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.AbstractDAO;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.util.ApsProperties;

/**
 * Data Access Object per i tipi di showlet (WidgetType).
 * @author 
 */
public class WidgetTypeDAO extends AbstractDAO implements IWidgetTypeDAO {
	
	/**
	 * Carica e restituisce il Map dei tipi di showlet.
	 * @return Il map dei tipi di showlet
	 */
	@Override
	public Map<String, WidgetType> loadShowletTypes() {
		Connection conn = null;
		Statement stat = null;
		ResultSet res = null;
		Map<String, WidgetType> showletTypes = new HashMap<String, WidgetType>();
		try {
			conn = this.getConnection();
			stat = conn.createStatement();
			res = stat.executeQuery(ALL_SHOWLET_TYPES);
			while (res.next()) {
				WidgetType showletType = this.showletTypeFromResultSet(res);
				showletTypes.put(showletType.getCode(), showletType);
			}
		} catch (Throwable t) {
			processDaoException(t, "Error loading showlets", "loadShowletTypes");
		} finally{
			closeDaoResources(res, stat, conn);
		}
		return showletTypes;
	}

	/**
	 * Costruisce e restituisce un tipo di showlet leggendo una riga di recordset.
	 * @param res Il resultset da leggere.
	 * @return Il tipo di showlet generato.
	 * @throws ApsSystemException In caso di errore
	 */
	protected WidgetType showletTypeFromResultSet(ResultSet res) throws ApsSystemException {
		WidgetType showletType = new WidgetType();
		String code = null;
		try {
			code = res.getString(1);
			showletType.setCode(code);
			String xmlTitles = res.getString(2);
			ApsProperties titles = new ApsProperties();
			titles.loadFromXml(xmlTitles);
			showletType.setTitles(titles);
			String xml = res.getString(3);
			if (null != xml && xml.trim().length() > 0) {
				WidgetTypeDOM showletTypeDom = new WidgetTypeDOM(xml, this.getLangManager().getLangs());
				showletType.setTypeParameters(showletTypeDom.getParameters());
				showletType.setAction(showletTypeDom.getAction());
			}
			showletType.setPluginCode(res.getString(4));
			showletType.setParentTypeCode(res.getString(5));
			String config = res.getString(6);
			if (null != config && config.trim().length() > 0) {
				ApsProperties defaultConfig = new ApsProperties();
				defaultConfig.loadFromXml(config);
				showletType.setConfig(defaultConfig);
			}
			if ((null != showletType.getConfig() && null == showletType.getParentTypeCode())) {
				throw new ApsSystemException("Default configuration found in the type '" +
						code + "' with no parent type assigned");
			}
			int isLocked = res.getInt(7);
			showletType.setLocked(isLocked == 1);
			String mainGroup = res.getString(8);
			if (null != mainGroup && mainGroup.trim().length() > 0) {
				showletType.setMainGroup(mainGroup.trim());
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "showletTypeFromResultSet",
					"Error parsing the Showlet Type '" + code + "'");
			throw new ApsSystemException("Error in the parsing in the Showlet Type '" + code + "'", t);
		}
		return showletType;
	}
	
	@Override
	public void addShowletType(WidgetType showletType) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(ADD_SHOWLET_TYPE);
			//(code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked)
			stat.setString(1, showletType.getCode());
			stat.setString(2, showletType.getTitles().toXml());
			if (null != showletType.getTypeParameters()) {
				WidgetTypeDOM showletTypeDom = new WidgetTypeDOM(showletType.getTypeParameters(), showletType.getAction());
				stat.setString(3, showletTypeDom.getXMLDocument());
			} else {
				stat.setNull(3, Types.VARCHAR);
			}
			stat.setString(4, showletType.getPluginCode());
			stat.setString(5, showletType.getParentTypeCode());
			if (null != showletType.getConfig()) {
				stat.setString(6, showletType.getConfig().toXml());
			} else {
				stat.setNull(6, Types.VARCHAR);
			}
			if (showletType.isLocked()) {
				stat.setInt(7, 1);
			} else {
				stat.setInt(7, 0);
			}
			stat.setString(8, showletType.getMainGroup());
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error while adding a new showlet type", "addShowletType");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	@Override
	public void deleteShowletType(String showletTypeCode) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(DELETE_SHOWLET_TYPE);
			stat.setString(1, showletTypeCode);
			stat.setInt(2, 0);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error deleting showlet type", "deleteShowletType");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	@Override
	@Deprecated
	public void updateShowletTypeTitles(String showletTypeCode,	ApsProperties titles) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(UPDATE_SHOWLET_TYPE_TITLES);
			stat.setString(1, titles.toXml());
			stat.setString(2, showletTypeCode);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error updating showlet type titles", "updateShowletTypeTitles");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	@Override
	@Deprecated
	public void updateShowletType(String showletTypeCode, ApsProperties titles, ApsProperties defaultConfig) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(UPDATE_SHOWLET_TYPE_DEPRECATED);
			stat.setString(1, titles.toXml());
			if (null == defaultConfig || defaultConfig.size() == 0) {
				stat.setNull(2, Types.VARCHAR);
			} else {
				stat.setString(2, defaultConfig.toXml());
			}
			stat.setString(3, showletTypeCode);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error updating showlet type", "updateShowletType");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	@Override
	public void updateShowletType(String showletTypeCode, ApsProperties titles, ApsProperties defaultConfig, String mainGroup) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(UPDATE_SHOWLET_TYPE);
			stat.setString(1, titles.toXml());
			if (null == defaultConfig || defaultConfig.size() == 0) {
				stat.setNull(2, Types.VARCHAR);
			} else {
				stat.setString(2, defaultConfig.toXml());
			}
			stat.setString(3, mainGroup);
			stat.setString(4, showletTypeCode);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error updating showlet type", "updateShowletType");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	protected ILangManager getLangManager() {
		return _langManager;
	}
	public void setLangManager(ILangManager langManager) {
		this._langManager = langManager;
	}
	
	private ILangManager _langManager;
	
	private final String ALL_SHOWLET_TYPES = 
		"SELECT code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup FROM showletcatalog";
	
	private final String ADD_SHOWLET_TYPE = 
		"INSERT INTO showletcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup) " +
		"VALUES ( ? , ? , ? , ? , ? , ? , ? , ?)";
	
	private final String DELETE_SHOWLET_TYPE = 
		"DELETE FROM showletcatalog WHERE code = ? AND locked = ? ";
	
	@Deprecated
	private final String UPDATE_SHOWLET_TYPE_DEPRECATED = 
		"UPDATE showletcatalog SET titles = ? , defaultconfig = ? WHERE code = ? ";
	
	private final String UPDATE_SHOWLET_TYPE = 
		"UPDATE showletcatalog SET titles = ? , defaultconfig = ? , maingroup = ? WHERE code = ? ";
	
	@Deprecated
	private final String UPDATE_SHOWLET_TYPE_TITLES = 
		"UPDATE showletcatalog SET titles = ? WHERE code = ? ";
	
}