/*
 *
 * Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
 *
 * This file is part of jAPS software.
 * jAPS is a free software; 
 * you can redistribute it and/or modify it
 * under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
 * 
 * See the file License for the specific language governing permissions   
 * and limitations under the License
 * 
 * 
 * 
 * Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
 *
 */
package com.agiletec.aps.system.services.page;

import java.util.List;

import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * Basic interface for the Data Acces Objects for the 'Page' objects
 * @author M.Diana - E.Santoboni
 */
public interface IPageDAO {

	/**
	 * Load a sorted list of the pages and the configuration of the showlets 
	 * @return the list of pages
	 */
	public List<IPage> loadPages();

	/**
	 * Insert a new page.
	 * @param page The new page to insert.
	 */
	public void addPage(IPage page);

	/**
	 * Delete the page identified by the given code.
	 * @param page The page to delete.
	 */
	public void deletePage(IPage page);

	/**
	 * Updates a page record in the database.
	 * @param page The page to update
	 */
	public void updatePage(IPage page);

	/**
	 * Updates the position for the page movement
	 * @param pageDown The page to move downwards
	 * @param pageUp The page to move upwards
	 */
	public void updatePosition(IPage pageDown, IPage pageUp);

	/**
	 * Setta la showlet (comprensiva della sua configurazione) nella pagina e nel frame specificato.
	 * Nel caso che la posizione specificata sia già occupata, la showlet corrente 
	 * sarà sostituita da quella specificata. 
	 * @param pageCode Il codice della pagina in cui settare la showlet.
	 * @param showlet La showlet da settare.
	 * @param pos La posizione della pagina su cui settare la showlet.
	 * @throws ApsSystemException In caso di errore.
	 */
	public void joinShowlet(String pageCode, Showlet showlet, int pos);

	/**
	 * Rimuove una showlet nella pagina specificata.
	 * @param pageCode Il codice della pagina nel quale rimuovere la showlet.
	 * @param pos La posizione dal liberare.
	 * @throws ApsSystemException In caso di errore.
	 */
	public void removeShowlet(String pageCode, int pos);

}