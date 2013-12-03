/**
 * Project: com.dianping.avatar-cache-remote-2.0.0-SNAPSHOT
 * 
 * File Created at 2011-2-15
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
package com.dianping.remote.cache;

import com.dianping.remote.cache.dto.CacheClearDTO;
import com.dianping.remote.cache.dto.CacheKeyConfigurationDTO;
import com.dianping.remote.cache.dto.GenericCacheConfigurationDTO;

/**
 * Cache Manage Web Service
 * @author danson.liu
 *
 */
public interface CacheManageWebService {
	
	/**
	 * 清除指定类别的所有缓存项
	 * @param category
	 * @see CacheManageWebService#clearByKey(CacheClearDTO)方法的"注意"项，相同
	 */
	void clearByCategory(String category);
	
	/**
	 * 清除指定缓存服务下指定key的缓存项
	 * @param key
	 * @deprecated 为了兼容老版本的avatar-cache，全部升级后移除
	 * @see CacheManageWebService#clearByKey(CacheClearDTO)方法的"注意"项，相同
	 */
	@Deprecated
	void clearByKey(String cacheType, String key);
	
	/**
	 * 注意，在调用该接口时，需要在调用端异步调用，因为目前还需要发送消息到activemq，而activemq很无稳定
	 * 不在该接口实现中做异步，因为调用端可能会关心清空是否成功的结果
	 * @param cacheClear
	 */
	void clearByKey(CacheClearDTO cacheClear);
	
	void createConfiguration(GenericCacheConfigurationDTO configuration);
	
	void updateConfiguration(GenericCacheConfigurationDTO configuration);
	
	void createCacheKeyConfig(CacheKeyConfigurationDTO config);
	
	void updateCacheKeyConfig(CacheKeyConfigurationDTO config);

    /**
     * @param category
     * @param serverOrGroup
     */
    void clearByCategory(String category, String serverOrGroup);
    
    void incVersion(String category);
    
    void pushCategoryConfig(String category, String serverOrGroup);
	
}
