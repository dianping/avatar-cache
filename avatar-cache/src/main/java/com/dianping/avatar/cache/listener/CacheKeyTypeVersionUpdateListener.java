/**
 * Project: avatar
 * 
 * File Created at 2010-10-15
 * $Id$
 * 
 * Copyright 2010 dianping.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.avatar.cache.listener;

import java.util.List;

import com.dianping.avatar.cache.configuration.CacheItemConfigManager;
import com.dianping.avatar.cache.configuration.CacheKeyType;
import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.avatar.util.IPUtils;
import com.dianping.remote.cache.dto.CacheKeyTypeVersionUpdateDTO;

/**
 * CacheKeyTypeVersionUpdateListener is used to listen message that to Update
 * version for some key type.
 * 
 * @author pengshan.zhang
 * 
 */
public class CacheKeyTypeVersionUpdateListener {

    private final AvatarLogger logger = AvatarLoggerFactory.getLogger(CacheKeyTypeVersionUpdateListener.class);

    private CacheItemConfigManager cacheItemConfigManager;
    
    public void handleMessage(CacheKeyTypeVersionUpdateDTO versionUpdateDTO) {
    	if (versionUpdateDTO != null) {
    		List<String> destinations = versionUpdateDTO.getDestinations();
    		String serverIp = IPUtils.getFirstNoLoopbackIP4Address();
    		if (destinations == null || destinations.contains(serverIp)) {
	    		String category = versionUpdateDTO.getMsgValue();
	            String version = versionUpdateDTO.getVersion();
	            int versionInt = 0;
	            try {
	                versionInt = Integer.parseInt(version);
	            } catch (Exception e) {
	            	logger.error("Illegal cache version[" + version + "] found, it must be integer.");
	                return;
	            }
	            CacheKeyType keyType = cacheItemConfigManager.getCacheKeyType(category);
	            if (keyType != null) {
	                keyType.setVersion(versionInt);
	                logger.info("Update cache version with category[" + category + "] succeed.");
	            } else {
	            	logger.error("Update cache version failed, because category[" + category + "] not found.");
	            }
    		}
    	}
    }

    /**
     * @param cacheItemConfigManager
     *            the cacheItemConfigManager to set
     */
    public void setCacheItemConfigManager(CacheItemConfigManager cacheItemConfigManager) {
        this.cacheItemConfigManager = cacheItemConfigManager;
    }

}
