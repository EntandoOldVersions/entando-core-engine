/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* Entando is a free software;
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
package org.entando.entando.aps.system.services.widgettype;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.entando.entando.aps.system.services.widgettype.events.WidgetTypeChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.GroupUtilizer;
import com.agiletec.aps.system.services.lang.events.LangsChangedEvent;
import com.agiletec.aps.system.services.lang.events.LangsChangedObserver;
import com.agiletec.aps.util.ApsProperties;

/**
 * Servizio di gestione dei tipi di widget (WidgetType) definiti
 * nel sistema. (Questo servizio non riguarda la configurazione delle
 * istanze di widget nelle pagine)
 * @author M.Diana - E.Santoboni
 */
public class WidgetTypeManager extends AbstractService 
		implements IWidgetTypeManager, LangsChangedObserver, GroupUtilizer {

	private static final Logger _logger =  LoggerFactory.getLogger(WidgetTypeManager.class);
	
	
	@Override
	public void init() throws Exception {
		this.loadWidgetTypes();
		_logger.debug("{} ready. Initialized {} widget types", this.getClass().getName(), this._widgetTypes.size());
	}
	
	/**
	 * Caricamento da db del catalogo dei tipi di widget.
	 * @throws ApsSystemException In caso di errori di lettura da db.
	 */
	private void loadWidgetTypes() throws ApsSystemException {
		try {
			this._widgetTypes = this.getWidgetTypeDAO().loadWidgetTypes();
			Iterator<WidgetType> iter = this._widgetTypes.values().iterator();
			while (iter.hasNext()) {
				WidgetType type = iter.next();
				String mainTypeCode = type.getParentTypeCode();
				if (null != mainTypeCode) {
					type.setParentType(this._widgetTypes.get(mainTypeCode));
				}
			}
		} catch (Throwable t) {
			_logger.error("Error loading widgets types", t);
			//ApsSystemUtils.logThrowable(t, this, "loadWidgetTypes");
			throw new ApsSystemException("Error loading widgets types", t);
		}
	}
	
	@Override
	public void updateFromLangsChanged(LangsChangedEvent event) {
		try {
			this.init();
		} catch (Throwable t) {
			_logger.error("Error on init method", t);
			//ApsSystemUtils.logThrowable(t, this, "updateFromLangsChanged", "Error on init method");
		}
	}
	
	/**
	 * @deprecated Use {@link #getWidgetType(String)} instead
	 */
	@Override
	public WidgetType getShowletType(String code) {
		return getWidgetType(code);
	}

	@Override
	public WidgetType getWidgetType(String code) {
		return this._widgetTypes.get(code);
	}
	
	/**
	 * @deprecated Use {@link #getWidgetTypes()} instead
	 */
	@Override
	public List<WidgetType> getShowletTypes() {
		return getWidgetTypes();
	}

	@Override
	public List<WidgetType> getWidgetTypes() {
		List<WidgetType> types = new ArrayList<WidgetType>();
		Iterator<WidgetType> masterTypesIter = this._widgetTypes.values().iterator();
		while (masterTypesIter.hasNext()) {
			WidgetType widgetType = masterTypesIter.next();
			types.add(widgetType.clone());
		}
		return types;
	}
	
	/**
	 * @deprecated Use {@link #addWidgetType(WidgetType)} instead
	 */
	@Override
	public void addShowletType(WidgetType showletType) throws ApsSystemException {
		addWidgetType(showletType);
	}

	@Override
	public void addWidgetType(WidgetType widgetType) throws ApsSystemException {
		try {
			WidgetType type = this._widgetTypes.get(widgetType.getCode());
			if (null != type) {
				_logger.error("Type already exists : type code {}", widgetType.getCode());
				return;
			}
			String parentTypeCode = widgetType.getParentTypeCode();
			if (null != parentTypeCode && null == this._widgetTypes.get(parentTypeCode)) {
				throw new ApsSystemException("ERROR : Parent type '" + parentTypeCode + "' doesn't exists");
			}
			if (null == parentTypeCode && null != widgetType.getConfig()) {
				throw new ApsSystemException("ERROR : Parent type null and default config not null");
			}
			if (null != widgetType.getTypeParameters() && null != widgetType.getConfig()) {
				throw new ApsSystemException("ERROR : Params not null and config not null");
			}
			this.getWidgetTypeDAO().addShowletType(widgetType);
			this._widgetTypes.put(widgetType.getCode(), widgetType);
		} catch (Throwable t) {
			_logger.error("Error adding a Widget Type", t);
			//ApsSystemUtils.logThrowable(t, this, "addWidgetType");
			throw new ApsSystemException("Error adding a Widget Type", t);
		}
	}
	
	/**
	 * @deprecated Use {@link #deleteWidgetType(String)} instead
	 */
	@Override
	public void deleteShowletType(String showletTypeCode) throws ApsSystemException {
		deleteWidgetType(showletTypeCode);
	}

	@Override
	public void deleteWidgetType(String widgetTypeCode) throws ApsSystemException {
		try {
			WidgetType type = this._widgetTypes.get(widgetTypeCode);
			if (null == type) {
				_logger.error("Type not exists : type code {}", widgetTypeCode);
				return;
			}
			if (type.isLocked()) {
				_logger.error("A locked widget can't be deleted - type {}", widgetTypeCode);
				return;
			}
			this.getWidgetTypeDAO().deleteWidgetType(widgetTypeCode);
			this._widgetTypes.remove(widgetTypeCode);
		} catch (Throwable t) {
			_logger.error("Error deleting widget type", t);
			//ApsSystemUtils.logThrowable(t, this, "deleteWidgetType");
			throw new ApsSystemException("Error deleting widget type", t);
		}
	}
	
	@Override
	@Deprecated
	public void updateShowletType(String showletTypeCode, ApsProperties titles, ApsProperties defaultConfig) throws ApsSystemException {
		try {
			WidgetType type = this._widgetTypes.get(showletTypeCode);
			if (null == type) {
				_logger.error("Type not exists : type code {}", showletTypeCode);
				return;
			}
			this.updateWidgetType(showletTypeCode, titles, defaultConfig, Group.FREE_GROUP_NAME);
		} catch (Throwable t) {
			_logger.error("Error updating Showlet type titles : type code {}", showletTypeCode, t);
			//ApsSystemUtils.logThrowable(t, this, "updateShowletTypeTitles");
			throw new ApsSystemException("Error updating Showlet type titles : type code" + showletTypeCode, t);
		}
	}
	
	/**
	 * @deprecated Use {@link #updateWidgetType(String,ApsProperties,ApsProperties,String)} instead
	 */
	@Override
	public void updateShowletType(String showletTypeCode, ApsProperties titles, ApsProperties defaultConfig, String mainGroup) throws ApsSystemException {
		updateWidgetType(showletTypeCode, titles, defaultConfig, mainGroup);
	}

	@Override
	public void updateWidgetType(String widgetTypeCode, ApsProperties titles, ApsProperties defaultConfig, String mainGroup) throws ApsSystemException {
		try {
			WidgetType type = this._widgetTypes.get(widgetTypeCode);
			if (null == type) {
				_logger.error("Type not exists : type code {}", widgetTypeCode);
				return;
			}
			if (type.isLocked() || !type.isLogic() || !type.isUserType()) {
				defaultConfig = type.getConfig();
			}
			this.getWidgetTypeDAO().updateWidgetType(widgetTypeCode, titles, defaultConfig, mainGroup);
			type.setTitles(titles);
			type.setConfig(defaultConfig);
			type.setMainGroup(mainGroup);
			WidgetTypeChangedEvent event = new WidgetTypeChangedEvent();
			event.setShowletTypeCode(widgetTypeCode);
			this.notifyEvent(event);
		} catch (Throwable t) {
			_logger.error("Error updating Widget type titles : type code {}", widgetTypeCode, t);
			//ApsSystemUtils.logThrowable(t, this, "updateWidgetType");
			throw new ApsSystemException("Error updating Widget type titles : type code" + widgetTypeCode, t);
		}
	}
	
	@Override
	@Deprecated
	public void updateShowletTypeTitles(String showletTypeCode, ApsProperties titles) throws ApsSystemException {
		try {
			WidgetType type = this._widgetTypes.get(showletTypeCode);
			if (null == type) {
				_logger.error("Type not exists : type code {}", showletTypeCode);
				return;
			}
			this.getWidgetTypeDAO().updateShowletTypeTitles(showletTypeCode, titles);
			type.setTitles(titles);
		} catch (Throwable t) {
			_logger.error("Error updating Showlet type titles : type code {}", showletTypeCode, t);
			//ApsSystemUtils.logThrowable(t, this, "updateShowletTypeTitles");
			throw new ApsSystemException("Error updating Showlet type titles : type code" + showletTypeCode, t);
		}
	}
	
	@Override
	public List<WidgetType> getGroupUtilizers(String groupName) throws ApsSystemException {
		List<WidgetType> utilizers = null;
		try {
			boolean freeTypes = (null == groupName || groupName.equals(Group.FREE_GROUP_NAME));
			List<WidgetType> allTypes = this.getWidgetTypes();
			for (int i = 0; i < allTypes.size(); i++) {
				WidgetType type = allTypes.get(i);
				String typeGroup = type.getMainGroup();
				if ((freeTypes && null == typeGroup) || groupName.equals(typeGroup)) {
					if (null == utilizers) {
						utilizers = new ArrayList<WidgetType>();
					}
					utilizers.add(type);
				}
			}
		} catch (Throwable t) {
			_logger.error("Error extracting utilizers", t);
			//ApsSystemUtils.logThrowable(t, this, "getGroupUtilizers");
			throw new ApsSystemException("Error extracting utilizers", t);
		}
		return utilizers;
	}
	
	public IWidgetTypeDAO getWidgetTypeDAO() {
		return _widgetTypeDAO;
	}

	public void setWidgetTypeDAO(IWidgetTypeDAO widgetTypeDAO) {
		this._widgetTypeDAO = widgetTypeDAO;
	}
	
	public void setWidgetTypes(Map<String, WidgetType> widgetTypes) {
		this._widgetTypes = widgetTypes;
	}

	private Map<String, WidgetType> _widgetTypes;
	
	private IWidgetTypeDAO _widgetTypeDAO;
	
}