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

import com.agiletec.aps.system.exception.ApsSystemException;
import java.io.File;
import org.entando.entando.aps.system.init.model.Component;
import java.util.*;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;

/**
 * @author E.Santoboni
 */
public abstract class AbstractDatabaseUtils implements BeanFactoryAware {
	
	protected String getLocalBackupsFolder() {
		StringBuilder dirName = new StringBuilder(this.getProtectedBaseDiskRoot());
		if (!dirName.toString().endsWith("\\") && !dirName.toString().endsWith("/")) {
			dirName.append(File.separator);
		}
		dirName.append("databaseBackups").append(File.separator);
		return dirName.toString();
	}
	
	protected List<Component> getComponents() throws ApsSystemException {
		return this.getComponentManager().getCurrentComponents();
	}
	
	protected String[] extractBeanNames(Class beanClass) {
		ListableBeanFactory factory = (ListableBeanFactory) this.getBeanFactory();
		return factory.getBeanNamesForType(beanClass);
	}
	
	protected BeanFactory getBeanFactory() {
		return _beanFactory;
	}
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this._beanFactory = beanFactory;
	}
	
	protected String getProtectedBaseDiskRoot() {
		return _protectedBaseDiskRoot;
	}
	public void setProtectedBaseDiskRoot(String protBaseDiskRoot) {
		this._protectedBaseDiskRoot = protBaseDiskRoot;
	}
	
	protected Map<String, List<String>> getEntandoTableMapping() {
		return _entandoTableMapping;
	}
	public void setEntandoTableMapping(Map<String, List<String>> entandoTableMapping) {
		this._entandoTableMapping = entandoTableMapping;
	}
	
	protected IComponentManager getComponentManager() {
		return _componentManager;
	}
	public void setComponentManager(IComponentManager componentManager) {
		this._componentManager = componentManager;
	}
	
	private String _protectedBaseDiskRoot;
	
	private BeanFactory _beanFactory;
	private Map<String, List<String>> _entandoTableMapping;
	
	private IComponentManager _componentManager;
	
}