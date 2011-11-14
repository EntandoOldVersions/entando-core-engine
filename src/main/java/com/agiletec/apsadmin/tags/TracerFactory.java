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
package com.agiletec.apsadmin.tags;

import org.apache.struts2.components.Component;

import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.apsadmin.system.entity.attribute.AttributeTracer;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * Component del tag TracerFactoryTag.
 * Il componente si occupa della creazione e restituisce di 
 * un tracciatore valorizzato con la lingua specificata.
 * @version 1.0
 * @author S.Puddu
 */
public class TracerFactory extends Component {
	
	public TracerFactory(ValueStack stack, Lang lang) {
		super(stack);
		this._attributeTracer = new AttributeTracer();
		this._attributeTracer.setLang(lang);
	}
	
	public void setAttributeTracer(AttributeTracer attributeTracer) {
		this._attributeTracer = attributeTracer;
	}
	
	public AttributeTracer getAttributeTracer() {
		return _attributeTracer;
	}
	
	private AttributeTracer _attributeTracer;
	
}
