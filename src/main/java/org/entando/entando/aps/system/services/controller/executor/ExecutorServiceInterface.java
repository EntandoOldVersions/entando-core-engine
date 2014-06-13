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
package org.entando.entando.aps.system.services.controller.executor;

import java.io.Serializable;

import org.springframework.beans.factory.InitializingBean;

import com.agiletec.aps.system.RequestContext;

/**
 * @author M.Diana - E.Santoboni
 */
public interface ExecutorServiceInterface extends InitializingBean, Serializable {
	
	/**
	 * Esegue le operazioni specifiche del sottoservizio.
	 * @param reqCtx Il contesto di richiesta.
	 */
	public void service(RequestContext reqCtx);

}
