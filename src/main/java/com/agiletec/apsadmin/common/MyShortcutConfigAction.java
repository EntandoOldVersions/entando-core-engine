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
package com.agiletec.apsadmin.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.SelectItem;
import com.agiletec.apsadmin.system.ApsAdminSystemConstants;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.apsadmin.system.services.shortcut.IShortcutManager;
import com.agiletec.apsadmin.system.services.shortcut.model.Shortcut;
import com.agiletec.apsadmin.system.services.shortcut.model.UserConfigBean;

/**
 * Action that manage the shortcut configuration of the current user.
 * @author E.Santoboni
 */
public class MyShortcutConfigAction extends BaseAction implements IMyShortcutConfigAction {
	
	@Override
	public String joinMyShortcut() {
		if (this.getStrutsAction() != ApsAdminSystemConstants.ADD) {
			this.addFieldError("strutsAction", this.getText("error.myShortcut.invalidAction"));
			return INPUT;
		}
		return this.executeUpdateConfig(this.getPosition(), this.getShortcutCode());
	}
	
	@Override
	public String removeMyShortcut() {
		return this.executeUpdateConfig(this.getPosition(), null);
	}
	
	private String executeUpdateConfig(Integer position, String shortcutCode) {
		try {
			String[] config = this.getUserConfig();
			if (null == config) {
				config = new String[this.getUserShortcutsMaxNumber()];
			}
			config[position] = shortcutCode;
			String[] savedConfig = this.getShortcutManager().saveUserConfig(this.getCurrentUser(), config);
			this.setUserConfig(savedConfig);
			this.setPosition(null);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "executeUpdateConfig");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String swapMyShortcut() {
		try {
			String[] config = this.getUserConfig();
			if (null == config) {
				config = new String[this.getUserShortcutsMaxNumber()];
			}
			String shortcutToMove = config[this.getPositionTarget()];
			config[this.getPositionTarget()] = config[this.getPositionDest()];
			config[this.getPositionDest()] = shortcutToMove;
			String[] savedConfig = this.getShortcutManager().saveUserConfig(this.getCurrentUser(), config);
			this.setUserConfig(savedConfig);
			this.setPositionDest(null);
			this.setPositionTarget(null);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "swapMyShortcut");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public List<Shortcut> getAllowedShortcuts() {
		List<Shortcut> myShortcuts = null;
		try {
			UserDetails currentUser = this.getCurrentUser();
			if (null == currentUser || currentUser.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
				return myShortcuts;
			}
			myShortcuts = this.getShortcutManager().getAllowedShortcuts(currentUser);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getAllowedShortcuts");
			throw new RuntimeException("Error extracting allowed shortcuts by user " + this.getCurrentUser(), t);
		}
		return myShortcuts;
	}
	
	public List<SelectItem> getAllowedShortcutSelectItems() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		try {
			List<Shortcut> myShortcuts = this.getAllowedShortcuts();
			Map<String, List<SelectItem>> groups = new HashMap<String, List<SelectItem>>();
			for (int i = 0; i < myShortcuts.size(); i++) {
				Shortcut shortcut = myShortcuts.get(i);
				String groupCode = shortcut.getSource();
				String optgroup = shortcut.getSource();
				if (groupCode.equals("core")) {
					groupCode += " - " + shortcut.getMenuSection().getId();
					String sectDescrKey = shortcut.getMenuSection().getDescriptionKey();
					String sectDescr = this.getText(sectDescrKey);
					if (null == sectDescrKey || sectDescrKey.equals(sectDescr)) {
						sectDescr = shortcut.getMenuSection().getDescription();
					}
					optgroup += " - " + sectDescr;
				} else {
					String labelCode = optgroup + ".name";
					String optgroupDescr = this.getText(labelCode);
					if (!optgroupDescr.equals(labelCode)) {
						optgroup = optgroupDescr;
					}
				}
				String descrKey = shortcut.getDescriptionKey();
				String descr = this.getText(descrKey);
				if (null == descrKey || descrKey.equals(descr)) {
					descr = shortcut.getDescription();
				}
				List<SelectItem> itemsByGroup = groups.get(groupCode);
				if (null == itemsByGroup) {
					itemsByGroup = new ArrayList<SelectItem>();
					groups.put(groupCode, itemsByGroup);
				}
				SelectItem selectItem = new SelectItem(shortcut.getId(), descr, optgroup);
				itemsByGroup.add(selectItem);
			}
			List<String> keys = new ArrayList<String>(groups.keySet());
			Collections.sort(keys);
			for (int i = 0; i < keys.size(); i++) {
				List<SelectItem> itemsByGroup = groups.get(keys.get(i));
				BeanComparator comparator = new BeanComparator("value");
				Collections.sort(itemsByGroup, comparator);
				items.addAll(itemsByGroup);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getAllowedShortcutItems");
			throw new RuntimeException("Error extracting allowed shortcut items by user " + this.getCurrentUser(), t);
		}
		return items;
	}
	
	public String[] getUserConfig() {
		return this.getUserConfigBean().getConfig();
	}
	
	public UserConfigBean getUserConfigBean() {
		UserConfigBean config = null;
		try {
			config = (UserConfigBean) this.getRequest().getSession().getAttribute(SESSION_PARAM_MY_SHORTCUTS);
			if (null == config || !this.getCurrentUser().getUsername().equals(config.getUsername())) {
				config = this.getShortcutManager().getUserConfigBean(this.getCurrentUser());
				this.setUserConfigBean(config);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getUserConfigBean");
			throw new RuntimeException("Error extracting user config bean by user " + this.getCurrentUser(), t);
		}
		return config;
	}
	
	protected void setUserConfig(String[] config) {
		UserConfigBean userConfig = new UserConfigBean(this.getCurrentUser().getUsername(), config);
		this.setUserConfigBean(userConfig);
	}
	
	protected void setUserConfigBean(UserConfigBean userConfig) {
		this.getRequest().getSession().setAttribute(SESSION_PARAM_MY_SHORTCUTS, userConfig);
	}
	
	public Shortcut getShortcut(String code) {
		return this.getShortcutManager().getShortcut(code);
	}
	
	public boolean isShortcutAllowed(String shortcutCode) {
		Shortcut shortcut = this.getShortcut(shortcutCode);
		if (null != shortcut) {
			String reqPerm = shortcut.getRequiredPermission();
			return (null == reqPerm || this.getAuthorizationManager().isAuthOnPermission(this.getCurrentUser(), reqPerm));
		}
		return false;
	}
	
	public Integer getUserShortcutsMaxNumber() {
		return this.getShortcutManager().getUserShortcutsMaxNumber();
	}
	
	public Integer getStrutsAction() {
		return _strutsAction;
	}
	public void setStrutsAction(Integer strutsAction) {
		this._strutsAction = strutsAction;
	}
	
	public Integer getPosition() {
		return _position;
	}
	public void setPosition(Integer position) {
		this._position = position;
	}
	
	public String getShortcutCode() {
		return _shortcutCode;
	}
	public void setShortcutCode(String shortcutCode) {
		this._shortcutCode = shortcutCode;
	}
	
	public Integer getPositionTarget() {
		return _positionTarget;
	}
	public void setPositionTarget(Integer positionTarget) {
		this._positionTarget = positionTarget;
	}
	
	public Integer getPositionDest() {
		return _positionDest;
	}
	public void setPositionDest(Integer positionDest) {
		this._positionDest = positionDest;
	}
	
	protected IShortcutManager getShortcutManager() {
		return _shortcutManager;
	}
	public void setShortcutManager(IShortcutManager shortcutManager) {
		this._shortcutManager = shortcutManager;
	}
	
	private Integer _strutsAction;
	private Integer _position;
	private String _shortcutCode;
	
	private Integer _positionTarget;
	private Integer _positionDest;
	
	private IShortcutManager _shortcutManager;
	
}