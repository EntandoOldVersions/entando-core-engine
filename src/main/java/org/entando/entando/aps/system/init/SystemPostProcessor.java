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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author E.Santoboni
 */
public class SystemPostProcessor implements BeanPostProcessor {
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		//throw new UnsupportedOperationException("Not supported yet.");
		//System.out.println(this.getClass() + " - postProcessBeforeInitialization - " + bean + " - " + beanName);
		//Nothing to do
		return bean;
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof InitializerManager) {
			((InitializerManager) bean).executePostInitProcesses();
		}
		//throw new UnsupportedOperationException("Not supported yet.");
		//System.out.println(this.getClass() + " - postProcessAfterInitialization - " + bean + " - " + beanName);
		return bean;
	}
	
}
