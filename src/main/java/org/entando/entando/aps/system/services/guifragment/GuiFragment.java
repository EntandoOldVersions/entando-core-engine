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
package org.entando.entando.aps.system.services.guifragment;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * @author E.Santoboni
 */
public class GuiFragment implements Serializable {

	/**
	 * Returns the relative path of a Widget fragment.
	 * If the fragment is not related to any Widget, it returns null.
	 * This function returns the path, it doesn't not check if the file really exists.
	 * @param guiFragment the {@link GuiFragment} the check
	 * @return the path of a widget fragment. Eg: /WEB-INF/aps/jsp/widgets/mywidget.jsp
	 */
	public static String getWidgetJspPath(GuiFragment guiFragment) {
		if (StringUtils.isBlank(guiFragment.getWidgetTypeCode())) return null;
		boolean isPlugin = (StringUtils.isNotBlank(guiFragment.getPluginCode()));
		
		StringBuilder jspPath = new StringBuilder("/WEB-INF/");
		if (isPlugin) {
			jspPath.append("plugins/").append(guiFragment.getPluginCode().trim()).append("/");
		}
		jspPath.append("aps/jsp/widgets/").append(guiFragment.getCode().trim()).append(".jsp");
		return jspPath.toString();
	}
	
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
	
	public String getDefaultGui() {
		return _defaultGui;
	}
	public void setDefaultGui(String defaultGui) {
		this._defaultGui = defaultGui;
	}
	
	/**
	 * Return the current gui in use.
	 * Return the default gui if the custom gui ("gui" property) are not available, else the default gui.
	 * @return The current gui in use.
	 */
	public String getCurrentGui() {
		boolean hasCustomGui = StringUtils.isNotBlank(this.getGui());
		if (hasCustomGui) {
			return this.getGui();
		} else {
			return this.getDefaultGui();
		}
	}
	
	public boolean isLocked() {
		return _locked;
	}
	public void setLocked(boolean locked) {
		this._locked = locked;
	}
	
	private String _code;
	private String _widgetTypeCode;
	private String _pluginCode;
	private String _gui;
	private String _defaultGui;
	private boolean _locked;
	
}
