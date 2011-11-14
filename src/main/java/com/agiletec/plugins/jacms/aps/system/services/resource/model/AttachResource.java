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
package com.agiletec.plugins.jacms.aps.system.services.resource.model;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * Classe rappresentante una risorsa Attach.
 * @author W.Ambu - E.Santoboni
 */
public class AttachResource extends AbstractMonoInstanceResource  {
	
    /**
     * Restituisce il path della risorsa attach.
     * La stringa restituita è comprensiva del folder della risorsa e 
     * del nome del file dell'istanza richiesta.
     * @return Il path della risorsa attach.
     */
    public String getAttachPath() {
    	ResourceInstance instance = this.getInstance();
    	String path = this.getUrlPath(instance);
    	return path;
    }
    
    /**
     * Restituisce il path della risorsa attach.
     * La stringa restituita è comprensiva del folder della risorsa e 
     * del nome del file dell'istanza richiesta.
     * @return Il path della risorsa attach.
     * @deprecated use getAttachPath
     */
    public String getDocumentPath() {
    	return this.getAttachPath();
    }
    
    @Override
	public void saveResourceInstances(ResourceDataBean bean) throws ApsSystemException {
		try {
			String fileName = this.getInstanceFileName(bean.getFileName());
			String baseDiskFolder = this.getInstanceHelper().getResourceDiskFolder(this);
			String filePath = baseDiskFolder + fileName;
			this.getInstanceHelper().save(filePath, bean.getInputStream());
			ResourceInstance instance = new ResourceInstance();
			instance.setSize(0);
			instance.setFileName(fileName);
			String mimeType = bean.getMimeType();
			instance.setMimeType(mimeType);
			instance.setFileLength(bean.getFileSize() + " Kb");
			this.addInstance(instance);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "saveResourceInstances");
			throw new ApsSystemException("Error on saving attach resource instances", t);
		}
	}
    
    @Override
	public void reloadResourceInstances() throws ApsSystemException {
		//Not supported
	}
    
}