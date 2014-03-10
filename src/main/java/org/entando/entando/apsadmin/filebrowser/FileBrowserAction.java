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

import com.agiletec.apsadmin.system.BaseAction;
import static com.agiletec.apsadmin.system.BaseAction.FAILURE;
import static com.opensymphony.xwork2.Action.INPUT;
import static com.opensymphony.xwork2.Action.SUCCESS;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.services.storage.IStorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author S.Loru
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
			File entry = this.getStorageManager().getFile(this.getCurrentPath() + this.getFilename(), false);
			if (entry.exists()) {
				if (entry.isDirectory()) {
					this.getStorageManager().deleteDirectory(this.getCurrentPath() + this.getFilename(), false);
				} else {
					this.getStorageManager().deleteFile(this.getCurrentPath() + this.getFilename(), false);
				}
			} else {
				this.addActionError(this.getText("filebrowser.error.delete.entryNotExists"));
				return INPUT;
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
			this.getStorageManager().editFile(this.getCurrentPath() + this.getFilename(), false, this.getFileText());
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
	
	public boolean isRootResource(){
		boolean path = false;
		List<File> fileList = this.getFileList();
		if(null != fileList && fileList.size() > 0){
			File file = fileList.get(0);
			path = this.getStorageManager().isFileRootResource(file, false);
		} 
		return path;
	}
	
	public String getSubpathForParent(){
		String path = "";
		List<File> fileList = this.getFileList();
		if(null != fileList && fileList.size() > 0){
			File file = fileList.get(0);
			path = this.getStorageManager().getSubPathFromFile(file, false);
		} 
		return path;
	}

	public IStorageManager getStorageManager() {
		return _storageManager;
	}

	public void setStorageManager(IStorageManager storageManager) {
		this._storageManager = storageManager;
	}

	public String getCurrentPath() {
		if (StringUtils.isBlank(_currentPath)) {
			_currentPath = "";
		} else if (!_currentPath.endsWith(File.separator)) {
			_currentPath = _currentPath + File.separator;
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

	private IStorageManager _storageManager;
	private String _currentPath;
	private List<File> _fileList;
	private String _fileText;
	private String _filename;
	private String _dirname;

	//variables for file upload
	private File _file;
	private String _uploadFileName;
	private InputStream _uploadInputStream;
}
