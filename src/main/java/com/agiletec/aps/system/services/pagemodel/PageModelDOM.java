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
package com.agiletec.aps.system.services.pagemodel;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.page.Showlet;
import com.agiletec.aps.system.services.showlettype.IShowletTypeManager;
import com.agiletec.aps.system.services.showlettype.ShowletType;
import com.agiletec.aps.util.ApsProperties;

/**
 * Classe di di supporto all'interpretazione dell'XML 
 * che rappresenta la configurazione di un modello di pagina.
 * @author E.Santoboni
 */
public class PageModelDOM {
	
	/**
	 * Costruttore della classe.
	 * @param xmlText La stringa xml da interpretare.
	 * @param showletTypeManager Il manager gestore dei tipi di showlet.
	 * @throws ApsSystemException
	 */
	public PageModelDOM(String xmlText, IShowletTypeManager showletTypeManager) throws ApsSystemException {
		this.decodeDOM(xmlText);
		this.buildFrames(showletTypeManager);
	}
	
	private void decodeDOM(String xmlText) throws ApsSystemException {
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		StringReader reader = new StringReader(xmlText);
		try {
			_doc = builder.build(reader);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "decodeDOM");
			throw new ApsSystemException("Error parsing the page model XML", t);
		}
	}
	
	private ApsProperties buildProperties(Element propertiesElement) {
		ApsProperties prop = new ApsProperties();
		List<Element> propertyElements = propertiesElement.getChildren(TAB_PROPERTY);
		Iterator<Element> propertyElementsIter = propertyElements.iterator();
		while (propertyElementsIter.hasNext()) {
			Element property = (Element) propertyElementsIter.next();
			prop.put(property.getAttributeValue(ATTRIBUTE_KEY), property.getText().trim());
		}
		return prop;
	}
	
	private void buildFrames(IShowletTypeManager showletTypeManager) throws ApsSystemException {
		List<Element> frameElements = this._doc.getRootElement().getChildren(TAB_FRAME);
		if (null != frameElements && frameElements.size() > 0) {
			int framesNumber = frameElements.size();
			_frames = new String[framesNumber];
			_defaultShowlet = new Showlet[framesNumber];
			_existMainFrame = false;
			Iterator<Element> frameElementsIter = frameElements.iterator();
			while (frameElementsIter.hasNext()) {
				Element frameElement = frameElementsIter.next();
				int pos = Integer.parseInt(frameElement.getAttributeValue(ATTRIBUTE_POS));
				if(pos >= framesNumber) {
					throw new ApsSystemException("The position 'pos' exceeds the number of frames defined in the page model");
				}
				String main = frameElement.getAttributeValue(ATTRIBUTE_MAIN);
				if (null != main && main.equals("true")) {
					_existMainFrame = true;
					_mainFrame = pos;
				}
				Element frameDescrElement = frameElement.getChild(TAB_DESCR);
				if (null != frameDescrElement) {
					_frames[pos] = frameDescrElement.getText();
				}
				Element defaultShowletElement = frameElement.getChild(TAB_DEFAULT_SHOWLET);
				if (null != defaultShowletElement) {
					this.buildDefaultShowlet(defaultShowletElement, pos, showletTypeManager);
				}
			}
		} else {
			_frames = new String[0];
			_defaultShowlet = new Showlet[0];
		}
	}
	
	private void buildDefaultShowlet(Element defaultShowletElement, int pos, IShowletTypeManager showletTypeManager) {
		Showlet showlet = new Showlet();
		String showletCode = defaultShowletElement.getAttributeValue(ATTRIBUTE_CODE);
		ShowletType type = showletTypeManager.getShowletType(showletCode);
		if (null == type) {
			throw new RuntimeException("The code of the default showlet '" + showletCode + "' unknown");
		}
		showlet.setType(type);
		Element propertiesElement = defaultShowletElement.getChild(TAB_PROPERTIES);
		if (null != propertiesElement) {
			ApsProperties prop = this.buildProperties(propertiesElement);
			showlet.setConfig(prop);
		} else {
			showlet.setConfig(new ApsProperties());
		}
		_defaultShowlet[pos] = showlet;
	}
	
	/**
	 * Restituisce l'insieme ordinato delle descrizioni dei "frames"
	 * del modello.  
	 * @return L'insieme delle descrizioni dei "frames"
	 */
	public String[] getFrames() {
		return this._frames;
	}
	
	/**
	 * La posizione del frame principale, se esiste;
	 * vale -1 se non esiste;
	 * @return La posizione del frame principale.
	 */
	public int getMainFrame() {
		if (_existMainFrame) {
			return this._mainFrame;
		} else {
			return -1;
		}
	}
	
	/**
	 * Restituisce la configurazione delle showlet di default.
	 * @return Le showlet di default.
	 */
	public Showlet[] getDefaultShowlet() {
		return this._defaultShowlet;
	}
	
	private Document _doc;
	private final String TAB_FRAME = "frame";
	private final String ATTRIBUTE_POS = "pos";
	private final String ATTRIBUTE_MAIN = "main";
	private final String TAB_DESCR = "descr";
	private final String TAB_DEFAULT_SHOWLET = "defaultShowlet";
	private final String ATTRIBUTE_CODE = "code";
	private final String TAB_PROPERTIES = "properties";
	private final String TAB_PROPERTY = "property";
	private final String ATTRIBUTE_KEY = "key";
	private boolean _existMainFrame;
	private int _mainFrame;
	private String[] _frames;
	private Showlet[] _defaultShowlet;
}
