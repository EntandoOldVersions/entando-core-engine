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
package org.entando.entando.plugins.jacms.aps.system.services.api.response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.entando.entando.aps.system.services.api.model.AbstractApiResponseResult;
import org.entando.entando.aps.system.services.api.model.ListResponse;

import org.entando.entando.plugins.jacms.aps.system.services.api.model.JAXBContent;

/**
 * @author E.Santoboni
 */
@XmlRootElement(name = "response")
public class ContentsResponseResult extends AbstractApiResponseResult {
	
	@Override
	@XmlElement(name = "item", required =false)
	public JAXBContent getResult() {
		if (this.getMainResult() instanceof JAXBContent) {
			return (JAXBContent) this.getMainResult();
		}
		return null;
	}
	
	@Override
	@XmlElement(name = "items", required = false)
	public ListResponse<String> getResults() {
		if (this.getMainResult() instanceof Collection) {
			List<String> contentsId = new ArrayList<String>();
			contentsId.addAll((Collection<String>) this.getMainResult());
			ListResponse<String> entity = new ListResponse<String>(contentsId) {};
			return entity;
		}
		return null;
	}
	
}