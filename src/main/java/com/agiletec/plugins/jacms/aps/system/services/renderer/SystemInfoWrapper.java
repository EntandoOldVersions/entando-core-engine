/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
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
package com.agiletec.plugins.jacms.aps.system.services.renderer;

import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * @author E.Santoboni
 */
public class SystemInfoWrapper {
	
	public SystemInfoWrapper(RequestContext reqCtx) {
		this.setReqCtx(reqCtx);
	}
	
    /**
	 * Return the value of a System parameter.
	 * @param paramName The name of parameters
	 * @return The value to return
	 */
    public String getConfigParameter(String paramName) {
		try {
            ConfigInterface configManager = 
					(ConfigInterface) ApsWebApplicationUtils.getBean(SystemConstants.BASE_CONFIG_MANAGER, this.getReqCtx().getRequest());
            return configManager.getParam(paramName);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getConfigParameter", "Error extracting config parameter - parameter " + paramName);
			return null;
        }
    }
	
    public IPage getCurrentPage() {
		try {
            IPage page = (IPage) this.getReqCtx().getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
            return page;
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getCurrentPage", "Error current page");
			return null;
        }
    }

    public IPage getPageWithWidget(String widgetCode) {
    	IPage page = null;
    	try {
            IPageManager pageManager = (IPageManager) ApsWebApplicationUtils.getBean(SystemConstants.PAGE_MANAGER, this.getReqCtx().getRequest());
    		List<IPage> pages = pageManager.getShowletUtilizers(widgetCode);
    		if (null != pages && !pages.isEmpty()) {
    			page = pages.get(0);
    		}
    		return page;
    	} catch (Throwable t) {
    		ApsSystemUtils.logThrowable(t, this, "getPageWithWidget", "Error getting page with widget: " + widgetCode);
    		return null;
    	}
    }

    public String getPageURLWithWidget(String widgetCode) {
    	String url = null;
    	try {
    		IPage page = this.getPageWithWidget(widgetCode);
    		if (null == page) return url;
    		IURLManager urlManager = (IURLManager) ApsWebApplicationUtils.getBean(SystemConstants.URL_MANAGER, this.getReqCtx().getRequest());
    		PageURL pageUrl = urlManager.createURL(this.getReqCtx());
    		pageUrl.setPage(page);
    		url = pageUrl.getURL();
    	} catch (Throwable t) {
    		ApsSystemUtils.logThrowable(t, this, "getPageURLWithWidget", "Error getting pageUrl with widget: " + widgetCode);
    		return null;
    	}
    	return url;
    }
    
    public Lang getCurrentLang() {
		try {
            return (Lang) this.getReqCtx().getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getCurrentLang", "Error current lang");
			return null;
        }
    }
	
    public Showlet getCurrentShowlet() {
		try {
            return (Showlet) this.getReqCtx().getExtraParam(SystemConstants.EXTRAPAR_CURRENT_SHOWLET);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getCurrentShowlet", "Error current Showlet");
			return null;
        }
    }
	
    protected RequestContext getReqCtx() {
        return _reqCtx;
    }
    private void setReqCtx(RequestContext reqCtx) {
        this._reqCtx = reqCtx;
    }
    
	private RequestContext _reqCtx;
	
}