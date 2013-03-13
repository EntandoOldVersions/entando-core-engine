/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
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
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package org.entando.entando.aps.system.init.util;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.util.FileTextReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.entando.entando.aps.system.init.model.Component;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * @author E.Santoboni
 */
public class ComponentLoader {
	
	public ComponentLoader(String locationPatterns, Map<String, String> postProcessClasses) throws ApsSystemException {
        try {
            StringTokenizer tokenizer = new StringTokenizer(locationPatterns, ",");
            while (tokenizer.hasMoreTokens()) {
                String locationPattern = tokenizer.nextToken().trim();
                this.loadComponent(locationPattern, postProcessClasses);
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "ComponentLoader", "Error loading component definitions");
            throw new ApsSystemException("Error loading component definitions", t);
        }
    }
    
    private void loadComponent(String locationPattern, Map<String, String> postProcessClasses) throws Throwable {
		PathMatchingResourcePatternResolver resolver = 
					new PathMatchingResourcePatternResolver();
		Resource[] resources = resolver.getResources(locationPattern);
		ComponentDefDOM dom = null;
		Logger logger = ApsSystemUtils.getLogger();
		Set<String> codes = new HashSet<String>();
		for (int i = 0; i < resources.length; i++) {
            Resource resource = resources[i];
            InputStream is = null;
            String path = resource.getURL().getPath();
            try {
                is = resource.getInputStream();
                String xml = FileTextReader.getText(is);
                dom = new ComponentDefDOM(xml, path);
				Component component = dom.getComponent(postProcessClasses);
				if (null != component) {
					if (codes.add(component.getCode())) {
						logger.info("Component '" + component.getCode() + "' loaded");
						this.getComponents().add(component);
					} else {
						logger.info("Component '" + component.getCode() + "' already loaded");
					}
				}
            } catch (Throwable t) {
                ApsSystemUtils.logThrowable(t, this, "ComponentLoader", 
                        "Error loading Component definition by location Pattern '" + path + "'");
            } finally {
                if (null != is) {
                    is.close();
                }
            }
        }
    }
	
	public List<Component> getComponents() {
		return _components;
	}
    
	private List<Component> _components = new ArrayList<Component>();
	
}
