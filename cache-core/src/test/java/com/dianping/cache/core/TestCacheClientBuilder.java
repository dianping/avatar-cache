/**
 * Project: cache-core
 * 
 * File Created at 2010-7-20
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
package com.dianping.cache.core;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.cache.memcached.MemcachedClientConfiguration;

/**
 * {@link CacheClientBuilder} test class.
 * 
 * @author guoqing.chen
 * 
 */

public class TestCacheClientBuilder {

	@Test
	public void testClasspathBuilder() throws IOException {
		CacheConfiguration.init("classpath:testClasspathBuilder.properties");
		MemcachedClientConfiguration config = new MemcachedClientConfiguration();
		config.addServer("192.168.8.45", 11211);
		CacheClient client = CacheClientBuilder.buildCacheClient("memcached", config);

		Assert.assertNotNull(client);

		CacheClientBuilder.closeCacheClient("memcached");
	}

	@Test
	public void testManualBuilder() throws IOException {
		CacheConfiguration.addCache("test", "com.dianping.cache.core.TestCacheClientBuilder$TestCacheClient");
		CacheClient client = CacheClientBuilder.buildCacheClient("test", null);
		Assert.assertNotNull(client);
	}

	public static class TestCacheClient implements CacheClient {

		@Override
		public void add(String key, Object value, int expiration, String category) {
		}

		@Override
		public long decrement(String key, int amount, String category) {
			return 0;
		}

		@Override
		public <T> T get(String key, String category) {
			return null;
		}

		@Override
		public <T> Map<String, T> getBulk(Collection<String> keys, Map<String, String> categories) {
			return null;
		}

		@Override
		public long increment(String key, int amount, String category) {
			return 0;
		}

		@Override
		public void clear() {
		}

		@Override
		public void remove(String key, String category) {
		}

		@Override
		public void replace(String key, Object value, int expiration, String category) {
		}

		@Override
		public void set(String key, Object value, int expiration, String category) {

		}

		@Override
		public boolean isDistributed() {
			return false;
		}

		/* (non-Javadoc)
		 * @see com.dianping.cache.core.CacheClient#get(java.lang.String, boolean)
		 */
		@Override
		public <T> T get(String key, boolean isHot, String category) {
			// TODO Auto-generated method stub
			return null;
		}

        /* (non-Javadoc)
         * @see com.dianping.cache.core.CacheClient#set(java.lang.String, java.lang.Object, int, boolean)
         */
        @Override
        public void set(String key, Object value, int expiration, boolean isHot, String category) {
            // TODO Auto-generated method stub
            
        }

		/* (non-Javadoc)
		 * @see com.dianping.cache.core.CacheClient#remove(java.lang.String)
		 */
		@Override
		public void remove(String key) {
			// TODO Auto-generated method stub
			
		}
	}
}
