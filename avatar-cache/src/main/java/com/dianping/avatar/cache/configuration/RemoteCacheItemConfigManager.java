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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.remote.cache.CacheConfigurationWebService;
import com.dianping.remote.cache.dto.CacheKeyConfigurationDTO;
import com.dianping.remote.util.DTOUtils;

/**
 * Remote centralized managed cache item config
 * @author danson.liu
 *
 */
public class RemoteCacheItemConfigManager implements CacheItemConfigManager, InitializingBean {
	
	private static transient AvatarLogger logger = AvatarLoggerFactory.getLogger(RemoteCacheItemConfigManager.class);
	
	private CacheConfigurationWebService configurationWebService;
	
	private Map<String, CacheKeyType> cacheKeyTypes = new ConcurrentHashMap<String, CacheKeyType>();
	
	private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
	private ReadLock readLock = readWriteLock.readLock();
	private WriteLock writeLock = readWriteLock.writeLock();

	@Override
	public CacheKeyType getCacheKeyType(String category) {
		readLock.lock();
		try {
			return cacheKeyTypes.get(category);
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 
	 */
	private void pollConfigurationFromServer() {
		writeLock.lock();
		try {
			cacheKeyTypes.clear();
			List<CacheKeyConfigurationDTO> allConfigurations = configurationWebService.getKeyConfigurations();
			for (CacheKeyConfigurationDTO configurationDTO : allConfigurations) {
				registerCacheKey(configurationDTO);
			}
		} catch (Exception e) {
			String errorMsg = "Poll cache key configuration from cache server failed.";
			logger.error(errorMsg, e);
			throw new RuntimeException(errorMsg, e);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * @param configurationDTO
	 */
	public void updateConfig(CacheKeyConfigurationDTO configurationDTO) {
		writeLock.lock();
		try {
			registerCacheKey(configurationDTO);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * @param configurationDTO
	 */
	private void registerCacheKey(CacheKeyConfigurationDTO configurationDTO) {
		CacheKeyType cacheKeyType = new CacheKeyType();
		DTOUtils.copyProperties(cacheKeyType, configurationDTO);
		cacheKeyTypes.put(cacheKeyType.getCategory(), cacheKeyType);
	}

	public void setConfigurationWebService(CacheConfigurationWebService configurationWebService) {
		this.configurationWebService = configurationWebService;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(configurationWebService, "configurationWebService required.");
		//poll configuration from remote cache server's service
		pollConfigurationFromServer();
	}

}
