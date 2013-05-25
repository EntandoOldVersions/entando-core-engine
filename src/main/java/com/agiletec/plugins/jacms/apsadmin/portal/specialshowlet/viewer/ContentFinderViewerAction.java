/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
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
package com.agiletec.plugins.jacms.apsadmin.portal.specialshowlet.viewer;

import java.util.ArrayList;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.showlettype.IShowletTypeManager;
import com.agiletec.aps.system.services.showlettype.ShowletType;
import com.agiletec.plugins.jacms.apsadmin.content.ContentFinderAction;

/**
 * Classe Action che cerca i contenuti per 
 * la configurazione delle showlet di tipo "Pubblica contenuto singolo".
 * @author E.Santoboni
 */
public class ContentFinderViewerAction extends ContentFinderAction implements IContentFinderViewerAction {
	
	@Override
	public List<String> getContents() {
		List<String> result = null;
		try {
			List<String> allowedGroups = this.getContentGroupCodes();
			result = this.getContentManager().loadPublicContentsId(null, this.getFilters(), allowedGroups);
			/*
			 * Non propriamente corretto; deve estrarre i contenuti che sono visualizzabili (singolarmente) 
			 * da tutti i gruppi a cui appartiene la pagina.
			 */
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getContents");
			throw new RuntimeException("Errore in ricerca contenuti", t);
		}
		return result;
	}
	
	@Override
	public String joinContent() {
		return SUCCESS;
	}
	
	@Override
	protected List<String> getContentGroupCodes() {
		List<String> allowedGroups = new ArrayList<String>();
		allowedGroups.add(Group.FREE_GROUP_NAME);
		IPage currentPage = this.getCurrentPage();
		allowedGroups.add(currentPage.getGroup());
		if (null != currentPage.getExtraGroups()) {
			allowedGroups.addAll(currentPage.getExtraGroups());
		}
    	return allowedGroups;
	}

	/**
	 * Check if the current user can access the specified page.
	 * @param page The page to check against the current user.
	 * @return True if the user has can access the given page, false otherwise.
	 */
	public boolean isUserAllowed(IPage page) {
		if (page == null) return false;
		String pageGroup = page.getGroup();
		return this.isCurrentUserMemberOf(pageGroup);
	}
	
	/**
	 * Returns the 'bread crumbs' targets.
	 * @param pageCode The code of the page being represented in the bread crumbs path.
	 * @return The bread crumbs targets requested.
	 */
	public List<IPage> getBreadCrumbsTargets(String pageCode) {
		IPage page = this.getPageManager().getPage(pageCode);
		if (null == page) return null;
		List<IPage> pages = new ArrayList<IPage>();
		this.getSubBreadCrumbsTargets(pages, page);
		return pages;
	}
	
	private void getSubBreadCrumbsTargets(List<IPage> pages, IPage current) {
		pages.add(0, current);
		IPage parent = current.getParent();
		if (parent != null && !parent.getCode().equals(current.getCode())) {
			this.getSubBreadCrumbsTargets(pages, parent);
		}
	}
	
	public ShowletType getShowletType(String typeCode) {
		return this.getShowletTypeManager().getShowletType(typeCode);
	}
	
	public IPage getCurrentPage() {
		return this.getPageManager().getPage(this.getPageCode());
	}
	
	public String getPageCode() {
		return _pageCode;
	}
	public void setPageCode(String pageCode) {
		this._pageCode = pageCode;
	}
	
	public int getFrame() {
		return _frame;
	}
	public void setFrame(int frame) {
		this._frame = frame;
	}
	
	public String getShowletTypeCode() {
		return _showletTypeCode;
	}
	public void setShowletTypeCode(String showletTypeCode) {
		this._showletTypeCode = showletTypeCode;
	}
	
	public String getContentId() {
		return _contentId;
	}
	public void setContentId(String contentId) {
		this._contentId = contentId;
	}
	
	public String getModelId() {
		return _modelId;
	}
	public void setModelId(String modelId) {
		this._modelId = modelId;
	}

	protected IPageManager getPageManager() {
		return _pageManager;
	}
	public void setPageManager(IPageManager pageManager) {
		this._pageManager = pageManager;
	}
	
	protected IShowletTypeManager getShowletTypeManager() {
		return _showletTypeManager;
	}
	public void setShowletTypeManager(IShowletTypeManager showletTypeManager) {
		this._showletTypeManager = showletTypeManager;
	}
	
	private String _pageCode;
	private int _frame = -1;
	private String _showletTypeCode;
	
	private String _contentId;
	private String _modelId;
	
	private IPageManager _pageManager;
	private IShowletTypeManager _showletTypeManager;
	
}