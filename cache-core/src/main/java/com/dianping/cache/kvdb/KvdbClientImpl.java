/**
 * Project: cache-core
 * 
 * File Created at 2011-7-14
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
package com.dianping.cache.kvdb;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

import com.dianping.cache.core.CacheClient;
import com.dianping.cache.core.CacheClientConfiguration;
import com.dianping.cache.core.InitialConfiguration;
import com.dianping.cache.core.KeyAware;
import com.dianping.cache.core.Lifecycle;
import com.dianping.cache.memcached.ExtendedConnectionFactory;
import com.dianping.cache.memcached.ExtendedKetamaConnectionFactory;
import com.dianping.cache.memcached.KvdbTranscoder;
import com.dianping.cache.memcached.MemcachedClientConfiguration;

/**
 * @author danson.liu
 *
 */
public class KvdbClientImpl implements CacheClient, Lifecycle, KeyAware, InitialConfiguration {
	
	private String key;
	
	private MemcachedClient readClient;
	
	private MemcachedClient writeClient;
	
	private MemcachedClientConfiguration config;
	
	private static final long DEFAULT_GET_TIMEOUT = 50;

	private long timeout = DEFAULT_GET_TIMEOUT;

	@Override
	public void init(CacheClientConfiguration config) {
		this.config = (MemcachedClientConfiguration) config;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public void set(String key, Object value, int expiration, String category) {
		writeClient.set(key, expiration, value);
	}

	@Override
	public void add(String key, Object value, int expiration, String category) {
		writeClient.add(key, expiration, value);
	}

	@Override
	public void replace(String key, Object value, int expiration, String category) {
		writeClient.replace(key, expiration, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key, String category, boolean timeoutAware) {
		Future<Object> future = readClient.asyncGet(key);
        try {
            // use timeout to eliminate memcachedb servers' crash
            return (T) future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            return null;
        }
	}

	@Override
	public <T> Map<String, T> getBulk(Collection<String> keys, Map<String, String> categories) {
		return getBulk(keys, categories, false);
	}

	@Override
	public void remove(String key, String category) {
		writeClient.delete(key);
	}

	@Override
	public long increment(String key, int amount, String category) {
		return writeClient.incr(key, amount);
	}

	@Override
	public long decrement(String key, int amount, String category) {
		return writeClient.decr(key, amount);
	}

	@Override
	public void clear() {
		writeClient.flush();
	}

	@Override
	public void start() {
		try {
			ExtendedConnectionFactory connectionFactory = new ExtendedKetamaConnectionFactory();
            if (config.getTranscoder() != null) {
            	connectionFactory.setTranscoder(config.getTranscoder());
            } else {
            	connectionFactory.setTranscoder(new KvdbTranscoder());
            }
            String servers = config.getServers();
            if (servers == null) {
            	throw new RuntimeException("Kvdb server address must be specified.");
            }
            String[] serverSplits = servers.split(" ");
            String writeServer = serverSplits[0].trim();
            String readServers = serverSplits.length == 1 ? writeServer : serverSplits[1].trim();
            readClient = new MemcachedClient(connectionFactory, AddrUtil.getAddresses(readServers));
            writeClient = new MemcachedClient(connectionFactory, AddrUtil.getAddresses(writeServer));
		} catch (IOException e) {
			throw new RuntimeException("Construct kvdb client failed, cause: ", e);
		}

	}

	@Override
	public void shutdown() {
		readClient.shutdown();
		writeClient.shutdown();
	}

	@Override
	public boolean isDistributed() {
		return true;
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
        Future<Map<String, Object>> future = readClient.asyncGetBulk(keys);
        try {
            // use timeout to eliminate memcachedb servers' crash
            return (Map<String, T>) future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            return null;
        }
    }

}