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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang.math.Fraction;

/**
 * Representation of a page template. 
 * This object contains the description and the definition of "frames" available. 
 * The definition of the page model is in the form of jsp or freemarker template. 
 * In the case of representation on jsp, the file name is equals then the model code.
 * The "frames" are page sections that can contains a "widget".
 * @author M.Diana
 */
public class PageModel implements Serializable {
	
	/**
	 * Return the code of page model.
	 * @return The code of page model.
	 */
	public String getCode() {
		return _code;
	}
	
	/**
	 * Set the code of page model.
	 * @param code The code to set
	 */
	public void setCode(String code) {
		this._code = code;
	}
	
	/**
	 * Return the description of page model.
	 * @return The description of page model.
	 */
	public String getDescription() {
		return _description;
	}
	
	/**
	 * Set the description of page model.
	 * @param description The description to set
	 */
	public void setDescription(String description) {
		this._description = description;
	}
	
	/**
	 * Return the description of page model.
	 * @return The description of page model.
	 * @deprecated use getDescription()
	 */
	public String getDescr() {
		return this.getDescription();
	}
	
	/**
	 * Set the description of page model.
	 * @param descr The code to set
	 * @deprecated use setDescription(String)
	 */
	public void setDescr(String descr) {
		this.setDescription(descr);
	}
	
	/**
	 * Restituisce l'insieme ordinato delle descrizioni dei "frames" del modello.
	 * @return L'insieme delle descrizioni dei "frames"
	 */
	public String[] getFrames() {
		Frame[] configuration = this.getConfiguration();
		String[] descriptions = new String[configuration.length];
		for (int i = 0; i < configuration.length; i++) {
			Frame frame = configuration[i];
			descriptions[i] = frame.getDescription();
		}
		return descriptions;
	}
	
	/*
	 * Imposta l'insieme ordinato delle descrizioni dei "frames" del modello. 
	 * @param frames L'insieme delle descrizioni dei "frames"
	 */
	//public void setFrames(String[] frames) {
	//	this._frames = frames;
	//}

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
	
	/*
	 * @deprecated Use {@link #getDefaultWidget()} instead
	 */
	//public Widget[] getDefaultShowlet() {
	//	return this.getDefaultWidget();
	//}
	
	/**
	 * Restituisce la configurazione dei widget di default.
	 * @return I widget di default.
	 */
	public Widget[] getDefaultWidget() {
		Frame[] configuration = this.getConfiguration();
		Widget[] defaultWidgets = new Widget[configuration.length];
		for (int i = 0; i < configuration.length; i++) {
			Frame frame = configuration[i];
			defaultWidgets[i] = frame.getDefaultWidget();
		}
		return defaultWidgets;
	}
	
	/*
	 * @deprecated Use {@link #setDefaultWidget(Widget[])} instead
	 */
	//public void setDefaultShowlet(Widget[] defaultShowlet) {
	//	this.setDefaultWidget(defaultShowlet);
	//}
	
	/*
	 * Setta la configurazione dei widget di default.
	 * @param defaultWidget I widget di default.
	 */
	//public void setDefaultWidget(Widget[] defaultWidget) {
	//	this._defaultWidget = defaultWidget;
	//}
	
	public Frame[] getConfiguration() {
		return _configuration;
	}
	
	public void setConfiguration(Frame[] configuration) {
		this._configuration = configuration;
	}
	
	/**
	 * Return the code of the plugin owner of page model.
	 * The field is null if the page model belong to Entando Core.
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
	
	public String getTemplate() {
		return _template;
	}
	public void setTemplate(String template) {
		this._template = template;
	}
	
	/**
	 * Il codice del modello di pagina
	 */
	private String _code;
	
	/**
	 * La descrizione del modello di pagina
	 */
	private String _description;
	
	/*
	 * L'insieme delle descrizioni dei frames.
	 */
	//private String[] _frames = new String[0];
	
	private Frame[] _configuration;
	
	/**
	 * La posizione del frame principale, se esiste;
	 * vale -1 se non esiste;
	 */
	private int _mainFrame = -1;
	
	/*
	 * L'insieme dei widget di default.
	 */
	//private Widget[] _defaultWidget;
	
	/**
	 * The code of the plugin owner of page model.
	 */
	private String _pluginCode;
	
	private String _template;
	
}
