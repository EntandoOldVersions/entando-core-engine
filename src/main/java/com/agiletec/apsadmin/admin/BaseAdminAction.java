/*
*
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
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
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.apsadmin.admin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.web.context.WebApplicationContext;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.event.ReloadingEntitiesReferencesEvent;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * This base action implements the default actions available for the system administration. 
 * @author E.Santoboni
 */
public class BaseAdminAction extends BaseAction implements IBaseAdminAction {
	
	@Override
	public String reloadConfig() {
		try {
			ApsWebApplicationUtils.executeSystemRefresh(this.getRequest());
			ApsSystemUtils.getLogger().info("Ricaricamento configurazione effettuata");
			this.setReloadingResult(SUCCESS_RELOADING_RESULT_CODE);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "reloadConfig");
			this.setReloadingResult(FAILURE_RELOADING_RESULT_CODE);
		}
		return SUCCESS;
	}
	
	@Override
	public String reloadEntitiesReferences() {
		try {
			ReloadingEntitiesReferencesEvent event = new ReloadingEntitiesReferencesEvent();
			WebApplicationContext wac = ApsWebApplicationUtils.getWebApplicationContext(this.getRequest());
			wac.publishEvent(event);
			ApsSystemUtils.getLogger().info("Ricaricamento referenze entità avviata");
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "reloadEntitiesReferences");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public int getReloadingResult() {
		return _reloadingResult;
	}
	public void setReloadingResult(int reloadingResult) {
		this._reloadingResult = reloadingResult;
	}
	
	@Override
	public String configSystemParams() {
		try {
			this.initLocalMap();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "configSystemParams");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String updateSystemParams() {
		try {
			this.initLocalMap();
			this.updateLocalParams();
			String xmlParams = this.getConfigManager().getConfigItem(SystemConstants.CONFIG_ITEM_PARAMS);
			String newXmlParams = SystemParamsUtils.getNewXmlParams(xmlParams, this.getSystemParams());
			this.getConfigManager().updateConfigItem(SystemConstants.CONFIG_ITEM_PARAMS, newXmlParams);
			this.addActionMessage(this.getText("message.configSystemParams.ok"));
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateSystemParams");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	private void initLocalMap() throws Throwable {
		String xmlParams = this.getConfigManager().getConfigItem(SystemConstants.CONFIG_ITEM_PARAMS);
		Map<String, String> systemParams = SystemParamsUtils.getParams(xmlParams);
		this.setSystemParams(systemParams);
	}
	
	/**
	 * Refresh the map of parameters with values fetched from the request
	 */
	private void updateLocalParams() {
		Iterator<String> paramNames = this.getSystemParams().keySet().iterator();
		while (paramNames.hasNext()) {
			String paramName = (String) paramNames.next();
			String newValue = this.getRequest().getParameter(paramName);
			if (null != newValue) {
				this.getSystemParams().put(paramName, newValue);
			} else {
				this.getSystemParams().put(paramName, "false");
			}
		}
	}
	
	/**
	 * Return a plain list of the free pages in the portal.
	 * @return the list of the free pages of the portal.
	 */
	public List<IPage> getFreePages() {
		IPage root = this.getPageManager().getRoot();
		List<IPage> pages = new ArrayList<IPage>();
		this.addPages(root, pages);
		return pages;
	}
	
	private void addPages(IPage page, List<IPage> pages) {
		if (page.getGroup().equals(Group.FREE_GROUP_NAME)) {
			pages.add(page);
		}
		IPage[] children = page.getChildren();
		for (int i=0; i<children.length; i++) {
			this.addPages(children[i], pages);
		}
	}
	
	protected ConfigInterface getConfigManager() {
		return _configManager;
	}
	public void setConfigManager(ConfigInterface configManager) {
		this._configManager = configManager;
	}
	
	protected IPageManager getPageManager() {
		return _pageManager;
	}
	public void setPageManager(IPageManager pageManager) {
		this._pageManager = pageManager;
	}
	
	@Override
	public Map<String, String> getSystemParams() {
		return _systemParams;
	}
	public void setSystemParams(Map<String, String> systemParams) {
		this._systemParams = systemParams;
	}
	
	private ConfigInterface _configManager;
	private IPageManager _pageManager;
	
	private Map<String, String> _systemParams;
	
	private int _reloadingResult = -1;
	
	public static final int FAILURE_RELOADING_RESULT_CODE = 0;
	public static final int SUCCESS_RELOADING_RESULT_CODE = 1;
	
}