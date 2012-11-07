/**
 * Project: avatar
 * 
 * File Created at 2010-10-15
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
package com.dianping.avatar.cache.configuration;

/**
 * Cache Item Config Manager
 * @author danson.liu
 *
 */
public interface CacheItemConfigManager {

	/**
	 * @param category
	 */
	CacheKeyType getCacheKeyType(String category);

}
