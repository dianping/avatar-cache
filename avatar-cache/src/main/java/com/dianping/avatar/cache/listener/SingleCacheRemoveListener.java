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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.avatar.util.IPUtils;
import com.dianping.cache.builder.CacheClientFactory;
import com.dianping.cache.core.CacheClient;
import com.dianping.remote.cache.dto.SingleCacheRemoveDTO;

/**
 * LocalCacheRemoveListener is used to remove local cache after receiving
 * removing local cache message.
 * 
 * @author pengshan.zhang
 * @author danson.liu
 * 
 */
public class SingleCacheRemoveListener {
    
    /**
     * Logger instance.
     */
    private final AvatarLogger logger = AvatarLoggerFactory.getLogger(SingleCacheRemoveListener.class);
    
    private static final String CACHE_FINAL_KEY_SEP = "@|$";

    /**
     * CacheClient factory instance
     */
    private CacheClientFactory cacheClientFactory;
    
    public void handleMessage(SingleCacheRemoveDTO cacheRemoveDTO) {
    	if (cacheRemoveDTO != null) {
    		List<String> destinations = cacheRemoveDTO.getDestinations();
    		String serverIp = IPUtils.getFirstNoLoopbackIP4Address();
    		if (destinations == null || destinations.contains(serverIp)) {
	    		String cacheType = cacheRemoveDTO.getCacheType();
	            String cacheKeys = cacheRemoveDTO.getCacheKey();
	            CacheClient cacheClient = cacheClientFactory.findCacheClient(cacheType);
	            if (cacheClient != null) {
	            	String[] keyList = StringUtils.splitByWholeSeparator(cacheKeys, CACHE_FINAL_KEY_SEP);
	            	if (keyList != null) {
	            		List<String> failedKeys = new ArrayList<String>();
	            		Throwable lastError = null;
		            	for (String finalKey : keyList) {
		            		try {
		            			cacheClient.remove(finalKey);
		            		} catch (Throwable e) {
		            			failedKeys.add(finalKey);
		            			lastError = e;
		            		}
		            	}
		            	if (!failedKeys.isEmpty()) {
		            		logger.info("Clear cache with key[" + cacheKeys + "] from cache[" + cacheType + "], but [" 
		            				+ StringUtils.join(failedKeys, ',') + "] failed.", lastError);
		            	} else {
		            		logger.info("Clear cache with key[" + cacheKeys + "] from cache[" + cacheType + "] succeed.");
		            	}
	            	}
	            } else {
	            	logger.error("Clear cache with key[" + cacheKeys + "] failed, because cache[" + cacheType + "] not found.");
	            }
    		}
    	}
    }

    /**
     * @param cacheClientFactory
     *            the cacheClientFactory to set
     */
    public void setCacheClientFactory(CacheClientFactory cacheClientFactory) {
        this.cacheClientFactory = cacheClientFactory;
    }

}
