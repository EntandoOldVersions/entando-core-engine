/*
*
* Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
* This file is part of jAPS software.
* jAPS is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
*/
package com.agiletec.aps.system.common.entity;

import java.io.InputStream;
import java.io.Serializable;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.util.FileTextReader;

/**
 * @author E.Santoboni
 */
public abstract class AbstractExtraAttributeSupportObject implements ServletContextAware, Serializable {
	
	/**
	 * Extract the xml with the definition of Attribute support object.
	 * The xml will be extracted from the file path in the instance param.
	 * @return The xml with the definition.
	 * @throws Exception In case of error
	 */
	protected String extractXml() throws Exception {
		String xml = null;
		InputStream is = null;
		try {
			is = this._servletContext.getResourceAsStream(this.getDefsFilePath());
			if (null == is) {
				ApsSystemUtils.getLogger().severe("Null Input Stream - Definition file path " + this.getDefsFilePath());
				return null;
			}
			xml = FileTextReader.getText(is);
		} catch (Throwable t) {
			String message = "Error detected while extracting extra Attribute Objects : file " + this.getDefsFilePath();
			ApsSystemUtils.logThrowable(t, this, "extractXml", message);
		} finally {
			if (null != is) {
				is.close();
			}
		}
		return xml;
	}
	
	protected IEntityManager getEntityManagerDest() {
		return _entityManagerDest;
	}
	public void setEntityManagerDest(IEntityManager entityManagerDest) {
		this._entityManagerDest = entityManagerDest;
	}
	
	protected String getDefsFilePath() {
		return _defsFilePath;
	}
	public void setDefsFilePath(String defsFilePath) {
		this._defsFilePath = defsFilePath;
	}
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		this._servletContext = servletContext;
	}
	
	private IEntityManager _entityManagerDest;
	private String _defsFilePath;
	
	private ServletContext _servletContext;
	
}