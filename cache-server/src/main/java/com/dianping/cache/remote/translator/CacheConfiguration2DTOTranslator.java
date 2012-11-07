/**
 * Project: cache-server
 * 
 * File Created at 2010-10-19
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
package com.dianping.cache.remote.translator;

import com.dianping.cache.entity.CacheConfiguration;
import com.dianping.cache.entity.SupportedSpecification.SupportedCacheClients;
import com.dianping.remote.cache.dto.CacheConfigDetailDTO;
import com.dianping.remote.cache.dto.CacheConfigurationDTO;
import com.dianping.remote.cache.dto.EhcacheConfigDetailDTO;
import com.dianping.remote.cache.dto.MemcachedConfigDetailDTO;
import com.dianping.remote.share.Translator;

/**
 * CacheConfiguration2DTO Translator
 * @author danson.liu
 *
 */
public class CacheConfiguration2DTOTranslator implements Translator<CacheConfiguration, CacheConfigurationDTO> {

	@Override
	public CacheConfigurationDTO translate(CacheConfiguration source) {
		CacheConfigurationDTO configuration = new CacheConfigurationDTO();
		configuration.setKey(source.getCacheKey());
		configuration.setDetail(translate2detail(source));
		return configuration;
	}

	/**
	 * @param source
	 * @return
	 */
	private CacheConfigDetailDTO translate2detail(CacheConfiguration source) {
		String clientClazz = source.getClientClazz();
		if (SupportedCacheClients.MEMCACHED_CLIENT_CLAZZ.equals(clientClazz)) {
			return translateMemcachedConfigDetail(source);
		} else if (SupportedCacheClients.EHCACHE_CLIENT_CLAZZ.equals(clientClazz)) {
			return translateEhcacheConfigDetail(source);
		}
		throw new UnsupportedOperationException("Configuration detail translation with client class[" 
				+ clientClazz + "] not supported now.");
	}

	/**
	 * @param source
	 * @return
	 */
	private CacheConfigDetailDTO translateEhcacheConfigDetail(CacheConfiguration source) {
		EhcacheConfigDetailDTO detail = new EhcacheConfigDetailDTO();
		return detail;
	}

	/**
	 * @param source
	 * @return
	 */
	private CacheConfigDetailDTO translateMemcachedConfigDetail(CacheConfiguration source) {
		MemcachedConfigDetailDTO detail = new MemcachedConfigDetailDTO();
		detail.setServerList(source.getServerList());
		detail.setTranscoderClazz(source.getTranscoderClazz());
		return detail;
	}

}
