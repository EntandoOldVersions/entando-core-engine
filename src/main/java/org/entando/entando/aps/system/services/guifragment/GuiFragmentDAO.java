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
		return "id";
	}
	
	@Override
	protected boolean isForceCaseInsensitiveLikeSearch() {
		return true;
	}

	@Override
	public List<Integer> searchGuiFragments(FieldSearchFilter[] filters) {
		List guiFragmentsId = null;
		try {
			guiFragmentsId  = super.searchId(filters);
		} catch (Throwable t) {
			_logger.error("error in searchGuiFragments",  t);
			throw new RuntimeException("error in searchGuiFragments", t);
		}
		return guiFragmentsId;
	}

	@Override
	public List<Integer> loadGuiFragments() {
		List<Integer> guiFragmentsId = new ArrayList<Integer>();
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(LOAD_GUISECTIONS_ID);
			res = stat.executeQuery();
			while (res.next()) {
				int id = res.getInt("id");
				guiFragmentsId.add(id);
			}
		} catch (Throwable t) {
			_logger.error("Error loading GuiFragment list",  t);
			throw new RuntimeException("Error loading GuiFragment list", t);
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return guiFragmentsId;
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
			stat = conn.prepareStatement(ADD_GUISECTION);
			int index = 1;
			stat.setInt(index++, guiFragment.getId());
 			stat.setString(index++, guiFragment.getCode());
 			if(StringUtils.isNotBlank(guiFragment.getWidgetCode())) {
				stat.setString(index++, guiFragment.getWidgetCode());				
			} else {
				stat.setNull(index++, Types.VARCHAR);
			}
 			if(StringUtils.isNotBlank(guiFragment.getPluginCode())) {
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
			_logger.error("Error updating guiFragment {}", guiFragment.getId(),  t);
			throw new RuntimeException("Error updating guiFragment", t);
		} finally {
			this.closeDaoResources(null, stat, conn);
		}
	}

	public void updateGuiFragment(GuiFragment guiFragment, Connection conn) {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(UPDATE_GUISECTION);
			int index = 1;

 			stat.setString(index++, guiFragment.getCode());
 			if(StringUtils.isNotBlank(guiFragment.getWidgetCode())) {
				stat.setString(index++, guiFragment.getWidgetCode());				
			} else {
				stat.setNull(index++, Types.VARCHAR);
			}
 			if(StringUtils.isNotBlank(guiFragment.getPluginCode())) {
				stat.setString(index++, guiFragment.getPluginCode());				
			} else {
				stat.setNull(index++, Types.VARCHAR);
			}
 			stat.setString(index++, guiFragment.getGui());
			stat.setInt(index++, guiFragment.getId());
			stat.executeUpdate();
		} catch (Throwable t) {
			_logger.error("Error updating guiFragment {}", guiFragment.getId(),  t);
			throw new RuntimeException("Error updating guiFragment", t);
		} finally {
			this.closeDaoResources(null, stat, null);
		}
	}

	@Override
	public void removeGuiFragment(int id) {
		PreparedStatement stat = null;
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.removeGuiFragment(id, conn);
 			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			_logger.error("Error deleting guiFragment {}", id, t);
			throw new RuntimeException("Error deleting guiFragment", t);
		} finally {
			this.closeDaoResources(null, stat, conn);
		}
	}
	
	public void removeGuiFragment(int id, Connection conn) {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(DELETE_GUISECTION);
			int index = 1;
			stat.setInt(index++, id);
			stat.executeUpdate();
		} catch (Throwable t) {
			_logger.error("Error deleting guiFragment {}", id, t);
			throw new RuntimeException("Error deleting guiFragment", t);
		} finally {
			this.closeDaoResources(null, stat, null);
		}
	}

	public GuiFragment loadGuiFragment(int id) {
		GuiFragment guiFragment = null;
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			guiFragment = this.loadGuiFragment(id, conn);
		} catch (Throwable t) {
			_logger.error("Error loading guiFragment with id {}", id, t);
			throw new RuntimeException("Error loading guiFragment with id " + id, t);
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return guiFragment;
	}

	public GuiFragment loadGuiFragment(int id, Connection conn) {
		GuiFragment guiFragment = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			stat = conn.prepareStatement(LOAD_GUISECTION);
			int index = 1;
			stat.setInt(index++, id);
			res = stat.executeQuery();
			if (res.next()) {
				guiFragment = this.buildGuiFragmentFromRes(res);
			}
		} catch (Throwable t) {
			_logger.error("Error loading guiFragment with id {}", id, t);
			throw new RuntimeException("Error loading guiFragment with id " + id, t);
		} finally {
			closeDaoResources(res, stat, null);
		}
		return guiFragment;
	}

	protected GuiFragment buildGuiFragmentFromRes(ResultSet res) {
		GuiFragment guiFragment = null;
		try {
			guiFragment = new GuiFragment();				
			guiFragment.setId(res.getInt("id"));
			guiFragment.setCode(res.getString("code"));
			guiFragment.setWidgetCode(res.getString("widgetcode"));
			guiFragment.setPluginCode(res.getString("plugincode"));
			guiFragment.setGui(res.getString("gui"));
		} catch (Throwable t) {
			_logger.error("Error in buildGuiFragmentFromRes", t);
		}
		return guiFragment;
	}
	
	private static final String ADD_GUISECTION = "INSERT INTO guifragment (id, code, widgetcode, plugincode, gui ) VALUES (?, ?, ?, ?, ? )";

	private static final String UPDATE_GUISECTION = "UPDATE guifragment SET code=?,  widgetcode=?,  plugincode=?, gui=? WHERE id = ?";

	private static final String DELETE_GUISECTION = "DELETE FROM guifragment WHERE id = ?";
	
	private static final String LOAD_GUISECTION = "SELECT id, code, widgetcode, plugincode, gui  FROM guifragment WHERE id = ?";
	
	private static final String LOAD_GUISECTIONS_ID  = "SELECT id FROM guifragment";
	
}