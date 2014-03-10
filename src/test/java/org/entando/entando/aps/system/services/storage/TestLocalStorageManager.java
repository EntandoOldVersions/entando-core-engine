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

package org.entando.entando.aps.system.services.storage;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author S.Loru
 */
public class TestLocalStorageManager extends BaseTestCase {
	
	private static final Logger _logger = LoggerFactory.getLogger(TestLocalStorageManager.class);
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.init();
	}
	
	public void testInitialize() {
		assertNotNull(this._localStorageManager);
	}
	
	public void testGetParent(){
		File file = this._localStorageManager.getFile("conf" + File.separator, false);
		assertTrue(file.exists());
		assertTrue(file.isDirectory());
		boolean isRoot = this._localStorageManager.isFileRootResource(file, false);
		assertFalse(isRoot);
		file = this._localStorageManager.getFile("" + File.separator, false);
		assertTrue(file.exists());
		assertTrue(file.isDirectory());
		isRoot = this._localStorageManager.isFileRootResource(file, false);
		assertTrue(isRoot);
	}
	
	public void testStorageList() {
		List<File> fileList = this._localStorageManager.fileList("", false);
		int dirCounter = 0;
		int fileCounter = 0;
		for (int i = 0; i < fileList.size(); i++) {
			File file = fileList.get(i);
			if(file.isDirectory()) {
				dirCounter++;
			} else {
				fileCounter++;
			}
		}
		assertEquals(3, fileList.size());
		assertEquals(1, dirCounter);
		assertEquals(2, fileCounter);
	}
	
	public void testStorageList_2() {
		List<File> fileList = this._localStorageManager.fileList("conf" + File.separator, false);
		int dirCounter = 0;
		int fileCounter = 0;
		for (int i = 0; i < fileList.size(); i++) {
			File file = fileList.get(i);
			if(file.isDirectory()) {
				dirCounter++;
			} else {
				fileCounter++;
			}
		}
		assertEquals(3, fileList.size());
		assertEquals(1, dirCounter);
		assertEquals(2, fileCounter);
	}
	
	public void testReadFile() throws ApsSystemException {
			String readFile = this._localStorageManager.readFile("test.txt", false);
			assertEquals("test_test", readFile);
	}
	
	public void testEditFile() throws ApsSystemException{
			String testtxt = "test.txt";
			String readFile = this._localStorageManager.readFile(testtxt, false);
			assertEquals("test_test", readFile);
			String backup = readFile;
			String text = "asd_test";
			this._localStorageManager.editFile(testtxt, false,  text);
			String readfileAfterWrite = this._localStorageManager.readFile(testtxt, false);
			assertEquals(text, readfileAfterWrite);
			this._localStorageManager.editFile(testtxt, false, backup);
			String readfileAfterWriteBackup = this._localStorageManager.readFile(testtxt, false);
			assertEquals(backup, readfileAfterWriteBackup);
	}
	
	public void testSaveAndDeleteFile() throws IOException, ApsSystemException {
			String testtxt = "test.txt";
			String newFiletxt = "newFile.txt";
			File newF = this._localStorageManager.getFile(newFiletxt, false);
			assertFalse(newF.exists());
			newF.createNewFile();
			assertTrue(newF.exists());
			this._localStorageManager.saveFile("test/"+newFiletxt, false, FileUtils.openInputStream(newF));
			assertTrue(this._localStorageManager.getFile("test/"+newFiletxt, false).exists());
			assertTrue(StringUtils.isBlank(this._localStorageManager.readFile(newFiletxt, false)));
			this._localStorageManager.editFile(newFiletxt, false, "TEST");
			String readFile1 = this._localStorageManager.readFile(newFiletxt, false);
			assertEquals("TEST", readFile1);
			this._localStorageManager.deleteFile("test/"+newFiletxt, false);
			assertFalse(this._localStorageManager.getFile("test/"+newFiletxt, false).exists());
			this._localStorageManager.deleteFile(newFiletxt, false);
			assertFalse(this._localStorageManager.getFile(newFiletxt, false).exists());
			this._localStorageManager.deleteDirectory("test", false);
			assertFalse(this._localStorageManager.getFile("test", false).exists());
	}
	
	
	public void testCreateDeleteDir() throws ApsSystemException {
		String[] listDirectory = this._localStorageManager.listDirectory("", false);
		assertEquals(1, listDirectory.length);
		this._localStorageManager.createDirectory("test_test", false);
		listDirectory = this._localStorageManager.listDirectory(null, false);
		assertEquals(2, listDirectory.length);
		this._localStorageManager.deleteDirectory("test_test", false);
		listDirectory = this._localStorageManager.listDirectory(null, false);
		assertEquals(1, listDirectory.length);
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
