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
package com.agiletec.apsadmin.portal;

import java.util.List;

import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.entando.entando.aps.system.services.widgettype.WidgetTypeParameter;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.apsadmin.system.ApsAdminSystemConstants;

/**
 * @author E.Santoboni
 */
public class WidgetTypeAction extends AbstractPortalAction implements IWidgetTypeAction {
	
	@Override
	public void validate() {
		super.validate();
		if (this.getStrutsAction() == ApsAdminSystemConstants.EDIT) return;
		try {
			if (this.getStrutsAction() == ApsAdminSystemConstants.PASTE) {
				this.checkShowletToCopy();
			} else if (this.getStrutsAction() == ApsAdminSystemConstants.ADD) {
				this.checkNewShowlet();
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "validate");
			throw new RuntimeException(t);
		}
	}
	
	/**
	 * @deprecated Use {@link #newUserWidget()} instead
	 */
	@Override
	public String newUserShowlet() {
		return newUserWidget();
	}

	@Override
	public String newUserWidget() {
		try {
			String check = this.checkNewShowlet();
			if (null != check) return check;
			this.setStrutsAction(ApsAdminSystemConstants.ADD);
			this.setMainGroup(Group.FREE_GROUP_NAME);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "newUserWidget");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String copy() {
		try {
			String check = this.checkShowletToCopy();
			if (null != check) return check;
			this.setStrutsAction(ApsAdminSystemConstants.PASTE);
			this.setMainGroup(Group.FREE_GROUP_NAME);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "copy");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String save() {
		try {
			if (this.getStrutsAction() != ApsAdminSystemConstants.EDIT) {
				if (!this.hasCurrentUserPermission(Permission.SUPERUSER)) {
					return USER_NOT_ALLOWED;
				}
				return this.saveUserShowlet();
			}
			String check = this.checkShowletType();
			if (null != check) return check;
			ApsProperties titles = new ApsProperties();
			titles.put("it", this.getItalianTitle());
			titles.put("en", this.getEnglishTitle());
			WidgetType type = this.getWidgetTypeManager().getWidgetType(this.getWidgetTypeCode());
			String mainGroupToSet = (this.hasCurrentUserPermission(Permission.SUPERUSER)) ? this.getMainGroup() : type.getMainGroup();
			ApsProperties configToSet = type.getConfig();
			if (type.isLogic() && type.isUserType() && !type.isLocked() && this.hasCurrentUserPermission(Permission.SUPERUSER)) {
				configToSet = this.extractWidgetTypeConfig(type.getParentType().getTypeParameters());
			}
			this.getWidgetTypeManager().updateWidgetType(this.getWidgetTypeCode(), titles, configToSet, mainGroupToSet);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "save");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	protected String saveUserShowlet() {
		try {
			boolean isCopy = (null != this.getPageCode() && this.getPageCode().trim().length() > 0);
			String check = (isCopy) ? this.checkShowletToCopy() : this.checkNewShowlet();
			if (null != check) return check;
			WidgetType newType = null;
			Widget showletToCopy = this.extractShowletToCopy();
			if (null == showletToCopy) {
				this.setReplaceOnPage(false);
				newType = this.createNewWidgetType();
				WidgetType parentType = this.getWidgetTypeManager().getWidgetType(this.getParentShowletTypeCode());
				newType.setParentType(parentType);
				ApsProperties config = this.extractWidgetTypeConfig(parentType.getTypeParameters());
				newType.setConfig(config);
			} else {
				newType = this.createCopiedWidget(showletToCopy);
			}
			
			//TODO CHECK MainGroup
			newType.setMainGroup(this.getMainGroup());
			
			this.getWidgetTypeManager().addWidgetType(newType);
			if (this.isReplaceOnPage()) {
				WidgetType type = this.getShowletType(this.getWidgetTypeCode());
				Widget widget = new Widget();
				widget.setType(type);
				IPage page = this.getPageManager().getPage(this.getPageCode());
				page.getWidgets()[this.getFramePos()] = widget;
				this.getPageManager().updatePage(page);
				return "replaceOnPage";
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "saveUserShowlet");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	private Widget extractShowletToCopy() throws Throwable {
		IPage page = this.getPageManager().getPage(this.getPageCode());
		if (null == page) return null;
		Widget[] widgets = page.getWidgets();
		Widget widget = widgets[this.getFramePos()];
		return widget;
	}
	
	private String checkNewShowlet() throws Throwable {
		WidgetType parentType = this.getWidgetTypeManager().getWidgetType(this.getParentShowletTypeCode());
		if (null == parentType) {
			this.addActionError(this.getText("error.widgetType.invalid.null", new String[]{this.getParentShowletTypeCode()}));
			return "inputShowletTypes";
		}
		if (null == parentType.getTypeParameters() || parentType.getTypeParameters().isEmpty()) {
			this.addActionError(this.getText("error.widgetType.invalid.typeWithNoParameters", new String[]{this.getParentShowletTypeCode()}));
			return "inputShowletTypes";
		}
		return null;
	}
	
	private String checkShowletToCopy() throws Throwable {
		IPage page = this.getPageManager().getPage(this.getPageCode());
		if (null == page) {
			this.addActionError(this.getText("error.page.invalidPageCode.adv", 
					new String[]{this.getPageCode()}));
			return "inputShowletTypes";
		}
		if (!this.getAuthorizationManager().isAuth(this.getCurrentUser(), page)) {
			this.addActionError(this.getText("error.page.userNotAllowed.adv", 
					new String[]{this.getPageCode()}));
			return "inputShowletTypes";
		}
		Widget[] widgets = page.getWidgets();
		if (null == this.getFramePos() || widgets.length <= this.getFramePos()) {
			String framePos = (null != this.getFramePos()) ? this.getFramePos().toString() : null;
			this.addActionError(this.getText("error.page.invalidPageFrame.adv", 
					new String[]{this.getPageCode(), framePos}));
			return "inputShowletTypes";
		}
		Widget widget = widgets[this.getFramePos()];
		if (null == widget) {
			this.addActionError(this.getText("error.page.nullWidgetOnFrame", 
					new String[]{this.getPageCode(), this.getFramePos().toString()}));
			return "inputShowletTypes";
		}
		this.setShowletToCopy(widget);
		return null;
	}
	
	private WidgetType createNewWidgetType() {
		WidgetType type = new WidgetType();
		type.setCode(this.getWidgetTypeCode());
		ApsProperties titles = new ApsProperties();
		titles.setProperty("it", this.getItalianTitle());
		titles.setProperty("en", this.getEnglishTitle());
		type.setTitles(titles);
		type.setLocked(false);
		return type;
	}
	
	private WidgetType createCopiedWidget(Widget widgetToCopy) {
		WidgetType type = this.createNewWidgetType();
		WidgetType parentType = widgetToCopy.getType();
		type.setParentType(parentType);
		type.setConfig(widgetToCopy.getConfig());
		return type;
	}
	
	private ApsProperties extractWidgetTypeConfig(List<WidgetTypeParameter> parameters) throws Exception {
		ApsProperties config = new ApsProperties();
		for (int i=0; i<parameters.size(); i++) {
			WidgetTypeParameter param = parameters.get(i);
			String paramName = param.getName();
			String value = this.getRequest().getParameter(paramName);
			if (value != null && value.trim().length()>0) {
				config.setProperty(paramName, value);
			}
		}
		return config;
	}
	
	@Override
	public String edit() {
		try {
			String check = this.checkShowletType();
			if (null != check) return check;
			this.setStrutsAction(ApsAdminSystemConstants.EDIT);
			WidgetType type = this.getWidgetTypeManager().getWidgetType(this.getWidgetTypeCode());
			ApsProperties titles = type.getTitles();
			this.setItalianTitle(titles.getProperty("it"));
			this.setEnglishTitle(titles.getProperty("en"));
			this.setMainGroup(type.getMainGroup());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "editShowletTitles");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	private String checkShowletType() {
		WidgetType type = this.getWidgetTypeManager().getWidgetType(this.getWidgetTypeCode());
		if (null == type) {
			this.addActionError(this.getText("error.widgetType.invalid.null", new String[]{this.getWidgetTypeCode()}));
			return "inputShowletTypes";
		}
		return null;
	}
	
	@Override
	public String trash() {
		try {
			String check = this.checkDeleteShowletType();
			if (null != check) return check;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "trash");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String delete() {
		try {
			String check = this.checkDeleteShowletType();
			if (null != check) return check;
			this.getWidgetTypeManager().deleteWidgetType(this.getWidgetTypeCode());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "delete");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	private String checkDeleteShowletType() {
		try {
			String check = this.checkShowletType();
			if (null != check) return check;
			WidgetType type = this.getWidgetTypeManager().getWidgetType(this.getWidgetTypeCode());
			if (type.isLocked()) {
				this.addActionError(this.getText("error.widgetType.locked.undeletable", new String[]{this.getWidgetTypeCode()}));
				return "inputShowletTypes";
			}
			List<IPage> utilizers = this.getPageManager().getWidgetUtilizers(this.getWidgetTypeCode());
			if (null != utilizers && utilizers.size() > 0) {
				this.addActionError(this.getText("error.widgetType.used.undeletable", new String[]{this.getWidgetTypeCode()}));
				return "inputShowletTypes";
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "checkDeleteShowletType");
			throw new RuntimeException("Error on checking delete operatione : showlet type code " + this.getWidgetTypeCode(), t);
		}
		return null;
	}
	
	public WidgetType getShowletType(String code) {
		return this.getWidgetTypeManager().getWidgetType(code);
	}
	
	public Group getGroup(String groupCode) {
		Group group = super.getGroupManager().getGroup(groupCode);
		if (null == group) {
			group = super.getGroupManager().getGroup(Group.FREE_GROUP_NAME);
		}
		return group;
	}
	
	public List<Group> getGroups() {
		return this.getGroupManager().getGroups();
	}
	
	public int getStrutsAction() {
		return _strutsAction;
	}
	public void setStrutsAction(int strutsAction) {
		this._strutsAction = strutsAction;
	}
	
	@Deprecated
	public String getShowletTypeCode() {
		return this.getWidgetTypeCode();
	}
	@Deprecated
	public void setShowletTypeCode(String showletTypeCode) {
		this.setWidgetTypeCode(showletTypeCode);
	}
	
	public String getEnglishTitle() {
		return _englishTitle;
	}
	public void setEnglishTitle(String englishTitle) {
		this._englishTitle = englishTitle;
	}
	
	public String getItalianTitle() {
		return _italianTitle;
	}
	public void setItalianTitle(String italianTitle) {
		this._italianTitle = italianTitle;
	}
	
	public String getMainGroup() {
		return _mainGroup;
	}
	public void setMainGroup(String mainGroup) {
		this._mainGroup = mainGroup;
	}
	
	@Deprecated
	public String getParentShowletTypeCode() {
		return this.getParentWidgetTypeCode();
	}
	@Deprecated
	public void setParentShowletTypeCode(String parentShowletTypeCode) {
		this.setParentWidgetTypeCode(parentShowletTypeCode);
	}
	
	public String getParentWidgetTypeCode() {
		return _parentWidgetTypeCode;
	}
	public void setParentWidgetTypeCode(String parentWidgetTypeCode) {
		this._parentWidgetTypeCode = parentWidgetTypeCode;
	}
	
	public String getPageCode() {
		return _pageCode;
	}
	public void setPageCode(String pageCode) {
		this._pageCode = pageCode;
	}
	
	public Integer getFramePos() {
		return _framePos;
	}
	public void setFramePos(Integer framePos) {
		this._framePos = framePos;
	}
	
	public Widget getShowletToCopy() {
		return _showletToCopy;
	}
	public void setShowletToCopy(Widget showletToCopy) {
		this._showletToCopy = showletToCopy;
	}
	
	public boolean isReplaceOnPage() {
		return _replaceOnPage;
	}
	public void setReplaceOnPage(boolean replaceOnPage) {
		this._replaceOnPage = replaceOnPage;
	}
	
	public String getWidgetTypeCode() {
		return _widgetTypeCode;
	}
	public void setWidgetTypeCode(String widgetTypeCode) {
		this._widgetTypeCode = widgetTypeCode;
	}

	private int _strutsAction;
	
	private String _widgetTypeCode;
	
	private String _englishTitle;
	private String _italianTitle;
	
	private String _mainGroup;
	
	private String _parentWidgetTypeCode;
	
	private String _pageCode;
	private Integer _framePos;
	private Widget _showletToCopy;
	private boolean _replaceOnPage;
	
}