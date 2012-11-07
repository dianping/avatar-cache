/**
 * Project: avatar
 * 
 * File Created at 2010-7-13
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
package com.dianping.cache.builder;

import java.util.List;

import com.dianping.cache.core.CacheClient;


/**
 * Cache service factory, it can retrieve available cache keys and find
 * {@link CacheService} implementation by cache key.
 * 
 * @author guoqing.chen
 * 
 */
public interface CacheClientFactory {

	/**
	 * Retrieve all available cache keys
	 */
	List<String> getCacheKeys();

	/**
	 * Retrieve a {@link CacheService} instance by key
	 */
	CacheClient findCacheClient(String cacheKey);
	
}
