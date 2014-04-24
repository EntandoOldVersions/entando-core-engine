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
package org.entando.entando.aps.system.services.guifragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import com.agiletec.aps.system.common.AbstractSearcherDAO;
import com.agiletec.aps.system.common.FieldSearchFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuiFragmentDAO extends AbstractSearcherDAO implements IGuiFragmentDAO {

	private static final Logger _logger =  LoggerFactory.getLogger(GuiFragmentDAO.class);

	@Override
	protected String getTableFieldName(String metadataFieldKey) {
		return metadataFieldKey;
	}
	
	@Override
	protected String getMasterTableName() {
		return "guifragment";
	}
	
	@Override
	protected String getMasterTableIdFieldName() {
		return "code";
	}
	
	@Override
	protected boolean isForceCaseInsensitiveLikeSearch() {
		return true;
	}
	
	@Override
	public List<String> searchGuiFragments(FieldSearchFilter[] filters) {
		List<String> guiFragmentsId = null;
		try {
			guiFragmentsId  = super.searchId(filters);
		} catch (Throwable t) {
			_logger.error("error in searchGuiFragments",  t);
			throw new RuntimeException("error in searchGuiFragments", t);
		}
		return guiFragmentsId;
	}
	
	@Override
	public List<String> loadGuiFragments() {
		/*
		List<String> guiFragmentsId = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(LOAD_GUIFRAGMENTS_ID);
			res = stat.executeQuery();
			while (res.next()) {
				String id = res.getString("code");
				guiFragmentsId.add(id);
			}
		} catch (Throwable t) {
			_logger.error("Error loading GuiFragment list",  t);
			throw new RuntimeException("Error loading GuiFragment list", t);
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return guiFragmentsId;
		*/
		return this.searchGuiFragments(null);
	}
	
	@Override
	public void insertGuiFragment(GuiFragment guiFragment) {
		PreparedStatement stat = null;
		Connection conn  = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.insertGuiFragment(guiFragment, conn);
 			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			_logger.error("Error on insert guiFragment",  t);
			throw new RuntimeException("Error on insert guiFragment", t);
		} finally {
			this.closeDaoResources(null, stat, conn);
		}
	}
	
	public void insertGuiFragment(GuiFragment guiFragment, Connection conn) {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(ADD_GUIFRAGMENT);
			int index = 1;
			//stat.setInt(index++, guiFragment.getId());
 			stat.setString(index++, guiFragment.getCode());
 			if (StringUtils.isNotBlank(guiFragment.getWidgetTypeCode())) {
				stat.setString(index++, guiFragment.getWidgetTypeCode());				
			} else {
				stat.setNull(index++, Types.VARCHAR);
			}
 			if (StringUtils.isNotBlank(guiFragment.getPluginCode())) {
				stat.setString(index++, guiFragment.getPluginCode());				
			} else {
				stat.setNull(index++, Types.VARCHAR);
			}
 			stat.setString(index++, guiFragment.getGui());
			stat.executeUpdate();
		} catch (Throwable t) {
			_logger.error("Error on insert guiFragment",  t);
			throw new RuntimeException("Error on insert guiFragment", t);
		} finally {
			this.closeDaoResources(null, stat, null);
		}
	}

	@Override
	public void updateGuiFragment(GuiFragment guiFragment) {
		PreparedStatement stat = null;
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.updateGuiFragment(guiFragment, conn);
 			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			_logger.error("Error updating guiFragment {}", guiFragment.getCode(),  t);
			throw new RuntimeException("Error updating guiFragment", t);
		} finally {
			this.closeDaoResources(null, stat, conn);
		}
	}

	public void updateGuiFragment(GuiFragment guiFragment, Connection conn) {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(UPDATE_GUIFRAGMENT);
			int index = 1;
 			//stat.setString(index++, guiFragment.getCode());
 			if(StringUtils.isNotBlank(guiFragment.getWidgetTypeCode())) {
				stat.setString(index++, guiFragment.getWidgetTypeCode());				
			} else {
				stat.setNull(index++, Types.VARCHAR);
			}
 			if(StringUtils.isNotBlank(guiFragment.getPluginCode())) {
				stat.setString(index++, guiFragment.getPluginCode());				
			} else {
				stat.setNull(index++, Types.VARCHAR);
			}
 			stat.setString(index++, guiFragment.getGui());
			//stat.setInt(index++, guiFragment.getId());
			stat.setString(index++, guiFragment.getCode());
			stat.executeUpdate();
		} catch (Throwable t) {
			_logger.error("Error updating guiFragment {}", guiFragment.getCode(),  t);
			throw new RuntimeException("Error updating guiFragment", t);
		} finally {
			this.closeDaoResources(null, stat, null);
		}
	}
	
	@Override
	public void removeGuiFragment(String code) {
		PreparedStatement stat = null;
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.removeGuiFragment(code, conn);
 			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			_logger.error("Error deleting guiFragment {}", code, t);
			throw new RuntimeException("Error deleting guiFragment", t);
		} finally {
			this.closeDaoResources(null, stat, conn);
		}
	}
	
	public void removeGuiFragment(String code, Connection conn) {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(DELETE_GUIFRAGMENT);
			int index = 1;
			stat.setString(index++, code);
			stat.executeUpdate();
		} catch (Throwable t) {
			_logger.error("Error deleting guiFragment {}", code, t);
			throw new RuntimeException("Error deleting guiFragment", t);
		} finally {
			this.closeDaoResources(null, stat, null);
		}
	}
	
	@Override
	public GuiFragment loadGuiFragment(String code) {
		GuiFragment guiFragment = null;
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			guiFragment = this.loadGuiFragment(code, conn);
		} catch (Throwable t) {
			_logger.error("Error loading guiFragment with id {}", code, t);
			throw new RuntimeException("Error loading guiFragment with id " + code, t);
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return guiFragment;
	}
	
	public GuiFragment loadGuiFragment(String code, Connection conn) {
		GuiFragment guiFragment = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			stat = conn.prepareStatement(LOAD_GUIFRAGMENT);
			int index = 1;
			//stat.setInt(index++, id);
			stat.setString(index++, code);
			res = stat.executeQuery();
			if (res.next()) {
				guiFragment = this.buildGuiFragmentFromRes(res);
			}
		} catch (Throwable t) {
			_logger.error("Error loading guiFragment with id {}", code, t);
			throw new RuntimeException("Error loading guiFragment with id " + code, t);
		} finally {
			closeDaoResources(res, stat, null);
		}
		return guiFragment;
	}

	protected GuiFragment buildGuiFragmentFromRes(ResultSet res) {
		GuiFragment guiFragment = null;
		try {
			guiFragment = new GuiFragment();				
			//guiFragment.setId(res.getInt("id"));
			guiFragment.setCode(res.getString("code"));
			guiFragment.setWidgetTypeCode(res.getString("widgettypecode"));
			guiFragment.setPluginCode(res.getString("plugincode"));
			guiFragment.setGui(res.getString("gui"));
		} catch (Throwable t) {
			_logger.error("Error in buildGuiFragmentFromRes", t);
		}
		return guiFragment;
	}
	
	private static final String ADD_GUIFRAGMENT = "INSERT INTO guifragment (code, widgettypecode, plugincode, gui ) VALUES (?, ?, ?, ? )";

	private static final String UPDATE_GUIFRAGMENT = "UPDATE guifragment SET widgettypecode = ?, plugincode = ? , gui = ? WHERE code = ? ";

	private static final String DELETE_GUIFRAGMENT = "DELETE FROM guifragment WHERE code = ?";
	
	private static final String LOAD_GUIFRAGMENT = "SELECT code, widgettypecode, plugincode, gui FROM guifragment WHERE code = ?";
	
	//private static final String LOAD_GUIFRAGMENTS_ID  = "SELECT code FROM guifragment";
	
}