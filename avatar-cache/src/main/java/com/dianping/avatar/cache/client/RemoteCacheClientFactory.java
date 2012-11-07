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

import java.util.ArrayList;
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
import com.dianping.cache.builder.CacheClientFactory;
import com.dianping.cache.core.CacheClient;
import com.dianping.cache.core.CacheClientBuilder;
import com.dianping.cache.core.CacheClientConfiguration;
import com.dianping.cache.core.CacheConfiguration;
import com.dianping.remote.cache.CacheConfigurationWebService;
import com.dianping.remote.cache.dto.CacheConfigDetailDTO;
import com.dianping.remote.cache.dto.CacheConfigurationDTO;
import com.dianping.remote.cache.dto.CacheConfigurationsDTO;

/**
 * Remote centralized managed cache client config
 * @author danson.liu
 *
 */	
public class RemoteCacheClientFactory implements CacheClientFactory, InitializingBean {
	
	private static transient AvatarLogger logger = AvatarLoggerFactory.getLogger(RemoteCacheClientFactory.class);
	
	private Map<String, CacheClientConfiguration> configMap = new ConcurrentHashMap<String, CacheClientConfiguration>();
	
	private CacheConfigurationWebService configurationWebService;
	
	private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
	private ReadLock readLock = readWriteLock.readLock();
	private WriteLock writeLock = readWriteLock.writeLock();

	@Override
	public CacheClient findCacheClient(String cacheKey) {
		readLock.lock();
		try {
			CacheClientConfiguration config = configMap.get(cacheKey);
			if (config == null) {
				throw new IllegalArgumentException("The configuraiton is not found for cache key[" + cacheKey + "].");
			}
			return CacheClientBuilder.buildCacheClient(cacheKey, config);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public List<String> getCacheKeys() {
		readLock.lock();
		try {
			return new ArrayList<String>(configMap.keySet());
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * poll configuration from remote cache server
	 */
	private void pollConfigurationFromServer() {
		writeLock.lock();
		try {
			configMap.clear();
			CacheConfigurationsDTO configurations = configurationWebService.getCacheConfigurations();
			for (String key : configurations.keys()) {
				registerCache(configurations.getConfiguration(key));
			}
		} catch (Exception e) {
			String errorMsg = "Poll cache configuration from cache server failed.";
			logger.error(errorMsg, e);
			throw new RuntimeException(errorMsg, e);
		} finally {
			writeLock.unlock();
		}
	}
	
	public void updateCache(CacheConfigurationDTO configurationDTO) {
		writeLock.lock();
		try {
			registerCache(configurationDTO);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * @param key
	 * @param configuration
	 */
	private void registerCache(CacheConfigurationDTO configuration) {
		String cacheKey = configuration.getKey();
		CacheConfigDetailDTO detail = configuration.getDetail();
		CacheConfiguration.removeCache(cacheKey);
		CacheClientBuilder.closeCacheClient(cacheKey);
		CacheConfiguration.addCache(cacheKey, detail.getClientClazz());
		configMap.put(cacheKey, CacheClientConfigurationHelper.parse(detail));
	}

	public void setConfigurationWebService(CacheConfigurationWebService configurationWebService) {
		this.configurationWebService = configurationWebService;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(configurationWebService, "configurationWebService required.");
		pollConfigurationFromServer();
	}
	
}
