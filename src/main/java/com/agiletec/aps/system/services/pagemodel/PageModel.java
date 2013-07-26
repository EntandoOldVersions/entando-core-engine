/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software. 
* Entando is a free software; 
* You can redistribute it and/or modify it
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
package com.agiletec.aps.system.services.pagemodel;

import java.io.Serializable;

import com.agiletec.aps.system.services.page.Widget;

/**
 * Rappresentazione di un modello di pagina. Questo oggetto contiene
 * solo la descrizione e la definizione dei "frames" disponibili. La
 * vera definizione del modello è in forma di jsp. Si assume che la jsp
 * associata abbia nome identico al codice del modello. I "frames" sono
 * le porzioni di pagina che possono ospitare una "showlet".
 * @author
 */
public class PageModel implements Serializable {
	
	/**
	 * Restituisce il codice univoco del modello
	 * @return Il codice
	 */
	public String getCode() {
		return _code;
	}

	/**
	 * Imposta il codice univoco del modello
	 * @param code Il codice da impostare
	 */
	public void setCode(String code) {
		this._code = code;
	}

	/**
	 * Restituisce la descrizione del modello
	 * @return La descrizione
	 */
	public String getDescr() {
		return _descr;
	}

	/**
	 * Imposta la descrizione del modello
	 * @param descr La descrizione da impostare
	 */
	public void setDescr(String descr) {
		this._descr = descr;
	}

	/**
	 * Restituisce l'insieme ordinato delle descrizioni dei "frames"
	 * del modello.
	 * @return L'insieme delle descrizioni dei "frames"
	 */
	public String[] getFrames() {
		return _frames;
	}

	/**
	 * Imposta l'insieme ordinato delle descrizioni dei "frames"
	 * del modello. 
	 * @param frames L'insieme delle descrizioni dei "frames"
	 */
	public void setFrames(String[] frames) {
		this._frames = frames;
	}

	/**
	 * Restituisce il numero relativo del mainFrame.
	 * @return Il numero relativo del mainFrame.
	 */
	public int getMainFrame() {
		return _mainFrame;
	}

	/**
	 * Setta il numero relativo del mainFrame.
	 * @param mainFrame Il numero relativo del mainFrame.
	 */
	public void setMainFrame(int mainFrame) {
		this._mainFrame = mainFrame;
	}
	
	/**
	 * Restituisce la configurazione delle showlet di default.
	 * @return Le showlet di default.
	 */
	public Widget[] getDefaultShowlet() {
		return _defaultShowlet;
	}

	/**
	 * Setta la configurazione delle showlet di default.
	 * @param defaultShowlet Le showlet di default.
	 */
	public void setDefaultShowlet(Widget[] defaultShowlet) {
		this._defaultShowlet = defaultShowlet;
	}
	
	/**
	 * Return the code of the plugin owner of page model.
	 * The field is null if the page model belong to jAPS Core.
	 * @return The plugin code.
	 */
	public String getPluginCode() {
		return _pluginCode;
	}
	
	/**
	 * Set the code of the plugin owner of page model.
	 * @param pluginCode The plugin code. 
	 */
	public void setPluginCode(String pluginCode) {
		this._pluginCode = pluginCode;
	}
	
	/**
	 * Il codice del modello di pagina
	 */
	private String _code;
	
	/**
	 * La descrizione del modello di pagina
	 */
	private String _descr;
	
	/**
	 * L'insieme delle descrizioni dei frames.
	 */
	private String[] _frames = new String[0];
	
	/**
	 * La posizione del frame principale, se esiste;
	 * vale -1 se non esiste;
	 */
	private int _mainFrame = -1;
	
	/**
	 * L'insieme delle showlet di default.
	 */
	private Widget[] _defaultShowlet;
	
	/**
	 * The code of the plugin owner of page model.
	 */
	private String _pluginCode;
	
}
