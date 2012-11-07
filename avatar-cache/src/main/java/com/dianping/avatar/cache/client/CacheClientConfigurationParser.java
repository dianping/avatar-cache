/**
 * Project: avatar
 * 
 * File Created at 2010-10-18
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
package com.dianping.avatar.cache.client;

import com.dianping.cache.core.CacheClientConfiguration;
import com.dianping.remote.cache.dto.CacheConfigDetailDTO;

/**
 * Parse cache client configuration
 * @author danson.liu
 *
 */
public interface CacheClientConfigurationParser {

	/**
	 * @param detail
	 * @return
	 */
	CacheClientConfiguration parse(CacheConfigDetailDTO detail);

}
