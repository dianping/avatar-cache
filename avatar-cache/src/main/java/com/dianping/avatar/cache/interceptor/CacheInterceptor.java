/**
 * Project: avatar
 * 
 * File Created at 2010-7-19
 * $Id$
 * 
 * Copyright 2010 Dianping.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Dianping.com.
 */
package com.dianping.avatar.cache.interceptor;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.beanutils.MethodUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import com.dianping.avatar.cache.CacheKey;
import com.dianping.avatar.cache.CacheService;
import com.dianping.avatar.cache.annotation.Cache;
import com.dianping.avatar.cache.annotation.CacheOperation;
import com.dianping.avatar.cache.util.CacheAnnotationUtils;

/**
 * CacheInterceptor to support {@link Cache} annotation
 * 
 * @author danson.liu
 * @author guoqing.chen
 * 
 */
public class CacheInterceptor implements MethodInterceptor, InitializingBean {
	/**
	 * Cache service
	 */
	private CacheService cacheService;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		Cache cache = AnnotationUtils.findAnnotation(method, Cache.class);
		if (cache == null) {
			Class<? extends Object> targetClazz = invocation.getThis().getClass();
			method = MethodUtils.getAccessibleMethod(targetClazz, method.getName(), method.getParameterTypes());
			cache = AnnotationUtils.findAnnotation(method, Cache.class);
		}
		if (cache != null) {

			CacheOperation operation = cache.operation();

			CacheKey cacheKey = CacheAnnotationUtils.getCacheKey(method, invocation.getArguments());

			if (operation == CacheOperation.SetAndGet) {
				Object cachedItem = cacheService.get(cacheKey);

				if (cachedItem != null) {
					return cachedItem;
				}

				Object item = invocation.proceed();
				// consider create an null object instead of null
				cacheService.add(cacheKey, item);

				return item;
			} else if (operation == CacheOperation.Update || operation == CacheOperation.Remove) {
				cacheService.remove(cacheKey);
				return invocation.proceed();
			}
		}
		return invocation.proceed();
	}

	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(cacheService, "cacheService required.");
	}
}
