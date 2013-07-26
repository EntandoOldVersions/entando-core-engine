/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
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
package org.entando.entando.aps.system.services.cache;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.events.PageChangedEvent;
import com.agiletec.aps.system.services.page.events.PageChangedObserver;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang.time.DateUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.expression.EvaluationContext;

/**
 * Manager of the System Cache
 * @author E.Santoboni
 */
@Aspect
public class CacheInfoManager extends AbstractService implements ICacheInfoManager, PageChangedObserver {
	
	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().config(this.getClass().getName() + ": cache info service initialized");
	}
	
	@Around("@annotation(cacheableInfo)")
	public Object aroundCacheableMethod(ProceedingJoinPoint pjp, CacheableInfo cacheableInfo) throws Throwable {
		Object result = pjp.proceed();
		if (null == result) {
			return result;
		}
		if (cacheableInfo.expiresInMinute() < 0 && (cacheableInfo.groups() == null || cacheableInfo.groups().trim().length() == 0)) {
			return result;
		}
		try {
			MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
			Method targetMethod = methodSignature.getMethod();
			Class targetClass = pjp.getTarget().getClass();
			Method effectiveTargetMethod = targetClass.getDeclaredMethod(targetMethod.getName(), targetMethod.getParameterTypes());
			Cacheable cacheable = effectiveTargetMethod.getAnnotation(Cacheable.class);
			if (null == cacheable) {
				return result;
			}
			Object key = this.evaluateExpression(cacheable.key().toString(), targetMethod, pjp.getArgs(), effectiveTargetMethod, targetClass);
			if (cacheableInfo.groups() != null && cacheableInfo.groups().trim().length() > 0) {
				Object groupsCsv = this.evaluateExpression(cacheableInfo.groups().toString(), targetMethod, pjp.getArgs(), effectiveTargetMethod, targetClass);
				if (null != groupsCsv && groupsCsv.toString().trim().length() > 0) {
					String[] groups = groupsCsv.toString().split(",");
					this.putInGroup(key.toString(), groups);
				}
			}
			if (cacheableInfo.expiresInMinute() > 0) {
				this.setExpirationTime(key.toString(), cacheableInfo.expiresInMinute());
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "aroundCacheableMethod", "Error while evaluating cacheableInfo annotation");
			throw new ApsSystemException("Error while evaluating cacheableInfo annotation", t);
		}
		return result;
	}
	
	@Around("@annotation(cacheInfoEvict)")
	public Object aroundCacheInfoEvictMethod(ProceedingJoinPoint pjp, CacheInfoEvict cacheInfoEvict) throws Throwable {
		try {
			MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
			Method targetMethod = methodSignature.getMethod();
			Class targetClass = pjp.getTarget().getClass();
			Method effectiveTargetMethod = targetClass.getDeclaredMethod(targetMethod.getName(), targetMethod.getParameterTypes());
			Object groupsCsv = this.evaluateExpression(cacheInfoEvict.groups().toString(), targetMethod, pjp.getArgs(), effectiveTargetMethod, targetClass);
			if (null != groupsCsv && groupsCsv.toString().trim().length() > 0) {
				String[] groups = groupsCsv.toString().split(",");
				for (int i = 0; i < groups.length; i++) {
					String group = groups[i];
					this.flushGroup(group);
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "aroundCacheInfoEvictMethod", "Error while flushing group");
			throw new ApsSystemException("Error while flushing group", t);
		}
		return pjp.proceed();
	}
	
	public void setExpirationTime(String key, int expiresInMinute) {
		Date expirationTime = DateUtils.addMinutes(new Date(), expiresInMinute);
		expirationTimes.put(key.toString(), expirationTime);
	}
	
	public void setExpirationTime(String key, long expiresInSeconds) {
		Date expirationTime = DateUtils.addSeconds(new Date(), (int) expiresInSeconds);
		expirationTimes.put(key.toString(), expirationTime);
	}
	
	@Override
	public void updateFromPageChanged(PageChangedEvent event) {
		IPage page = event.getPage();
		String pageCacheGroupName = SystemConstants.PAGES_CACHE_GROUP_PREFIX + page.getCode();
		this.flushGroup(pageCacheGroupName);
	}
	
	@Override
	protected void release() {
		super.release();
		this.destroy();
	}
	
	@Override
	public void destroy() {
		this.flushAll();
		super.destroy();
	}
	
	public void flushAll() {
		Cache cache = this.getCache();
		cache.clear();
		this._groups.clear();
	}
	
	@Override
	public void flushEntry(String key) {
		this.getCache().evict(key);
	}
	
	public void putInCache(String key, Object obj) {
		Cache cache = this.getCache();
		cache.put(key, obj);
	}
	
	public void putInCache(String key, Object obj, String[] groups) {
		Cache cache = this.getCache();
		cache.put(key, obj);
		this.accessOnGroupMapping(1, groups, key);
	}
	
	public Object getFromCache(String key) {
		Cache cache = this.getCache();
		Cache.ValueWrapper element = cache.get(key);
		if (null == element) {
			return null;
		}
		if (isExpired(key)) {
			this.flushEntry(key);
			return null;
		}
		return element.get();
	}
	
	@Deprecated
	public Object getFromCache(String key, int myRefreshPeriod) {
		return this.getFromCache(key);
	}
	
	@Override
	public void flushGroup(String group) {
		String[] groups = {group};
		this.accessOnGroupMapping(-1, groups, null);
	}
	
	@Override
	public void putInGroup(String key, String[] groups) {
		this.accessOnGroupMapping(1, groups, key);
	}
	
	protected synchronized void accessOnGroupMapping(int operationId, String[] groups, String key) {
		if (operationId>0) {
			//aggiunta
			for (int i = 0; i < groups.length; i++) {
				String group = groups[i];
				List<String> objectKeys = this._groups.get(group);
				if (null == objectKeys) {
					objectKeys = new ArrayList<String>();
					this._groups.put(group, objectKeys);
				}
				if (!objectKeys.contains(key)) {
					objectKeys.add(key);
				}
			}
		} else {
			//rimozione
			for (int i = 0; i < groups.length; i++) {
				String group = groups[i];
				List<String> objectKeys = this._groups.get(group);
				if (null != objectKeys) {
					for (int j = 0; j < objectKeys.size(); j++) {
						String extractedKey = objectKeys.get(j);
						this.flushEntry(extractedKey);
					}
					this._groups.remove(group);
				}
			}
		}
	}
	
	protected Object evaluateExpression(String expression, Method method, Object[] args, Object target, Class<?> targetClass) {
		Collection<Cache> caches = this.getCaches();
		ExpressionEvaluator evaluator = new ExpressionEvaluator();
		EvaluationContext context = evaluator.createEvaluationContext(caches, 
				method, args, target, targetClass, ExpressionEvaluator.NO_RESULT);
		return evaluator.evaluateExpression(expression.toString(), method, context);
	}
	
	protected Collection<Cache> getCaches() {
		Collection<Cache> caches = new ArrayList<Cache>();
		Iterator<String> iter = this.getSpringCacheManager().getCacheNames().iterator();
		while (iter.hasNext()) {
			String cacheName = iter.next();
			caches.add(this.getSpringCacheManager().getCache(cacheName));
		}
		return caches;
	}
	
	public static boolean isNotExpired(String key) {
		return !isExpired(key);
	}
	
	public static boolean isExpired(String key) {
		Date expirationTime = expirationTimes.get(key);
		if (null == expirationTime) {
			return false;
		}
		if (expirationTime.before(new Date())) {
			expirationTimes.remove(key);
			return true;
		} else {
			return false;
		}
	}
	
	protected Cache getCache() {
		return this.getSpringCacheManager().getCache(CACHE_NAME);
	}
	
	protected AbstractCacheManager getSpringCacheManager() {
		return _springCacheManager;
	}
	public void setSpringCacheManager(AbstractCacheManager springCacheManager) {
		this._springCacheManager = springCacheManager;
	}
	
	private AbstractCacheManager _springCacheManager;
	
	private Map<String, List<String>> _groups = new HashMap<String, List<String>>();
	
	private static Map<String, Date> expirationTimes = new HashMap<String, Date>();
	
}