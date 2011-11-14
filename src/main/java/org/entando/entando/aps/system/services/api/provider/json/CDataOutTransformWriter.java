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
package org.entando.entando.aps.system.services.api.provider.json;

import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.cxf.staxutils.transform.OutTransformWriter;
import org.entando.entando.aps.system.services.api.model.CDataAdapter;


/**
 * @author E.Santoboni
 */
public class CDataOutTransformWriter extends OutTransformWriter {
	
	public CDataOutTransformWriter(XMLStreamWriter writer,
			Map<String, String> outMap, Map<String, String> append,
			List<String> dropEls, boolean attributesToElements) {
		super(writer, outMap, append, dropEls, attributesToElements);
	}
	
	@Override
    public void writeCharacters(String text) throws XMLStreamException {
		if (CDataAdapter.isCdata(new String(text))) {
			String parsedCDataText = CDataAdapter.parse(new String(text));
			super.writeCharacters(parsedCDataText);
		} else {
			super.writeCharacters(text);
		}
    }
	
}