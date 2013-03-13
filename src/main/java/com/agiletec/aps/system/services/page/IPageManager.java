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
package com.agiletec.aps.system.services.page;

import java.util.List;

import com.agiletec.aps.system.common.tree.ITreeNodeManager;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * Basic interface for the page manager services.
 * @author M.Diana
 */
public interface IPageManager extends ITreeNodeManager {

	/**
	 * Delete a page and eventually the association with the showlets. 
	 * @param pageCode the code of the page to delete
	 * @throws ApsSystemException In case of database access error.
	 */
	public void deletePage(String pageCode) throws ApsSystemException;

	/**
	 * Add a new page to the database.
	 * @param page The page to add
	 * @throws ApsSystemException In case of database access error.
	 */
	public void addPage(IPage page) throws ApsSystemException;

	/**
	 * Update a page record in the database.
	 * @param page The modified page.
	 * @throws ApsSystemException In case of database access error.
	 */
	public void updatePage(IPage page) throws ApsSystemException;

	/**
	 * Move a page.
	 * @param pageCode The code of the page to move.
	 * @param moveUp When true the page is moved to a higher level of the tree, otherwise to a lower level.
	 * @return The result of the operation: false if the move request could not be satisfied, true otherwise. 
	 * @throws ApsSystemException In case of database access error.
	 */
	public boolean movePage(String pageCode, boolean moveUp) throws ApsSystemException;

	/**
	 * Set the showlet -including its configuration- in the given page in the desidered position.
	 * If the position is already occupied by another showlet this will be substituted with the
	 * new one.
	 * @param pageCode the code of the page where to set the showlet
	 * @param showlet The showlet to set
	 * @param pos The position where to place the showlet in
	 * @throws ApsSystemException In case of error.
	 */
	public void joinShowlet(String pageCode, Showlet showlet, int pos) throws ApsSystemException;

	/**
	 * Remove a showlet from the given page.
	 * @param pageCode the code of the showlet to remove from the page
	 * @param pos The position in the page to free
	 * @throws ApsSystemException In case of error
	 */
	public void removeShowlet(String pageCode, int pos) throws ApsSystemException;

	/**
	 * Return the root of the pages tree.
	 * @return the root page.
	 */
	public IPage getRoot();

	/**
	 * Return a page given the name.
	 * @param pageCode The code of the page
	 * @return the requested page.
	 */
	public IPage getPage(String pageCode);

	/**
	 * Search pages by a token of its code.
	 * @param pageCodeToken The token containing to be looked up across the pages.
	 * @param allowedGroups The codes of allowed page groups.
	 * @return A list of candidates containing the given token. If the pageCodeToken is null then
	 * this method will return the full list of pages.
	 * @throws ApsSystemException in case of error.
	 */
	public List<IPage> searchPages(String pageCodeToken, List<String> allowedGroups) throws ApsSystemException;
	
	public List<IPage> getShowletUtilizers(String showletTypeCode) throws ApsSystemException;
	
}