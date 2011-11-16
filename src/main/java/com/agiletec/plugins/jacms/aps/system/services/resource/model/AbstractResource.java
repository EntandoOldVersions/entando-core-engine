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
package com.agiletec.plugins.jacms.aps.system.services.resource.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.agiletec.aps.system.services.category.Category;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.util.IResourceInstanceHelper;
import com.agiletec.plugins.jacms.aps.system.services.resource.parse.ResourceDOM;

/**
 * Classe astratta di base per gli oggetti Resource.
 * @author W.Ambu - E.Santoboni
 */
public abstract class AbstractResource implements ResourceInterface, Serializable {
	
	/**
	 * Inizializza gli elementi base costituenti la Risorsa.
	 */
    public AbstractResource() {
		this.setId("");
		this.setType("");
		this.setDescr("");
		this.setMainGroup("");
		this.setMasterFileName("");
		this._categories = new ArrayList<Category>();
		this.setFolder("");
	}
	
	/**
	 * Restituisce l'identificativo della risorsa.
	 * @return L'identificativo della risorsa.
	 */
    @Override
	public String getId() {
		return _id;
	}
	
	/**
	 * Setta l'identificativo della risorsa.
	 * @param id L'identificativo della risorsa.
	 */
    @Override
	public void setId(String id) {
		this._id = id;
	}
	
	/**
	 * Restituisce il codice del tipo di risorsa.
	 * @return Il codice del tipo di risorsa.
	 */
    @Override
	public String getType() {
		return _typeCode;
	}
    
	/**
	 * Setta il codice del tipo di risorsa.
	 * @param typeCode Il codice del tipo di risorsa.
	 */
    @Override
	public void setType(String typeCode) {
		this._typeCode = typeCode;
	}
	
	/**
	 * Restituisce la descrizione della risorsa.
	 * @return La descrizione della risorsa.
	 */
    @Override
	public String getDescr() {
		return _descr;
	}
	
	/**
	 * Setta la descrizione della risorsa.
	 * @param descr La descrizione della risorsa.
	 */
    @Override
	public void setDescr(String descr) {
		this._descr = descr;
	}
	
	/**
     * Restituisce la stringa identificante 
     * il gruppo principale di cui la risorsa è membro.
     * @return Il gruppo principale di cui la risorsa è membro.
     */
    @Override
	public String getMainGroup() {
		return _mainGroup;
	}
	
    /**
	 * Setta la stringa identificante 
     * il gruppo principale di cui il contenuto è membro.
	 * @param mainGroup Il gruppo principale di cui il contenuto è membro.
	 */
    @Override
	public void setMainGroup(String mainGroup) {
		this._mainGroup = mainGroup;
	}
    
    @Override
	public String getMasterFileName() {
		return _masterFileName;
	}
    @Override
	public void setMasterFileName(String masterFileName) {
		this._masterFileName = masterFileName;
	}
	
	/**
	 * Aggiunge una categoria alla lista delle categorie della risorsa.
	 * @param category La categoria da aggiungere.
	 */
    @Override
	public void addCategory(Category category) {
		this._categories.add(category);
	}
	
	/**
	 * Restituisce la lista di categorie associate alla risorsa.
	 * @return La lista di categorie associate alla risorsa.
	 */
    @Override
	public List<Category> getCategories() {
		return _categories;
	}
	
	/**
	 * Setta la lista di categorie associate alla risorsa.
	 * @param categories La lista di categorie associate alla risorsa.
	 */
    @Override
	public void setCategories(List<Category> categories) {
		this._categories = categories;
	}
	
	/**
	 * Rimuove una categoria alla lista delle categorie della risorsa.
	 * @param category La categoria da rimuovere.
	 */
	public void removeCategory(Category category) {
		this._categories.remove(category);
	}
	
	/**
	 * Restituisce il nome della cartella contenitore delle risorse.
	 * @return Il nome della cartella contenitore delle risorse.
	 */
	@Override
	public String getFolder() {
		return _folder;
	}
	
	/**
	 * Setta il nome della cartella contenitore delle risorse.
	 * @param folder Il nome della cartella contenitore delle risorse.
	 */
	@Override
	public void setFolder(String folder) {
		if (!folder.endsWith("/")) {
			folder += "/";
		}
		this._folder = folder;
	}
	
	/**
	 * Setta l'url base della cartella delle risorse.
	 * @param baseURL L'url base della cartella delle risorse.
	 */
	@Override
	public void setBaseURL(String baseURL) {
		if (!baseURL.endsWith("/")) {
			baseURL += "/";
		}
		this._baseURL = baseURL;
	}

	/**
	 * Restituisce l'url base della cartella delle risorse.
	 * @return L'url base della cartella delle risorse.
	 */
	protected String getBaseURL() {
		return _baseURL;
	}
	
	/**
     * Restituice il percorso base su disco della cartella delle risorse.
     * @return Il percorso base su disco della cartella delle risorse.
     */
    private String getBaseDiskRoot() {
		return _baseDiskRoot;
	}

    /**
     * Setta il percorso base su disco della cartella delle risorse.
     * @param baseDiskRoot Il percorso base su disco della cartella delle risorse.
     */
    @Override
	public void setBaseDiskRoot(String baseDiskRoot) {
		this._baseDiskRoot = baseDiskRoot;
	}
	
    private String getProtectedBaseDiskRoot() {
		return _protectedBaseDiskRoot;
	}
    
    @Override
	public void setProtectedBaseDiskRoot(String protBaseDiskRoot) {
		this._protectedBaseDiskRoot = protBaseDiskRoot;
	}
	
	/**
	 * Restituisce l'url base della cartella delle risorse pretette.
	 * @return L'url base della cartella delle risorse protette.
	 */
    protected String getProtectedBaseURL() {
		return _protectedBaseURL;
	}
    
    @Override
	public void setProtectedBaseURL(String protBaseURL) {
		this._protectedBaseURL = protBaseURL;
	}
	
    @Override
	public String[] getAllowedFileTypes() {
		return this.getAllowedExtensions().split(",");
	}
	
	/**
	 * Setta la stringa rappresentante l'insieme delle estensioni consentite separate da virgola.
	 * @return L'insieme delle estensioni consentite.
	 */
	protected String getAllowedExtensions() {
		return _allowedExtensions;
	}
	
	/**
	 * Setta la stringa rappresentante l'insieme delle estensioni consentite separate da virgola.
	 * @param allowedExtensions L'insieme delle estensioni consentite.
	 */
	public void setAllowedExtensions(String allowedExtensions) {
		this._allowedExtensions = allowedExtensions;
	}
	
	@Override
	public ResourceInterface getResourcePrototype() {
		AbstractResource prototype = null;
		try {
			Class resourceClass = Class.forName(this.getClass().getName());
			prototype = (AbstractResource) resourceClass.newInstance();
		} catch (Throwable t) {
			throw new RuntimeException("Errore in creazione prototipo " +
					"Risorsa tipo '" + this.getType() + "'", t);
		}
		prototype.setId("");
		prototype.setType(this.getType());
		prototype.setDescr("");
		prototype.setMainGroup("");
		prototype.setMasterFileName("");
		prototype.setCategories(new ArrayList<Category>());
		prototype.setFolder(this.getFolder());
		prototype.setBaseURL(this.getBaseURL());
		prototype.setBaseDiskRoot(this.getBaseDiskRoot());
		prototype.setProtectedBaseDiskRoot(this.getProtectedBaseDiskRoot());
		prototype.setProtectedBaseURL(this.getProtectedBaseURL());
		prototype.setAllowedExtensions(this.getAllowedExtensions());
		prototype.setInstanceHelper(this.getInstanceHelper());
		return prototype;
	}
	
	/**
	 * Restituisce la classe dom (necessaria per la generazione dell'xml della risorsa) 
	 * preparata con gli attributi base della risorsa.
	 * @return La classe dom preparata con gli attributi base della risorsa.
	 */
	protected ResourceDOM getResourceDOM() {
		ResourceDOM resourceDom = this.getNewResourceDOM();
    	resourceDom.setTypeCode(this.getType());
    	resourceDom.setId(this.getId());
    	resourceDom.setDescr(this.getDescr());
    	resourceDom.setMainGroup(this.getMainGroup());
    	resourceDom.setMasterFileName(this.getMasterFileName());
    	if (null != this.getCategories()) {
    		for (int i=0; i<this.getCategories().size(); i++) {
        		Category cat = (Category) this.getCategories().get(i);
        		resourceDom.addCategory(cat.getCode());
        	}
    	}
    	return resourceDom;
	}
	
	protected ResourceDOM getNewResourceDOM() {
		return new ResourceDOM();
	}
	
	/**
     * Restituisce il path assoluto su disco del folder contenitore 
     * dei file delle istanze relative alla risorsa specificata. 
     * Questo path è necessario al salvataggio o alla rimozione
     * dei file associati ad ogni istanza della risorse.
     * @return Il path assoluto su disco completo.
     */
	@Override
	public String getDiskFolder() {
    	StringBuffer diskFolder = new StringBuffer();
    	if (!Group.FREE_GROUP_NAME.equals(this.getMainGroup())) {
    		//RISORSA PROTETTA
    		diskFolder.append(this.getProtectedBaseDiskRoot());
    	} else {
    		//RISORSA LIBERA
    		diskFolder.append(this.getBaseDiskRoot());
    	}
    	String folder = this.getFolder();
    	if ((!diskFolder.toString().endsWith("\\") || !diskFolder.toString().endsWith("/")) 
    			&& (!folder.startsWith("\\") || !folder.startsWith("/"))) {
    		diskFolder.append(File.separator);
    	}
    	diskFolder.append(folder);
    	if (!Group.FREE_GROUP_NAME.equals(this.getMainGroup())) {
    		//RISORSA PROTETTA
    		diskFolder.append(this.getMainGroup() + File.separator);
    	}
    	return diskFolder.toString();
    }
    
    /**
     * Restitituisce il nome file corretto da utilizzare 
     * per i salvataggi di istanze risorse all'interno del fileSystem.
     * @param masterFileName Il nome del file principale.
     * @return Il nome file corretto.
     * @deprecated from jAPS 2.1
     */
    protected String getRevisedInstanceFileName(String masterFileName) {
		String instanceFileName = masterFileName.replaceAll("[^ _.a-zA-Z0-9]", "");
		instanceFileName = instanceFileName.trim().replace(' ', '_');
		return instanceFileName;
	}
    
    /**
	 * Restituisce il path del file relativo all'istanza.
	 * @return Il path del file relativo all'istanza.
	 */
	protected String getUrlPath(ResourceInstance instance) {
		if (null == instance) return null;
		StringBuffer urlPath = null;
		if (!Group.FREE_GROUP_NAME.equals(this.getMainGroup())) {
			//PATH di richiamo della servlet di autorizzazione
			//Sintassi /<RES_ID>/<SIZE>/<LANG_CODE>/
			String DEF = "def";
			urlPath = new StringBuffer(this.getProtectedBaseURL());
			if (!urlPath.toString().endsWith("/")) urlPath.append("/");
			urlPath.append(this.getId()).append("/");
			if (instance.getSize() < 0) {
				urlPath.append(DEF);
			} else {
				urlPath.append(instance.getSize());
			}
			urlPath.append("/");
			if (instance.getLangCode() == null) {
				urlPath.append(DEF);
			} else {
				urlPath.append(instance.getLangCode());
			}
			urlPath.append("/");
    	} else {
    		urlPath = new StringBuffer(this.getBaseURL());
    		if (!urlPath.toString().endsWith("/")) urlPath.append("/");
    		urlPath.append(this.getFolder());
    		if (!urlPath.toString().endsWith("/")) urlPath.append("/");
    		urlPath.append(instance.getFileName());
    	}
		return urlPath.toString();
	}
	
	protected IResourceInstanceHelper getInstanceHelper() {
		return _instanceHelper;
	}
	public void setInstanceHelper(IResourceInstanceHelper instanceHelper) {
		this._instanceHelper = instanceHelper;
	}
	
	private String _id;
	private String _typeCode;
	private String _descr;
	private String _mainGroup;
	private String _masterFileName;
	private List<Category> _categories;
	private String _folder;
	private String _baseURL;
	private String _baseDiskRoot;
	private String _protectedBaseDiskRoot;
	private String _protectedBaseURL;
	
	private String _allowedExtensions;
	
	private IResourceInstanceHelper _instanceHelper;
	
}