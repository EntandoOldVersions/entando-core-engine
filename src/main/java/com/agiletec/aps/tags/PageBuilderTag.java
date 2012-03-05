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
package com.agiletec.aps.tags;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.TagSupport;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.IPage;

/**
 * Tag per l'inclusione della jsp corrispondente ad un modello di pagina.
 * Da usare esclusivamente nella main.jsp per innescare l'elaborazione dell'output.
 * E' implementato  in modo che in caso di eccezione durante l'elaborazione di una
 * delle jsp incluse non venga inviato alcun output. Ciò consente
 * la redirezione ad una pagina di errore, ma per avere garantito questo risultato
 * occorre che la main.jsp contenga esclusivamente questo tag (e nessun altro contenuto
 * nè statico nè dinamico, salvo altri tag senza output).
 * @author 
 */
public class PageBuilderTag extends TagSupport {

	/** 
	 * Include la jsp corrispondente al modello di pagina. Se si verificano eccezioni,
	 * non viene emesso alcun output e viene rilanciata un'eccezione.
	 * @throws JspException In caso di errori occorsi in questo metodo o in una delle jsp incluse.
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException{
		ServletRequest req =  this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) req.getAttribute(RequestContext.REQCTX);
		try {
			IPage page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
			String jspPath = this.getPageModelJspPath(page);
			// Utilizzo un buffer diverso
			BodyContent body = this.pageContext.pushBody();
			// l'inclusione opera sul nuovo buffer
			this.pageContext.include(jspPath);
			// ripristino il canale di output originale
			this.pageContext.popBody();
			// se è andato tutto bene, scrivo l'output sul buffer originale
			body.writeOut(this.pageContext.getOut());
		} catch (ServletException e) {
			String msg = "Error detected while including a page model";
			ApsSystemUtils.logThrowable(e, this, "doEndTag", msg);
			throw new JspException(msg, e);
		} catch (IOException e) {
			String msg = "IO error detected while including the page model";
			ApsSystemUtils.logThrowable(e, this, "doEndTag", msg);
			throw new JspException(msg, e);
		}
		return EVAL_PAGE;
	}

	/**
	 * Return the jsp path of current page model.
	 * @param page The current page.
	 * @return The jsp path of current page model.
	 */
	protected String getPageModelJspPath(IPage page) {
		String pluginCode = page.getModel().getPluginCode();
		boolean isPluginPageModel = (null != pluginCode && pluginCode.trim().length()>0);
		StringBuffer jspPath = new StringBuffer("/WEB-INF/");
		if (isPluginPageModel) {
			jspPath.append("plugins/").append(pluginCode.trim()).append("/");
		}
		jspPath.append("aps/jsp/models/").append(page.getModel().getCode()).append(".jsp");
		return jspPath.toString();
	}

}
