/**
 * Project: avatar-cache
 * 
 * File Created at 2011-9-19
 * $Id$
 * 
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.avatar.cache.interceptor;

import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.dianping.avatar.cache.CacheKey;
import com.dianping.avatar.cache.CacheService;
import com.dianping.avatar.cache.annotation.Cache;
import com.dianping.avatar.cache.configuration.CacheItemConfigManager;
import com.dianping.avatar.cache.configuration.CacheKeyType;
import com.dianping.avatar.cache.configuration.EnhancedCacheItemConfigManager;
import com.dianping.avatar.cache.listener.CacheKeyTypeVersionUpdateListener;
import com.dianping.avatar.cache.util.CacheAnnotationUtils;
import com.dianping.avatar.cache.util.CacheMonitorUtil;
import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;

/**
 * dedicates to statistics the cache hit-rate, the corresponding result could be shown on the hawk-console.
 * <br>Statistics category:
 * <ul>
 * 		<li>命中率：打点格式(k1: cache，k2: hit, k3: category, k4: get/mGet, value: 0/1---0: 未命中，1: 命中)
        <li>操作耗时：打点格式(k1: cache, k2: cost, k3: category, k4: get/add/remove, value: 时间[ms])
        <li>调用次数：打点格式(k1: cache, k2: count, k3: category, k4: get/add/remove, value: 1)
   <ul>
 * @author youngphy.yang
 *
 */
public class CacheMonitorInterceptor implements MethodInterceptor{
	private final AvatarLogger logger = AvatarLoggerFactory.getLogger(CacheMonitorInterceptor.class);
	
	private static String COUNT = "count";
	private static String COST = "cost";
	private static String HIT = "hit";
	private static String ADD_METHOD = "add";
	private static String MADD_METHOD = "mAdd";
	private static String GET_METHOD = "get";
	private static String MGET_METHOD = "mGet";
	private static String REMOVE_METHOD = "remove";
	
	private CacheItemConfigManager cacheItemConfigManager = null;
	
	@SuppressWarnings("unchecked")
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		long startTime = System.currentTimeMillis();
		Object result = invocation.proceed();
		long endTime = System.currentTimeMillis();
		try {
			Object[] paras = invocation.getArguments();
			String cacheType = null;
			String category = "default";
			if(ADD_METHOD.equals(invocation.getMethod().getName())) {
				if(paras != null && paras.length == 1 ) {
					Class<?> cz = paras[0].getClass();
					Cache cache = cz.getAnnotation(Cache.class);
					if(cache.category() != null) {
						category = cache.category();
					}
				} else if(paras != null && CacheKey.class.isAssignableFrom(paras[0].getClass())) {
					if(((CacheKey)paras[0]).getCategory() != null) {
						category = ((CacheKey)paras[0]).getCategory();
					}
				}
				cacheType = getCacheType(category);
				CacheMonitorUtil.log(cacheType, COUNT, category, ADD_METHOD, 1);
				CacheMonitorUtil.log(cacheType, COST, category, ADD_METHOD, endTime - startTime);
			} else if(MADD_METHOD.equals(invocation.getMethod().getName())) {
				if(paras != null && paras.length == 1) {
					if(((List)paras[0]).size() > 0) {
						Class<?> cz = ((List)paras[0]).get(0).getClass();
						Cache cache = cz.getAnnotation(Cache.class);
						if(cache.category() != null) {
							category = cache.category();
						}
					}
				} else if(paras != null && paras.length == 2 ) {
					if(((CacheKey)paras[0]).getCategory() != null) {
						category = ((CacheKey)paras[0]).getCategory();
					}
				}
				cacheType = getCacheType(category);
				CacheMonitorUtil.log(cacheType, COUNT, category, MADD_METHOD, 1);
				CacheMonitorUtil.log(cacheType, COST, category, MADD_METHOD, endTime - startTime);
			} else if(GET_METHOD.equals(invocation.getMethod().getName())) {
				if(paras != null && paras.length == 2) {
					category = CacheAnnotationUtils.getCacheCategory((Class)paras[0]);
				} else if(paras != null && paras.length == 1 
						&& CacheKey.class.isAssignableFrom(paras[0].getClass())) {
					if(((CacheKey)paras[0]).getCategory() != null) {
						category = ((CacheKey)paras[0]).getCategory();
					}
				}
				cacheType = getCacheType(category);
				CacheMonitorUtil.log(cacheType, COUNT, category, GET_METHOD, 1);
				CacheMonitorUtil.log(cacheType, COST, category, GET_METHOD, endTime - startTime);
				if(result != null) {
					CacheMonitorUtil.log(cacheType, HIT, category, GET_METHOD, 1);
				} else {
					CacheMonitorUtil.log(cacheType, HIT, category, GET_METHOD, 0);
				}
			} else if(MGET_METHOD.equals(invocation.getMethod().getName())) {
				if(paras != null && paras.length == 1 ) {
					if(CacheKey.class.isAssignableFrom(paras[0].getClass())
							&& ((CacheKey)paras[0]).getCategory() != null) {
						category = ((CacheKey)paras[0]).getCategory();
					} else if(List.class.isAssignableFrom(paras[0].getClass())) {
						if(((List)paras[0]).size() > 0 && CacheKey.class.isAssignableFrom(((List)paras[0]).get(0).getClass())
								&& ((CacheKey)paras[0]).getCategory() != null) {
							category = ((CacheKey)((List)paras[0]).get(0)).getCategory();
						}
					}
				} else if(paras != null && paras.length == 2) {
					category = CacheAnnotationUtils.getCacheCategory((Class)paras[0]);
				} else if(paras != null
						&& CacheService.EntityKey.class.isAssignableFrom(paras[0].getClass())) {
					//TODO check variable-paras
					category = CacheAnnotationUtils.getCacheCategory(paras[0].getClass());
				}
				cacheType = getCacheType(category);
				CacheMonitorUtil.log(cacheType, COUNT, category, MGET_METHOD, 1);
				CacheMonitorUtil.log(cacheType, COST, category, MGET_METHOD, endTime - startTime);
				if(result != null) {
					CacheMonitorUtil.log(cacheType, HIT, category, MGET_METHOD, 1);
				} else {
					CacheMonitorUtil.log(cacheType, HIT, category, MGET_METHOD, 0);
				}
			} else if(REMOVE_METHOD.equals(invocation.getMethod().getName())) {
				if(paras != null && paras.length == 1 ) {
					if(((CacheKey)paras[0]).getCategory() != null) {
						category = ((CacheKey)paras[0]).getCategory();
					}
				}
				cacheType = getCacheType(category);
				CacheMonitorUtil.log(cacheType, COUNT, category, REMOVE_METHOD, 1);
				CacheMonitorUtil.log(cacheType, COST, category, REMOVE_METHOD, endTime - startTime);
			}
		} catch(Exception e) {
			CacheMonitorUtil.logCacheError("Error occured when try to statistic cache!", e);
		}
		return result;
	}
	
	/**
	 * 
	 * @param paras
	 * @param type 1-->para0-CacheKey
	 * @return
	 */
	protected String getCategory(Object[] paras, int type) {
		String category = null;
		switch(type) {
		case 1: {
			break;
		}
		case 2: {
			break;
		}
		default : {
			
		}
		}
		return category;
	}

	public CacheItemConfigManager getCacheItemConfigManager() {
		return cacheItemConfigManager;
	}

	public void setCacheItemConfigManager(
			CacheItemConfigManager cacheItemConfigManager) {
		this.cacheItemConfigManager = new EnhancedCacheItemConfigManager(cacheItemConfigManager);
	}
	
	private String getCacheType(String category) {
		String cacheHawkType = "default";
		CacheKeyType ckType = cacheItemConfigManager.getCacheKeyType(category);
		if(ckType != null) {
			cacheHawkType = ckType.getCacheType();
/*			if("web".equals(ckType.getCacheType())) {
				cacheHawkType = "local";
			} else if("kvdb".equals(ckType.getCacheType())) {
				cacheHawkType = "kvdb";
			} else if("memcached".equals(ckType.getCacheType())) {
				cacheHawkType = "memcached";
			}*/
		}
		return cacheHawkType;
	}
	
}
