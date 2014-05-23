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
package org.entando.entando.aps.system.services.widgettype.api;

import com.agiletec.aps.util.ApsProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.entando.entando.aps.system.services.guifragment.GuiFragment;
import org.entando.entando.aps.system.services.guifragment.api.JAXBGuiFragment;
import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.entando.entando.aps.system.services.widgettype.WidgetTypeParameter;

/**
 * @author E.Santoboni
 */
@XmlRootElement(name = "widgetType")
@XmlType(propOrder = {"code", "titles", "pluginCode", "mainGroup", "typeParameters", "action", "parentTypeCode", "config", "locked", "fragment", "fragments"})
public class JAXBWidgetType implements Serializable {
	
	public JAXBWidgetType() {}
	
	public JAXBWidgetType(WidgetType widgetType, GuiFragment fragment, List<GuiFragment> fragments) {
		this.setAction(widgetType.getAction());
		this.setCode(widgetType.getCode());
		this.setConfig(widgetType.getConfig());
		this.setLocked(widgetType.isLocked());
		this.setMainGroup(widgetType.getMainGroup());
		this.setParentTypeCode(widgetType.getPluginCode());
		this.setPluginCode(widgetType.getPluginCode());
		this.setTitles(widgetType.getTitles());
		this.setTypeParameters(widgetType.getTypeParameters());
		if (null != fragment) {
			JAXBGuiFragment jaxbGuiFragment = new JAXBGuiFragment(fragment);
			this.setFragment(jaxbGuiFragment);
		}
		if (null != fragments) {
			List<JAXBGuiFragment> jaxbFragments = null;
			for (int i = 0; i < fragments.size(); i++) {
				GuiFragment guiFragment = fragments.get(i);
				if (null != guiFragment) {
					JAXBGuiFragment jaxbGuiFragment = new JAXBGuiFragment(guiFragment);
					if (null == jaxbFragments) {
						jaxbFragments = new ArrayList<JAXBGuiFragment>();
					}
					jaxbFragments.add(jaxbGuiFragment);
				}
			}
			this.setFragments(jaxbFragments);
		}
	}
	
	public WidgetType getNewWidgetType(IWidgetTypeManager widgetTypeManager) {
		WidgetType type = new WidgetType();
		type.setCode(this.getCode());
		type.setTitles(this.getTitles());
		List<WidgetTypeParameter> parameters = this.getTypeParameters();
		if (null != parameters && !parameters.isEmpty()) {
			type.setTypeParameters(parameters);
			type.setAction("configSimpleParameter");
		}
		type.setMainGroup(this.getMainGroup());
		type.setLocked(this.isLocked());
		type.setPluginCode(this.getPluginCode());
		ApsProperties configuration = this.getConfig();
		String parentTypeCode = this.getParentTypeCode();
		if (null != parentTypeCode && null != configuration && !configuration.isEmpty()) {
			WidgetType parentType = widgetTypeManager.getWidgetType(parentTypeCode);
			type.setParentType(parentType);
			type.setConfig(configuration);
		}
		return type;
	}
	
	public WidgetType getModifiedWidgetType(IWidgetTypeManager widgetTypeManager) {
		WidgetType type = widgetTypeManager.getWidgetType(this.getCode());
		type.setTitles(this.getTitles());
		if (type.isLogic()) {
			ApsProperties configuration = this.getConfig();
			type.setConfig(configuration);
		} else {
			List<WidgetTypeParameter> parameters = this.getTypeParameters();
			if (null != parameters && !parameters.isEmpty()) {
				type.setTypeParameters(parameters);
				type.setAction("configSimpleParameter");
			}
		}
		type.setMainGroup(this.getMainGroup());
		type.setLocked(this.isLocked());
		type.setPluginCode(this.getPluginCode());
		return type;
	}
	
	@XmlElement(name = "code", required = false)
	public String getCode() {
		return _code;
	}
	public void setCode(String code) {
		this._code = code;
	}
	
	@XmlElement(name = "titles", required = false)
	public ApsProperties getTitles() {
		return _titles;
	}
	public void setTitles(ApsProperties titles) {
		this._titles = titles;
	}
	
	@XmlElement(name = "typeParameter", required = false)
    @XmlElementWrapper(name = "typeParameters", required = false)
	public List<WidgetTypeParameter> getTypeParameters() {
		return _parameters;
	}
	public void setTypeParameters(List<WidgetTypeParameter> typeParameters) {
		this._parameters = typeParameters;
	}
	
	@XmlElement(name = "action", required = false)
	public String getAction() {
		return _action;
	}
	public void setAction(String action) {
		this._action = action;
	}
	
	@XmlElement(name = "pluginCode", required = false)
	public String getPluginCode() {
		return _pluginCode;
	}
	public void setPluginCode(String pluginCode) {
		this._pluginCode = pluginCode;
	}
	
	@XmlElement(name = "parentTypeCode", required = false)
	protected String getParentTypeCode() {
		return _parentTypeCode;
	}
	protected void setParentTypeCode(String parentTypeCode) {
		this._parentTypeCode = parentTypeCode;
	}
	
	@XmlElement(name = "configuration", required = false)
    @XmlElementWrapper(name = "configurations", required = false)
	public ApsProperties getConfig() {
		return _config;
	}
	public void setConfig(ApsProperties config) {
		this._config = config;
	}
	
	@XmlElement(name = "locked", required = false)
	public boolean isLocked() {
		return _locked;
	}
	public void setLocked(boolean locked) {
		this._locked = locked;
	}
	
	@XmlElement(name = "mainGroup", required = false)
	public String getMainGroup() {
		return _mainGroup;
	}
	public void setMainGroup(String mainGroup) {
		this._mainGroup = mainGroup;
	}
	
	@XmlElement(name = "fragment", required = false)
	public JAXBGuiFragment getFragment() {
		return _fragment;
	}
	protected void setFragment(JAXBGuiFragment fragment) {
		this._fragment = fragment;
	}
	
	@XmlElement(name = "fragment", required = false)
    @XmlElementWrapper(name = "fragments", required = false)
	public List<JAXBGuiFragment> getFragments() {
		return _fragments;
	}
	protected void setFragments(List<JAXBGuiFragment> fragments) {
		this._fragments = fragments;
	}
	
	private String _code;
	private ApsProperties _titles;
	private List<WidgetTypeParameter> _parameters;
	private String _action;
	private String _pluginCode;
	private String _parentTypeCode;
	private ApsProperties _config;
	private boolean _locked;
	private String _mainGroup;
	private JAXBGuiFragment _fragment;
	private List<JAXBGuiFragment> _fragments;
	
}