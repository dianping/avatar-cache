/**
 * Project: com.dianping.avatar-cache-2.0.0-SNAPSHOT
 * 
 * File Created at 2011-2-19
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
package com.dianping.avatar.cache.listener;

import com.dianping.avatar.cache.client.RemoteCacheClientFactory;
import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.remote.cache.dto.CacheConfigurationDTO;

/**
 * Cache Configuration Update Listener
 * @author danson.liu
 *
 */
public class CacheConfigurationUpdateListener {

	private final AvatarLogger logger = AvatarLoggerFactory.getLogger(CacheConfigurationUpdateListener.class);
	
	private RemoteCacheClientFactory cacheClientFactory;
	
	public void handleMessage(CacheConfigurationDTO configurationDTO) {
		if (configurationDTO != null) {
			try {
				if (cacheClientFactory != null) {
					cacheClientFactory.updateCache(configurationDTO);
					logger.info("Cache[" + configurationDTO.getKey() + "]'s config update succeed.");
				}
			} catch (Exception e) {
				logger.error("Cache[" + configurationDTO.getKey() + "]'s config update failed.", e);
			}
		}
	}

	/**
	 * @param cacheClientFactory the cacheClientFactory to set
	 */
	public void setCacheClientFactory(RemoteCacheClientFactory cacheClientFactory) {
		this.cacheClientFactory = cacheClientFactory;
	}
	
}
