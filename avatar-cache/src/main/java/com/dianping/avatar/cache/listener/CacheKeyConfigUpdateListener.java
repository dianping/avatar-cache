/**
 * Project: com.dianping.avatar-cache-2.0.0-SNAPSHOT
 * 
 * File Created at 2011-2-20
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

import com.dianping.avatar.cache.configuration.RemoteCacheItemConfigManager;
import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.remote.cache.dto.CacheKeyConfigurationDTO;

/**
 * Cache Key Configuration Update Listener
 * @author danson.liu
 *
 */
public class CacheKeyConfigUpdateListener {
	
	private final AvatarLogger logger = AvatarLoggerFactory.getLogger(CacheKeyConfigUpdateListener.class);
	
	private RemoteCacheItemConfigManager cacheItemConfigManager;

	public void handleMessage(CacheKeyConfigurationDTO configurationDTO) {
		if (configurationDTO != null) {
			try {
				if (cacheItemConfigManager != null) {
					cacheItemConfigManager.updateConfig(configurationDTO);
					logger.info("CacheItem[" + configurationDTO.getCacheType() + "]'s config update succeed.");
				}
			} catch (Exception e) {
				logger.error("CacheItem[" + configurationDTO.getCacheType() + "]'s config update failed.", e);
			}
		}
	}

	/**
	 * @param cacheItemConfigManager the cacheItemConfigManager to set
	 */
	public void setCacheItemConfigManager(RemoteCacheItemConfigManager cacheItemConfigManager) {
		this.cacheItemConfigManager = cacheItemConfigManager;
	}
	
}
