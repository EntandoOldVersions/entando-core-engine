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
package com.agiletec.apsadmin.portal;

import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.showlettype.ShowletType;
import com.agiletec.aps.system.services.showlettype.ShowletTypeParameter;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.apsadmin.system.ApsAdminSystemConstants;

/**
 * @author E.Santoboni
 */
public class ShowletTypeAction extends AbstractPortalAction implements IShowletTypeAction {
	
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
	
	@Override
	public String newUserShowlet() {
		try {
			String check = this.checkNewShowlet();
			if (null != check) return check;
			this.setStrutsAction(ApsAdminSystemConstants.ADD);
			this.setMainGroup(Group.FREE_GROUP_NAME);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "newShowlet");
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
			ApsSystemUtils.logThrowable(t, this, "newShowlet");
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
			ShowletType type = this.getShowletTypeManager().getShowletType(this.getShowletTypeCode());
			String mainGroupToSet = (this.hasCurrentUserPermission(Permission.SUPERUSER)) ? this.getMainGroup() : type.getMainGroup();
			ApsProperties configToSet = type.getConfig();
			if (type.isLogic() && type.isUserType() && !type.isLocked() && this.hasCurrentUserPermission(Permission.SUPERUSER)) {
				configToSet = this.extractShowletTypeConfig(type.getParentType().getTypeParameters());
			}
			this.getShowletTypeManager().updateShowletType(this.getShowletTypeCode(), titles, configToSet, mainGroupToSet);
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
			ShowletType newType = null;
			Showlet showletToCopy = this.extractShowletToCopy();
			if (null == showletToCopy) {
				this.setReplaceOnPage(false);
				newType = this.createNewShowletType();
				ShowletType parentType = this.getShowletTypeManager().getShowletType(this.getParentShowletTypeCode());
				newType.setParentType(parentType);
				ApsProperties config = this.extractShowletTypeConfig(parentType.getTypeParameters());
				newType.setConfig(config);
			} else {
				newType = this.createCopiedShowlet(showletToCopy);
			}
			
			//TODO CHECK MainGroup
			newType.setMainGroup(this.getMainGroup());
			
			this.getShowletTypeManager().addShowletType(newType);
			if (this.isReplaceOnPage()) {
				ShowletType type = this.getShowletType(this.getShowletTypeCode());
				Showlet showlet = new Showlet();
				showlet.setType(type);
				IPage page = this.getPageManager().getPage(this.getPageCode());
				page.getShowlets()[this.getFramePos()] = showlet;
				this.getPageManager().updatePage(page);
				return "replaceOnPage";
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "saveUserShowlet");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	private Showlet extractShowletToCopy() throws Throwable {
		IPage page = this.getPageManager().getPage(this.getPageCode());
		if (null == page) return null;
		Showlet[] showlets = page.getShowlets();
		Showlet showlet = showlets[this.getFramePos()];
		return showlet;
	}
	
	private String checkNewShowlet() throws Throwable {
		ShowletType parentType = this.getShowletTypeManager().getShowletType(this.getParentShowletTypeCode());
		if (null == parentType) {
			this.addActionError(this.getText("error.showletType.invalid.null", new String[]{this.getParentShowletTypeCode()}));
			return "inputShowletTypes";
		}
		if (null == parentType.getTypeParameters() || parentType.getTypeParameters().isEmpty()) {
			this.addActionError(this.getText("error.showletType.invalid.typeWithNoParameters", new String[]{this.getParentShowletTypeCode()}));
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
		Showlet[] showlets = page.getShowlets();
		if (null == this.getFramePos() || showlets.length <= this.getFramePos()) {
			String framePos = (null != this.getFramePos()) ? this.getFramePos().toString() : null;
			this.addActionError(this.getText("error.page.invalidPageFrame.adv", 
					new String[]{this.getPageCode(), framePos}));
			return "inputShowletTypes";
		}
		Showlet showlet = showlets[this.getFramePos()];
		if (null == showlet) {
			this.addActionError(this.getText("error.page.nullShowletOnFrame", 
					new String[]{this.getPageCode(), this.getFramePos().toString()}));
			return "inputShowletTypes";
		}
		this.setShowletToCopy(showlet);
		return null;
	}
	
	private ShowletType createNewShowletType() {
		ShowletType type = new ShowletType();
		type.setCode(this.getShowletTypeCode());
		ApsProperties titles = new ApsProperties();
		titles.setProperty("it", this.getItalianTitle());
		titles.setProperty("en", this.getEnglishTitle());
		type.setTitles(titles);
		type.setLocked(false);
		return type;
	}
	
	private ShowletType createCopiedShowlet(Showlet showletToCopy) {
		ShowletType type = this.createNewShowletType();
		ShowletType parentType = showletToCopy.getType();
		type.setParentType(parentType);
		type.setConfig(showletToCopy.getConfig());
		return type;
	}
	
	private ApsProperties extractShowletTypeConfig(List<ShowletTypeParameter> parameters) throws Exception {
		ApsProperties config = new ApsProperties();
		for (int i=0; i<parameters.size(); i++) {
			ShowletTypeParameter param = parameters.get(i);
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
			ShowletType type = this.getShowletTypeManager().getShowletType(this.getShowletTypeCode());
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
		ShowletType type = this.getShowletTypeManager().getShowletType(this.getShowletTypeCode());
		if (null == type) {
			this.addActionError(this.getText("error.showletType.invalid.null", new String[]{this.getShowletTypeCode()}));
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
			this.getShowletTypeManager().deleteShowletType(this.getShowletTypeCode());
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
			ShowletType type = this.getShowletTypeManager().getShowletType(this.getShowletTypeCode());
			if (type.isLocked()) {
				this.addActionError(this.getText("error.showletType.locked.undeletable", new String[]{this.getShowletTypeCode()}));
				return "inputShowletTypes";
			}
			List<IPage> utilizers = this.getPageManager().getShowletUtilizers(this.getShowletTypeCode());
			if (null != utilizers && utilizers.size() > 0) {
				this.addActionError(this.getText("error.showletType.used.undeletable", new String[]{this.getShowletTypeCode()}));
				return "inputShowletTypes";
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "checkDeleteShowletType");
			throw new RuntimeException("Error on checking delete operatione : showlet type code " + this.getShowletTypeCode(), t);
		}
		return null;
	}
	
	public ShowletType getShowletType(String code) {
		return this.getShowletTypeManager().getShowletType(code);
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
	
	public String getShowletTypeCode() {
		return _showletTypeCode;
	}
	public void setShowletTypeCode(String showletTypeCode) {
		this._showletTypeCode = showletTypeCode;
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
	
	public String getParentShowletTypeCode() {
		return _parentShowletTypeCode;
	}
	public void setParentShowletTypeCode(String parentShowletTypeCode) {
		this._parentShowletTypeCode = parentShowletTypeCode;
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
	
	public Showlet getShowletToCopy() {
		return _showletToCopy;
	}
	public void setShowletToCopy(Showlet showletToCopy) {
		this._showletToCopy = showletToCopy;
	}
	
	public boolean isReplaceOnPage() {
		return _replaceOnPage;
	}
	public void setReplaceOnPage(boolean replaceOnPage) {
		this._replaceOnPage = replaceOnPage;
	}
	
	private int _strutsAction;
	
	private String _showletTypeCode;
	private String _englishTitle;
	private String _italianTitle;
	
	private String _mainGroup;
	
	private String _parentShowletTypeCode;
	
	private String _pageCode;
	private Integer _framePos;
	private Showlet _showletToCopy;
	private boolean _replaceOnPage;
	
}