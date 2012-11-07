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

import com.dianping.cache.core.CacheClientConfiguration;

/**
 * The configuration for local cache client implementation
 * @author danson.liu
 *
 */
public class LocalCacheConfiguration implements CacheClientConfiguration {
	
	private long maxIdleTime = -1;

	public long getMaxIdleTime() {
		return maxIdleTime;
	}

	public void setMaxIdleTime(long maxIdleTime) {
		this.maxIdleTime = maxIdleTime;
	}

}
