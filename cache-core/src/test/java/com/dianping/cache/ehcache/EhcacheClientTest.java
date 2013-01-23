/**
 * Project: cache-core
 * 
 * File Created at 2012-7-11 $Id$
 * 
 * Copyright 2010 dianping.com. All rights reserved.
 * 
 * This software is the confidential and proprietary information of Dianping
 * Company. ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with dianping.com.
 */
package com.dianping.cache.ehcache;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

/**
 * TODO Comment of EhcacheClientTest
 * 
 * @author Leo Liang
 * 
 */
public class EhcacheClientTest {

    @Test
    public void testHotKey() throws Exception {

        final String key = "a";
        int value = 111;
        int threadCount = 10;
        final EhcacheClientImpl cacheClient = new EhcacheClientImpl();
        cacheClient.init(new EhcacheConfiguration());
        cacheClient.start();
        cacheClient.set(key, value, 100, true, null);
        Assert.assertEquals(value, cacheClient.get(key, true, null));

        cacheClient.remove(key, null);

        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(threadCount);

        final Queue<Object> cacheReturnNullList = new ConcurrentLinkedQueue<Object>();

        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        start.await();

                        if (cacheClient.get(key, true, null) == null) {
                            cacheReturnNullList.add(new Object());
                        }

                        end.countDown();

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            });
            t.start();
        }

        start.countDown();

        end.await();
        Assert.assertEquals(1, cacheReturnNullList.size());
        cacheClient.shutdown();
    }

    @Test
    public void testHotKey2() throws Exception {

        final String key = "a";
        int value = 111;
        int threadCount = 10;
        final EhcacheClientImpl cacheClient = new EhcacheClientImpl();
        cacheClient.init(new EhcacheConfiguration());
        cacheClient.start();
        cacheClient.set(key, value, 100, true, null);
        Assert.assertEquals(value, cacheClient.get(key, true, null));

        cacheClient.remove(key, null);

        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(threadCount);

        final Queue<Object> cacheReturnNullList = new ConcurrentLinkedQueue<Object>();

        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        start.await();

                        if (cacheClient.get(key, true, null) == null) {
                            cacheReturnNullList.add(new Object());
                        }

                        Thread.sleep(31 * 1000);

                        if (cacheClient.get(key, true, null) == null) {
                            cacheReturnNullList.add(new Object());
                        }

                        end.countDown();

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            });
            t.start();
        }

        start.countDown();

        end.await();
        Assert.assertEquals(2, cacheReturnNullList.size());
        cacheClient.shutdown();
    }

    @Test
    public void testEternal() throws Exception {
        final String key = "a";
        int value = 111;
        final EhcacheClientImpl cacheClient = new EhcacheClientImpl();
        cacheClient.init(new EhcacheConfiguration());
        cacheClient.start();
        cacheClient.set(key, value, 1, true, null);
        int i = 0;
        while (i++ < 20) {
            Assert.assertNotNull(cacheClient.get(key + "_bak", null));
            Thread.sleep(1000);
        }
        cacheClient.shutdown();
    }

    @Test
    @Ignore
    public void testHotKey3() throws Exception {

        final String key = "a";
        final int oldValue = 111;
        final int newValue = 222;
        int threadCount = 10;
        final EhcacheClientImpl cacheClient = new EhcacheClientImpl();
        cacheClient.init(new EhcacheConfiguration());
        cacheClient.start();
        cacheClient.set(key, oldValue, 100, true, null);
        Assert.assertEquals(oldValue, cacheClient.get(key, true, null));

        cacheClient.remove(key, null);

        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(threadCount);

        final Queue<Integer> oldValueList = new ConcurrentLinkedQueue<Integer>();
        final Queue<Integer> newValueList = new ConcurrentLinkedQueue<Integer>();

        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        start.await();
                        Integer value = cacheClient.get(key, true, null);
                        if (value == null) {
                            cacheClient.add(key, newValue, 100, null);
                            Thread.sleep(3 * 1000);
                        } else {
                            if (value == oldValue) {
                                oldValueList.add(value);
                            } else if (value == newValue) {
                                newValueList.add(value);
                            }
                        }

                        Thread.sleep(5 * 1000);
                        value = cacheClient.get(key, true, null);
                        if (value == oldValue) {
                            oldValueList.add(value);
                        } else if (value == newValue) {
                            newValueList.add(value);
                        }
                        end.countDown();

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            });
            t.start();
        }

        start.countDown();

        end.await();
        Assert.assertEquals(threadCount - 1, oldValueList.size());
        Assert.assertEquals(threadCount, newValueList.size());
        cacheClient.shutdown();
    }
}
