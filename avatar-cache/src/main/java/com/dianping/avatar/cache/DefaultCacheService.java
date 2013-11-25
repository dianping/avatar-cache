/**
 * Project: avatar
 * 
 * File Created at 2010-7-14
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import com.dianping.avatar.cache.configuration.CacheItemConfigManager;
import com.dianping.avatar.cache.util.CacheMonitorUtil;
import com.dianping.avatar.pattern.command.Command;
import com.dianping.cache.builder.CacheClientFactory;
import com.dianping.remote.cache.CacheManageWebService;

/**
 * Default Cache Service Interface implement. The class should be used in
 * spring. All cache requests will be forward to {@link CacheServiceContainer}
 * 
 * @author danson.liu
 * 
 */
public class DefaultCacheService implements CacheService {
	
	/**
	 * Container
	 */
	private final CacheServiceContainer container;
	
	/**
	 * Default duration(3h)
	 */
	private final static int DEFAULT_EXPIRE_TIME = 3;

	/**
	 * Constructor
	 */
	public DefaultCacheService(CacheClientFactory cacheClientFactory,
			CacheManageWebService cacheManageWebService,
			CacheItemConfigManager itemConfigManager) {
		this.container = new CacheServiceContainer(
			cacheClientFactory,
			cacheManageWebService,
			itemConfigManager
		);
	}

	@Override
	public boolean add(String key, Object value) {
		return add(key, value, DEFAULT_EXPIRE_TIME);
	}

	@Override
	public boolean add(final String key, final Object value, final int expire) {
		return executeWithNoError(new Command() {
			@Override
			public Object execute() throws Exception {
				container.add(key, value, expire);
				return true;
			}
		}, false, "Add item to cache with key[" + key + "] failed.");
	}

	@Override
	public boolean add(final CacheKey key, final Object value) {
		return executeWithNoError(new Command() {
			@Override
			public Object execute() throws Exception {
				container.add(key, value);
				return true;
			}
		}, false, "Add item to cache with cachekey[" + key + "] failed.");
	}

	@Override
	public boolean add(final Object entity) {
		return executeWithNoError(new Command() {
			@Override
			public Object execute() throws Exception {
				container.add(entity);
				return true;
			}
		}, false, "Add entity[" + entity + "] to cache failed.");
	}

	public <T> boolean mAdd(final List<T> entities) {
		return executeWithNoError(new Command() {
			@Override
			public Object execute() throws Exception {
				container.mAdd(entities);
				return true;
			}
		}, false, "MAdd entities to cache failed.");
	}

	@Override
	public <T> boolean mAdd(final CacheKey cacheKey, final List<T> entities) {
		return executeWithNoError(new Command() {
			@Override
			public Object execute() throws Exception {
				container.mAdd(cacheKey, entities);
				return true;
			}
		}, false, "MAdd entities with cachekey[" + cacheKey + "] failed.");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(final String key) {
		return (T) executeWithNoError(new Command() {
			@Override
			public Object execute() throws Exception {
				return container.get(key);
			}
		}, null, "Get item from cache with key[" + key + "] failed.");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(final CacheKey key) {
		return (T) executeWithNoError(new Command() {
			@Override
			public Object execute() throws Exception {
				return container.get(key);
			}
		}, null, "Get item from cache with cachekey[" + key + "] failed.");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(final Class<?> cz, final List<?> params) {
		return (T) executeWithNoError(new Command() {
			@Override
			public Object execute() throws Exception {
				return container.get(cz, params);
			}
		}, null, "Get entity[type=" + cz.getSimpleName() + "] with index params[" + params + "] failed.");
	}

	@Override
	public <T> List<T> mGet(final Class<?> cz, final List<?> params) {
		return executeWithNoError(new Command() {
			@Override
			public Object execute() throws Exception {
				return container.mGet(cz, params);
			}
		}, null, "MGet entities[type=" + cz.getSimpleName() + "] failed.");
	}

	@Override
	public <T> List<T> mGet(final EntityKey... keys) {
		return executeWithNoError(new Command() {
			@Override
			public Object execute() throws Exception {
				return container.mGet(keys);
			}
		}, null, "MGet entities by entitykeys failed.");
	}

	@Override
	public <T> List<T> mGet(final List<CacheKey> keys) {
		return executeWithNoError(new Command() {
			@Override
			public Object execute() throws Exception {
				return container.mGet(keys);
			}
		}, null, "MGet entities by cachekeys failed.");
	}

	@Override
	public <T> Map<String, T> mGet(final Set<String> keys) {
		return executeWithNoError(new Command() {
			@Override
			public Object execute() throws Exception {
				return container.mGet(keys);
			}
		}, null, "MGet items with keys[" + keys + "] failed.");
	}

	@Override
	public <T> List<T> mGet(final CacheKey cacheKey) {
		return executeWithNoError(new Command() {
			@Override
			public Object execute() throws Exception {
				return container.mGet(cacheKey);
			}
		}, null, "MGet items with cachekey[" + cacheKey + "] failed.");
	}

	@Override
	public boolean remove(final CacheKey cacheKey) {
		return executeWithNoError(new Command() {
			@Override
			public Object execute() throws Exception {
				container.remove(cacheKey);
				return true;
			}
		}, false, "Remove item from cache with cachekey[" + cacheKey + "] failed.");
	}

	@Override
	public boolean remove(final String cacheType, final String key) {
		return executeWithNoError(new Command() {
			@Override
			public Object execute() throws Exception {
				container.remove(cacheType, key);
				return true;
			}
		}, false, "Remove item from cache with key[" + key + "] failed.");
	}
	
	@Override
	public String getFinalKey(CacheKey key) {
		return container.getFinalKey(key);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T executeWithNoError(Command command, T returnValueIfError, String errorMsg) {
		try {
			return (T) command.execute();
		} catch (Throwable throwable) {
			CacheMonitorUtil.logCacheError(errorMsg, throwable);
			return returnValueIfError;
		}
	}

    @Override
    public <T> Map<CacheKey, T> mGetWithNonExists(final List<CacheKey> keys) {
        return executeWithNoError(new Command() {
            @Override
            public Object execute() throws Exception {
                return container.mGetWithNonExists(keys);
            }
        }, new HashMap<CacheKey, T>(), "MGet entities by cachekeys failed.");
    }

    /* (non-Javadoc)
     * @see com.dianping.avatar.cache.CacheService#getWithTimeoutAware(com.dianping.avatar.cache.CacheKey)
     */
    @Override
    public <T> T getOrTimeout(CacheKey key) throws TimeoutException {
        return (T)container.getWithTimeoutAware(key);
    }
    
    /* (non-Javadoc)
     * @see com.dianping.avatar.cache.CacheService#mGetOrTimeout(com.dianping.avatar.cache.CacheKey)
     */
    @Override
    public <T> Map<CacheKey, T> mGetOrTimeout(List<CacheKey> keys) throws TimeoutException {
        return container.mGetWithTimeoutAware(keys);
    }
}