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
package com.agiletec.plugins.jacms.aps.system.services.linkresolver;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.plugins.jacms.aps.system.services.content.model.SymbolicLink;

/**
 * Interfaccia base per i Servizi gestori della risoluzione dei link sinbolici.
 * @author 
 */
public interface ILinkResolverManager {
	
	/**
	 * Sotituisce nel testo i link simbolici con URL reali.
	 * @param text Il testo che può contenere link simbolici.
	 * @param reqCtx Il contesto di richiesta
	 * @return Il testo in cui i link simbolici sono sostituiti con URL reali.
	 */
	public String resolveLinks(String text, RequestContext reqCtx);
	
	/**
	 * restituisce il link risolto sulla base del symbolic link.
	 * @param symbolicLink Il symbolic linj da risolvere.
	 * @param reqCtx Il contesto della richiesta. Può essere null. 
	 * @return Il link generato.
	 */
	public String resolveLink(SymbolicLink symbolicLink, RequestContext reqCtx);
	
}