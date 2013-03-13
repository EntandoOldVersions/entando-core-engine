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
package com.agiletec.apsadmin.portal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.tree.ITreeNode;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.apsadmin.portal.helper.IPageActionHelper;
import com.agiletec.apsadmin.system.AbstractTreeAction;

/**
 * Action principale per la gestione dell'albero delle pagine.
 * @author E.Santoboni
 */
public class PageTreeAction extends AbstractTreeAction implements IPageTreeAction {
	
	@Override
	public String execute() throws Exception {
		if (null != this.getSelectedNode()) {
			super.getTreeNodesToOpen().add(this.getSelectedNode());
		}
		return super.execute();
	}
	
	@Override
	public String moveUp() {
		return this.movePage(true);
	}
	
	@Override
	public String moveDown() {
		return this.movePage(false);
	}
	
	protected String movePage(boolean moveUp) {
		String selectedNode = this.getSelectedNode();
		try {
			String check = this.checkSelectedNode(selectedNode);
			if (null != check) return check;
			IPage currentPage = this.getPageManager().getPage(selectedNode);
			if (!isUserAllowed(currentPage.getParent())) {
				this.addActionError(this.getText("error.page.userNotAllowed"));
				return SUCCESS;
			}
			if (this.getPageManager().getRoot().getCode().equals(currentPage.getCode())) {
				this.addActionError(this.getText("error.page.movementHome.notAllowed"));
				return SUCCESS;
			}
			boolean result = this.getPageManager().movePage(selectedNode, moveUp);
			if (!result) {
				if (moveUp) {
					this.addActionError(this.getText("error.page.movementUp.notAllowed"));
				} else {
					this.addActionError(this.getText("error.page.movementDown.notAllowed"));
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "movePage");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String copy() {
		String selectedNode = this.getSelectedNode();
		String check = this.checkSelectedNode(selectedNode);
		if (null != check) return check;
		//FIXME RICORDARSI DELL'AREA DI NOTIFICA QUANDO PASSEREMO ALL'ULTIMO LAYER... se è possibile mettere un'indicazione della pagina copiata 
		this.setCopyingPageCode(selectedNode);
		return SUCCESS;
	}
	
	protected String checkSelectedNode(String selectedNode) {
		if (null == selectedNode || selectedNode.trim().length() == 0) {
			this.addActionError(this.getText("error.page.noSelection"));
			return "pageTree";
		}
		if (AbstractPortalAction.VIRTUAL_ROOT_CODE.equals(selectedNode)) {
			this.addActionError(this.getText("error.page.virtualRootSelected"));
			return "pageTree";
		}
		IPage selectedPage = this.getPageManager().getPage(selectedNode);
		if (null == selectedPage) {
			this.addActionError(this.getText("error.page.selectedPage.null"));
			return "pageTree";
		}
		if (!this.isUserAllowed(selectedPage)) {
			this.addActionError(this.getText("error.page.userNotAllowed"));
			return "pageTree";
		}
		return null;
	}
	
	/**
	 * Check if the current user can access the specified page.
	 * @param page The page to check against the current user.
	 * @return True if the user has can access the given page, false otherwise.
	 */
	protected boolean isUserAllowed(IPage page) {
		if (page == null) return false;
		String pageGroup = page.getGroup();
		return this.isCurrentUserMemberOf(pageGroup);
	}
	
	@Override
	protected Collection<String> getNodeGroupCodes() {
		List<String> allowedGroupCodes = new ArrayList<String>();
		List<Group> allowedGroups = this.getTreeHelper().getAllowedGroups(this.getCurrentUser());
		if (null != allowedGroups) {
			for (int i = 0; i < allowedGroups.size(); i++) {
				allowedGroupCodes.add(allowedGroups.get(i).getName());
			}
		}
		return allowedGroupCodes;
	}
	
	@Override
	@Deprecated
	public IPage getRoot() {
		return this.getPageManager().getRoot();
	}
	
	@Override
	@Deprecated
	public ITreeNode getTreeRootNode() {
		return this.getAllowedTreeRootNode();
	}
	
	public String getSelectedNode() {
		return _selectedNode;
	}
	public void setSelectedNode(String selectedNode) {
		this._selectedNode = selectedNode;
	}
	
	public String getCopyingPageCode() {
		return _copyingPageCode;
	}
	public void setCopyingPageCode(String copyingPageCode) {
		this._copyingPageCode = copyingPageCode;
	}
	
	@Override
	protected IPageActionHelper getTreeHelper() {
		return (IPageActionHelper) super.getTreeHelper();
	}
	
	protected IPageManager getPageManager() {
		return _pageManager;
	}
	public void setPageManager(IPageManager pageManager) {
		this._pageManager = pageManager;
	}
	
	private String _selectedNode;
	
	private String _copyingPageCode;
	
	private IPageManager _pageManager;
	
}