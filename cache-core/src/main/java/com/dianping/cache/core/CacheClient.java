/**
 * Project: avatar-cache
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
package com.dianping.cache.core;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Consistent cache client interface for the general purpose of transparent
 * handling all third cache implementations.
 * 
 * @author danson.liu
 * @author jinhua.liang
 * 
 */
public interface CacheClient {

	/**
	 * Set an object in the cache regardless of any existing value
	 * 
	 * @param key
	 *            the key under which this object should be added
	 * @param value
	 *            the object to cache
	 * @param expiration
	 *            the expiration of this cached item, in seconds from now on
	 */
	void set(String key, Object value, int expiration, String category);
	
	/**
     * Set an object in the cache regardless of any existing value
     * 
     * @param key
     *            the key under which this object should be added
     * @param value
     *            the object to cache
     * @param expiration
     *            the expiration of this cached item, in seconds from now on
     * @param isHot
     */
    void set(String key, Object value, int expiration, boolean isHot, String category);

	/**
	 * Add an object to the cache if it does not exist already
	 * 
	 * @param key
	 *            the key under which this object should be added
	 * @param value
	 *            the object to cache
	 * @param expiration
	 *            expiration the expiration of this cached item, in seconds from
	 *            now on
	 */
	void add(String key, Object value, int expiration, String category);

	/**
	 * Replace an object with the given value if there is already a value for
	 * the given key
	 * 
	 * @param key
	 *            the key under which this object should be added
	 * @param value
	 *            value the object to cache
	 * @param expiration
	 *            expiration expiration the expiration of this cached item, in
	 *            seconds from now on
	 */
	void replace(String key, Object value, int expiration, String category);

	/**
	 * Get with a single key
	 * 
	 * @param <T>
	 *            cached item's type
	 * @param key
	 *            the key to get
	 * @return the result from the cache (null if there is none)
	 */
	<T> T get(String key, String category);
	
	<T> T get(String key, String category, boolean timeoutAware) throws TimeoutException;
	
	/**
	 * Get with a single key
	 * 
	 * @param <T>
	 *            cached item's type
	 * @param key
	 *            the key to get
	 * @param isHot whether hot key or not
	 * @return the result from the cache (null if there is none)
	 */
	<T> T get(String key, boolean isHot, String category);
	
	<T> T get(String key, boolean isHot, String category, boolean timeoutAware) throws TimeoutException;

	/**
	 * Get the values for multiple keys from the cache
	 * 
	 * @param <T>
	 *            cached item's type
	 * @param keys
	 *            keys to get
	 * @return a map of the values (for each value that exists)
	 */
	<T> Map<String, T> getBulk(Collection<String> keys, Map<String, String> categories);

	/**
	 * remove the given key from the cache
	 * 
	 * @param key
	 *            item of the key to delete
	 */
	void remove(String key, String category);
	
	void remove(String key);

	/**
	 * Atomic-increase cached data with specified key by specified amount, and
	 * return the new value.(The method is optional for client implementation.)
	 * @param key the key
	 * @param amount the amount to increment
	 * @return the new value (-1 if the key doesn't exist)
	 */
	long increment(String key, int amount, String category);

	/**
	 * Atomic-decrement the cache data with amount.(The method is optional for
	 * client implementation.)
	 * @param key the key
	 * @param amount the amount to decrement
	 * @return the new value (-1 if the key doesn't exist)
	 */
	long decrement(String key, int amount, String category);
	
	void clear();
	
	boolean isDistributed();
	
}
