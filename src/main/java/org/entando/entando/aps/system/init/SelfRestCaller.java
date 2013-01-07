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
package org.entando.entando.aps.system.init;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.user.IAuthenticationProviderManager;
import org.entando.entando.aps.system.init.model.IPostProcess;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @author E.Santoboni
 */
public class SelfRestCaller implements IPostProcessor, BeanFactoryAware {
	
	@Override
	public int executePostProcess(IPostProcess postProcess) throws ApsSystemException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	protected ConfigInterface getConfigManager() {
		return (ConfigInterface) this.getBeanFactory().getBean(SystemConstants.BASE_CONFIG_MANAGER);
	}
	
	protected IAuthenticationProviderManager getAuthenticationProvider() {
		return (IAuthenticationProviderManager) this.getBeanFactory().getBean(SystemConstants.AUTHENTICATION_PROVIDER_MANAGER);
	}
	
	protected BeanFactory getBeanFactory() {
		return _beanFactory;
	}
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this._beanFactory = beanFactory;
	}
	
	private BeanFactory _beanFactory;
	
}
