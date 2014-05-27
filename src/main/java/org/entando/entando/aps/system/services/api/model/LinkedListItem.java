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
package org.entando.entando.aps.system.services.api.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author E.Santoboni
 */
@XmlRootElement(name = "item")
@XmlType(propOrder = {"code", "url"})
public class LinkedListItem implements Serializable {
	
	@XmlElement(name = "code", required = true)
	public String getCode() {
		return _code;
	}
	public void setCode(String code) {
		this._code = code;
	}
	
	@XmlElement(name = "url", required = false)
	public String getUrl() {
		return _url;
	}
	public void setUrl(String url) {
		this._url = url;
	}
	
	private String _code;
	private String _url;
	
}