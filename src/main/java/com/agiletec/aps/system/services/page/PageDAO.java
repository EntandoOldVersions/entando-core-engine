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
package com.agiletec.aps.system.services.page;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.entando.entando.aps.system.services.widgettype.WidgetType;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.AbstractDAO;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.pagemodel.IPageModelManager;
import com.agiletec.aps.util.ApsProperties;

/**
 * Data Access Object for the 'page' objects
 * @author M.Diana - E.Santoboni
 */
public class PageDAO extends AbstractDAO implements IPageDAO {
	
	/**
	 * Load a sorted list of the pages and the configuration of the showlets 
	 * @return the list of pages
	 */
	@Override
	public List<IPage> loadPages() {
		Connection conn = null;
		Statement stat = null;
		ResultSet res = null;
		List<IPage> pages = null;
		try {
			conn = this.getConnection();
			stat = conn.createStatement();
			res = stat.executeQuery(ALL_PAGES);
			pages = this.createPages(res);
		} catch (Throwable t) {
			processDaoException(t, "Error loading pages", "loadPages");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return pages;
	}

	/**
	 * Read & create in a single passage, for efficiency reasons, the pages and the 
	 * association of the associated showlets.
	 * @param res the result set where to extract pages information from.
	 * @return The list of the pages defined in the system
	 * @throws Throwable In case of error
	 */
	protected List<IPage> createPages(ResultSet res) throws Throwable {
		List<IPage> pages = new ArrayList<IPage>();
		Page page = null;
		Widget widgets[] = null;
		int numFrames = 0;
		String prevCode = "...no previous code...";
		while (res.next()) {
			String code = res.getString(3);
			if (!code.equals(prevCode)) {
				if (page != null) {
					pages.add(page);
				}
				page = this.createPage(code, res);
				numFrames = page.getModel().getFrames().length;
				widgets = new Widget[numFrames];
				page.setWidgets(widgets);
				prevCode = code;
			}
			int pos = res.getInt(9);
			if (pos >= 0 && pos < numFrames) {
				Widget widget = this.createShowlet(page, pos, res);//this.createShowlet(record, page);
				widgets[pos] = widget;
			} else {
				ApsSystemUtils.getLogger().info("The position read from the database exceeds " +
						"the numer of frames defined in the model of the page '"+ page.getCode()+"'");
			}
		}
		pages.add(page);
		return pages;
	}
	
	protected Page createPage(String code, ResultSet res) throws Throwable {
		Page page = new Page();
		page.setCode(code);
		page.setParentCode(res.getString(1));
		page.setPosition(res.getInt(2));
		Integer showable = new Integer (res.getInt(4));
		page.setShowable(showable.intValue() == 1);
		page.setModel(this.getPageModelManager().getPageModel(res.getString(5)));
		String titleText = res.getString(6);
		ApsProperties titles = new ApsProperties();
		try {
			titles.loadFromXml(titleText);
		} catch (Throwable t) {
			String msg = "IO error detected while parsing the titles of the page '" + page.getCode()+"'";
			ApsSystemUtils.logThrowable(t, this, "createPage", msg);
			throw new ApsSystemException(msg, t);
		}
		page.setTitles(titles);
		page.setGroup(res.getString(7));
		String extraConfig = res.getString(8);
		if (null != extraConfig && extraConfig.trim().length() > 0) {
			try {
				PageExtraConfigDOM configDom = new PageExtraConfigDOM();
				configDom.addExtraConfig(page, extraConfig);
			} catch (Throwable t) {
				String msg = "IO error detected while parsing the extra config of the page '" + page.getCode()+"'";
				ApsSystemUtils.logThrowable(t, this, "createPage", msg);
				throw new ApsSystemException(msg, t);
			}
		}
		return page;
	}
	
	protected Widget createShowlet(IPage page, int pos, ResultSet res) throws Throwable {
		String typeCode = res.getString(10);
		if (null == typeCode) {
			return null;
		}
		Widget widget = new Widget();
		WidgetType type = this.getWidgetTypeManager().getWidgetType(typeCode);
		widget.setType(type);
		ApsProperties config = new ApsProperties();
		String configText = res.getString(11);
		if (null != configText && configText.trim().length() > 0) {
			try {
				config.loadFromXml(configText);
			} catch (Throwable t) {
				String msg = "IO error detected while parsing the configuration of the showlet in position " +pos+
					" of the page '"+ page.getCode()+"'";
				ApsSystemUtils.logThrowable(t, this, "createShowlet", msg);
				throw new ApsSystemException(msg, t);
			}
		} else {
			config = type.getConfig();
		}
		widget.setConfig(config);
		String contentPublished = res.getString(12);
		widget.setPublishedContent(contentPublished);
		return widget;
	}

	/**
	 * Insert a new page.
	 * @param page The new page to insert.
	 */
	@Override
	public void addPage(IPage page) {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.addPageRecord(page, conn);
			this.addWidgetForPage(page, conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error while adding a new page", "addPage");
		} finally {
			closeConnection(conn);
		}
	}
	
	protected void addPageRecord(IPage page, Connection conn) throws ApsSystemException {
		int position = 1;
		IPage[] sisters = page.getParent().getChildren();
		if (null != sisters && sisters.length > 0) {
			IPage last = sisters[sisters.length - 1];
			if (null != last) {
				position = last.getPosition() + 1;
			} else {
				position = sisters.length + 1;
			}
		}
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(ADD_PAGE);
			stat.setString(1, page.getCode());
			stat.setString(2, page.getParent().getCode());
			if (page.isShowable()) {
				stat.setInt(3, 1);
			} else {
				stat.setInt(3, 0);
			}
			stat.setInt(4, position);
			stat.setString(5, page.getModel().getCode());
			stat.setString(6, page.getTitles().toXml());
			stat.setString(7, page.getGroup());
			String extraConfig = this.getExtraConfig(page);
			stat.setString(8, extraConfig);
			stat.executeUpdate();
		} catch (Throwable t) {
			processDaoException(t, "Error adding a new page record", "addPageRecord");
		} finally {
			closeDaoResources(null, stat);
		}
	}
	
	/**
	 * Delete the page identified by the given code.
	 * @param page The page to delete.
	 */
	@Override
	public void deletePage(IPage page) {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.deleteWidgets(page.getCode(), conn);
			this.deletePageRecord(page.getCode(), conn);
			this.shiftPages(page.getParentCode(), page.getPosition(), conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error deleting the page", "deletePage");
		} finally {
			closeConnection(conn);
		}
	}

	protected void deletePageRecord(String pageCode, Connection conn) throws ApsSystemException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(DELETE_PAGE);
			stat.setString(1, pageCode);
			stat.executeUpdate();
		} catch (Throwable t) {
			processDaoException(t, "Error deleting a page record", "deletePageRecord");
		} finally {
			closeDaoResources(null, stat);
		}
	}

	/**
	 * @throws ApsSystemException In case of database error
	 * @deprecated Use {@link #deleteWidgets(String,Connection)} instead
	 */
	protected void deleteShowlets(String codePage, Connection conn) throws ApsSystemException {
		deleteWidgets(codePage, conn);
	}

	/**
	 * Delete the widget associated to a page.
	 * @param codePage The code of the page containing the widget to delete.
	 * @throws ApsSystemException In case of database error
	 */
	protected void deleteWidgets(String codePage, Connection conn) throws ApsSystemException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(DELETE_WIDGETS_FOR_PAGE);
			stat.setString(1, codePage);
			stat.executeUpdate();
		} catch (Throwable t) {
			processDaoException(t, "Error while deleting showlets", "deleteWidgets");
		} finally {
			closeDaoResources(null, stat);
		}
	}

	/**
	 * Decrement by one the position of a group of pages to compact the positions after a deletion
	 * @param parentCode the code of the parent of the pages to compact.
	 * @param position The empty position which needs to be compacted.
	 * @param conn The connection to the database
	 * @throws ApsSystemException In case of database error
	 */
	protected void shiftPages(String parentCode, int position, Connection conn) throws ApsSystemException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(SHIFT_PAGE);
			stat.setString(1, parentCode);
			stat.setInt(2, position);
			stat.executeUpdate();
		} catch (Throwable t) {
			processDaoException(t, "Error while compating positions", "shiftPages");
		} finally {
			closeDaoResources(null, stat);
		}
	}

	/**
	 * Updates the position for the page movement
	 * @param pageDown The page to move downwards
	 * @param pageUp The page to move upwards
	 */
	@Override
	public void updatePosition(IPage pageDown, IPage pageUp) {
		Connection conn = null;
		PreparedStatement stat = null;
		PreparedStatement stat2 = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);

			stat = conn.prepareStatement(MOVE_DOWN);
			stat.setString(1, pageDown.getCode());
			stat.executeUpdate();

			stat2 = conn.prepareStatement(MOVE_UP);
			stat2.setString(1, pageUp.getCode());
			stat2.executeUpdate();

			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error detected while updating positions", "updatePosition");
		} finally {
			closeDaoResources(null, stat);
			closeDaoResources(null, stat2, conn);
		}
	}

	/**
	 * Updates a page record in the database.
	 * @param page The page to update
	 */
	@Override
	public void updatePage(IPage page) {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.deleteWidgets(page.getCode(), conn);
			this.updatePageRecord(page, conn);
			this.addWidgetForPage(page, conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error while updating the page", "updatePage");
		} finally {
			closeConnection(conn);
		}
	}

	protected void updatePageRecord(IPage page, Connection conn) throws ApsSystemException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(UPDATE_PAGE);
			stat.setString(1, page.getParentCode());
			if (page.isShowable()) {
				stat.setInt(2, 1);
			} else {
				stat.setInt(2, 0);
			}
			stat.setString(3, page.getModel().getCode());
			stat.setString(4, page.getTitles().toXml());
			stat.setString(5, page.getGroup());
			String extraConfig = this.getExtraConfig(page);
			stat.setString(6, extraConfig);
			stat.setString(7, page.getCode());
			stat.executeUpdate();
		} catch (Throwable t) {
			processDaoException(t, "Error while updating the page record", "updatePageRecord");
		} finally {
			closeDaoResources(null, stat);
		}
	}
	
	protected String getExtraConfig(IPage page) {
		PageExtraConfigDOM dom = this.getExtraConfigDOM();
		return dom.extractXml(page);
	}
	
	protected PageExtraConfigDOM getExtraConfigDOM() {
		return new PageExtraConfigDOM();
	}
	
	/**
	 * @deprecated Use {@link #addWidgetForPage(IPage,Connection)} instead
	 */
	protected void addShowletForPage(IPage page, Connection conn) throws ApsSystemException {
		addWidgetForPage(page, conn);
	}

	protected void addWidgetForPage(IPage page, Connection conn) throws ApsSystemException {
		if (null == page.getWidgets()) return;
		PreparedStatement stat = null;
		try {
			Widget[] widgets = page.getWidgets();
			stat = conn.prepareStatement(ADD_WIDGET_FOR_PAGE);
			for (int i = 0; i < widgets.length; i++) {
				Widget widget = widgets[i];
				if (widget != null) {
					if (null == widget.getType()) {
						ApsSystemUtils.getLogger().error("Widget Type null when adding " +
								"widget on frame '" + i + "' of page '" + page.getCode() + "'");
						continue;
					}
					this.valueAddShowletStatement(page.getCode(), i, widget, stat);
					stat.addBatch();
					stat.clearParameters();
				}
			}
			stat.executeBatch();
		} catch (Throwable t) {
			processDaoException(t, "Error while inserting the showlets in a page", "addWidgetForPage");
		} finally {
			closeDaoResources(null, stat);
		}
	}

	/**
	 * @deprecated Use {@link #removeWidget(String,int)} instead
	 */
	@Override
	public void removeShowlet(String pageCode, int pos) {
		removeWidget(pageCode, pos);
	}

	@Override
	public void removeWidget(String pageCode, int pos) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(REMOVE_WIDGET_FROM_FRAME);
			stat.setString(1, pageCode);
			stat.setInt(2, pos);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "" +
					"Error removing the widget in the page '" +pageCode + "' in the frame " + pos, "removeWidget");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	/**
	 * @deprecated Use {@link #joinWidget(String,Widget,int)} instead
	 */
	@Override
	public void joinShowlet(String pageCode, Widget widget, int pos) {
		joinWidget(pageCode, widget, pos);
	}

	@Override
	public void joinWidget(String pageCode, Widget widget, int pos) {
		this.removeWidget(pageCode, pos);
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(ADD_WIDGET_FOR_PAGE);
			this.valueAddShowletStatement(pageCode, pos, widget, stat);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error adding a widget in the frame " +pos+" of the page '"+pageCode+"'", "joinWidget");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
	
	private void valueAddShowletStatement(String pageCode, 
			int pos, Widget widget, PreparedStatement stat) throws Throwable {
		stat.setString(1, pageCode);
		stat.setInt(2, pos);
		stat.setString(3, widget.getType().getCode());
		if (!widget.getType().isLogic()) {
			String config = null;
			if (null != widget.getConfig()) {
				config = widget.getConfig().toXml();
			}
			stat.setString(4, config);
		} else {
			stat.setNull(4, Types.VARCHAR);
		}
		stat.setString(5, widget.getPublishedContent());
	}

	protected IPageModelManager getPageModelManager() {
		return _pageModelManager;
	}
	public void setPageModelManager(IPageModelManager pageModelManager) {
		this._pageModelManager = pageModelManager;
	}

	public IWidgetTypeManager getWidgetTypeManager() {
		return _widgetTypeManager;
	}
	
	public void setWidgetTypeManager(IWidgetTypeManager widgetTypeManager) {
		this._widgetTypeManager = widgetTypeManager;
	}

	private IPageModelManager _pageModelManager;
	private IWidgetTypeManager _widgetTypeManager;

	// attenzione: l'ordinamento deve rispettare prima l'ordine delle pagine
	// figlie nelle pagine madri, e poi l'ordine delle showlet nella pagina.
	private static final String ALL_PAGES = 
		"SELECT p.parentcode, p.pos, p.code, p.showinmenu, "
		+ "p.modelcode, p.titles, p.groupcode, p.extraconfig, "
		+ "s.framepos, s.widgetcode, s.config, s.publishedcontent "
		+ "FROM pages p LEFT JOIN widgetconfig s ON p.code = s.pagecode "
		+ "ORDER BY p.parentcode, p.pos, p.code, s.framepos";
	
	private static final String ADD_PAGE = 
		"INSERT INTO pages(code, parentcode, showinmenu, pos, modelcode, titles, groupcode, extraconfig) VALUES ( ? , ? , ? , ? , ? , ? , ? , ? )";

	private static final String DELETE_PAGE = 
		"DELETE FROM pages WHERE code = ? ";

	private static final String DELETE_WIDGETS_FOR_PAGE = 
		"DELETE FROM widgetconfig WHERE pagecode = ? ";

	private static final String REMOVE_WIDGET_FROM_FRAME = 
		DELETE_WIDGETS_FOR_PAGE + " AND framepos = ? ";

	private static final String MOVE_UP = 
		"UPDATE pages SET pos = (pos - 1) WHERE code = ? ";

	private static final String MOVE_DOWN = 
		"UPDATE pages SET pos = (pos + 1) WHERE code = ? ";

	private static final String UPDATE_PAGE = 
		"UPDATE pages SET parentcode = ? , showinmenu = ? , modelcode = ? , titles = ? , groupcode = ? , extraconfig = ? WHERE code = ? ";

	private static final String SHIFT_PAGE = 
		"UPDATE pages SET pos = (pos - 1) WHERE parentcode = ? AND pos > ? ";

	private static final String ADD_WIDGET_FOR_PAGE = 
		"INSERT INTO widgetconfig (pagecode, framepos, widgetcode, config, publishedcontent) VALUES ( ?, ?, ?, ?, ?)";

}