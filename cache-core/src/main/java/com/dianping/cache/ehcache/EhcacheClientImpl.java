/**
 * Project: cache-core
 * 
 * File Created at 2010-9-1 $Id$
 * 
 * Copyright 2010 dianping.com Corporation Limited. All rights reserved.
 * 
 * This software is the confidential and proprietary information of Dianping
 * Company. ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with dianping.com.
 */
package com.dianping.cache.ehcache;

import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.management.MBeanServer;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.blocking.BlockingCache;
import net.sf.ehcache.management.ManagementService;

import com.dianping.cache.core.CacheClient;
import com.dianping.cache.core.CacheClientConfiguration;
import com.dianping.cache.core.InitialConfiguration;
import com.dianping.cache.core.Lifecycle;
import com.dianping.lion.client.ConfigCache;

/**
 * EhcacheClientImpl 4 avatar local cache!
 * 
 * @author pengshan.zhang
 * @author jinhua.liang
 * 
 */
public class EhcacheClientImpl implements CacheClient, Lifecycle, InitialConfiguration {

    /**
     * Template cache name
     */
    private static final String TEMPLATE_CACHE_NAME    = "templateCache";

    private static final String HOTKEY_LOCAL_LOCK_TIME = "avatar-cache.hotkey.local.locktime";

    private static Class<?>     configCacheClass       = null;

    private ReentrantLock       lock                   = new ReentrantLock();

    static {
        try {
            configCacheClass = Class.forName("com.dianping.lion.client.ConfigCache");
        } catch (ClassNotFoundException e) {
            configCacheClass = null;
        }
    }

    /**
     * Ehcache CacheManager instance
     */
    private CacheManager        manager;

    private BlockingCache       defaultBlockingCache;

    /**
     * @see com.dianping.cache.core.CacheClient#add()
     */
    @Override
    public void add(String key, Object value, int expiration, String category) {
        if (!findCache(category).isKeyInCache(key)) {
            findCache(category).put(
                    new Element(key, value, Boolean.FALSE, Integer.valueOf(0), Integer.valueOf(expiration)));
        }
    }

    /**
     * @see com.dianping.cache.core.CacheClient#clear()
     */
    @Override
    public void clear() {
        defaultBlockingCache.removeAll();
        for (String cacheName : manager.getCacheNames()) {
            manager.getCache(cacheName).removeAll();
        }
    }

    /**
     * @see com.dianping.cache.core.CacheClient#decrement(java.lang.String, int)
     */
    @Override
    public long decrement(String key, int amount, String category) {
        Element element = findCache(category).get(key);
        if (element != null) {
            Object value = element.getObjectValue();
            if (value instanceof Long) {
                findCache(category).remove(key);
                long newValue = (Long) value - amount;
                findCache(category).put(
                        new Element(key, newValue, Boolean.FALSE, Integer.valueOf(0), Integer.valueOf(element
                                .getTimeToLive())));
                return newValue;
            }
        }
        return -1;
    }

    /**
     * @see com.dianping.cache.core.CacheClient#get(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, String category) {
        Element element = findCache(category).get(key);
        return (T) (element == null ? null : element.getObjectValue());
    }

    /**
     * @see com.dianping.cache.core.CacheClient#getBulk(java.util.Collection)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, T> getBulk(Collection<String> keys, Map<String, String> categories) {
        Map<String, T> map = new HashMap<String, T>();
        for (String key : keys) {
            Element element = findCache(categories == null ? null : categories.get(key)).get(key);
            map.put(key, (element == null ? null : (T) element.getObjectValue()));
        }
        return map;
    }

    /**
     * @see com.dianping.cache.core.CacheClient#increment(java.lang.String, int)
     */
    @Override
    public long increment(String key, int amount, String category) {
        Element element = findCache(category).get(key);
        if (element != null) {
            Object value = element.getObjectValue();
            if (value instanceof Long) {
                findCache(category).remove(key);
                long newValue = (Long) value + amount;
                findCache(category).put(
                        new Element(key, newValue, Boolean.FALSE, Integer.valueOf(0), Integer.valueOf(element
                                .getTimeToLive())));
                return newValue;
            }
        }
        return -1;
    }

    /**
     * @see com.dianping.cache.core.CacheClient#remove(java.lang.String)
     */
    @Override
    public void remove(String key, String category) {
        findCache(category).remove(key);
    }

    /**
     * @see com.dianping.cache.core.CacheClient#replace(java.lang.String,
     *      java.lang.Object, int)
     */
    @Override
    public void replace(String key, Object value, int expiration, String category) {
        if (findCache(category).isKeyInCache(key)) {
            findCache(category).put(
                    new Element(key, value, Boolean.FALSE, Integer.valueOf(0), Integer.valueOf(expiration)));
        }
    }

    /**
     * @see com.dianping.cache.core.CacheClient#set(java.lang.String,
     *      java.lang.Object, int)
     */
    @Override
    public void set(String key, Object value, int expiration, boolean isHot, String category) {
        findCache(category)
                .put(new Element(key, value, Boolean.FALSE, Integer.valueOf(0), Integer.valueOf(expiration)));
        if (isHot) {
            findCache(category).put(
                    new Element(key + "_bak", value, Boolean.TRUE, Integer.valueOf(0), Integer.valueOf(0)));
            String lastVersionKey = genLastVersionCacheKey(key);
            // 当版本升级后，要清理上一个版本的hotkey数据
            if (!key.equals(lastVersionKey)) {
                findCache(category).remove(lastVersionKey + "_bak");
            }
        }
    }

    /**
     * @see com.dianping.cache.core.Lifecycle#shutdown()
     */
    @Override
    public void shutdown() {
        manager.shutdown();
    }

    /**
     * @see com.dianping.cache.core.Lifecycle#start()
     */
    @Override
    public void start() {
        Ehcache cache = manager.getCache(TEMPLATE_CACHE_NAME);
        defaultBlockingCache = new LooseBlockingCache(cache);
        manager.replaceCacheWithDecoratedCache(cache, defaultBlockingCache);
    }

    /**
     * @see com.dianping.cache.core.InitialConfiguration#init(com.dianping.cache.core.CacheClientConfiguration)
     */
    @Override
    public void init(CacheClientConfiguration config) {
        if (config instanceof EhcacheConfiguration) {
            manager = ((EhcacheConfiguration) config).buildEhcacheManager();
        }
        if (manager == null) {
            manager = CacheManager.create();
        }
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ManagementService.registerMBeans(manager, server, true, true, true, true);
    }

    @Override
    public boolean isDistributed() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dianping.cache.core.CacheClient#get(java.lang.String, boolean)
     */
    @Override
    public <T> T get(String key, boolean isHot, String category) {
        T result = get(key, category);

        if (isHot) {
            if (result == null) {
                boolean locked = false;
                try {
                    lock.lock();
                    if (findCache(category).get(key + "_lock") == null) {
                        findCache(category).put(
                                new Element(key + "_lock", true, Boolean.FALSE, Integer.valueOf(0), Integer
                                        .valueOf(getHotkeyLockTime())));
                        locked = true;
                    }
                } finally {
                    lock.unlock();
                }

                if (locked) {
                    return null;
                } else {
                    // 批量清理时，因为version升级了，所以bak数据要考虑从上一个版本中查找
                    result = get(key + "_bak", category);
                    if (result == null) {
                        String lastVersionKey = genLastVersionCacheKey(key);
                        if (!key.equals(lastVersionKey)) {
                            result = get(lastVersionKey + "_bak", category);
                        }
                    }
                    return result;
                }
            }
        }

        return result;
    }

    private String genLastVersionCacheKey(String currentVersionCacheKey) {
        if (currentVersionCacheKey == null) {
            return currentVersionCacheKey;
        }

        int versionSplitPos = currentVersionCacheKey.lastIndexOf("_");
        if (versionSplitPos < 0) {
            return currentVersionCacheKey;
        }

        String versionStr = currentVersionCacheKey.substring(versionSplitPos + 1);
        if (!isNumeric(versionStr)) {
            return currentVersionCacheKey;
        }

        Integer currentVersion = Integer.valueOf(versionStr);
        if (currentVersion > 0) {
            return currentVersionCacheKey.substring(0, versionSplitPos + 1) + (currentVersion - 1);
        } else {
            return currentVersionCacheKey;
        }

    }

    private boolean isNumeric(String src) {
        if (src == null || src.length() == 0) {
            return false;
        }

        for (int i = 0; i < src.length(); i++) {
            if (src.charAt(i) < '0' || src.charAt(i) > '9') {
                return false;
            }
        }

        return true;
    }

    private int getHotkeyLockTime() {
        Integer lockTime = null;
        if (configCacheClass != null) {
            try {
                lockTime = ConfigCache.getInstance().getIntProperty(HOTKEY_LOCAL_LOCK_TIME);
            } catch (Throwable e) {
            }
        }
        return lockTime == null ? 30 : lockTime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dianping.cache.core.CacheClient#set(java.lang.String,
     * java.lang.Object, int)
     */
    @Override
    public void set(String key, Object value, int expiration, String category) {
        set(key, value, expiration, false, category);
    }

    private Ehcache findCache(String category) {
        if (category == null) {
            return defaultBlockingCache;
        }
        Ehcache cache = manager.getCache(category);
        if (cache == null) {
            return defaultBlockingCache;
        }
        return cache;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dianping.cache.core.CacheClient#remove(java.lang.String)
     */
    @Override
    public void remove(String key) {
        remove(key, null);
    }
}
