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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author S.Loru - E.Santoboni
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
	
	public void testStorageFileList() throws Throwable {
		String[] filenames = this._localStorageManager.listFile("", false);
		assertEquals(1, filenames.length);
		assertEquals("entando_logo.jpg", filenames[0]);
		filenames = this._localStorageManager.listFile("conf" + File.separator, false);
		assertEquals(2, filenames.length);
		for (int i = 0; i < filenames.length; i++) {
			String filename = filenames[i];
			assertTrue(filename.equals("contextTestParams.properties") || filename.equals("systemParams.properties"));
		}
	}
	
	public void testStorageDirectoryList() throws Throwable {
		String[] directoryNames = this._localStorageManager.listDirectory("", false);
		assertTrue(directoryNames.length >= 1);
		List<String> list = Arrays.asList(directoryNames);
		assertTrue(list.contains("conf"));
		
		directoryNames = this._localStorageManager.listDirectory("conf" + File.separator, false);
		assertEquals(0, directoryNames.length);
	}
	/*
	public void testStorageList() {
		List<File> fileList = this._localStorageManager.fileList("", false);
		int dirCounter = 0;
		int fileCounter = 0;
		for (int i = 0; i < fileList.size(); i++) {
			File file = fileList.get(i);
			if (file.isDirectory()) {
				dirCounter++;
			} else {
				fileCounter++;
			}
		}
		assertEquals(2, fileList.size());
		assertEquals(1, dirCounter);
		assertEquals(1, fileCounter);
	}
	*/
	public void testStorageList_2() {
		List<File> fileList = this._localStorageManager.fileList("conf" + File.separator, false);
		int dirCounter = 0;
		int fileCounter = 0;
		for (int i = 0; i < fileList.size(); i++) {
			File file = fileList.get(i);
			if (file.isDirectory()) {
				dirCounter++;
			} else {
				fileCounter++;
			}
		}
		assertEquals(2, fileList.size());
		assertEquals(0, dirCounter);
		assertEquals(2, fileCounter);
	}
	
	public void testSaveEditDeleteFile() throws Throwable {
		String testFilePath = "testfolder/test.txt";
		InputStream stream = this._localStorageManager.getStream(testFilePath, false);
		assertNull(stream);
		try {
			String content = "Content of new text file";
			this._localStorageManager.saveFile(testFilePath, false, new ByteArrayInputStream(content.getBytes()));
			stream = this._localStorageManager.getStream(testFilePath, false);
			assertNotNull(stream);
			String extractedString = IOUtils.toString(stream, "UTF-8");
			assertEquals(content, extractedString);
			String newContent = "This is the new content of text file";
			this._localStorageManager.editFile(testFilePath, false, new ByteArrayInputStream(newContent.getBytes()));
			stream = this._localStorageManager.getStream(testFilePath, false);
			String extractedNewString = IOUtils.toString(stream, "UTF-8");
			assertEquals(newContent, extractedNewString);
			String readfileAfterWriteBackup = this._localStorageManager.readFile(testFilePath, false);
			assertEquals(extractedNewString, readfileAfterWriteBackup);
			this._localStorageManager.deleteFile(testFilePath, false);
			stream = this._localStorageManager.getStream(testFilePath, false);
			assertNull(stream);
		} catch (Throwable t) {
			throw t;
		} finally {
			this._localStorageManager.deleteDirectory("testfolder/", false);
			stream = this._localStorageManager.getStream(testFilePath, false);
			assertNull(stream);
		}
	}
	
	public void testCreateDeleteDir() throws Throwable {
		String directoryName = "testfolder";
		String subDirectoryName = "subfolder";
		assertFalse(this._localStorageManager.exists(directoryName, false));
		try {
			this._localStorageManager.createDirectory(directoryName + File.separator + subDirectoryName, false);
			assertTrue(this._localStorageManager.exists(directoryName, false));
			String[] listDirectory = this._localStorageManager.listDirectory(directoryName, false);
			assertEquals(1, listDirectory.length);
			listDirectory = this._localStorageManager.listDirectory(directoryName + File.separator + subDirectoryName, false);
			assertEquals(0, listDirectory.length);
		} catch (Throwable t) {
			throw t;
		} finally {
			this._localStorageManager.deleteDirectory(directoryName, false);
			assertFalse(this._localStorageManager.exists(directoryName, false));
		}
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