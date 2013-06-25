/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software. 
* Entando is a free software; 
* You can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.aps.system.services.cache;

/**
 * Interfaccia base per i servizi gestore cache.
 * @author E.Santoboni
 */
public interface ICacheManager {
	
	/**
	 * Flush the entire cache immediately.
	 */
	public void flushAll();
	
	/**
	 * Flushes a single cache entry.
	 * @param key The key entered by the user.
	 */
	public void flushEntry(String key);
	
	/**
	 * Flushes all items that belong to the specified group. 
	 * @param group The name of the group to flush.
	 */
	public void flushGroup(String group);
	
	/**
	 * Put an object in a cache.
	 * @param key The key entered by the user.
	 * @param obj The object to store.
	 */
	public void putInCache(String key, Object obj);
	
	/**
	 * Put an object in a cache.
	 * @param key The key entered by the user.
	 * @param obj The object to store.
	 * @param groups The groups that this object belongs to.
	 */
	public void putInCache(String key, Object obj, String[] groups);
	
	/**
	 * Get an object from the cache.
	 * @param key The key entered by the user.
	 * @return The object from cache.
	 */
	public Object getFromCache(String key);
	
	/**
	 * Get an object from the cache.
	 * @param key The key entered by the user.
	 * @param myRefreshPeriod How long the object can stay in cache in seconds.
	 * @return The object from cache.
	 */
	public Object getFromCache(String key, int myRefreshPeriod);
	
	public static final String CACHE_NAME = "Entando_Cache";
	
}
