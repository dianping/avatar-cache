/**
 * Project: avatar-cache
 * 
 * File Created at 2010-7-12
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

/**
 * The interface is used for the general purpose of retrieving configuration
 * items.
 * 
 * @author guoqing.chen
 * 
 */
public interface InitialConfiguration {

	/**
	 * Initialize the cache client
	 */
	void init(CacheClientConfiguration config);
}
