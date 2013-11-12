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
package com.agiletec.apsadmin.system;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.util.ApsWebApplicationUtils;

import com.opensymphony.xwork2.ActionInvocation;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.FileUploadInterceptor;

/**
 * Extension of default FileUploadInterceptor.
 * @author E.Santoboni
 */
public class ApsFileUploadInterceptor extends FileUploadInterceptor {
	
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		if (null == super.maximumSize || super.maximumSize == 0) {
			ConfigInterface configManager = (ConfigInterface) ApsWebApplicationUtils.getBean(SystemConstants.BASE_CONFIG_MANAGER, ServletActionContext.getRequest());
			String maxSizeParam = configManager.getParam(SystemConstants.PAR_FILEUPLOAD_MAXSIZE);
			if (null != maxSizeParam) {
				try {
					this.setMaximumSize(Long.parseLong(maxSizeParam));
				} catch (Throwable t) {
					ApsSystemUtils.getLogger().error("Error parsing param 'maxSize' - "
							+ "value '" + maxSizeParam + "' - message " + t.getMessage());
				}
			}
		}
		if (null == super.maximumSize || super.maximumSize == 0) {
			this.setMaximumSize(DEFAULT_MAX_SIZE);
		}
		return super.intercept(invocation);
	}
	
	public static final Long DEFAULT_MAX_SIZE = 10485760l;
	
}