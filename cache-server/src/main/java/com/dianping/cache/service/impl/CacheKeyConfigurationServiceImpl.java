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
package com.dianping.cache.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.avatar.exception.DuplicatedIdentityException;
import com.dianping.cache.dao.CacheKeyConfigurationDao;
import com.dianping.cache.entity.CacheKeyConfiguration;
import com.dianping.cache.remote.jms.CacheMessageProducer;
import com.dianping.cache.remote.translator.CacheKeyConfiguration2DTOTranslator;
import com.dianping.cache.service.CacheKeyConfigurationService;
import com.dianping.cache.service.OperationLogService;
import com.dianping.cache.service.condition.CacheKeyConfigSearchCondition;
import com.dianping.core.type.PageModel;
import com.dianping.remote.cache.dto.CacheKeyConfigurationDTO;
import com.dianping.remote.share.Translator;

/**
 * CacheKeyConfiguration service implementation
 * @author danson.liu
 *
 */
public class CacheKeyConfigurationServiceImpl implements CacheKeyConfigurationService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private OperationLogService operationLogService;
	
	private CacheKeyConfigurationDao configurationDao;
	
	private CacheMessageProducer cacheMessageProducer;
	
	private Translator<CacheKeyConfiguration, CacheKeyConfigurationDTO> translator = new CacheKeyConfiguration2DTOTranslator();

	@Override
	public List<CacheKeyConfiguration> findAll() {
		return configurationDao.findAll();
	}

	@Override
	public PageModel paginate(PageModel paginater, CacheKeyConfigSearchCondition searchCondition) {
		return configurationDao.paginate(paginater, searchCondition);
	}

	@Override
	public CacheKeyConfiguration find(String category) {
		return configurationDao.find(category);
	}

    /**
     * @see com.dianping.cache.service.CacheKeyConfigurationService#incAndRetriveVersion(java.lang.String)
     */
    @Override
    public String incAndRetriveVersion(String category) {
        configurationDao.incVersion(category);
        return configurationDao.loadVersion(category);
    }

	@Override
	public CacheKeyConfiguration create(CacheKeyConfiguration config) throws DuplicatedIdentityException {
		try {
			String category = config.getCategory();
			CacheKeyConfiguration found = find(category);
			if (found != null) {
				throw new DuplicatedIdentityException("CacheKey with category[" + category + "] already exists.");
			}
			configurationDao.create(config);
			CacheKeyConfiguration created = configurationDao.find(category);
			cacheMessageProducer.sendMessageToTopic(translator.translate(created));
			logCacheKeyConfigCreate(config, true);
			return created;
		} catch (RuntimeException e) {
			logger.error("Create CacheKey config failed.", e);
			logCacheKeyConfigCreate(config, false);
			throw e;
		}
	}

	@Override
	public CacheKeyConfiguration update(CacheKeyConfiguration config) {
		CacheKeyConfiguration oldConfig = null;
		try {
			oldConfig = find(config.getCategory());
			configurationDao.update(config);
			//保存后，从新从数据库加载数据，可能有数据库级别的触发逻辑
			CacheKeyConfiguration updated = configurationDao.find(config.getCategory());
			cacheMessageProducer.sendMessageToTopic(translator.translate(updated));
			logCacheKeyConfigUpdate(oldConfig, updated, true);
			return updated;
		} catch (RuntimeException e) {
			logCacheKeyConfigUpdate(oldConfig, config, false);
			logger.error("Update CacheKey config failed.", e);
			throw e;
		}
	}

	@Override
	public void delete(String category) {
		CacheKeyConfiguration configFound = find(category);
		try {
			configurationDao.delete(category);
			logCacheKeyConfigDelete(configFound, true);
		} catch (RuntimeException e) {
			logCacheKeyConfigDelete(configFound, false);
			logger.error("Delete CacheKey config failed.", e);
			throw e;
		}
	}

	private void logCacheKeyConfigDelete(CacheKeyConfiguration config, boolean succeed) {
		if (config != null) {
			operationLogService.create(succeed, "删除缓存项配置", transferConfigDetail(config, null), true);
		}
	}

	private void logCacheKeyConfigCreate(CacheKeyConfiguration config, boolean succeed) {
		operationLogService.create(succeed, "创建缓存项配置", transferConfigDetail(config, null), true);
	}

	private void logCacheKeyConfigUpdate(CacheKeyConfiguration oldConfig, CacheKeyConfiguration newConfig, boolean succeed) {
		Map<String, String> detail = new TreeMap<String, String>();
		if (oldConfig != null) {
			detail.putAll(transferConfigDetail(oldConfig, "old"));
		} else {
			detail.put("old.config", null);
		}
		detail.putAll(transferConfigDetail(newConfig, "new"));
		operationLogService.create(succeed, "修改缓存项配置", detail, true);
	}
	
	private Map<String, String> transferConfigDetail(CacheKeyConfiguration config, String prefix) {
		prefix = prefix != null ? prefix + "." : "";
		Map<String, String> detail = new HashMap<String, String>();
		detail.put(prefix + "category", config.getCategory());
		detail.put(prefix + "duration", config.getDuration());
		detail.put(prefix + "indexTemplate", config.getIndexTemplate());
		detail.put(prefix + "indexDesc", config.getIndexDesc());
		detail.put(prefix + "cacheType", config.getCacheType());
		detail.put(prefix + "version", String.valueOf(config.getVersion()));
		return detail;
	}

	public void setConfigurationDao(CacheKeyConfigurationDao configurationDao) {
		this.configurationDao = configurationDao;
	}

	/**
	 * @param cacheMessageProducer the cacheMessageProducer to set
	 */
	public void setCacheMessageProducer(CacheMessageProducer cacheMessageProducer) {
		this.cacheMessageProducer = cacheMessageProducer;
	}

	public void setOperationLogService(OperationLogService operationLogService) {
		this.operationLogService = operationLogService;
	}

}
