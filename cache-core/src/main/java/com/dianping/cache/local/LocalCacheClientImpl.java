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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.dianping.cache.core.CacheClient;
import com.dianping.cache.core.CacheClientConfiguration;
import com.dianping.cache.core.InitialConfiguration;
import com.dianping.cache.core.Lifecycle;
import com.dianping.cache.ehcache.EhcacheClientImpl;

/**
 * Local Cache implementation provide all types of access to cache
 * 
 * @author danson.liu
 * 
 * @Deprecated Use {@link EhcacheClientImpl} to replace this localcache. 
 * <p>
 * So your configuration in xml should like this:
 *  &lt;cache key="web" clientClass="com.dianping.cache.ehcache.EhcacheClientImpl"
 *       metadataClass="com.dianping.cache.builder.metadata.EhcacheMetadataParser"&gt;
 *       &lt;xmlFile&gt;/ehcache.xml&lt;/xmlFile&gt;
 *  &lt;/cache&gt;
 * </p>
 * <p>
 * Your configuration in database should like this:
 * In DianPing.DP_CacheConfiguration table
 * ----------------------------------------------------------------------------------------
 * |  CachedKey | ClientClazz                                  | Servers | TranscoderClazz|                                                               
 * ----------------------------------------------------------------------------------------
 * |  web       | com.dianping.cache.ehcache.EhcacheClientImpl | NULL    | NULL           | 
 * ----------------------------------------------------------------------------------------
 * </p>
 */
@Deprecated
public class LocalCacheClientImpl implements CacheClient, Lifecycle, InitialConfiguration {

    private CacheControl cacheControl;

    private LocalCacheConfiguration config;

    @Override
    public void set(String key, Object value, int expiration, String category) {
        cacheControl.set(key, createCacheElement(key, value, expiration));
    }

    @Override
    public void add(String key, Object value, int expiration, String category) {
        cacheControl.add(key, createCacheElement(key, value, expiration));
    }

    @Override
    public void replace(String key, Object value, int expiration, String category) {
        cacheControl.replace(key, createCacheElement(key, value, expiration));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, String category, boolean timeoutAware) {
        CacheElement element = cacheControl.get(key);
        return (T) (element != null ? element.getValue() : null);
    }

    @Override
    public <T> Map<String, T> getBulk(Collection<String> keys, Map<String, String> categories) {
        return getBulk(keys, categories, false);
    }

    @Override
    public void remove(String key, String category) {
        cacheControl.remove(key);
    }

    @Override
    public long increment(String key, int amount, String category) {
        return cacheControl.increment(key, amount);
    }

    @Override
    public long decrement(String key, int amount, String category) {
        return cacheControl.decrement(key, amount);
    }

    @Override
    public void clear() {
        // TODO:
    }

    @Override
    public void init(CacheClientConfiguration config) {
        this.config = (LocalCacheConfiguration) config;
    }

    /**
     * @param key
     * @param value
     * @param expiration
     */
    private CacheElement createCacheElement(String key, Object value, int expiration) {
        CacheElement element = new CacheElement();
        element.setKey(key);
        element.setValue(value);
        ElementAttributes attributes = new ElementAttributes();
        attributes.setMaxIdleTimeSeconds(config.getMaxIdleTime());
        attributes.setMaxLifeSeconds(expiration);
        element.setAttributes(attributes);
        return element;
    }

    @Override
    public void shutdown() {
        this.cacheControl.destroy();
    }

    @Override
    public void start() {
        this.cacheControl = new CacheControl();
    }

	@Override
	public boolean isDistributed() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.dianping.cache.core.CacheClient#get(java.lang.String, boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key, boolean isHot, String category, boolean timeoutAware) {
		return (T) get(key, category, timeoutAware);
	}

    /* (non-Javadoc)
     * @see com.dianping.cache.core.CacheClient#set(java.lang.String, java.lang.Object, int, boolean)
     */
    @Override
    public void set(String key, Object value, int expiration, boolean isHot, String category) {
        set(key, value, expiration, category);
    }

	/* (non-Javadoc)
	 * @see com.dianping.cache.core.CacheClient#remove(java.lang.String)
	 */
	@Override
	public void remove(String key) {
		remove(key, null);
	}

    /* (non-Javadoc)
     * @see com.dianping.cache.core.CacheClient#get(java.lang.String, java.lang.String)
     */
    @Override
    public <T> T get(String key, String category) {
        return (T)get(key, category, false);
    }

    /* (non-Javadoc)
     * @see com.dianping.cache.core.CacheClient#get(java.lang.String, boolean, java.lang.String)
     */
    @Override
    public <T> T get(String key, boolean isHot, String category) {
        return (T)get(key, isHot, category, false);
    }

    /* (non-Javadoc)
     * @see com.dianping.cache.core.CacheClient#getBulk(java.util.Collection, java.util.Map, boolean)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, T> getBulk(Collection<String> keys, Map<String, String> categories, boolean timeoutAware)
            {
        Collection<CacheElement> elements = cacheControl.gets(keys);
        Map<String, T> map = new HashMap<String, T>();
        if (elements != null && !elements.isEmpty()) {
            for (CacheElement element : elements) {
                map.put(element.getKey(), (T) element.getValue());
            }
        }
        return map;
    }

}