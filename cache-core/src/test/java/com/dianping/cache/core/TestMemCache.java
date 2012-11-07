/**
 * Project: cache-core
 * 
 * File Created at 2010-8-31
 * $Id$
 * 
 * Copyright 2010 dianping.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.cache.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.dianping.cache.memcached.HessianTranscoder;
import com.dianping.cache.memcached.MemcachedClientConfiguration;

/**
 * 
 * @author pengshan.zhang
 * 
 */
public class TestMemCache {

    private CacheClient client;

    private final String key = "memcached_key 3";

    private final String value = "memcached_value";

    private final String replaceValue = "memcached_replaceValue";

    private final int longValue = 20;

    private final int expiration = 5;

    @Before
    public void setUp() throws IOException {
        CacheConfiguration.init("classpath:testClasspathBuilder.properties");
        MemcachedClientConfiguration config = new MemcachedClientConfiguration();
        config.addServer("192.168.8.45", 11211);
        config.setTranscoder(new HessianTranscoder());
        client = CacheClientBuilder.buildCacheClient("memcached", config);
    }

    @After
    public void tearDown() {
        CacheClientBuilder.closeCacheClient("memcached");
    }

    @Test
    public void testSet() throws IOException {
        client.set(key, value, 10, null);
        Assert.assertEquals(value, client.get(key, null));
        client.remove(key, null);
        Assert.assertNull(client.get(key, null));
    }

    @Test
    public void testReplace() throws IOException {
        client.set(key, value, 10, null);
        Assert.assertEquals(value, client.get(key, null));

        client.replace(key, replaceValue, 10, null);
        Assert.assertEquals(replaceValue, client.get(key, null));

        client.remove(key, null);
        Assert.assertNull(client.get(key, null));
    }

    @Test
    public void testReplaceUnExist() throws IOException {
        client.replace(key, replaceValue, 10, null);
        Assert.assertNull(client.get(key, null));
    }

    @Test
    public void testAdd() throws IOException {
        client.set(key, value, 10, null);
        Assert.assertEquals(value, client.get(key, null));

        client.add(key, replaceValue, 10, null);
        Assert.assertEquals(value, client.get(key, null));

        client.remove(key, null);
        Assert.assertNull(client.get(key, null));

        client.add(key, replaceValue, 10, null);
        Assert.assertEquals(replaceValue, client.get(key, null));

        client.remove(key, null);
        Assert.assertNull(client.get(key, null));
    }

    @Test
    public void testIncrementAndDecrement() throws IOException, InterruptedException {
        client.set(key, Integer.valueOf(longValue)+"", 10, null);
        Assert.assertEquals(longValue+"", client.get(key, null));

        int amount = 10;
        client.decrement(key, amount, null);
        
        Assert.assertEquals((longValue - amount)+"", client.get(key, null));

        client.increment(key, amount, null);
        Assert.assertEquals(longValue+"", client.get(key, null));

        client.remove(key, null);
        Assert.assertNull(client.get(key, null));
    }
    
    @Test
    public void testIncrementAndDecrement2() throws IOException, InterruptedException {
        client.set(key, Integer.valueOf(longValue), 10, null);
        Assert.assertEquals(longValue, client.get(key, null));

        int amount = 10;
        client.decrement(key, amount, null);
        
        Assert.assertEquals((longValue - amount), client.get(key, null));

        client.increment(key, amount, null);
        Assert.assertEquals(longValue, client.get(key, null));

        client.remove(key, null);
        Assert.assertNull(client.get(key, null));
    }

    @Test
    public void testGetBulks() throws IOException {
        client.set(key + "1", value + "1", 10, null);
        Assert.assertEquals(value + "1", client.get(key + "1", null));

        client.set(key + "2", value + "2", 10, null);
        Assert.assertEquals(value + "2", client.get(key + "2", null));

        List<String> list = new ArrayList<String>();
        list.add(key + "1");
        list.add(key + "2");

        Map<String, String> map = client.getBulk(list, null);

        Assert.assertNotNull(map);
        Assert.assertEquals(2, map.size());

        Assert.assertEquals(value + "1", map.get(key + "1"));
        Assert.assertEquals(value + "2", map.get(key + "2"));

        client.remove(key + "2", null);
        Assert.assertNull(client.get(key + "2", null));

        client.remove(key + "1", null);
        Assert.assertNull(client.get(key + "1", null));
    }

    @SuppressWarnings("static-access")
    @Test
    @Ignore
    public void testExpiration() throws IOException, InterruptedException {
        client.set(key, value, expiration, null);
        Assert.assertEquals(value, client.get(key, null));

        Thread.currentThread().sleep((expiration + 2) * 1000);

        Assert.assertNull(client.get(key, null));

        client.remove(key, null);
        Assert.assertNull(client.get(key, null));
    }

}
