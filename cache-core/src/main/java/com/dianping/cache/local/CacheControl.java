/**
 * Project: cache-core
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
package com.dianping.cache.local;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Concrete cache access implementation, using map to cache items
 * @author danson.liu
 *
 */
public class CacheControl {
	
	private Map<String, CacheElement> storage = new HashMap<String, CacheElement>();
	
	private final Lock lock = new ReentrantLock();

	/**
	 * @param key
	 * @param element
	 */
	public void set(String key, CacheElement element) {
		lock.lock();
		try {
			element.getAttributes().setLastAccessTimeNow();
			storage.put(key, element);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @param key
	 * @param element
	 */
	public void add(String key, CacheElement element) {
		lock.lock();
		try {
			if (!storage.containsKey(key)) {
				element.getAttributes().setLastAccessTimeNow();
				storage.put(key, element);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @param key
	 * @param element
	 */
	public void replace(String key, CacheElement element) {
		lock.lock();
		try {
			if (storage.containsKey(key)) {
				element.getAttributes().setLastAccessTimeNow();
				storage.put(key, element);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @param key
	 * @return
	 */
	public CacheElement get(String key) {
		lock.lock();
		try {
			CacheElement element = storage.get(key);
			if (element != null) {
				if (!isExpired(element)) {
					element.getAttributes().setLastAccessTimeNow();
					return element;
				} else {
					remove(key);
					return null;
				}
			}
			return null;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @param keys
	 * @return
	 */
	public Collection<CacheElement> gets(Collection<String> keys) {
		lock.lock();
		try {
			Collection<CacheElement> elements = new ArrayList<CacheElement>();
			for (String key : keys) {
				CacheElement element = get(key);
				if (element != null) {
					elements.add(element);
				}
			}
			return elements;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @param key
	 */
	public void remove(String key) {
		lock.lock();
		try {
			storage.remove(key);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @param key
	 * @param amount
	 * @return
	 */
	public long increment(String key, int amount) {
		lock.lock();
		try {
			CacheElement element = get(key);
			if (element != null) {
				Long value = (Long) element.getValue();
				Long newValue = Long.valueOf(value.longValue() + amount);
				if (newValue < 0) {
					newValue = 0L;
				}
				element.setValue(newValue);
				element.getAttributes().setLastAccessTimeNow();
				return newValue;
			}
			return -1;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @param key
	 * @param amount
	 * @return
	 */
	public long decrement(String key, int amount) {
		lock.lock();
		try {
			CacheElement element = get(key);
			if (element != null) {
				Long value = (Long) element.getValue();
				Long newValue = Long.valueOf(value.longValue() - amount);
				if (newValue < 0) {
					newValue = 0L;
				}
				element.setValue(newValue);
				element.getAttributes().setLastAccessTimeNow();
				return newValue;
			}
			return -1;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @param element
	 * @return
	 */
	private boolean isExpired(CacheElement element) {
		ElementAttributes attributes = element.getAttributes();
		long now = System.currentTimeMillis();
		
		long maxLifeSeconds = attributes.getMaxLifeSeconds();
		long createTime = attributes.getCreateTime();
		if (maxLifeSeconds != -1 && (now - createTime > maxLifeSeconds * 1000)) {
			return true;
		}
		
		long maxIdleTimeSeconds = attributes.getMaxIdleTimeSeconds();
		long lastAccessTime = attributes.getLastAccessTime();
		if (maxIdleTimeSeconds != -1 && (now - lastAccessTime > maxIdleTimeSeconds * 1000)) {
			return true;
		}
		
		return false;
	}

	/**
	 * 
	 */
	public void destroy() {
		lock.lock();
		try {
			storage = null;
		} finally {
			lock.unlock();
		}
	}

}
