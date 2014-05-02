/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando Enterprise Edition software.
* You can redistribute it and/or modify it
* under the terms of the Entando's EULA
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package org.entando.entando.apsadmin.portal.guifragment;

import com.agiletec.aps.util.SelectItem;
import com.agiletec.apsadmin.portal.AbstractPortalAction;
import org.entando.entando.aps.system.services.guifragment.GuiFragment;
import org.entando.entando.aps.system.services.guifragment.IGuiFragmentManager;

import com.agiletec.apsadmin.system.ApsAdminSystemConstants;

import java.util.List;
import java.util.Map;

import org.entando.entando.aps.system.services.widgettype.WidgetType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author E.Santoboni
 */
public class GuiFragmentAction extends AbstractPortalAction {
	
	private static final Logger _logger =  LoggerFactory.getLogger(GuiFragmentAction.class);
	
	public String newGuiFragment() {
		try {
			this.setStrutsAction(ApsAdminSystemConstants.ADD);
		} catch (Throwable t) {
			_logger.error("error in newGuiFragment", t);
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public String edit() {
		try {
			GuiFragment guiFragment = this.getGuiFragmentManager().getGuiFragment(this.getCode());
			if (null == guiFragment) {
				this.addActionError(this.getText("error.guiFragment.null"));
				return INPUT;
			}
			this.populateForm(guiFragment);
			this.setStrutsAction(ApsAdminSystemConstants.EDIT);
		} catch (Throwable t) {
			_logger.error("error in edit", t);
			return FAILURE;
		}
		return SUCCESS;
	}

	public String save() {
		try {
			GuiFragment guiFragment = this.createGuiFragment();
			int strutsAction = this.getStrutsAction();
			if (ApsAdminSystemConstants.ADD == strutsAction) {
				this.getGuiFragmentManager().addGuiFragment(guiFragment);
			} else if (ApsAdminSystemConstants.EDIT == strutsAction) {
				this.getGuiFragmentManager().updateGuiFragment(guiFragment);
			}
		} catch (Throwable t) {
			_logger.error("error in save", t);
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public String trash() {
		try {
			GuiFragment guiFragment = this.getGuiFragmentManager().getGuiFragment(this.getCode());
			if (null == guiFragment) {
				this.addActionError(this.getText("error.guiFragment.null"));
				return INPUT;
			}
			this.populateForm(guiFragment);
			this.setStrutsAction(ApsAdminSystemConstants.DELETE);
		} catch (Throwable t) {
			_logger.error("error in trash", t);
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public String delete() {
		try {
			if (this.getStrutsAction() == ApsAdminSystemConstants.DELETE) {
				this.getGuiFragmentManager().deleteGuiFragment(this.getCode());
			}
		} catch (Throwable t) {
			_logger.error("error in delete", t);
			return FAILURE;
		}
		return SUCCESS;
	}
	
	public String view() {
		try {
			GuiFragment guiFragment = this.getGuiFragmentManager().getGuiFragment(this.getCode());
			if (null == guiFragment) {
				this.addActionError(this.getText("error.guiFragment.null"));
				return INPUT;
			}
			this.populateForm(guiFragment);
		} catch (Throwable t) {
			_logger.error("error in view", t);
			return FAILURE;
		}
		return SUCCESS;
	}
	
	private void populateForm(GuiFragment guiFragment) throws Throwable {
		//this.setId(guiFragment.getId());
		this.setCode(guiFragment.getCode());
		this.setWidgetTypeCode(guiFragment.getWidgetTypeCode());
		this.setPluginCode(guiFragment.getPluginCode());
		this.setGui(guiFragment.getGui());
	}
	
	private GuiFragment createGuiFragment() {
		GuiFragment guiFragment = new GuiFragment();
		//guiFragment.setId(this.getId());
		guiFragment.setCode(this.getCode());
		guiFragment.setWidgetTypeCode(this.getWidgetTypeCode());
		guiFragment.setPluginCode(this.getPluginCode());
		guiFragment.setGui(this.getGui());
		return guiFragment;
	}
	
	@Override
	protected void addFlavourWidgetType(String mapCode, WidgetType type, Map<String, List<SelectItem>> mapping) {
		if (!type.isLogic()) {
			super.addFlavourWidgetType(mapCode, type, mapping);
		}
	}
	
	public int getStrutsAction() {
		return _strutsAction;
	}
	public void setStrutsAction(int strutsAction) {
		this._strutsAction = strutsAction;
	}
	/*
	public int getId() {
		return _id;
	}
	public void setId(int id) {
		this._id = id;
	}
	*/
	public String getCode() {
		return _code;
	}
	public void setCode(String code) {
		this._code = code;
	}
	
	@Deprecated
	public String getWidgetCode() {
		return this.getWidgetTypeCode();
	}
	@Deprecated
	public void setWidgetCode(String widgetCode) {
		this.setWidgetTypeCode(widgetCode);
	}
	
	public String getWidgetTypeCode() {
		return _widgetTypeCode;
	}
	public void setWidgetTypeCode(String widgetTypeCode) {
		this._widgetTypeCode = widgetTypeCode;
	}
	
	public String getPluginCode() {
		return _pluginCode;
	}
	public void setPluginCode(String pluginCode) {
		this._pluginCode = pluginCode;
	}

	public String getGui() {
		return _gui;
	}
	public void setGui(String gui) {
		this._gui = gui;
	}
	
	protected IGuiFragmentManager getGuiFragmentManager() {
		return _guiFragmentManager;
	}
	public void setGuiFragmentManager(IGuiFragmentManager guiFragmentManager) {
		this._guiFragmentManager = guiFragmentManager;
	}
	
	private int _strutsAction;
	//private int _id;
	private String _code;
	//private String _widgetCode;
	private String _widgetTypeCode;
	private String _pluginCode;
	private String _gui;
	
	private IGuiFragmentManager _guiFragmentManager;
	
}