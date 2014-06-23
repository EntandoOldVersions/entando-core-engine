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
package org.entando.entando.apsadmin.filebrowser;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.apsadmin.ApsAdminBaseTestCase;

import com.opensymphony.xwork2.Action;
import java.io.ByteArrayInputStream;
import java.io.File;
import org.entando.entando.aps.system.services.storage.BasicFileAttributeView;
import org.entando.entando.aps.system.services.storage.IStorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author E.Santoboni
 */
public class TestFileBrowserAction extends ApsAdminBaseTestCase {
	
	private static final Logger _logger = LoggerFactory.getLogger(TestFileBrowserAction.class);
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.init();
	}
	
	public void testBrowseFileSystemWithUserNotAllowed() throws Throwable {
		String result = this.executeList("developersConf", null);
		assertEquals("apslogin", result);
	}
	
	public void testBrowseFileSystem_1() throws Throwable {
		String result = this.executeList("admin", null);
		assertEquals(Action.SUCCESS, result);
		FileBrowserAction action = (FileBrowserAction) super.getAction();
		BasicFileAttributeView[] fileAttributes = action.getFilesAttributes();
		assertNotNull(fileAttributes);
		boolean containsConf = false;
		boolean prevDirectory = true;
		String prevName = null;
		for (int i = 0; i < fileAttributes.length; i++) {
			BasicFileAttributeView bfav = fileAttributes[i];
			if (!prevDirectory && bfav.isDirectory()) {
				fail();
			}
			if (bfav.isDirectory() && bfav.getName().equals("conf")) {
				containsConf = true;
			}
			if ((bfav.isDirectory() == prevDirectory) && null != prevName) {
				assertTrue(bfav.getName().compareTo(prevName) > 0);
			}
			prevName = bfav.getName();
			prevDirectory = bfav.isDirectory();
		}
		assertTrue(containsConf);
	}
	
	public void testBrowseFileSystem_2() throws Throwable {
		String result = this.executeList("admin", "conf" + File.separator);
		assertEquals(Action.SUCCESS, result);
		FileBrowserAction action = (FileBrowserAction) super.getAction();
		BasicFileAttributeView[] fileAttributes = action.getFilesAttributes();
		assertEquals(2, fileAttributes.length);
		int dirCounter = 0;
		int fileCounter = 0;
		for (int i = 0; i < fileAttributes.length; i++) {
			BasicFileAttributeView bfav = fileAttributes[i];
			if (bfav.isDirectory()) {
				dirCounter++;
			} else {
				fileCounter++;
			}
		}
		assertEquals(0, dirCounter);
		assertEquals(2, fileCounter);
	}
	
	public void testValidateAddTextFile() throws Throwable {
		String path = "conf" + File.separator;
		try {
			String result = this.executeAddTextFile("developersConf", path, "filename", "css", "content");
			assertEquals("apslogin", result);
			
			result = this.executeAddTextFile("admin", path, "", "", "content");
			assertEquals(Action.INPUT, result);
			assertEquals(2, this.getAction().getFieldErrors().size());
			
			result = this.executeAddTextFile("admin", path, "filename", "", "");
			assertEquals(Action.INPUT, result);
			assertEquals(1, this.getAction().getFieldErrors().size());
			
			result = this.executeAddTextFile("admin", path, "filename", "exe", "content");
			assertEquals(Action.INPUT, result);
			assertEquals(1, this.getAction().getFieldErrors().size());
			
		} catch (Throwable t) {
			throw t;
		}
	}
	
	public void testAddTextFile() throws Throwable {
		String path = "conf" + File.separator;
		String filename = "test_filename_1";
		String extension = "css";
		String fullPath = path + filename + "." + extension;
		String text = "This is the content";
		try {
			String result = this.executeAddTextFile("admin", path, filename, extension, text);
			assertEquals(Action.SUCCESS, result);
			assertTrue(this._localStorageManager.exists(fullPath, false));
			
			result = this.executeAddTextFile("admin", path, filename, extension, text);
			assertEquals(Action.INPUT, result);
			assertEquals(1, this.getAction().getFieldErrors().size());
			assertEquals(1, this.getAction().getFieldErrors().get("filename").size());
			
			String extractedText = this._localStorageManager.readFile(fullPath, false);
			assertEquals(text, extractedText);
			this._localStorageManager.deleteFile(fullPath, false);
			assertFalse(this._localStorageManager.exists(fullPath, false));
		} catch (Throwable t) {
			this._localStorageManager.deleteFile(fullPath, false);
			throw t;
		}
	}
	
	public void testDeleteFile() throws Throwable {
		String path = "conf" + File.separator;
		String filename = "test_filename_2";
		String extension = "css";
		String fullFilename = filename + "." + extension;
		String fullPath = path + fullFilename;
		String text = "This is the content";
		try {
			assertFalse(this._localStorageManager.exists(fullPath, false));
			this._localStorageManager.saveFile(fullPath, false, new ByteArrayInputStream(text.getBytes()));
			assertTrue(this._localStorageManager.exists(fullPath, false));
			String result = this.executeDeleteFile("admin", path, fullFilename, true);
			assertEquals(Action.SUCCESS, result);
			assertFalse(this._localStorageManager.exists(fullPath, false));
		} catch (Throwable t) {
			this._localStorageManager.deleteFile(fullPath, false);
			throw t;
		}
	}
	
	private String executeList(String currentUser, String path) throws Throwable {
		this.setUserOnSession(currentUser);
		this.initAction("/do/FileBrowser", "list");
		this.addParameter("currentPath", path);
		return this.executeAction();
	}
	
	private String executeAddTextFile(String currentUser, String currentPath, 
			String filename, String extension, String content) throws Throwable {
		this.setUserOnSession(currentUser);
		this.initAction("/do/FileBrowser", "save");
		this.addParameter("currentPath", currentPath);
		this.addParameter("filename", filename);
		this.addParameter("textFileExtension", extension);
		this.addParameter("fileText", content);
		this.addParameter("strutsAction", FileBrowserAction.ADD_NEW_FILE);
		return this.executeAction();
	}
	
	private String executeDeleteFile(String currentUser, String currentPath, 
			String filename, boolean deleteFile) throws Throwable {
		this.setUserOnSession(currentUser);
		this.initAction("/do/FileBrowser", "delete");
		this.addParameter("currentPath", currentPath);
		this.addParameter("filename", filename);
		this.addParameter("deleteFile", new Boolean(deleteFile).toString());
		return this.executeAction();
	}
	
	private void init() throws Exception {
		try {
			this._localStorageManager = (IStorageManager) this.getApplicationContext().getBean(SystemConstants.STORAGE_MANAGER);
		} catch (Throwable t) {
			_logger.error("error on init", t);
		}
	}
	
	private IStorageManager _localStorageManager;
	
}
