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

import java.util.List;

import org.entando.entando.aps.system.init.util.ComponentLoader;

/**
 * @author E.Santoboni
 */
public class ComponentManager implements IComponentManager {
	
	public void init() throws Exception {
		this.loadComponents();
		ApsSystemUtils.getLogger().config(this.getClass().getName() + ": initializated");
	}
	
    protected void loadComponents() throws ApsSystemException {
        try {
			ComponentLoader loader = new ComponentLoader(this.getLocationPatterns());
			this.setComponents(loader.getComponents());
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "loadComponents", "Error loading components definitions");
            throw new ApsSystemException("Error loading components definitions", t);
        }
    }
    
	@Override
	public List<Component> getCurrentComponents() throws ApsSystemException {
		return this.getComponents();
	}
	
    protected String getLocationPatterns() {
        if (null == this._locationPatterns) {
            return DEFAULT_LOCATION_PATTERN;
        }
        return _locationPatterns;
    }
    public void setLocationPatterns(String locationPatterns) {
        this._locationPatterns = locationPatterns;
    }
	
	protected List<Component> getComponents() {
		return _components;
	}
	protected void setComponents(List<Component> components) {
		this._components = components;
	}
	
	private String _locationPatterns;
	
	private List<Component> _components;
	
	public static final String DEFAULT_LOCATION_PATTERN = "classpath*:component/**/**component.xml";
}
