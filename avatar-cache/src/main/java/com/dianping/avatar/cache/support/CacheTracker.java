/**
 * Project: avatar
 * 
 * File Created at 2010-11-1
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
package com.dianping.avatar.cache.support;

/**
 * Cache Profiler
 * @author danson.liu
 *
 */
public interface CacheTracker {

	/**
	 * @param cacheDesc
	 * @param timeCost
	 */
	void addGetInfo(String cacheDesc, long timeConsumed);

}
