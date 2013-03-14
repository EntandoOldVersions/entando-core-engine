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
package org.entando.entando.aps.system.init;

import org.entando.entando.aps.system.init.model.Component;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import java.util.Collections;

import java.util.List;
import java.util.Map;

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
			ComponentLoader loader = 
					new ComponentLoader(this.getLocationPatterns(), this.getPostProcessClasses());
			List<Component> components = loader.getComponents();
			Collections.sort(components);
			this.setComponents(components);
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
	
	protected Map<String, String> getPostProcessClasses() {
		return _postProcessClasses;
	}
	public void setPostProcessClasses(Map<String, String> postProcessClasses) {
		this._postProcessClasses = postProcessClasses;
	}
	
	private String _locationPatterns;
	
	private List<Component> _components;
	private Map<String, String> _postProcessClasses;
	
	public static final String DEFAULT_LOCATION_PATTERN = "classpath*:component/**/**component.xml";
}
