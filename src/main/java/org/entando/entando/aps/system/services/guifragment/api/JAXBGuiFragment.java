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
package org.entando.entando.aps.system.services.guifragment.api;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.entando.entando.aps.system.services.guifragment.GuiFragment;

@XmlRootElement(name = "guiFragment")
@XmlType(propOrder = {/*"id", */"code", "widgetCode", "pluginCode", "gui"})
public class JAXBGuiFragment {
	
    public JAXBGuiFragment() {
        super();
    }
	
    public JAXBGuiFragment(GuiFragment guiFragment) {
		//this.setId(guiFragment.getId());
		this.setCode(guiFragment.getCode());
		this.setWidgetCode(guiFragment.getWidgetTypeCode());
		this.setPluginCode(guiFragment.getPluginCode());
		this.setGui(guiFragment.getGui());
    }
    
	@XmlTransient
    public GuiFragment getGuiFragment() {
    	GuiFragment guiFragment = new GuiFragment();
		//guiFragment.setId(this.getId());
		guiFragment.setCode(this.getCode());
		guiFragment.setWidgetTypeCode(this.getWidgetCode());
		guiFragment.setPluginCode(this.getPluginCode());
		guiFragment.setGui(this.getGui());
    	return guiFragment;
    }
	/*
	@XmlElement(name = "id", required = true)
	public int getId() {
		return _id;
	}
	public void setId(int id) {
		this._id = id;
	}
	*/
	@XmlElement(name = "code", required = true)
	public String getCode() {
		return _code;
	}
	public void setCode(String code) {
		this._code = code;
	}

	@XmlElement(name = "widgetCode", required = true)
	public String getWidgetCode() {
		return _widgetCode;
	}
	public void setWidgetCode(String widgetCode) {
		this._widgetCode = widgetCode;
	}

	@XmlElement(name = "pluginCode", required = true)
	public String getPluginCode() {
		return _pluginCode;
	}
	public void setPluginCode(String pluginCode) {
		this._pluginCode = pluginCode;
	}

	@XmlElement(name = "gui", required = true)
	public String getGui() {
		return _gui;
	}
	public void setGui(String gui) {
		this._gui = gui;
	}
	
	//private int _id;
	private String _code;
	private String _widgetCode;
	private String _pluginCode;
	private String _gui;
	
}
