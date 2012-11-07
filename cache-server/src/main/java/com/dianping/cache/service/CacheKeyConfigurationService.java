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

import org.springframework.transaction.annotation.Transactional;

import com.dianping.avatar.exception.DuplicatedIdentityException;
import com.dianping.cache.entity.CacheKeyConfiguration;
import com.dianping.cache.service.condition.CacheKeyConfigSearchCondition;
import com.dianping.core.type.PageModel;

/**
 * CacheKeyConfigurationService
 * @author danson.liu
 *
 */
@Transactional
public interface CacheKeyConfigurationService {
	
	/**
	 * retrieve all configurations
	 * @return
	 */
	List<CacheKeyConfiguration> findAll();
	
	PageModel paginate(PageModel paginater, CacheKeyConfigSearchCondition searchCondition);
	
	CacheKeyConfiguration find(String category);
	
	/**
	 * inc version and get version 
	 */
	String incAndRetriveVersion(String keyType);

	/**
	 * @param config
	 */
	CacheKeyConfiguration create(CacheKeyConfiguration config) throws DuplicatedIdentityException;

	/**
	 * @param config
	 */
	CacheKeyConfiguration update(CacheKeyConfiguration config);
	
	void delete(String category);
	
}
