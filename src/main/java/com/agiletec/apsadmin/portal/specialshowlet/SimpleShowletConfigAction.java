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
package com.agiletec.apsadmin.portal.specialshowlet;

import java.util.List;
import java.util.logging.Logger;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.system.services.showlettype.ShowletType;
import com.agiletec.aps.system.services.showlettype.ShowletTypeParameter;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.apsadmin.portal.AbstractPortalAction;

/**
 * This action class handles the configuration of the showlets with parameters.
 * @author E.Santoboni
 */
public class SimpleShowletConfigAction extends AbstractPortalAction implements ISimpleShowletConfigAction {
	
	@Override
	public String init() {
		this.checkBaseParams();
		return this.extractInitConfig();
	}
	
	protected String extractInitConfig() {
		if (null != this.getShowlet()) return SUCCESS;
		Showlet showlet = this.getCurrentPage().getShowlets()[this.getFrame()];
		Logger log = ApsSystemUtils.getLogger();
		if (null == showlet) {
			try {
				showlet = this.createNewShowlet();
			} catch (Exception e) {
				log.severe(e.getMessage());
				//TODO METTI MESSAGGIO DI ERRORE NON PREVISO... Vai in pageTree con messaggio di errore Azione non prevista o cosa del genere
				this.addActionError(this.getText("Message.userNotAllowed"));
				return "pageTree";
			}
			log.info("Configurating new Showlet " + this.getShowletTypeCode() + 
					" - Page " + this.getPageCode() + " - Frame " + this.getFrame());
		} else {
			log.info("Edit Showlet config " + showlet.getType().getCode() + 
					" - Page " + this.getPageCode() + " - Frame " + this.getFrame());
			showlet = this.createCloneFrom(showlet);
		}
		this.setShowlet(showlet);
		return SUCCESS;
	}
	
	protected Showlet createNewShowlet() throws Exception {
		if (this.getShowletTypeCode() == null || this.getShowletType(this.getShowletTypeCode()) == null) {
			throw new Exception("Showlet Code missin or invalid : " + this.getShowletTypeCode());
		}
		Showlet showlet = new Showlet();
		ShowletType type = this.getShowletType(this.getShowletTypeCode());
		showlet.setType(type);
		showlet.setConfig(new ApsProperties());
		return showlet;
	}
	
	protected Showlet createCloneFrom(Showlet showlet) {
		Showlet clone = new Showlet();
		clone.setType(showlet.getType());
		if (null != showlet.getConfig()) {
			clone.setConfig((ApsProperties) showlet.getConfig().clone());
		}
		clone.setPublishedContent(showlet.getPublishedContent());
		return clone;
	}
	
	@Override
	public String save() {
		Logger log = ApsSystemUtils.getLogger();
		try {
			this.checkBaseParams();
			this.createValuedShowlet();
			this.getPageManager().joinShowlet(this.getPageCode(), this.getShowlet(), this.getFrame());
			log.finest("Saving Showlet - code = " + this.getShowlet().getType().getCode() + 
					", pageCode = " + this.getPageCode() + ", frame = " + this.getFrame());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "save");
			return FAILURE;
		}
		return "configure";
	}
	
	protected void createValuedShowlet() throws Exception {
		Showlet showlet = this.createNewShowlet();
		List<ShowletTypeParameter> parameters = showlet.getType().getTypeParameters();
		for (int i=0; i<parameters.size(); i++) {
			ShowletTypeParameter param = parameters.get(i);
			String paramName = param.getName();
			String value = this.getRequest().getParameter(paramName);
			if (value != null && value.trim().length()>0) {
				showlet.getConfig().setProperty(paramName, value);
				if ("contentId".equals(paramName)) {
					showlet.setPublishedContent(value);
				}
			}
		}
		this.setShowlet(showlet);
	}
	
	protected String checkBaseParams() {
		IPage page = this.getPage(this.getPageCode());
		if (null== page || !this.isUserAllowed(page)) {
			ApsSystemUtils.getLogger().info("User not allowed");
			this.addActionError(this.getText("error.page.userNotAllowed"));
			return "pageTree";
		}
		if (this.getFrame() == -1 || this.getFrame()>= page.getShowlets().length) {
			ApsSystemUtils.getLogger().info("invalid frame '" + this.getFrame() + "'");
			this.addActionError(this.getText("error.page.invalidPageFrame"));
			return "pageTree";
		}
		return null;
	}
	
	public IPage getCurrentPage() {
		return this.getPage(this.getPageCode());
	}
	
	public String getPageCode() {
		return _pageCode;
	}
	public void setPageCode(String pageCode) {
		this._pageCode = pageCode;
	}
	
	public int getFrame() {
		return _frame;
	}
	public void setFrame(int frame) {
		this._frame = frame;
	}
	
	public ShowletType getShowletType(String typeCode) {
		return this.getShowletTypeManager().getShowletType(typeCode);
	}
	
	@Override
	public Showlet getShowlet() {
		return _showlet;
	}
	public void setShowlet(Showlet showlet) {
		this._showlet = showlet;
	}
	
	public String getShowletTypeCode() {
		return _showletTypeCode;
	}
	public void setShowletTypeCode(String showletTypeCode) {
		this._showletTypeCode = showletTypeCode;
	}
	
	private String _pageCode;
	private int _frame = -1;
	private String _showletTypeCode;
	
	private Showlet _showlet;
	
}
