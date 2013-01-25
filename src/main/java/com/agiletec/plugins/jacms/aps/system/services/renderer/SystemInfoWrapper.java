/*
*
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
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
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.plugins.jacms.aps.system.services.renderer;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.Showlet;
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
            return (IPage) this.getReqCtx().getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "getCurrentPage", "Error current page");
			return null;
        }
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