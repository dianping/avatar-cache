/**
 * Project: cache-builder
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
package com.dianping.cache.builder.metadata;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.cache.builder.CacheClientFactory;
import com.dianping.cache.core.CacheClient;
import com.dianping.cache.core.CacheClientBuilder;

/**
 * Test class for XMLMetadataFactory
 * 
 * @author guoqing.chen
 * 
 */
public class TestXMLMetadataFactory {

	@Test
	public void testBuilderFromConfig() {
		CacheClientFactory factory = new XMLCacheClientFactory("classpath:caches.xml");

		CacheClient client = factory.findCacheClient("memcached");

		Assert.assertNotNull(client);

		CacheClientBuilder.closeCacheClient("memcached");

		try {
			factory.findCacheClient("444");
			Assert.assertTrue(false);
		} catch (Exception e) {

		}
		
		List<String> keys = factory.getCacheKeys();
		Assert.assertNotNull(keys);
		Assert.assertEquals(keys.size(), 1);
	}
}
