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

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import java.io.File;
import java.lang.reflect.Method;
import org.entando.entando.aps.system.init.model.Component;
import java.util.*;
import javax.sql.DataSource;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;

/**
 * @author E.Santoboni
 */
public abstract class AbstractDatabaseUtils implements BeanFactoryAware {
	
	protected IDatabaseManager.DatabaseType getType(DataSource dataSource) throws ApsSystemException {
		String typeString = null;
		try {
			String driverClassName = this.invokeGetMethod("getDriverClassName", dataSource);
			Iterator<Object> typesIter = this.getDatabaseTypeDrivers().keySet().iterator();
			while (typesIter.hasNext()) {
				String typeCode = (String) typesIter.next();
				List<String> driverClassNames = (List<String>) this.getDatabaseTypeDrivers().get(typeCode);
				if (null != driverClassNames && driverClassNames.contains(driverClassName)) {
					typeString = typeCode;
					break;
				}
			}
			if (null == typeString) {
				ApsSystemUtils.getLogger().severe("Type not recognized for Driver '" + driverClassName + "' - "
						+ "Recognized types '" + IDatabaseManager.DatabaseType.values() + "'");
				return IDatabaseManager.DatabaseType.UNKNOWN;
			}
			return Enum.valueOf(IDatabaseManager.DatabaseType.class, typeString.toUpperCase());
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().severe("Invalid type for db - '" + typeString + "' - " + t.getMessage());
			throw new ApsSystemException("Invalid type for db - '" + typeString + "'", t);
		}
	}
	
	protected String invokeGetMethod(String methodName, DataSource dataSource) throws Throwable {
		Method method = dataSource.getClass().getDeclaredMethod(methodName);
		return (String) method.invoke(dataSource);
	}
	
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
	
	protected Properties getDatabaseTypeDrivers() {
		return _databaseTypeDrivers;
	}
	public void setDatabaseTypeDrivers(Properties databaseTypeDrivers) {
		this._databaseTypeDrivers = databaseTypeDrivers;
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
	
	private Properties _databaseTypeDrivers;
	
	private IComponentManager _componentManager;
	
}