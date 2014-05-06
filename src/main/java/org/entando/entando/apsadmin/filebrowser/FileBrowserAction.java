/*
 *
 * Copyright 2014 Entando S.r.l. (http://www.entando.com) All rights reserved.
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
 * Copyright 2014 Entando S.r.l. (http://www.entando.com) All rights reserved.
 *
 */
package org.entando.entando.apsadmin.filebrowser;

import com.agiletec.aps.util.SelectItem;
import com.agiletec.apsadmin.system.BaseAction;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.services.storage.IStorageManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author S.Loru - E.Santoboni
 */
public class FileBrowserAction extends BaseAction implements IFileBrowserAction {

	private static final Logger _logger = LoggerFactory.getLogger(FileBrowserAction.class);

	@Override
	public String edit() {
		String text;
		try {
			text = this.getStorageManager().readFile(this.getCurrentPath() + this.getFilename(), false);
			this.setFileText(text);
		} catch (Throwable t) {
			_logger.error("error editing file, fullPath: {}", this.getCurrentPath(), t);
		}
		return SUCCESS;
	}
	
	@Override
	public String list() {
		List<File> fileList = this.getStorageManager().fileList(this.getCurrentPath(), false);
		this.setFileList(fileList);
		return SUCCESS;
	}

	@Override
	public String upload() {
		try {
			this.getStorageManager().saveFile(this.getCurrentPath() + this.getUploadFileName(), false, this.getInputStream());
		} catch (Throwable t) {
			_logger.error("error in upload", t);
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String delete() {
		try {
			if (null == this.isDeleteFile()) {
				this.addActionError(this.getText("filebrowser.error.delete.missingInformation"));
				return INPUT;
			}
			String subPath = this.getCurrentPath() + this.getFilename();
			if (this.isDeleteFile()) {
				this.getStorageManager().deleteFile(subPath, false);
			} else {
				this.getStorageManager().deleteDirectory(subPath, false);
			}
		} catch (Throwable t) {
			_logger.error("error in delete", t);
			return FAILURE;
		}
		return SUCCESS;
	}

	@Override
	public String save() {
		try {
			InputStream stream = new ByteArrayInputStream(this.getFileText().getBytes());
			this.getStorageManager().editFile(this.getCurrentPath() + this.getFilename(), false, stream);
		} catch (Throwable t) {
			_logger.error("error saving file, fullPath: {} text:  {}", this.getCurrentPath(), this.getFileText(), t);
			return FAILURE;
		}
		return SUCCESS;
	}

	@Override
	public String createDir() {
		try {
			this.getStorageManager().createDirectory(this.getCurrentPath() + this.getDirname(), false);
		} catch (Throwable t) {
			_logger.error("error creating dir, fullPath: {} text:  {}", this.getCurrentPath(), this.getDirname(), t);
			return FAILURE;
		}
		return SUCCESS;
	}
	/*
	public String[] getDirectoryNames() {
		try {
			return this.getStorageManager().listDirectory(this.getCurrentPath(), true);
		} catch (Throwable t) {
			_logger.error("error listing directories, fullPath: {}", this.getCurrentPath(), t);
			return new String[0];
		}
	}
	
	public String[] getFileNames() {
		try {
			return this.getStorageManager().listFile(this.getCurrentPath(), true);
		} catch (Throwable t) {
			_logger.error("error listing files, fullPath: {}", this.getCurrentPath(), t);
			return new String[0];
		}
	}
	*/
	
	public List<SelectItem> getBreadCrumbsTargets() {
		String currentPath = this.getCurrentPath();
		if (StringUtils.isEmpty(currentPath)) {
			return null;
		}
		List<SelectItem> items = new ArrayList<SelectItem>();
		String[] folders = currentPath.split(File.separator);
		for (int i = 0; i < folders.length; i++) {
			String folderName = folders[i];
			String subpath = null;
			if (i == 0) {
				subpath = folderName + File.separator;
			} else if (i == (folders.length-1)) {
				subpath = currentPath;
			} else {
				int index = currentPath.indexOf(folderName) + folderName.length();
				subpath = currentPath.substring(0, index) + File.separator;
			}
			items.add(new SelectItem(subpath, folderName));
		}
		return items;
	}
	
	public String getCurrentPath() {
		if (StringUtils.isBlank(_currentPath)) {
			_currentPath = "";
		} else if (!_currentPath.endsWith(File.separator)) {
			_currentPath = _currentPath + File.separator;
		}
		if (this._currentPath.contains("../") 
				|| this._currentPath.contains("%2e%2e%2f") 
				|| this._currentPath.contains("..%2f") 
				|| this._currentPath.contains(".."+File.separator) 
				|| this._currentPath.contains("%2e%2e/") 
				|| this._currentPath.contains("%2e%2e"+File.separator)) {
			_logger.info("Attack avoided - requested path {}", this._currentPath);
			_currentPath = "";
		}
		return _currentPath;
	}
	
	public void setCurrentPath(String currentPath) {
		this._currentPath = currentPath;
	}
	
	public List<File> getFileList() {
		return _fileList;
	}
	public void setFileList(List<File> fileList) {
		this._fileList = fileList;
	}
	
	public String getFileText() {
		return _fileText;
	}
	public void setFileText(String fileText) {
		fileText = (null != fileText) ? fileText : "";
		this._fileText = fileText;
	}

	public void setUpload(File file) {
		this._file = file;
	}
	public File getUpload() {
		return this._file;
	}

	public String getUploadFileName() {
		return _uploadFileName;
	}
	public void setUploadFileName(String uploadFileName) {
		this._uploadFileName = uploadFileName;
	}

	public InputStream getUploadInputStream() {
		return _uploadInputStream;
	}
	public void setUploadInputStream(InputStream uploadInputStream) {
		this._uploadInputStream = uploadInputStream;
	}

	public int getFileSize() {
		return (int) this._file.length() / 1000;
	}

	public File getFile() {
		return _file;
	}

	public InputStream getInputStream() throws Throwable {
		if (null == this.getFile()) {
			return null;
		}
		return new FileInputStream(this.getFile());
	}

	public String getFilename() {
		if (StringUtils.isBlank(_filename)) {
			_filename = "";
		}
		return _filename;
	}
	public void setFilename(String filename) {
		this._filename = filename;
	}

	public String getDirname() {
		return _dirname;
	}
	public void setDirname(String dirname) {
		this._dirname = dirname;
	}
	
	public Boolean isDeleteFile() {
		return _deleteFile;
	}
	public void setDeleteFile(Boolean deleteFile) {
		this._deleteFile = deleteFile;
	}
	
	protected IStorageManager getStorageManager() {
		return _storageManager;
	}
	public void setStorageManager(IStorageManager storageManager) {
		this._storageManager = storageManager;
	}
	
	private String _currentPath;
	private List<File> _fileList;
	private String _fileText;
	private String _filename;
	private String _dirname;
	private Boolean _deleteFile;
	
	//variables for file upload
	private File _file;
	private String _uploadFileName;
	private InputStream _uploadInputStream;
	
	private IStorageManager _storageManager;
	
}
