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
 * The object that implements this interface will be set key. Generally, the
 * cache client implementations will drive from this interface for configuration
 * purpose.
 * 
 * @author guoqing.chen
 * 
 */
public interface KeyAware {

	/**
	 * Set cache client unique key(the key be used to indicate the client
	 * instance.)
	 */
	void setKey(String key);

	/**
	 * Retrieve the unique key
	 */
	String getKey();
}
