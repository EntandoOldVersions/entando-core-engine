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
package org.entando.entando.aps.system.services.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.entando.entando.aps.system.services.page.events.PageChangedEvent;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.common.tree.ITreeNode;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.GroupUtilizer;
import com.agiletec.aps.system.services.lang.events.LangsChangedEvent;
import com.agiletec.aps.system.services.lang.events.LangsChangedObserver;
import com.agiletec.aps.system.services.pagemodel.PageModel;

/**
 * This is the page manager service class. Pages are held in a tree-like structure,
 * to allow a hierarchical access, and stored in a map, to allow a key-value type access.
 * In the tree, the father points the son and vice versa; the order between the pages
 * in the same level is always kept.
 * @author M.Diana - E.Santoboni
 */
public class PageManager extends AbstractService implements IPageManager, GroupUtilizer, LangsChangedObserver {

	@Override
	public void init() throws Exception {
		this.loadPageTree();
		ApsSystemUtils.getLogger().config(this.getClass().getName() 
				+ ": Initialized " + _pages.size() + " pages.");
	}

	/**
	 * Load the page and organize them in a tree structure
	 * @throws ApsSystemException In case of database access error.
	 */
	private void loadPageTree() throws ApsSystemException {
		IPage newRoot = null;
		List<IPage> pageList = null;
		try {
			pageList = this.getPageDAO().loadPages();
		} catch (Throwable t) {
			throw new ApsSystemException("Error loading the list of pages", t);
		}
		try {
			Map<String, IPage> newMap = new HashMap<String, IPage>(pageList.size());
			for (int i = 0; i < pageList.size(); i++) {
				IPage page = (IPage) pageList.get(i);
				newMap.put(page.getCode(), page);
				if (page.getCode().equals(page.getParentCode())) {
					newRoot = page;
				}
			}
			for (int i = 0; i < pageList.size(); i++) {
				Page page = (Page) pageList.get(i);
				Page parent = (Page) newMap.get(page.getParentCode());
				page.setParent(parent);
				if (page != newRoot) {
					parent.addChild(page);
				}
			}
			if (newRoot == null) {
				throw new ApsSystemException( "Error in the page tree: root page undefined");
			}
			this._root = newRoot;
			this._pages = newMap;
		} catch (ApsSystemException e) {
			throw e;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "loadPageTree");
			throw new ApsSystemException("Error while building the tree of pages", t);
		}
	}
	
	@Override
	public void updateFromLangsChanged(LangsChangedEvent event) {
		try {
			this.init();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateFromLangsChanged", "Error on init method");
		}
	}
	
	/**
	 * Delete a page and eventually the association with the showlets. 
	 * @param pageCode the code of the page to delete
	 * @throws ApsSystemException In case of database access error.
	 */
	@Override
	public void deletePage(String pageCode) throws ApsSystemException {
		IPage page =  this.getPage(pageCode);
		if (null != page && page.getChildren().length <= 0) {
			try {
				this.getPageDAO().deletePage(page);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "deletePage");
				throw new ApsSystemException("Error detected while deleting a page", t);
			}
		}
		this.loadPageTree();
		this.notifyPageChangedEvent(page, PageChangedEvent.REMOVE_OPERATION_CODE, null);
	}

	/**
	 * Add a new page to the database.
	 * @param page The page to add
	 * @throws ApsSystemException In case of database access error.
	 */
	@Override
	public void addPage(IPage page) throws ApsSystemException {
		try {
			this.getPageDAO().addPage(page);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "addPage");
			throw new ApsSystemException("Error adding a page", t);
		}
		this.loadPageTree();
		this.notifyPageChangedEvent(this.getPage(page.getCode()), PageChangedEvent.INSERT_OPERATION_CODE, null);
	}

	/**
	 * Update a page record in the database.
	 * @param page The modified page.
	 * @throws ApsSystemException In case of database access error.
	 */
	@Override
	public void updatePage(IPage page) throws ApsSystemException {
		try {
			this.getPageDAO().updatePage(page);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updatePage");
			throw new ApsSystemException("Error updating a page", t);
		}
		this.loadPageTree();
		this.notifyPageChangedEvent(page, PageChangedEvent.UPDATE_OPERATION_CODE, null);
	}
	
	private void notifyPageChangedEvent(IPage page, int operationCode, Integer framePos) {
		PageChangedEvent event = new PageChangedEvent();
		event.setPage(page);
		event.setOperationCode(operationCode);
		if (null != framePos) {
			event.setFramePosition(framePos);
		}
		this.notifyEvent(event);
	}

	/**
	 * Move a page.
	 * @param pageCode The code of the page to move.
	 * @param moveUp When true the page is moved to a higher level of the tree, otherwise to a lower level.
	 * @return The result of the operation: false if the move request could not be satisfied, true otherwise. 
	 * @throws ApsSystemException In case of database access error.
	 */
	@Override
	public boolean movePage(String pageCode, boolean moveUp) throws ApsSystemException {
		boolean resultOperation = true; 
		try {
			IPage currentPage = this.getPage(pageCode);
			if (null == currentPage) {
				throw new ApsSystemException("The page '" + pageCode + "' does not exist!");
			}
			IPage parent = currentPage.getParent();
			IPage[] sisterPages = parent.getChildren();
			for (int i=0; i < sisterPages.length; i++) {
				IPage sisterPage = sisterPages[i];
				if (sisterPage.getCode().equals(pageCode)) {
					if (!verifyRequiredMovement(i, moveUp, sisterPages.length)) {
						return false;
					} else {
						if (moveUp) {
							IPage pageDown = sisterPages[i - 1];
							this.moveUpDown(pageDown, currentPage);
						} else {
							IPage pageUp = sisterPages[i + 1];
							this.moveUpDown(currentPage, pageUp);
						}
					}
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "movePage");
			throw new ApsSystemException("Error while moving a page", t);
		}
		this.loadPageTree();
		return resultOperation;
	}

	/**
	 * Verify the possibility of the page to be moved elsewhere.
	 * @param position The position of the page to move
	 * @param moveUp When true the page is moved to a higher level of the tree, otherwise to a lower level.
	 * @param dimension The number the number of the pages of the parent of the page to move.
	 * @return if true then the requested movement is possible (but not performed) false otherwise.
	 */
	private boolean verifyRequiredMovement(int position, boolean moveUp, int dimension) {
		boolean result = true;
		if (moveUp) {
			if (position == 0) {
				result = false;
			}
		} else {
			if (position == (dimension-1)) {
				result = false;
			}
		}
		return result;
	}

	/**
	 * Perform the movement of a page 
	 * @param pageDown
	 * @param pageUp
	 * @throws ApsSystemException In case of database access error.
	 */
	private void moveUpDown(IPage pageDown, IPage pageUp) throws ApsSystemException {
		try {
			this.getPageDAO().updatePosition(pageDown, pageUp);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "moveUpDown");
			throw new ApsSystemException("Error while moving a page", t);
		}
	}

	/**
	 * Remove a showlet from the given page.
	 * @param pageCode the code of the showlet to remove from the page
	 * @param pos The position in the page to free
	 * @throws ApsSystemException In case of error
	 */
	@Override
	public void removeShowlet(String pageCode, int pos) throws ApsSystemException {
		this.checkPagePos(pageCode, pos);
		try {
			this.getPageDAO().removeShowlet(pageCode, pos);
			IPage currentPage = this.getPage(pageCode);
			currentPage.getShowlets()[pos] = null;
			this.notifyPageChangedEvent(currentPage, PageChangedEvent.EDIT_FRAME_OPERATION_CODE, pos);
		} catch (Throwable t) {
			String message = "Error removing the showlet from the page '" + pageCode + "' in the frame "+ pos;
			ApsSystemUtils.logThrowable(t, this, "removeShowlet", message);
			throw new ApsSystemException(message, t);
		}
	}

	/**
	 * Set the showlet -including its configuration- in the given page in the desidered position.
	 * If the position is already occupied by another showlet this will be substituted with the
	 * new one.
	 * @param pageCode the code of the page where to set the showlet
	 * @param widget The showlet to set
	 * @param pos The position where to place the showlet in
	 * @throws ApsSystemException In case of error.
	 */
	@Override
	public void joinShowlet(String pageCode, Widget widget, int pos) throws ApsSystemException {
		this.checkPagePos(pageCode, pos);
		if (null == widget || null == widget.getType()) {
			throw new ApsSystemException("Invalid null value found in either the Widget or the showletType");
		}
		try {
			this.getPageDAO().joinShowlet(pageCode, widget, pos);
			IPage currentPage = this.getPage(pageCode);
			currentPage.getShowlets()[pos] = widget;
			this.notifyPageChangedEvent(currentPage, PageChangedEvent.EDIT_FRAME_OPERATION_CODE, pos);
		} catch (Throwable t) {
			String message = "Error during the assignation of a showlet to the frame " + pos +" in the page code "+pageCode;
			ApsSystemUtils.logThrowable(t, this, "joinShowlet", message);
			throw new ApsSystemException(message, t);
		}
	}

	/**
	 * Utility method which perform checks on the parameters submitted when editing the page.
	 * @param pageCode The code of the page
	 * @param pos The given position
	 * @throws ApsSystemException In case of database access error.
	 */
	private void checkPagePos(String pageCode, int pos) throws ApsSystemException {
		IPage currentPage = this.getPage(pageCode);
		if (null == currentPage) {
			throw new ApsSystemException("The page '" + pageCode + "' does not exist!");
		}
		PageModel model = currentPage.getModel();
		if (pos < 0 || pos >= model.getFrames().length) {
			throw new ApsSystemException("The Position '" + pos + "' is not defined in the model '" + 
					model.getDescr() + "' of the page '" + pageCode + "'!");
		}
	}

	/**
	 * Set the root page.
	 * @param root the Page to be set as root
	 */
	protected void setRoot(IPage root) {
		this._root = root;
	}

	/**
	 * Return the root of the pages tree.
	 * @return the root page
	 */
	@Override
	public IPage getRoot() {
		return _root;
	}

	/**
	 * Return the page given the name
	 * @param pageCode The code of the page.
	 * @return the requested page.
	 */
	@Override
	public IPage getPage(String pageCode) {
		return this._pages.get(pageCode);
	}

	/**
	 * Search pages by a token of its code.
	 * @param pageCodeToken The token containing to be looked up across the pages.
	 * @param allowedGroups The codes of allowed page groups.
	 * @return A list of candidates containing the given token. If the pageCodeToken is null then
	 * this method will return a set containing all the pages.
	 * @throws ApsSystemException in case of error.
	 */
	@Override
	public List<IPage> searchPages(String pageCodeToken, List<String> allowedGroups) throws ApsSystemException {
		List<IPage> searchResult = new ArrayList<IPage>();
		try {
			if (null == this._pages || this._pages.isEmpty() 
					|| null == allowedGroups || allowedGroups.isEmpty()) {
				return searchResult; 
			}
			IPage root = this.getRoot();
			this.searchPages(root, pageCodeToken, allowedGroups, searchResult);
		} catch (Throwable t) {
			String message = "Error during searching pages with token " + pageCodeToken;
			ApsSystemUtils.logThrowable(t, this, "searchPages", message);
			throw new ApsSystemException(message, t);
		}
		return searchResult;
	}

	private void searchPages(IPage currentTarget, String pageCodeToken, List<String> allowedGroups, List<IPage> searchResult) {
		if ((null == pageCodeToken || currentTarget.getCode().toLowerCase().contains(pageCodeToken.toLowerCase())) 
				&& (allowedGroups.contains(currentTarget.getGroup()) || allowedGroups.contains(Group.ADMINS_GROUP_NAME))) {
			searchResult.add(currentTarget);
		}
		IPage[] children = currentTarget.getChildren();
		for (int i = 0; i < children.length; i++) {
			this.searchPages(children[i], pageCodeToken, allowedGroups, searchResult);
		}
	}

	@Override
	public ITreeNode getNode(String code) {
		return this.getPage(code);
	}

	@Override
	public List getGroupUtilizers(String groupName) throws ApsSystemException {
		List<IPage> utilizers = new ArrayList<IPage>();
		try {
			IPage root = this.getRoot();
			this.searchUtilizers(groupName, utilizers, root);
		} catch (Throwable t) {
			String message = "Error during searching page utilizers of group " + groupName;
			ApsSystemUtils.logThrowable(t, this, "getGroupUtilizers", message);
			throw new ApsSystemException(message, t);
		}
		return utilizers;
	}
	
	private void searchUtilizers(String groupName, List<IPage> utilizers, IPage page) {
		if (page.getGroup().equals(groupName)) {
			utilizers.add(page);
		} else {
			Collection<String> extraGroups = page.getExtraGroups();
			if (null != extraGroups && !extraGroups.isEmpty()) {
				Iterator<String> extraGroupIterator = extraGroups.iterator();
				while (extraGroupIterator.hasNext()) {
					String extraGroup = extraGroupIterator.next();
					if (extraGroup.equals(groupName)) {
						utilizers.add(page);
					}
				}
			}
		}
		IPage[] children = page.getChildren();
		for (int i=0; i<children.length; i++) {
			this.searchUtilizers(groupName, utilizers, children[i]);
		}
	}
	
	@Override
	public List<IPage> getShowletUtilizers(String showletTypeCode) throws ApsSystemException {
		List<IPage> pages = new ArrayList<IPage>();
		try {
			if (null == this._pages || this._pages.isEmpty() || null == showletTypeCode) {
				return pages; 
			}
			IPage root = this.getRoot();
			this.getShowletUtilizers(root, showletTypeCode, pages);
		} catch (Throwable t) {
			String message = "Error during searching page utilizers of showlet with code " + showletTypeCode;
			ApsSystemUtils.logThrowable(t, this, "getShowletUtilizers", message);
			throw new ApsSystemException(message, t);
		}
		return pages;
	}
	
	private void getShowletUtilizers(IPage page, String showletTypeCode, List<IPage> showletUtilizers) {
		Widget[] showlets = page.getShowlets();
		for (int i = 0; i < showlets.length; i++) {
			Widget widget = showlets[i];
			if (null != widget && null != widget.getType() && showletTypeCode.equals(widget.getType().getCode())) {
				showletUtilizers.add(page);
				break;
			}
		}
		IPage[] children = page.getChildren();
		for (int i = 0; i < children.length; i++) {
			IPage child = children[i];
			this.getShowletUtilizers(child, showletTypeCode, showletUtilizers);
		}
	}

	protected IPageDAO getPageDAO() {
		return _pageDao;
	}
	public void setPageDAO(IPageDAO pageDao) {
		this._pageDao = pageDao;
	}

	/**
	 * The root of the pages tree.
	 */
	private IPage _root;

	/**
	 * The map of pages, indexed by code.
	 */
	private Map<String, IPage> _pages;

	private IPageDAO _pageDao;

}