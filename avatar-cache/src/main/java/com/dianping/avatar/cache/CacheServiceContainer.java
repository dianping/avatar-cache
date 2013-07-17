/**
 * Project: avatar
 * 
 * File Created at 2010-7-15 $Id$
 * 
 * Copyright 2010 Dianping.com Corporation Limited. All rights reserved.
 * 
 * This software is the confidential and proprietary information of Dianping
 * Company. ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with Dianping.com.
 */
package com.dianping.avatar.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import com.dianping.avatar.cache.CacheService.EntityKey;
import com.dianping.avatar.cache.annotation.Cache;
import com.dianping.avatar.cache.configuration.CacheItemConfigManager;
import com.dianping.avatar.cache.configuration.CacheKeyType;
import com.dianping.avatar.cache.configuration.EnhancedCacheItemConfigManager;
import com.dianping.avatar.cache.support.CacheTracker;
import com.dianping.avatar.cache.support.DefaultCacheTracker;
import com.dianping.avatar.cache.util.CacheAnnotationUtils;
import com.dianping.avatar.exception.SystemException;
import com.dianping.cache.builder.CacheClientFactory;
import com.dianping.cache.core.CacheClient;
import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.remote.cache.CacheManageWebService;
import com.dianping.remote.cache.dto.CacheClearDTO;
import com.site.helper.Stringizers;

/**
 * Cache service container
 * 
 * @author danson.liu
 * @author guoqing.chen
 */
public class CacheServiceContainer {

    /**
     * Default cache
     */
    private String                 defaultCacheType = CacheKeyType.DEFAULT_CACHE_TYPE;

    /**
     * Default cache category
     */
    private String                 defaultCategory  = "Default";

    /**
     * Cache client factory
     */
    private CacheClientFactory     cacheClientFactory;

    private CacheManageWebService  cacheManageWebService;

    /**
     * retrieve cache item's configuration
     */
    private CacheItemConfigManager configManager;

    private CacheTracker           cacheTracker     = new DefaultCacheTracker();

    /**
     * Constructor
     * 
     * @param itemConfigManager
     */
    public CacheServiceContainer(CacheClientFactory cacheClientFactory, CacheManageWebService cacheManageWebService,
            CacheItemConfigManager itemConfigManager) {
        this.cacheClientFactory = cacheClientFactory;
        this.cacheManageWebService = cacheManageWebService;
        this.configManager = new EnhancedCacheItemConfigManager(itemConfigManager);
    }

    public void add(String key, Object value, int expire) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key/Value is null.");
        }
        Transaction t = Cat.getProducer().newTransaction("Cache." + defaultCacheType, defaultCategory + ":add");
        try {
            CacheClient cacheClient = getCacheClient(defaultCacheType);
            cacheClient.set(key, value, expire * 60 * 60, null);
            t.addData("finalKey", key);
            t.setStatus(Message.SUCCESS);
        } catch (RuntimeException e) {
            t.setStatus(e);
            Cat.getProducer().logError(e);
            throw e;
        } finally {
            t.complete();
        }
    }

    public void add(CacheKey key, Object value) {

        if (key == null || value == null) {
            throw new IllegalArgumentException("Key/Value is null.");
        }

        CacheKeyType cacheKeyType = getCacheKeyMetadata(key);
        String cacheType = cacheKeyType.getCacheType();
        String category = key.getCategory();

        Transaction t = Cat.getProducer().newTransaction("Cache." + cacheType, category + ":add");
        try {
            String finalKey = cacheKeyType.getKey(key.getParams());
            CacheClient cacheClient = getCacheClient(cacheKeyType.getCacheType());
            cacheClient.set(finalKey, value, cacheKeyType.getDurationSeconds(), cacheKeyType.isHot(), cacheKeyType
                    .getCategory());
            t.addData("finalKey", finalKey);
            t.setStatus(Message.SUCCESS);
        } catch (RuntimeException e) {
            t.setStatus(e);
            Cat.getProducer().logError(e);
            throw e;
        } finally {
            t.complete();
        }

    }

    public <T> void add(T entity) {
        add(getCacheKey(entity), entity);
    }

    public <T> void mAdd(List<T> entities) {
        if (entities == null) {
            throw new IllegalArgumentException("entities is null.");
        }

        for (T entity : entities) {
            add(entity);
        }
    }

    public <T> void mAdd(CacheKey cacheKey, List<T> objects) {
        if (objects == null) {
            throw new IllegalArgumentException("objs is null.");
        }

        final List<CacheKey> keys = new ArrayList<CacheKey>();

        for (T object : objects) {
            keys.add(getCacheKey(object));
            add(object);
        }

        add(cacheKey, keys);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null.");
        }
        Transaction t = Cat.getProducer().newTransaction("Cache." + defaultCacheType, defaultCategory + ":get");
        try {
            CacheClient cacheClient = getCacheClient(defaultCacheType);
            long begin = System.nanoTime();
            Object cachedItem = cacheClient.get(key, null);
            long end = System.nanoTime();
            if (cachedItem != null) {
                cacheTracker.addGetInfo(key + "[" + defaultCacheType + "]", end - begin);
            } else {
                Cat.getProducer().logEvent("Cache." + defaultCacheType, defaultCategory + ":missed", Message.SUCCESS,
                        "");
            }
            t.addData("finalKey", key);
            t.setStatus(Message.SUCCESS);
            return (T) cachedItem;
        } catch (RuntimeException e) {
            t.setStatus(e);
            Cat.getProducer().logError(e);
            throw e;
        } finally {
            t.complete();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(CacheKey key) {
        if (key == null) {
            throw new IllegalArgumentException("CacheKey is null.");
        }
        CacheKeyType cacheKeyType = getCacheKeyMetadata(key);
        String cacheType = cacheKeyType.getCacheType();
        String category = key.getCategory();

        Transaction t = Cat.getProducer().newTransaction("Cache." + cacheType, category + ":get");
        try {
            String finalKey = cacheKeyType.getKey(key.getParams());
            CacheClient cacheClient = getCacheClient(cacheKeyType.getCacheType());
            long begin = System.nanoTime();
            Object cachedItem = cacheClient.get(finalKey, cacheKeyType.isHot(), cacheKeyType.getCategory());
            long end = System.nanoTime();
            if (cachedItem != null) {
                cacheTracker.addGetInfo(finalKey + "[" + cacheKeyType.getCacheType() + "]", end - begin);
            } else {
                Cat.getProducer().logEvent("Cache." + cacheType, category + ":missed", Message.SUCCESS, "");
            }
            t.addData("finalKey", finalKey);
            t.setStatus(Message.SUCCESS);
            return (T) cachedItem;
        } catch (RuntimeException e) {
            t.setStatus(e);
            Cat.getProducer().logError(e);
            throw e;
        } finally {
            t.complete();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getWithTimeoutAware(CacheKey key) throws TimeoutException {
        if (key == null) {
            throw new IllegalArgumentException("CacheKey is null.");
        }
        CacheKeyType cacheKeyType = getCacheKeyMetadata(key);
        String cacheType = cacheKeyType.getCacheType();
        String category = key.getCategory();

        Transaction t = Cat.getProducer().newTransaction("Cache." + cacheType, category + ":get");
        try {
            String finalKey = cacheKeyType.getKey(key.getParams());
            CacheClient cacheClient = getCacheClient(cacheKeyType.getCacheType());
            long begin = System.nanoTime();
            Object cachedItem = null;
            try {
                cachedItem = cacheClient.get(finalKey, cacheKeyType.isHot(), cacheKeyType.getCategory(), true);
            } catch (TimeoutException e) {
                Cat.getProducer().logEvent("Cache." + cacheType, category + ":timeout", Message.SUCCESS, "");
                Cat.getProducer().logEvent("Cache." + cacheType, category + ":missed", Message.SUCCESS, "");
                t.addData("finalKey", finalKey);
                t.setStatus(e);
                Cat.getProducer().logError(e);
                throw e;
            }
            long end = System.nanoTime();
            if (cachedItem != null) {
                cacheTracker.addGetInfo(finalKey + "[" + cacheKeyType.getCacheType() + "]", end - begin);
            } else {
                Cat.getProducer().logEvent("Cache." + cacheType, category + ":missed", Message.SUCCESS, "");
            }
            t.addData("finalKey", finalKey);
            t.setStatus(Message.SUCCESS);
            return (T) cachedItem;
        } catch (RuntimeException e) {
            t.setStatus(e);
            Cat.getProducer().logError(e);
            throw e;
        } finally {
            t.complete();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<?> cz, List<?> params) {
        String category = CacheAnnotationUtils.getCacheCategory(cz);
        CacheKey cacheKey = new CacheKey(category, params.toArray(new Object[params.size()]));

        return (T) get(cacheKey);
    }

    public <T> List<T> mGet(Class<?> cz, List<?> params) {

        if (params == null || params.isEmpty()) {
            throw new IllegalArgumentException("params must not be null or empty.");
        }

        String category = CacheAnnotationUtils.getCacheCategory(cz);

        List<CacheKey> cacheKeys = new ArrayList<CacheKey>();

        for (Object param : params) {
            if (param instanceof List<?>) {
                param = ((List<?>) param).toArray(new Object[((List<?>) param).size()]);
            }
            cacheKeys.add(new CacheKey(category, param));
        }

        return mGet(cacheKeys);
    }

    public <T> List<T> mGet(EntityKey... keys) {
        if (keys == null || keys.length == 0) {
            return null;
        }

        List<CacheKey> cacheKeys = new ArrayList<CacheKey>();

        for (EntityKey eKey : keys) {
            String category = CacheAnnotationUtils.getCacheCategory(eKey.cz);
            cacheKeys.add(new CacheKey(category, eKey.params));
        }

        return mGet(cacheKeys);
    }

    public <T> Map<String, T> mGet(Set<String> keys) {
        if (keys == null) {
            throw new IllegalArgumentException("Parameter keys is null.");
        }

        Transaction t = Cat.getProducer().newTransaction("Cache." + defaultCacheType, defaultCategory + ":mGet");
        try {
            if (keys.isEmpty()) {
                return Collections.emptyMap();
            }

            CacheClient cacheClient = getCacheClient(defaultCacheType);
            long begin = System.nanoTime();
            Map<String, T> cachedDataMap = cacheClient.getBulk(keys, null);
            long end = System.nanoTime();
            if (cachedDataMap != null && !cachedDataMap.isEmpty()) {
                cacheTracker.addGetInfo("*[mget(" + keys.size() + ")-" + defaultCacheType + "]", end - begin);
            } else {
                Cat.getProducer().logEvent("Cache." + defaultCacheType, defaultCategory + ":missed", Message.SUCCESS,
                        "");
            }
            t.addData("finalKeys", Stringizers.forJson().compact().from(keys, CatConstants.MAX_LENGTH,
                    CatConstants.MAX_ITEM_LENGTH));
            return cachedDataMap;
        } catch (RuntimeException e) {
            t.setStatus(e);
            Cat.getProducer().logError(e);
            throw e;
        } finally {
            t.complete();
        }
    }

    public <T> List<T> mGet(CacheKey cacheKey) {
        if (cacheKey == null) {
            throw new IllegalArgumentException("Parameter cacheKey is null.");
        }

        List<CacheKey> cacheKeys = get(cacheKey);
        if (cacheKeys == null || cacheKeys.isEmpty()) {
            return null;
        }

        return this.mGet(cacheKeys);
    }

    public <T> List<T> mGet(List<CacheKey> keys) {
        if (keys == null) {
            throw new IllegalArgumentException("Parameter keys is null.");
        }

        if (keys.isEmpty()) {
            return Collections.emptyList();
        }

        final List<String> finalKeys = new ArrayList<String>();
        final Map<String, String> categories = new HashMap<String, String>();

        CacheKeyType cacheKeyType = getCacheKeyMetadata(keys.get(0));
        String cacheType = cacheKeyType.getCacheType();
        String category = keys.get(0).getCategory();

        Transaction t = Cat.getProducer().newTransaction("Cache." + cacheType, category + ":mGet");
        t.setStatus(Message.SUCCESS);
        try {

            for (CacheKey key : keys) {
                String finalKey = cacheKeyType.getKey(key.getParams());
                finalKeys.add(finalKey);
                categories.put(finalKey, cacheKeyType.getCategory());
            }

            CacheClient cacheClient = getCacheClient(cacheKeyType.getCacheType());
            long begin = System.nanoTime();
            Map<String, T> cachedDataMap = cacheClient.getBulk(finalKeys, categories);
            long end = System.nanoTime();
            boolean allGeted = cachedDataMap != null && cachedDataMap.size() == keys.size();
            if (!allGeted) {
                Cat.getProducer().logEvent("Cache." + cacheType, category + ":missed", Message.SUCCESS, "");
                return null;
            }
            cacheTracker.addGetInfo(cacheKeyType.getCategory() + "[mget(" + keys.size() + ")-"
                    + cacheKeyType.getCacheType() + "]", end - begin);
            // no cache expired, then reform result data by keys order
            List<T> sortedCachedData = new ArrayList<T>();
            for (String finalKey : finalKeys) {
                sortedCachedData.add(cachedDataMap.get(finalKey));
            }
            t.setStatus(Message.SUCCESS);
            return sortedCachedData;
        } catch (RuntimeException e) {
            t.setStatus(e);
            Cat.getProducer().logError(e);
            throw e;
        } finally {
            t.complete();
        }
    }

    public void remove(CacheKey key) {
        if (key == null) {
            throw new IllegalArgumentException("Parameter key is null.");
        }

        CacheKeyType cacheKeyType = getCacheKeyMetadata(key);
        String finalKey = cacheKeyType.getKey(key.getParams());
        String cacheType = cacheKeyType.getCacheType();
        remove(cacheType, finalKey, key.getCategory(), key.getParams());
    }

    public void remove(String cacheType, String key) {
        if (key == null) {
            throw new IllegalArgumentException("Parameter key is null.");
        }
        remove(cacheType, key, null, null);
    }

    private void remove(String cacheType, String key, String category, Object[] params) {
        String categoryName = category == null ? defaultCategory : category;
        Transaction t = Cat.getProducer().newTransaction("Cache." + cacheType, categoryName + ":remove");
        try {
            CacheClient cacheClient = getCacheClient(cacheType);
            boolean isDistributed = cacheClient.isDistributed();
            if (isDistributed) {
                cacheClient.remove(key, category);
            }
            // 是否考虑可以直接通过swallow发送清除缓存和同步.net缓存的消息，而不需要经过cache-service?
            if (!isDistributed || (category != null && getCacheKeyMetadata(category).isSync2Dnet())) {
                cacheClient.remove(key, category);
                cacheManageWebService.clearByKey(new CacheClearDTO(cacheType, key, category, params != null ? Arrays
                        .asList(params) : new ArrayList<Object>()));
            }
            t.addData("finalKey", key);
            t.setStatus(Message.SUCCESS);
        } catch (RuntimeException e) {
            t.setStatus(e);
            Cat.getProducer().logError(e);
            throw e;
        } finally {
            t.complete();
        }
    }

    public String getFinalKey(CacheKey key) {
        CacheKeyType cacheKeyType = getCacheKeyMetadata(key);
        return cacheKeyType.getKey(key.getParams());
    }

    private CacheClient getCacheClient(String cacheType) {
        CacheClient cacheClient = cacheClientFactory.findCacheClient(cacheType);
        if (cacheClient == null) {
            throw new SystemException("No CacheClient found with type[" + cacheType + "].");
        }
        return cacheClient;
    }

    /**
     * Retrieve {@link CacheKeyType} instance by {@link CacheKey} instance
     */
    private CacheKeyType getCacheKeyMetadata(CacheKey key) {
        CacheKeyType cacheKeyType = getCacheKeyMetadata(key.getCategory());
        if (cacheKeyType == null) {
            throw new SystemException("The cache item  for " + key.toString() + " is not found in configuration files.");
        }
        return cacheKeyType;
    }

    /**
     * Retrieve {@link CacheKeyType} object by category from metadata
     */
    private CacheKeyType getCacheKeyMetadata(String category) {
        CacheKeyType cacheKeyType = configManager.getCacheKeyType(category);
        if (cacheKeyType == null) {
            throw new SystemException("Configuration not found with cache category[" + category + "].");
        }
        return cacheKeyType;
    }

    /**
     * Generate the cache key for object. The object should be annotated by
     * {@link Cache}
     */
    private CacheKey getCacheKey(Object entity) {
        Class<?> cz = entity.getClass();

        Object[] cacheKeyValues = CacheAnnotationUtils.getCacheKeyValues(entity);

        Cache cache = cz.getAnnotation(Cache.class);

        if (cache == null) {
            throw new SystemException("No Cache Annotation found on class[" + cz.getName() + "].");
        }

        return new CacheKey(cache.category(), cacheKeyValues);
    }

    /**
     * @return the defaultCacheType
     */
    public String getDefaultCacheType() {
        return defaultCacheType;
    }

    /**
     * @param defaultCacheType
     *            the defaultCacheType to set
     */
    public void setDefaultCacheType(String defaultCacheType) {
        this.defaultCacheType = defaultCacheType;
    }

    @SuppressWarnings("unchecked")
    public <T> Map<CacheKey, T> mGetWithNonExists(List<CacheKey> keys) {
        if (keys == null) {
            throw new IllegalArgumentException("Parameter keys is null.");
        }

        if (keys.isEmpty()) {
            return Collections.EMPTY_MAP;
        }

        final List<String> finalKeys = new ArrayList<String>();
        final Map<String, String> categories = new HashMap<String, String>();
        final Map<String, CacheKey> finalKeyCacheKeyMapping = new HashMap<String, CacheKey>();

        CacheKeyType cacheKeyType = getCacheKeyMetadata(keys.get(0));
        String cacheType = cacheKeyType.getCacheType();
        String category = keys.get(0).getCategory();

        Transaction t = Cat.getProducer().newTransaction("Cache." + cacheType, category + ":mGet");
        t.setStatus(Message.SUCCESS);
        try {

            for (CacheKey key : keys) {
                String finalKey = cacheKeyType.getKey(key.getParams());
                finalKeys.add(finalKey);
                categories.put(finalKey, cacheKeyType.getCategory());
                finalKeyCacheKeyMapping.put(finalKey, key);
            }

            CacheClient cacheClient = getCacheClient(cacheKeyType.getCacheType());
            long begin = System.nanoTime();
            Map<String, T> cachedDataMap = cacheClient.getBulk(finalKeys, categories);
            long end = System.nanoTime();
            cacheTracker.addGetInfo(cacheKeyType.getCategory() + "[mget(" + keys.size() + ")-"
                    + cacheKeyType.getCacheType() + "]", end - begin);
            t.setStatus(Message.SUCCESS);
            Map<CacheKey, T> res = new HashMap<CacheKey, T>();
            if (cachedDataMap == null) {
                cachedDataMap = new HashMap<String, T>();
            }
            for (String finalKey : finalKeys) {
                res.put(finalKeyCacheKeyMapping.get(finalKey), cachedDataMap.get(finalKey));
            }
            return res;
        } catch (RuntimeException e) {
            t.setStatus(e);
            Cat.getProducer().logError(e);
            throw e;
        } finally {
            t.complete();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Map<CacheKey, T> mGetWithTimeoutAware(List<CacheKey> keys) throws TimeoutException {
        if (keys == null) {
            throw new IllegalArgumentException("Parameter keys is null.");
        }

        if (keys.isEmpty()) {
            return Collections.EMPTY_MAP;
        }

        final List<String> finalKeys = new ArrayList<String>();
        final Map<String, String> categories = new HashMap<String, String>();
        final Map<String, CacheKey> finalKeyCacheKeyMapping = new HashMap<String, CacheKey>();

        CacheKeyType cacheKeyType = getCacheKeyMetadata(keys.get(0));
        String cacheType = cacheKeyType.getCacheType();
        String category = keys.get(0).getCategory();

        Transaction t = Cat.getProducer().newTransaction("Cache." + cacheType, category + ":mGet");
        t.setStatus(Message.SUCCESS);
        try {

            for (CacheKey key : keys) {
                String finalKey = cacheKeyType.getKey(key.getParams());
                finalKeys.add(finalKey);
                categories.put(finalKey, cacheKeyType.getCategory());
                finalKeyCacheKeyMapping.put(finalKey, key);
            }

            CacheClient cacheClient = getCacheClient(cacheKeyType.getCacheType());
            long begin = System.nanoTime();
            Map<String, T> cachedDataMap = null;
            try {
                cachedDataMap = cacheClient.getBulk(finalKeys, categories, true);
            } catch (TimeoutException e) {
                Cat.getProducer().logEvent("Cache." + cacheType, category + ":[mGet]timeout", Message.SUCCESS, "");
                Cat.getProducer().logEvent("Cache." + cacheType, category + ":[mGet]missed", Message.SUCCESS, "");
                t.setStatus(e);
                Cat.getProducer().logError(e);
                throw e;
            }

            long end = System.nanoTime();
            cacheTracker.addGetInfo(cacheKeyType.getCategory() + "[mget(" + keys.size() + ")-"
                    + cacheKeyType.getCacheType() + "]", end - begin);
            t.setStatus(Message.SUCCESS);
            Map<CacheKey, T> res = new HashMap<CacheKey, T>();
            if (cachedDataMap == null) {
                cachedDataMap = new HashMap<String, T>();
            }
            for (String finalKey : finalKeys) {
                res.put(finalKeyCacheKeyMapping.get(finalKey), cachedDataMap.get(finalKey));
            }
            return res;
        } catch (RuntimeException e) {
            t.setStatus(e);
            Cat.getProducer().logError(e);
            throw e;
        } finally {
            t.complete();
        }
    }

}
