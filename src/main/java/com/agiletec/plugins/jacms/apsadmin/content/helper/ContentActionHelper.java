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
package com.agiletec.plugins.jacms.apsadmin.content.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.entando.entando.aps.system.services.page.IPage;
import org.entando.entando.aps.system.services.page.IPageManager;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.entity.EntityActionHelper;
import com.agiletec.plugins.jacms.aps.system.services.content.ContentUtilizer;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.helper.IContentAuthorizationHelper;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.apsadmin.util.CmsPageActionUtil;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Classe Helper della ContentAction.
 * @author E.Santoboni
 */
public class ContentActionHelper extends EntityActionHelper implements IContentActionHelper {
	
	@Override
	public List<Group> getAllowedGroups(UserDetails currentUser) {
		return super.getAllowedGroups(currentUser);
    }
	
	@Override
	public void updateEntity(IApsEntity entity, HttpServletRequest request) {
		Content content = (Content) entity;
		try {
            if (null != content) {
            	String descr = request.getParameter("descr");
            	if (descr != null) content.setDescr(descr.trim());
            	String status = request.getParameter("status");
            	if (status != null) content.setStatus(status);
	            if (null == content.getId()) {
	            	String mainGroup = request.getParameter("mainGroup");
	            	if (mainGroup != null) content.setMainGroup(mainGroup);
	            }
	            super.updateEntity(content, request);
            }
        } catch (Throwable t) {
        	ApsSystemUtils.getLogger().throwing("ContentActionHelper", "updateContent", t);
        	throw new RuntimeException("Error updating Content", t);
        }
	}
	
	@Override
	public void scanEntity(IApsEntity entity, ActionSupport action) {
		Content content = (Content) entity;
		if (null == content) {
    		ApsSystemUtils.getLogger().severe("Null Content");
    		return;
    	}
		String descr = content.getDescr();
    	if (descr == null || descr.length() == 0) {
			action.addFieldError("descr", action.getText("error.content.descr.required"));
		} else {
			int maxLength = 250;
			if (descr.length() > maxLength) {
				String[] args = {String.valueOf(maxLength)};
				action.addFieldError("descr", action.getText("error.content.descr.wrongMaxLength", args));
			}
			if (!descr.matches("([^\"])+")) {
				action.addFieldError("descr", action.getText("error.content.descr.wrongCharacters"));
			}
		}
		if (null == content.getId() && (content.getMainGroup() == null || content.getMainGroup().length() == 0)) {
			action.addFieldError("mainGroup", action.getText("error.content.mainGroup.required"));
		}
		try {
			this.scanReferences(content, action);
			super.scanEntity(content, action);
		} catch (Throwable t) {
			throw new RuntimeException("Error checking entity", t);
		}
	}
    
	@Override
	public EntitySearchFilter getOrderFilter(String groupBy, String order) {
		String key = null;
		if (null == groupBy || groupBy.trim().length()== 0 || groupBy.equals("lastModified")) {
			key = IContentManager.CONTENT_MODIFY_DATE_FILTER_KEY;
		} else if (groupBy.equals("code")) {
			key = IContentManager.ENTITY_ID_FILTER_KEY;
		} else if (groupBy.equals("descr")) {
			key = IContentManager.CONTENT_DESCR_FILTER_KEY;
		} else if (groupBy.equals("created")) {
			key = IContentManager.CONTENT_CREATION_DATE_FILTER_KEY;
		} else throw new RuntimeException("Invalid Filter '" + groupBy + "'");
		EntitySearchFilter filter = new EntitySearchFilter(key, false);
		if (null == order || order.trim().length() == 0) {
			filter.setOrder(EntitySearchFilter.DESC_ORDER);
		} else {
			filter.setOrder(order);
		}
		return filter;
	}
	
	/**
     * Verifica che l'utente corrente possegga 
     * i diritti di accesso al contenuto selezionato.
     * @param content Il contenuto.
     * @param currentUser Il contenuto corrente.
     * @return True nel caso che l'utente corrente abbia i permessi 
     * di lettura/scrittura sul contenuto, false in caso contrario.
     */
	@Override
	public boolean isUserAllowed(Content content, UserDetails currentUser) {
		try {
			return this.getContentAuthorizationHelper().isAuthToEdit(currentUser, content);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "isUserAllowed");
			throw new RuntimeException("Error checking user authority", t);
		}
	}
	
	@Override
	public Map getReferencingObjects(Content content, HttpServletRequest request) throws ApsSystemException {
    	Map<String, List> references = new HashMap<String, List>();
    	try {
    		String[] defNames = ApsWebApplicationUtils.getWebApplicationContext(request).getBeanNamesForType(ContentUtilizer.class);
			for (int i=0; i<defNames.length; i++) {
				Object service = null;
				try {
					service = ApsWebApplicationUtils.getWebApplicationContext(request).getBean(defNames[i]);
				} catch (Throwable t) {
					ApsSystemUtils.logThrowable(t, this, "hasReferencingObject");
					service = null;
				}
				if (service != null) {
					ContentUtilizer contentUtilizer = (ContentUtilizer) service;
					List utilizers = contentUtilizer.getContentUtilizers(content.getId());
					if (utilizers != null && !utilizers.isEmpty()) {
						references.put(contentUtilizer.getName()+"Utilizers", utilizers);
					}
				}
			}
    	} catch (Throwable t) {
    		throw new ApsSystemException("Error in hasReferencingObject method", t);
    	}
    	return references;
    }
	
	/**
	 * Controlla le referenziazioni di un contenuto. Verifica la referenziazione di un contenuto con altri contenuti o pagine nel caso 
	 * di operazioni di ripubblicazione di contenuti non del gruppo ad accesso libero.
	 * L'operazione si rende necessaria per ovviare a casi nel cui il contenuto, di un particolare gruppo, sia stato
	 * pubblicato precedentemente in una pagina o referenziato in un'altro contenuto grazie alla associazione di questo con 
	 * altri gruppi abilitati alla visualizzazione. Il controllo evidenzia quali devono essere i gruppi al quale il contenuto 
	 * deve essere necessariamente associato (ed il perchè) per salvaguardare le precedenti relazioni. 
	 * @param content Il contenuto da analizzare.
	 * @param action L'action da valorizzare con i messaggi di errore.
	 * @return I messaggi di errore relativi alle eventuali referenziazioni errate.
	 * @throws ApsSystemException In caso di errore.
	 */
	@Override
	public void scanReferences(Content content, ActionSupport action) throws ApsSystemException {
		if (!Group.FREE_GROUP_NAME.equals(content.getMainGroup()) && !content.getGroups().contains(Group.FREE_GROUP_NAME)) {
			List referencingContents = ((ContentUtilizer) this.getContentManager()).getContentUtilizers(content.getId());
			if (referencingContents!= null && !referencingContents.isEmpty()) {
				for (int i=0; i<referencingContents.size(); i++) {
					String contentId = (String) referencingContents.get(i);
					Content refContent = this.getContentManager().loadContent(contentId, true);
					if (!content.getMainGroup().equals(refContent.getMainGroup()) && 
							!content.getGroups().contains(refContent.getMainGroup())) {
						String[] args = {this.getGroupManager().getGroup(refContent.getMainGroup()).getDescr(), contentId+" '"+refContent.getDescr()+"'"};
						action.addFieldError("mainGroup", action.getText("error.content.referencedContent.wrongGroups", args));
					}
				}
			}
			List referencingPages = ((ContentUtilizer) this.getPageManager()).getContentUtilizers(content.getId());
			if (referencingPages!= null && !referencingPages.isEmpty()) {
				Lang lang = this.getLangManager().getDefaultLang();
				for (int i=0; i<referencingPages.size(); i++) {
					IPage page = (IPage) referencingPages.get(i);
					if (!CmsPageActionUtil.isContentPublishableOnPage(content, page)) {
						List<String> pageGroups = new ArrayList<String>();
						pageGroups.add(page.getGroup());
						if (null != page.getExtraGroups()) {
							pageGroups.addAll(page.getExtraGroups());
						}
						String[] args = {pageGroups.toString(), page.getTitle(lang.getCode())};
						action.addFieldError("mainGroup", action.getText("error.content.referencedPage.wrongGroups", args));
					}
				}
			}
		}
	}
	
	protected IContentManager getContentManager() {
		return _contentManager;
	}
	public void setContentManager(IContentManager contentManager) {
		this._contentManager = contentManager;
	}
	
	protected IPageManager getPageManager() {
		return _pageManager;
	}
	public void setPageManager(IPageManager pageManager) {
		this._pageManager = pageManager;
	}
	
	protected IContentAuthorizationHelper getContentAuthorizationHelper() {
		return _contentAuthorizationHelper;
	}
	public void setContentAuthorizationHelper(IContentAuthorizationHelper contentAuthorizationHelper) {
		this._contentAuthorizationHelper = contentAuthorizationHelper;
	}
	
	private IContentManager _contentManager;
	private IPageManager _pageManager;
	
	private IContentAuthorizationHelper _contentAuthorizationHelper;
	
}