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
 * Life cycle interface for representing Object that can be managed. Generally,
 * it will be extended by CacheClient implementation for destroy.
 * 
 * @author guoqing.chen
 * 
 */
public interface Lifecycle {

	/**
	 * Start the Object
	 */
	void start();

	/**
	 * Shutdown the object
	 */
	void shutdown();
}
