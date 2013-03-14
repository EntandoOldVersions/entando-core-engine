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
package com.agiletec.plugins.jacms.aps.system.services.resource.model.util;

import java.io.InputStream;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceDataBean;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInterface;

/**
 * Interfaccia base per le classi Helper per la gestione delle istanze delle risorse.
 * @author E.Santoboni
 */
public interface IResourceInstanceHelper {
	
	/**
     * Salva un file (ralativo ad un'istanza).
     * Metodo utilizzato da tutti i tipi di risorsa.
     * @param filePath Il path assoluto su disco dove salvare la risorsa.
     * @param bean L'oggetto detentore dei dati della risorsa da inserire.
     * @throws ApsSystemException In caso di errore nel salvataggio del file.
     */
	public void save(String filePath, ResourceDataBean bean) throws ApsSystemException;
    
	public void save(String filePath, InputStream is) throws ApsSystemException;
	
    public String getFileExtension(String fileName);
	
    /**
     * Restituisce il path assoluto su disco della cartella di destinazione della risorsa specificata.
     * La posizione su disco dipende (oltre che dai parametri base di configurazione del sistema) dal 
     * tipo di risorsa e dal suo gruppo proprietario.
     * @param resource La risorsa cui ricavare il path assoluto su disco.
     * @return Il path assoluto su disco della risorsa.
     */
	public String getResourceDiskFolder(ResourceInterface resource);
	
	public boolean delete(String filePath) throws ApsSystemException;
	
	public boolean exists(String filePath) throws ApsSystemException;
	
}