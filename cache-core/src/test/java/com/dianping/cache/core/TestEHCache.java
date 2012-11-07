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
import org.junit.Test;

import com.dianping.cache.ehcache.EhcacheConfiguration;

/**
 * 
 * @author pengshan.zhang
 * 
 */
public class TestEHCache {

    private CacheClient client;

    private final String key = "key";

    private final String value = "value";

    private final String replaceValue = "replaceValue";

    private final long longValue = 10;

    private final int expiration = 5;

    @Before
    public void setUp() throws IOException {
        CacheConfiguration.init("classpath:com/dianping/cache/core/testClasspathBuilder.properties");
        EhcacheConfiguration config = new EhcacheConfiguration();
        client = CacheClientBuilder.buildCacheClient("ehcache", config);
    }

    @After
    public void tearDown() {
        CacheClientBuilder.closeCacheClient("ehcache");
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
    public void testIncrementAndDecrement() throws IOException {
        client.set(key, longValue, 10, null);
        Assert.assertEquals(longValue, client.get(key, null));

        int amount = 10;
        client.decrement(key, amount, null);
        Assert.assertEquals(longValue - amount, client.get(key, null));

        client.increment(key, amount, null);
        Assert.assertEquals(longValue, client.get(key, null));

        client.remove(key, null);
        Assert.assertNull(client.get(key, null));
    }

    @Test
    public void testIncrementAndDecrementException() throws IOException {
        client.set(key, value, 10, null);
        Assert.assertEquals(value, client.get(key, null));

        int amount = 10;
        long result = client.decrement(key, amount, null);
        Assert.assertEquals(result, -1);
        Assert.assertEquals(value, client.get(key, null));

        result = client.increment(key, amount, null);
        Assert.assertEquals(result, -1);
        Assert.assertEquals(value, client.get(key, null));

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
    public void testExpiration() throws IOException, InterruptedException {
        client.set(key, value, expiration, null);
        Assert.assertEquals(value, client.get(key, null));

        Thread.currentThread().sleep((expiration + 2) * 1000);

        Assert.assertNull(client.get(key, null));

        client.remove(key, null);
        Assert.assertNull(client.get(key, null));
    }

}
