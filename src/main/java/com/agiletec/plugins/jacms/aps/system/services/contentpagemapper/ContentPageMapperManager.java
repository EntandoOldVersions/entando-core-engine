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
package com.agiletec.plugins.jacms.aps.system.services.contentpagemapper;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.system.services.page.events.PageChangedEvent;
import com.agiletec.aps.system.services.page.events.PageChangedObserver;
import com.agiletec.aps.system.services.pagemodel.PageModel;

/**
 * Servizio gestore della mappa dei contenuti pubblicati nelle pagine. 
 * Il servizio carica e gestisce nella mappa esclusivamente i contenuti 
 * pubblicati esplicitamente nel frame principale delle pagine.
 * @author W.Ambu
 */
public class ContentPageMapperManager extends AbstractService 
		implements IContentPageMapperManager, PageChangedObserver {
	
	@Override
	public void init() throws Exception {
		this.createContentPageMapper();
		ApsSystemUtils.getLogger().config(this.getClass().getName() 
				+ ": inizializzato Mapper Contenuti pubblicati / pagine " );
	}
	
    /**
     * Effettua il caricamento della mappa contenuti pubblicati / pagine
     * @throws ApsSystemException
     */
	@Override
	public void reloadContentPageMapper() throws ApsSystemException {
    	this.createContentPageMapper();
    }
	
	@Override
    public String getPageCode(String contentId) {
		return this.getContentPageMapper().getPageCode(contentId);
	}
    
    /**
     * Crea la mappa dei contenuti pubblicati nelle pagine.
     * @throws ApsSystemException
     */
    private void createContentPageMapper() throws ApsSystemException {
    	this._contentPageMapper = new ContentPageMapper();
    	try {
            IPage root = this.getPageManager().getRoot();
            this.searchPublishedContents(root);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "createContentPageMapper");
            throw new ApsSystemException("Errore in fase caricamento mappa contenuti pubblicati / pagine", t);
        }
    }
    
    /**
     * Cerca i contenuti pubblicati e li aggiunge al mapper. 
     * Nella ricerca vengono considerati solamente i contenuti pubblicati nel mainFrame 
     * e la ricerca viene estesa anche alle pagine figlie di quella specificate. 
     * @param page La pagina nel qual cercare i contenuti pubblicati.
     */
    private void searchPublishedContents(IPage page) {
    	PageModel pageModel = page.getModel();
        int mainFrame = pageModel.getMainFrame();
        Showlet[] showlets = page.getShowlets();
        Showlet showlet = null;
        if (null != showlets && mainFrame != -1){
            showlet = showlets[mainFrame];
        }
        String contentId = null;
        if (null != showlet) {
            contentId = showlet.getPublishedContent();
        }
        if (null != contentId) {
        	this.getContentPageMapper().add(contentId, page.getCode());
        }
        IPage[] children = page.getChildren();
        for (int i=0; i<children.length; i++) {
        	this.searchPublishedContents(children[i]);
        }
    }
    
    @Override
	public void updateFromPageChanged(PageChangedEvent event) {
		try {
			this.reloadContentPageMapper();
			Logger log = ApsSystemUtils.getLogger();
			if (log.isLoggable(Level.FINEST)) {
				log.info("Notificato modifica pagina " + event.getPage().getCode());
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateFromPageChanged", "Errore in notificazione Evento");
		}
	}
    
    /**
     * Restituisce la mappa dei contenuti pubblicati nelle pagine.
     * @return La mappa dei contenuti pubblicati nelle pagine.
     */
    protected ContentPageMapper getContentPageMapper() {
        return _contentPageMapper;
    }
	
	protected IPageManager getPageManager() {
		return _pageManager;
	}
	public void setPageManager(IPageManager pageManager) {
		this._pageManager = pageManager;
	}
	
	private IPageManager _pageManager;
    
    /**
     * La mappa dei contenuti pubblicati nelle pagine.
     */
    private ContentPageMapper _contentPageMapper;
    
}