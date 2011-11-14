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
package com.agiletec.aps.system.services.cache;

import com.agiletec.aps.BaseTestCase;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.cache.ICacheManager;

/**
 * Classe test del servizio gestore cache.
 * @version 1.0
 * @author E.Santoboni
 */
public class TestCacheManager extends BaseTestCase {
	
    protected void setUp() throws Exception {
    	super.setUp();
    	this.init();
    }
    
    public void testPutGetFromCache_1() {
    	String value = "Stringa prova";
    	String key = "Chiave_prova";
    	this._cacheManager.putInCache(key, value);
    	Object extracted = this._cacheManager.getFromCache(key);
    	assertEquals(value, extracted);
    	this._cacheManager.flushEntry(key);
    	extracted = this._cacheManager.getFromCache(key);
    	assertNull(extracted);
    }
    
    public void testPutGetFromCache_2() {
    	String key = "Chiave_prova";
    	Object extracted = this._cacheManager.getFromCache(key);
    	assertNull(extracted);
    	extracted = this._cacheManager.getFromCache(key);
    	assertNull(extracted);
    	
    	String value = "Stringa prova";
    	this._cacheManager.putInCache(key, value);
    	
    	extracted = this._cacheManager.getFromCache(key);
    	assertNotNull(extracted);
    	assertEquals(value, extracted);
    	this._cacheManager.flushEntry(key);
    	extracted = this._cacheManager.getFromCache(key);
    	assertNull(extracted);
    }
    
    public void testPutGetFromCacheOnRefreshPeriod() throws Throwable {
    	String value = "Stringa prova";
    	String key = "Chiave prova";
    	this._cacheManager.putInCache(key, value);
    	synchronized (this) {
    		this.wait(3000);
		}
    	Object extracted = this._cacheManager.getFromCache(key);
    	assertEquals(value, extracted);
    	extracted = this._cacheManager.getFromCache(key, 2);
    	assertNull(extracted);
    }
    
    public void testPutGetFromCacheGroup() {
    	String value = "Stringa prova";
    	String key = "Chiave prova";
    	String group1 = "group1";
    	String[] groups = {group1};
    	_cacheManager.putInCache(key, value, groups);
    	Object extracted = _cacheManager.getFromCache(key);
    	assertEquals(value, extracted);
    	_cacheManager.flushGroup(group1);
    	extracted = _cacheManager.getFromCache(key);
    	assertNull(extracted);
    }
    
    private void init() throws Exception {
    	try {
    		_cacheManager = (ICacheManager) this.getService(SystemConstants.CACHE_MANAGER);
    	} catch (Throwable t) {
    		throw new Exception(t);
        }
    }
    
    private ICacheManager _cacheManager = null;
    
}