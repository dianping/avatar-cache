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

import java.util.HashMap;
import java.util.Map;

/**
 * Build cache client from configuration file. Each cache key will be built only
 * one instance. And, if the client implements {@link Lifecycle}, it will be
 * started when first built, and will be shutdown when invoking
 * {@link #closeCacheClient(String)}.
 * 
 * @author guoqing.chen
 * 
 */
public class CacheClientBuilder {
	/**
	 * The caches for all client implementation
	 */
	private static Map<String, CacheClient> caches = new HashMap<String, CacheClient>();

	/**
	 * Build a cache client by configuration file. The client will be started if
	 * it implements {@link Lifecycle} interface. The client instance will be
	 * cached to HashMap for multiple retrieves. Every key will only be built
	 * for one instance.
	 */
	public synchronized static CacheClient buildCacheClient(String key, CacheClientConfiguration config) {
		if (key == null) {
			throw new IllegalArgumentException("Cache key is null.");
		}

		CacheClient cacheClient = caches.get(key);

		if (cacheClient != null) {
			return cacheClient;
		}

		String cacheImplementation = CacheConfiguration.getCache(key);

		if (cacheImplementation == null) {
			throw new IllegalArgumentException("Cache implementation for key " + key + " is not found.");
		}

		Class<?> cz = null;
		try {
			cz = Thread.currentThread().getContextClassLoader().loadClass(cacheImplementation);
		} catch (ClassNotFoundException e) {
		}

		if (cz == null) {
			try {
				cz = CacheClientBuilder.class.getClassLoader().loadClass(cacheImplementation);
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException("The cache implementation [" + cacheImplementation
						+ "] class is not found.");
			}
		}

		if (!CacheClient.class.isAssignableFrom(cz)) {
			throw new IllegalArgumentException("The cache implementation[" + cacheImplementation
					+ "] is not drived from " + CacheClient.class.getName());
		}

		try {
			cacheClient = (CacheClient) cz.newInstance();

		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Cann't instantiate cache implementation[" + cacheImplementation + "]",
					e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}

		if (cacheClient instanceof KeyAware) {
			((KeyAware) cacheClient).setKey(key);
		}

		if (cacheClient instanceof InitialConfiguration) {
			((InitialConfiguration) cacheClient).init(config);
		}

		if (cacheClient instanceof Lifecycle) {
			((Lifecycle) cacheClient).start();
		}

		caches.put(key, cacheClient);

		return cacheClient;
	}

	/**
	 * Close the cache client by key.
	 */
	public synchronized static void closeCacheClient(String key) {
		if (key == null) {
			return;
		}

		CacheClient cacheClient = caches.get(key);

		if (cacheClient == null) {
			return;
		}

		if (cacheClient instanceof Lifecycle) {
			((Lifecycle) cacheClient).shutdown();
		}

		caches.remove(key);
	}
}
