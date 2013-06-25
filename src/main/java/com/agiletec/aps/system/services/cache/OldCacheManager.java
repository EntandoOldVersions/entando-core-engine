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
package com.agiletec.aps.system.services.cache;

import org.entando.entando.aps.system.services.cache.CacheInfoManager;

/**
 * Manager of the System Cache
 * @author E.Santoboni
 */
public class OldCacheManager extends CacheInfoManager implements ICacheManager {
	
	@Override
	public void flushEntry(String key) {
		super.flushEntry(key);
	}
	
	@Override
	public void flushGroup(String group) {
		super.flushGroup(group);
	}
	
	@Override
	@Deprecated
	public Object getFromCache(String key, int myRefreshPeriod) {
		return super.getFromCache(key);
	}
	
	@Override
	public Object getFromCache(String key) {
		return super.getFromCache(key);
	}
	
	@Override
	public void putInCache(String key, Object obj, String[] groups) {
		super.putInCache(key, obj, groups);
	}
	
	@Override
	public void putInCacheGroups(String key, String[] groups) {
		super.putInGroup(key, groups);
	}
	
	@Override
	public void putInCache(String key, Object obj) {
		super.putInCache(key, obj);
	}
	
	@Override
	public void setExpirationTime(String key, long expiresInSeconds) {
		super.setExpirationTime(key, expiresInSeconds);
	}
	
}