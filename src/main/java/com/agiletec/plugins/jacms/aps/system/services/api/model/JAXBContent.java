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
package com.agiletec.plugins.jacms.aps.system.services.api.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.agiletec.aps.system.common.entity.model.JAXBEntity;
import com.agiletec.aps.system.common.entity.model.attribute.DefaultJAXBAttribute;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.SymbolicLink;
import com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute.JAXBLinkValue;
import com.agiletec.plugins.jacms.aps.system.services.content.model.extraAttribute.JAXBResourceValue;

/**
 * @author E.Santoboni
 */
@XmlRootElement(name = "content")
@XmlType(propOrder = {"id", "descr", "typeCode", "typeDescr", "mainGroup", 
		"categories", "groups", "created", "lastModified", "version", "lastEditor", "attributes"})
@XmlSeeAlso({ArrayList.class, JAXBResourceValue.class, JAXBLinkValue.class, SymbolicLink.class})
public class JAXBContent extends JAXBEntity {
	
	public JAXBContent() {
		super();
	}
	
	public JAXBContent(Content mainContent, String langCode) {
		super(mainContent, langCode);
		this.setCreated(mainContent.getCreated());
		this.setLastModified(mainContent.getLastModified());
		this.setVersion(mainContent.getVersion());
		this.setLastEditor(mainContent.getLastEditor());
	}
	
	/**
	 * Return the ID of the Entity.
	 * @return The identification string of the Entity.
	 */
	@XmlElement(name = "id", required = true)
	public String getId() {
		return super.getId();
	}
	
	/**
	 * Return the code of the Entity Type.
	 * @return The code of the Entity Type.
	 */
	@XmlElement(name = "typeCode", required = true)
	public String getTypeCode() {
		return super.getTypeCode();
	}
	
	/**
	 * Return the description of the Content Type.
	 * @return The description of the Content Type.
	 */
	@XmlElement(name = "typeDescription", required = true)
	public String getTypeDescr() {
		return super.getTypeDescr();
	}
	
	/**
	 * Return the description of the Entity.
	 * @return The Entity description.
	 */
	@XmlElement(name = "description", required = true)
	public String getDescr() {
		return super.getDescr();
	}
	
	/**
	 * Return the string that identifies the main group this Entity belongs to.
	 * @return The main group this Entity belongs to.
	 */
	@XmlElement(name = "mainGroup", required = true)
	public String getMainGroup() {
		return super.getMainGroup();
	}
	
	/**
	 * Return the set of codes of the additional groups. 
	 * @return The set of codes belonging to the additional groups. 
	 */
	@XmlElement(name = "group", required = true)
	@XmlElementWrapper(name = "groups")  
	public Set<String> getGroups() {
		return super.getGroups();
	}
	
	/**
	 * Return the set of codes of the additional categories. 
	 * @return The set of codes belonging to the additional categories. 
	 */
	@XmlElement(name = "category", required = true)
	@XmlElementWrapper(name = "categories")  
	public Set<String> getCategories() {
		return super.getCategories();
	}
	
	@XmlElement(name = "created", required = true)
	public Date getCreated() {
		return _created;
	}
	public void setCreated(Date created) {
		this._created = created;
	}
	
	@XmlElement(name = "lastModified", required = true)
	public Date getLastModified() {
		return _lastModified;
	}
	public void setLastModified(Date lastModified) {
		this._lastModified = lastModified;
	}
	
	@XmlElement(name = "version", required = true)
	public String getVersion() {
		return _version;
	}
	public void setVersion(String version) {
		this._version = version;
	}
	
	@XmlElement(name = "lastEditor", required = true)
	public String getLastEditor() {
		return _lastEditor;
	}
	public void setLastEditor(String lastEditor) {
		this._lastEditor = lastEditor;
	}
	
	@XmlElement(name = "attribute", required = true)
	@XmlElementWrapper(name = "attributes")
	public List<DefaultJAXBAttribute> getAttributes() {
		return super.getAttributes();
	}
	
	private Date _created;
	private Date _lastModified;
	private String _version;
	private String _lastEditor;
	
}