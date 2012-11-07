/**
 * Project: com.dianping.avatar-cache-2.1.4
 * 
 * File Created at 2011-9-16
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
package com.dianping.avatar.cache.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO Comment of EnhancedCacheItemConfigManagerTest
 * @author liujian
 *
 */
public class EnhancedCacheItemConfigManagerTest {
	
	private static CacheItemConfigManager cacheItemConfigManager;
	
	private static final String CATEGORY_EXISTS = "category-exist";
	private static final String CATEGORY_EXISTS_CACHE_TYPE = "web";
	private static final String CATEGORY_INEXISTS = "category-inexist";
	
	@BeforeClass
	public static void before() {
		cacheItemConfigManager = new EnhancedCacheItemConfigManager(new MockCacheItemConfigManager());
	}

	/**
	 * Test method for {@link com.dianping.avatar.cache.configuration.EnhancedCacheItemConfigManager#getCacheKeyType(java.lang.String)}.
	 */
	@Test
	public void testGetExistentCacheKeyType() {
		CacheKeyType cacheKeyType = cacheItemConfigManager.getCacheKeyType(CATEGORY_EXISTS);
		assertNotNull(cacheKeyType);
		assertFalse(cacheKeyType instanceof DefaultCacheKeyType);
	}
	
	@Test
	public void testGetInexistentCacheKeyType() {
		CacheKeyType cacheKeyType = cacheItemConfigManager.getCacheKeyType(CATEGORY_INEXISTS);
		assertNotNull(cacheKeyType);
		assertTrue(cacheKeyType instanceof DefaultCacheKeyType);
		assertEquals(CacheKeyType.DEFAULT_CACHE_TYPE, cacheKeyType.getCacheType());
		assertEquals(CATEGORY_INEXISTS, cacheKeyType.getCategory());
		assertEquals("category-inexist.(hello)(vv)_0", cacheKeyType.getKey(new String[] {"hello", "vv"}));
	}
	
	static class MockCacheItemConfigManager implements CacheItemConfigManager {

		@Override
		public CacheKeyType getCacheKeyType(String category) {
			if (CATEGORY_EXISTS.equals(category)) {
				CacheKeyType cacheKeyType = new CacheKeyType();
				cacheKeyType.setCategory(CATEGORY_EXISTS);
				cacheKeyType.setCacheType(CATEGORY_EXISTS_CACHE_TYPE);
				cacheKeyType.setDuration("30m");
				cacheKeyType.setIndexTemplate("c{0}d{1}");
				cacheKeyType.setVersion(3);
				return cacheKeyType;
			}
			return null;
		}

	}

}

