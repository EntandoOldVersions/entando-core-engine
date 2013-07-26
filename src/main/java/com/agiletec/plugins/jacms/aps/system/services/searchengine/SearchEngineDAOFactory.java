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
package com.agiletec.plugins.jacms.aps.system.services.searchengine;

import java.io.File;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;

/**
 * Classe factory degli elementi ad uso del SearchEngine.
 * @author E.Santoboni
 */
public class SearchEngineDAOFactory implements ISearchEngineDAOFactory/*, BeanFactoryAware*/ {
	
	@Override
	public void init() throws Exception {
		this._subDirectory = this.getConfigManager().getConfigItem(JacmsSystemConstants.CONFIG_ITEM_CONTENT_INDEX_SUB_DIR);
		if (_subDirectory == null) {
			throw new ApsSystemException("Item configurazione assente: " + JacmsSystemConstants.CONFIG_ITEM_CONTENT_INDEX_SUB_DIR);
		}
	}
	
	@Override
	public IIndexerDAO getIndexer(boolean newIndex) throws ApsSystemException {
		return this.getIndexer(newIndex, this._subDirectory);
	}
	
	@Override
	public ISearcherDAO getSearcher() throws ApsSystemException {
		return this.getSearcher(this._subDirectory);
	}
	
	@Override
	public IIndexerDAO getIndexer(boolean newIndex, String subDir) throws ApsSystemException {
		IIndexerDAO indexerDao = null;
		try {
			Class indexerClass = Class.forName(this.getIndexerClassName());
            indexerDao = (IIndexerDAO) indexerClass.newInstance();
			indexerDao.setLangManager(this.getLangManager());
			indexerDao.init(this.getDirectory(subDir), newIndex);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getIndexer", "Error creating new indexer");
			throw new ApsSystemException("Error creating new indexer", t);
		}
		return indexerDao;
	}
	
	@Override
	public ISearcherDAO getSearcher(String subDir) throws ApsSystemException {
		ISearcherDAO searcherDao = null;
		try {
			Class searcherClass = Class.forName(this.getSearcherClassName());
            searcherDao = (ISearcherDAO) searcherClass.newInstance();
			searcherDao.init(this.getDirectory(subDir));
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getSearcher", "Error creating new searcher");
			throw new ApsSystemException("Error creating new searcher", t);
		}
		return searcherDao;
	}
	
	@Override
	public void updateSubDir(String newSubDirectory) throws ApsSystemException {
		this.getConfigManager().updateConfigItem(JacmsSystemConstants.CONFIG_ITEM_CONTENT_INDEX_SUB_DIR, newSubDirectory);
		String oldDir = _subDirectory;
		this._subDirectory = newSubDirectory;
		this.deleteSubDirectory(oldDir);
	}
	
	private File getDirectory(String subDirectory) throws ApsSystemException {
		String dirName = this.getIndexDiskRootFolder();
		if (!dirName.endsWith("/")) {
			dirName += "/";
		}
		dirName += subDirectory;
		ApsSystemUtils.getLogger().config("Index Directory: " + dirName);
		File dir = new File(dirName);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
			ApsSystemUtils.getLogger().config("Index Directory created");
		}
		if (!dir.canRead() || !dir.canWrite()) {
			throw new ApsSystemException(dirName + " does not have r/w rights");
		}
		return dir;
	}
    
	@Override
	public void deleteSubDirectory(String subDirectory) {
		String dirName = this.getIndexDiskRootFolder();
		if (!dirName.endsWith("/") || !dirName.endsWith(File.separator)) {
			dirName += File.separator;
		}
		dirName += subDirectory;
		File dir = new File(dirName);
		if (dir.exists() || dir.isDirectory()) {
			String[] filesName = dir.list();
			for (int i=0; i<filesName.length; i++) {
				File fileToDelete = new File(dirName + File.separator + filesName[i]);
				fileToDelete.delete();
			}
			dir.delete();
			ApsSystemUtils.getLogger().config("Deleted subfolder " + subDirectory);
		}
	}
	
	public String getIndexerClassName() {
		return _indexerClassName;
	}
	public void setIndexerClassName(String indexerClassName) {
		this._indexerClassName = indexerClassName;
	}
	
	public String getSearcherClassName() {
		return _searcherClassName;
	}
	public void setSearcherClassName(String searcherClassName) {
		this._searcherClassName = searcherClassName;
	}
	
	protected String getIndexDiskRootFolder() {
		return _indexDiskRootFolder;
	}
	public void setIndexDiskRootFolder(String indexDiskRootFolder) {
		this._indexDiskRootFolder = indexDiskRootFolder;
	}
	
	protected ConfigInterface getConfigManager() {
		return _configManager;
	}
	public void setConfigManager(ConfigInterface configService) {
		this._configManager = configService;
	}
	
	protected ILangManager getLangManager() {
		return _langManager;
	}
	public void setLangManager(ILangManager langManager) {
		this._langManager = langManager;
	}
	
	private String _indexerClassName;
	private String _searcherClassName;
	
	private String _indexDiskRootFolder;
	private String _subDirectory;
	
	private ConfigInterface _configManager;
	private ILangManager _langManager;
	
}
