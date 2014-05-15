/**
 * Project: avatar-cache
 * 
 * File Created at 2010-7-12 $Id$
 * 
 * Copyright 2010 Dianping.com Corporation Limited. All rights reserved.
 * 
 * This software is the confidential and proprietary information of Dianping
 * Company. ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with Dianping.com.
 */
package com.dianping.cache.memcached;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.DefaultConnectionFactory;
import net.spy.memcached.MemcachedClient;

import com.dianping.cache.core.CacheClient;
import com.dianping.cache.core.CacheClientBuilder;
import com.dianping.cache.core.CacheClientConfiguration;
import com.dianping.cache.core.CacheConfiguration;
import com.dianping.cache.core.InitialConfiguration;
import com.dianping.cache.core.KeyAware;
import com.dianping.cache.core.Lifecycle;
import com.dianping.cache.ehcache.EhcacheClientImpl;
import com.dianping.cache.ehcache.EhcacheConfiguration;
import com.dianping.lion.client.ConfigCache;

/**
 * The memcached client implementation adaptor(sypmemcached)
 * 
 * @author guoqing.chen
 * @author danson.liu
 * 
 */
public class MemcachedClientImpl implements CacheClient, Lifecycle, KeyAware, InitialConfiguration {

    /**
     * in milliseconds
     */
    private static final long            DEFAULT_GET_TIMEOUT             = 50;
    private static final long            DEFAULT_OP_QUEUE_MAX_BLOCK_TIME = 2;

    private static final String          DUAL_RW_SWITCH_NAME             = "avatar-cache.dualrw.enabled";
    private static final String          HOTKEY_DISTRIBUTED_LOCK_TIME    = "avatar-cache.hotkey.locktime";
    private static final String          GET_TIMEOUT_KEY                 = "avatar-cache.memcached.get.timeout";
    private static final String          CUSTOM_CONFIG_FILE              = "/config/memcached.properties";

    private static final String          PROP_OP_QUEUE_LEN               = "opQueueLen";
    private static final String          PROP_READ_BUF_SIZE              = "readBufSize";
    private static final String          PROP_OP_QUEUE_MAX_BLOCK_TIME    = "opQueueMaxBlockTime";

    private static int                   opQueueLen                      = DefaultConnectionFactory.DEFAULT_OP_QUEUE_LEN;
    private static int                   readBufSize                     = DefaultConnectionFactory.DEFAULT_READ_BUFFER_SIZE;
    private static long                  opQueueMaxBlockTime             = DEFAULT_OP_QUEUE_MAX_BLOCK_TIME;

    /**
     * Memcached client unique key
     */
    private String                       key;

    /**
     * Memcached client
     */
    private MemcachedClient              readClient;

    private MemcachedClient              writeClient;

    private MemcachedClient              backupClient                 = null;

    /**
     * Spymemcached client configuration
     */
    private MemcachedClientConfiguration config;

    private static Class<?>              configCacheClass             = null;

    static {
        try {
            configCacheClass = Class.forName("com.dianping.lion.client.ConfigCache");
        } catch (ClassNotFoundException e) {
            configCacheClass = null;
        }

        InputStream is = null;

        try {
            Properties prop = new Properties();
            is = MemcachedClientImpl.class.getClassLoader().getResourceAsStream(CUSTOM_CONFIG_FILE);
            if (is != null) {
                prop.load(is);
                opQueueLen = prop.containsKey(PROP_OP_QUEUE_LEN) ? Integer.valueOf(prop.getProperty(PROP_OP_QUEUE_LEN))
                        : DefaultConnectionFactory.DEFAULT_OP_QUEUE_LEN;
                readBufSize = prop.containsKey(PROP_READ_BUF_SIZE) ? Integer.valueOf(prop
                        .getProperty(PROP_READ_BUF_SIZE)) : DefaultConnectionFactory.DEFAULT_READ_BUFFER_SIZE;
                opQueueMaxBlockTime = prop.containsKey(PROP_OP_QUEUE_MAX_BLOCK_TIME) ? Integer.valueOf(prop
                        .getProperty(PROP_OP_QUEUE_MAX_BLOCK_TIME))
                        : DEFAULT_OP_QUEUE_MAX_BLOCK_TIME;
            }

        } catch (Exception e) {

        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public void init(CacheClientConfiguration config) {
        this.config = (MemcachedClientConfiguration) config;
    }

    @Override
    public void add(String key, Object value, int expiration, String category) {
        String reformedKey = reformKey(key);
        writeClient.add(reformedKey, expiration, value);
        if (needDualRW()) {
            backupClient.add(reformedKey, expiration, value);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, String category, boolean timeoutAware) throws TimeoutException {
        String reformedKey = reformKey(key);
        T result = null;
        TimeoutException timeoutException = null;
        Future<Object> future = null;
        try {
            future = readClient.asyncGet(reformedKey);
        } catch (IllegalStateException e) {
            timeoutException = new TimeoutException(e.getMessage());
            result = null;
        }

        if (future != null) {
            try {
                // use timeout to eliminate memcached servers' crash
                result = (T) future.get(getGetTimeout(), TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
            	 future.cancel(true);
                timeoutException = e;
                result = null;
            } catch (Exception e) {
            	 future.cancel(true);
                result = null;
            }
        }

        if (result == null && needDualRW()) {
            try {
               future = backupClient.asyncGet(reformedKey);
					result = (T) future.get(getGetTimeout(), TimeUnit.MILLISECONDS);
            } catch (IllegalStateException e) {
                timeoutException = new TimeoutException(e.getMessage());
                result = null;
            } catch (TimeoutException e) {
            	 future.cancel(true);
                timeoutException = e;
                result = null;
            } catch (Exception e1) {
            	 future.cancel(true);
                result = null;
            }
        }

        if (timeoutAware && timeoutException != null && result == null) {
            throw timeoutException;
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, T> getBulk(Collection<String> keys, Map<String, String> categories, boolean timeoutAware)
            throws TimeoutException {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> needReformed = reform(keys);
        Future<Map<String, Object>> future = null;
        boolean hasReformed = needReformed != null && !needReformed.isEmpty();
        Map<String, T> result = null;
        TimeoutException timeoutException = null;
        if (!hasReformed) {
            try {
                future = readClient.asyncGetBulk(keys);
            } catch (IllegalStateException e) {
                timeoutException = new TimeoutException(e.getMessage());
                result = null;
            }
        } else {
            Collection<String> reformedKeys = new HashSet<String>();
            for (String key : keys) {
                String reformedKey = needReformed.get(key);
                reformedKeys.add(reformedKey != null ? reformedKey : key);
            }
            keys = reformedKeys;
            try {
                future = readClient.asyncGetBulk(reformedKeys);
            } catch (IllegalStateException e) {
                timeoutException = new TimeoutException(e.getMessage());
                result = null;
            }
        }

        if (future != null) {
            try {
                // use timeout to eliminate memcached servers' crash
                result = (Map<String, T>) future.get(getGetTimeout(), TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
            	 future.cancel(true);
                timeoutException = e;
                result = null;
            } catch (Exception e) {
            	 future.cancel(true);
                result = null;
            }
        }

        if (result == null && needDualRW()) {
            try {
               future = backupClient.asyncGetBulk(keys);
					result = (Map<String, T>) future.get(getGetTimeout(), TimeUnit.MILLISECONDS);
            } catch (IllegalStateException e) {
                timeoutException = new TimeoutException(e.getMessage());
                result = null;
            } catch (TimeoutException e) {
            	 future.cancel(true);
                timeoutException = e;
                result = null;
            } catch (Exception e) {
                result = null;
            }
        }

        if (timeoutAware && timeoutException != null && result == null) {
            throw timeoutException;
        }

        if (result == null) {
            return null;
        }

        if (!hasReformed || result.isEmpty()) {
            return result;
        } else {
            Map<String, T> reformBack = new HashMap<String, T>(result.size());
            for (Entry<String, T> entry : result.entrySet()) {
                String originalKey = needReformed.get(entry.getKey());
                reformBack.put(originalKey != null ? originalKey : entry.getKey(), entry.getValue());
            }
            return reformBack;
        }
    }

    public <T> Map<String, T> getBulk(Collection<String> keys, Map<String, String> categories) {
        try {
            return getBulk(keys, categories, false);
        } catch (TimeoutException e) {
            return null;
        }
    }

    @Override
    public void remove(String key, String category) {
        String reformedKey = reformKey(key);
        writeClient.delete(reformedKey);
        if (needDualRW()) {
            backupClient.delete(reformedKey);
        }
    }

    @Override
    public void replace(String key, Object value, int expiration, String category) {
        String reformedKey = reformKey(key);
        writeClient.replace(reformedKey, expiration, value);
        if (needDualRW()) {
            backupClient.replace(reformedKey, expiration, value);
        }
    }

    @Override
    public void set(String key, Object value, int expiration, String category) {
        String reformedKey = reformKey(key);
        writeClient.set(reformedKey, expiration, value);
        if (needDualRW()) {
            backupClient.set(reformedKey, expiration, value);
        }
    }

    @Override
    public long decrement(String key, int amount, String category) {
        String reformedKey = reformKey(key);
        long result = writeClient.decr(reformedKey, amount);
        if (needDualRW()) {
            backupClient.decr(reformedKey, amount);
        }
        return result;
    }

    @Override
    public long increment(String key, int amount, String category) {
        String reformedKey = reformKey(key);
        long result = writeClient.incr(reformedKey, amount);
        if (needDualRW()) {
            backupClient.incr(reformedKey, amount);
        }
        return result;
    }

    private String reformKey(String key) {
        return key != null ? key.replace(" ", "@+~") : key;
    }

    private Map<String, String> reform(Collection<String> keys) {
        Map<String, String> keyMap = null;
        if (keys != null) {
            for (String key : keys) {
                if (key.contains(" ")) {
                    keyMap = keyMap != null ? keyMap : new HashMap<String, String>(keys.size());
                    String reformedKey = reformKey(key);
                    keyMap.put(key, reformedKey);
                    keyMap.put(reformedKey, key);
                }
            }
        }
        return keyMap;
    }

    @Override
    public void clear() {
        writeClient.flush();
        if (needDualRW()) {
            backupClient.flush();
        }
    }

    @Override
    public void shutdown() {
        readClient.shutdown();
        writeClient.shutdown();
        if (backupClient != null) {
            backupClient.shutdown();
        }
    }

    @Override
    public void start() {
        try {
            // use ketama to provide consistent node hashing
            ExtendedConnectionFactory connectionFactory = new ExtendedKetamaConnectionFactory(opQueueLen, readBufSize,
                    opQueueMaxBlockTime);
            if (config.getTranscoder() != null) {
                connectionFactory.setTranscoder(config.getTranscoder());
            } else {
                // set transcoder to HessianTranscoder:
                // 1. fast
                // 2. Fixed bug in https://bugs.launchpad.net/play/+bug/503349
                connectionFactory.setTranscoder(new HessianTranscoder());
            }
            String servers = config.getServers();
            if (servers == null) {
                throw new RuntimeException("Server address must be specified.");
            }
            // 由于使用KvdbClientImpl上线会对线上环境影响很大，所以采取折衷方案，混合memcached和memcachedb客户端
            if (!servers.contains(",")) {
                // memcached
                String[] serverSplits = config.getServers().split("\\|");
                String mainServer = serverSplits[0].trim();
                String backupServer = serverSplits.length == 1 ? null : serverSplits[1].trim();
                MemcachedClient client = new MemcachedClient(connectionFactory, AddrUtil.getAddresses(mainServer));
                readClient = client;
                writeClient = client;
                if (backupServer != null) {
                    backupClient = new MemcachedClient(connectionFactory, AddrUtil.getAddresses(backupServer));
                }
            } else {
                // kvdb
                String[] serverSplits = servers.split(" ");
                String writeServer = serverSplits[0].trim();
                String readServers = serverSplits.length == 1 ? writeServer : serverSplits[1].trim();
                readClient = new MemcachedClient(connectionFactory, AddrUtil.getAddresses(readServers));
                writeClient = new MemcachedClient(connectionFactory, AddrUtil.getAddresses(writeServer));

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isDistributed() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dianping.cache.core.CacheClient#get(java.lang.String, boolean)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, boolean isHot, String category, boolean timeoutAware) throws TimeoutException {
        T result = null;
        TimeoutException timeoutException = null;

        try {
            result = (T) get(key, category, timeoutAware);
        } catch (TimeoutException e) {
            timeoutException = e;
            result = null;
        }

        if (isHot) {
            String lastVersionCacheKey = genLastVersionCacheKey(key);
            if (result == null) {
                Future<Boolean> future = writeClient.add(key + "_lock", getHotkeyLockTime(), true);
                Future<Boolean> backupFuture = needDualRW() ? backupClient
                        .add(key + "_lock", getHotkeyLockTime(), true) : null;
                Boolean locked = null;
                try {
                    locked = future.get(getGetTimeout(), TimeUnit.MILLISECONDS);
                } catch(Exception e) {
               	  future.cancel(true);
                }

                if (backupFuture != null && locked == null) {
                    try {
                        locked = backupFuture.get(getGetTimeout(), TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                  	  backupFuture.cancel(true);
                    }
                }

                if (locked == null || !locked.booleanValue()) {
                    try {
                        result = (T) getLocalCacheClient().get(key, category, timeoutAware);
                        // 如果版本升级了，需要从老版本上查找
                        if (result == null) {
                            if (!key.equals(lastVersionCacheKey)) {
                                result = (T) getLocalCacheClient().get(lastVersionCacheKey, category, timeoutAware);
                            }
                        }
                    } catch (TimeoutException e) {
                        timeoutException = e;
                        result = null;
                    }
                } else {
                    result = null;
                }

            } else {
                // 如果版本升级了，需要删除老版本
                if (!key.equals(lastVersionCacheKey)) {
                    getLocalCacheClient().remove(lastVersionCacheKey, category);
                }
                getLocalCacheClient().set(key, result, 3600 * 24, category);
            }
        }

        if (timeoutAware && timeoutException != null && result == null) {
            throw timeoutException;
        }
        return result;
    }

    private CacheClient getLocalCacheClient() {
        if (CacheConfiguration.getCache("web") != null) {
            return CacheClientBuilder.buildCacheClient("web", new EhcacheConfiguration());
        } else {
            CacheConfiguration.addCache("web", EhcacheClientImpl.class.getName());
            return CacheClientBuilder.buildCacheClient("web", new EhcacheConfiguration());
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

    private boolean needDualRW() {
        boolean needDualRW = false;
        if (configCacheClass != null) {
            try {
                needDualRW = ConfigCache.getInstance().getBooleanProperty(DUAL_RW_SWITCH_NAME);
            } catch (Throwable e) {
            }
        }
        return needDualRW && backupClient != null;
    }

    private int getHotkeyLockTime() {
        Integer lockTime = null;
        if (configCacheClass != null) {
            try {
                lockTime = ConfigCache.getInstance().getIntProperty(HOTKEY_DISTRIBUTED_LOCK_TIME);
            } catch (Throwable e) {
            }
        }
        return lockTime == null ? 30 : lockTime;
    }

    private long getGetTimeout() {
        Long timeout = null;
        if (configCacheClass != null) {
            try {
                timeout = ConfigCache.getInstance().getLongProperty(GET_TIMEOUT_KEY);
            } catch (Throwable e) {
            }
        }
        return timeout == null ? DEFAULT_GET_TIMEOUT : timeout;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dianping.cache.core.CacheClient#set(java.lang.String,
     * java.lang.Object, int, boolean)
     */
    @Override
    public void set(String key, Object value, int expiration, boolean isHot, String category) {
        set(key, value, expiration, category);
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

    /*
     * (non-Javadoc)
     * 
     * @see com.dianping.cache.core.CacheClient#get(java.lang.String,
     * java.lang.String)
     */
    @SuppressWarnings("unchecked")
   @Override
    public <T> T get(String key, String category) {
        try {
            return (T) get(key, category, false);
        } catch (TimeoutException e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dianping.cache.core.CacheClient#get(java.lang.String, boolean,
     * java.lang.String)
     */
    @SuppressWarnings("unchecked")
   @Override
    public <T> T get(String key, boolean isHot, String category) {
        try {
            return (T) get(key, isHot, category, false);
        } catch (TimeoutException e) {
            return null;
        }
    }
}