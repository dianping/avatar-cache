/**
 * Project: avatar-cache-remote
 * 
 * File Created at 2010-10-14
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
package com.dianping.remote.cache;

import java.util.List;

import com.dianping.remote.cache.dto.CacheConfigurationsDTO;
import com.dianping.remote.cache.dto.CacheKeyConfigurationDTO;

/**
 * CacheConfiguration poll interface
 * @author danson.liu
 *
 */
public interface CacheConfigurationWebService {

	/**
	 * retrieve all cache key configurations
	 * @return
	 */
	List<CacheKeyConfigurationDTO> getKeyConfigurations();
	
	/**
	 * retrieve cache client configuration, e.g. memcached, kvdb
	 * @return
	 */
	CacheConfigurationsDTO getCacheConfigurations();
	
}
