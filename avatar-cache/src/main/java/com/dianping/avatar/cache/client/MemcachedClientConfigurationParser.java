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

import java.util.List;

import net.spy.memcached.transcoders.Transcoder;

import org.apache.commons.lang.ClassUtils;

import com.dianping.cache.core.CacheClientConfiguration;
import com.dianping.cache.memcached.MemcachedClientConfiguration;
import com.dianping.remote.cache.dto.CacheConfigDetailDTO;
import com.dianping.remote.cache.dto.MemcachedConfigDetailDTO;

/**
 * TODO Comment of MemcachedClientConfigurationParser
 * @author danson.liu
 *
 */
public class MemcachedClientConfigurationParser implements CacheClientConfigurationParser {

	@SuppressWarnings("unchecked")
	@Override
	public CacheClientConfiguration parse(CacheConfigDetailDTO detail) {
		assert detail instanceof MemcachedConfigDetailDTO;
		MemcachedClientConfiguration config = new MemcachedClientConfiguration();

		MemcachedConfigDetailDTO memcachedDetail = (MemcachedConfigDetailDTO) detail;
		String transcoderClass = memcachedDetail.getTranscoderClazz();

		if (transcoderClass != null && !transcoderClass.trim().isEmpty()) {
			try {
				Class<?> cz = ClassUtils.getClass(transcoderClass.trim());
				Transcoder<Object> transcoder = (Transcoder<Object>) cz.newInstance();
				config.setTranscoder(transcoder);
			} catch (Exception ex) {
				throw new RuntimeException("Failed to set memcached's transcoder.", ex);
			}
		}
		
		List<String> serverList = memcachedDetail.getServerList();
		if (serverList == null || serverList.size() == 0) {
			throw new RuntimeException("Memcached config's server list must not be empty.");
		}
		config.setServers(serverList);
		return config;
	}

}
