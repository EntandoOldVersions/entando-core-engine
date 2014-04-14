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

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.common.FieldSearchFilter;

import org.entando.entando.aps.system.services.guifragment.GuiFragment;
import org.entando.entando.aps.system.services.guifragment.IGuiFragmentManager;

import com.agiletec.apsadmin.system.BaseAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuiFragmentFinderAction extends BaseAction {

	private static final Logger _logger =  LoggerFactory.getLogger(GuiFragmentFinderAction.class);

	public List<Integer> getGuiFragmentsId() {
		try {
			FieldSearchFilter[] filters = new FieldSearchFilter[0];
			if (null != this.getId()) {
				//TODO add a constant into your IGuiFragmentManager class
				FieldSearchFilter filterToAdd = new FieldSearchFilter(("id"), this.getId(), false);
				filters = this.addFilter(filters, filterToAdd);
			}
			if (StringUtils.isNotBlank(this.getCode())) {
				//TODO add a constant into your IGuiFragmentManager class
				FieldSearchFilter filterToAdd = new FieldSearchFilter(("code"), this.getCode(), true);
				filters = this.addFilter(filters, filterToAdd);
			}
			if (StringUtils.isNotBlank(this.getWidgetCode())) {
				//TODO add a constant into your IGuiFragmentManager class
				FieldSearchFilter filterToAdd = new FieldSearchFilter(("widgetCode"), this.getWidgetCode(), true);
				filters = this.addFilter(filters, filterToAdd);
			}
			if (StringUtils.isNotBlank(this.getPluginCode())) {
				//TODO add a constant into your IGuiFragmentManager class
				FieldSearchFilter filterToAdd = new FieldSearchFilter(("pluginCode"), this.getPluginCode(), true);
				filters = this.addFilter(filters, filterToAdd);
			}
			if (StringUtils.isNotBlank(this.getGui())) {
				//TODO add a constant into your IGuiFragmentManager class
				FieldSearchFilter filterToAdd = new FieldSearchFilter(("gui"), this.getGui(), true);
				filters = this.addFilter(filters, filterToAdd);
			}
			List<Integer> guiFragments = this.getGuiFragmentManager().searchGuiFragments(filters);
			return guiFragments;
		} catch (Throwable t) {
			_logger.error("Error getting guiFragments list", t);
			throw new RuntimeException("Error getting guiFragments list", t);
		}
	}
	
	protected FieldSearchFilter[] addFilter(FieldSearchFilter[] filters, FieldSearchFilter filterToAdd) {
		int len = filters.length;
		FieldSearchFilter[] newFilters = new FieldSearchFilter[len + 1];
		for(int i=0; i < len; i++){
			newFilters[i] = filters[i];
		}
		newFilters[len] = filterToAdd;
		return newFilters;
	}
	
	public GuiFragment getGuiFragment(int id) {
		GuiFragment guiFragment = null;
		try {
			guiFragment = this.getGuiFragmentManager().getGuiFragment(id);
		} catch (Throwable t) {
			_logger.error("Error getting guiFragment with id {}", id, t);
			throw new RuntimeException("Error getting guiFragment with id " + id, t);
		}
		return guiFragment;
	}
	
	public Integer getId() {
		return _id;
	}
	public void setId(Integer id) {
		this._id = id;
	}
	
	public String getCode() {
		return _code;
	}
	public void setCode(String code) {
		this._code = code;
	}
	
	public String getWidgetCode() {
		return _widgetCode;
	}
	public void setWidgetCode(String widgetCode) {
		this._widgetCode = widgetCode;
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
	
	private Integer _id;
	private String _code;
	private String _widgetCode;
	private String _pluginCode;
	private String _gui;
	private IGuiFragmentManager _guiFragmentManager;
	
}