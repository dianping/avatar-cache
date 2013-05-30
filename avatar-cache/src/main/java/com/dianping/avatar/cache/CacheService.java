/**
 * Project: avatar
 * 
 * File Created at 2010-7-12
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
package com.dianping.avatar.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * Cache interface provided for business.
 * 
 * @author guoqing.chen
 * 
 */
public interface CacheService {

	/**
	 * Add simple object to 'default' cache with specified simple key and default expire time - "3 hours"
	 */
	boolean add(String key, Object value);

	/**
	 * Add simple object to 'default' cache with specified simple key and expire time
	 * @param expire expire time in hours
	 */
	boolean add(String key, Object value, int expire);

	/**
	 * Add simple object to cache with specified CacheKey
	 */
	boolean add(CacheKey key, Object value);

	/**
	 * Add entity to cache,CacheKey will be resolved by <tt>entity</tt>'s
	 * annotations
	 */
	<T> boolean add(T entity);

	/**
	 * Add entities to cache
	 */
	<T> boolean mAdd(List<T> entities);

	/**
	 * Add multiple instances,the <tt>cacheKey</tt> will be used to store
	 * instances keys
	 */
	<T> boolean mAdd(CacheKey cacheKey, List<T> objs);

	/**
	 * Retrieve cached item with specified simple key from 'default' cache
	 */
	<T> T get(String key);

	/**
	 * Retrieve cached item with specified CacheKey
	 */
	<T> T get(CacheKey key);

	/**
	 * Retrieve cached item by annotated class and parameters
	 */
	<T> T get(Class<?> cz, List<?> params);

	/**
	 * Retrieve all entity instance by class and parameters,it assume every
	 * Class only have one parameter.
	 */
	<T> List<T> mGet(Class<?> cz, List<?> params);

	/**
	 * Retrieve all entity instance by class and parameters
	 */
	<T> List<T> mGet(EntityKey... keys);

	/**
	 * Retrieve cached items with keys cached by the specified CacheKey
	 */
	<T> List<T> mGet(List<CacheKey> keys);
	
	/**
     * Retrieve cached items with keys cached by the specified CacheKey
     */
    <T> Map<CacheKey, T> mGetWithNonExists(List<CacheKey> keys);

	/**
	 * Retrieve cached items with specified simple keys
	 */
	<T> Map<String, T> mGet(Set<String> keys);

	/**
	 * Retrieve cached items with cache key
	 */
	<T> List<T> mGet(CacheKey cacheKey);

	/**
	 * Remove cache with specified key
	 * Notice: for both java and .net app's cache
	 */
	boolean remove(CacheKey key);

	/**
	 * Remove item from specified cache with specified simple key
	 * Notice: only for java app's cache, except for .net's
	 */
	boolean remove(String cacheType, String key);
	
	/**
	 * Generate final cache key string
	 * @param key
	 * @return
	 */
	String getFinalKey(CacheKey key);
	
	<T> T getOrTimeout(CacheKey key) throws TimeoutException;
	
	<T> Map<CacheKey, T> mGetOrTimeout(List<CacheKey> keys) throws TimeoutException;
	
	/**
	 * The composite key for multiple-get entities.
	 */
	class EntityKey {
		/**
		 * Entity class
		 */
		public final Class<?> cz;
		/**
		 * Parameters
		 */
		public final Object[] params;

		public EntityKey(Class<?> cz, Object... params) {
			this.cz = cz;
			this.params = params;
		}
	}
}
