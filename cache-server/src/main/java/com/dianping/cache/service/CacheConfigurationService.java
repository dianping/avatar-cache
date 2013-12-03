/**
 * Project: cache-server
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
package com.dianping.cache.service;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dianping.avatar.exception.DuplicatedIdentityException;
import com.dianping.cache.entity.CacheConfiguration;

/**
 * CacheKeyConfigurationService
 * @author danson.liu
 *
 */
@Transactional
public interface CacheConfigurationService {
	
	/**
	 * retrieve all configurations
	 * @return
	 */
	List<CacheConfiguration> findAll();
	
	CacheConfiguration find(String key);
	
	/**
	 * 批量清除，仅清除Java端
	 * @param category
	 */
	void clearByCategory(String category);
	
	/**
	 * 批量清除指定机器或组的缓存，仅清除Java端
	 * @param category
	 * @param serverOrGroup
	 */
	void clearByCategory(String category, String serverOrGroup);
	
	/**
	 * 批量清除，同时通知到.net系统
	 * @param category
	 */
	void clearByCategoryBothSide(String category);
	
	/**
	 * 仅在JAVA端清除缓存(可调整为同时清除.net缓存，但尚无需求，可在.net后台管理系统操作)
	 * @param cacheType
	 * @param key
	 */
	void clearByKey(String cacheType, String key);

	/**
	 * 前台APP调用，可同时发送消息通知.net清除相应缓存(category+params)
	 * @param cacheType
	 * @param key
	 * @param category
	 * @param params
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	void clearByKeyBothSide(String cacheType, String key, String category, List<Object> params);
	
	CacheConfiguration create(CacheConfiguration config) throws DuplicatedIdentityException;
	
	CacheConfiguration update(CacheConfiguration config);
	
	void delete(String key);
	
	void incVersion(String category);
	
	void pushCategoryConfig(String category, String serverOrGroup);
	
}
