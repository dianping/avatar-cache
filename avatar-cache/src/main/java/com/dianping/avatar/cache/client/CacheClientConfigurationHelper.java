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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dianping.cache.core.CacheClient;
import com.dianping.cache.core.CacheClientConfiguration;
import com.dianping.cache.ehcache.EhcacheClientImpl;
import com.dianping.cache.kvdb.KvdbClientImpl;
import com.dianping.cache.memcached.MemcachedClientImpl;
import com.dianping.remote.cache.dto.CacheConfigDetailDTO;

/**
 * CacheClientConfiguration parse helper class
 * 
 * @author danson.liu
 * 
 */
public class CacheClientConfigurationHelper {

	private static Map<Class<? extends CacheClient>, CacheClientConfigurationParser> parserMap = 
			new ConcurrentHashMap<Class<? extends CacheClient>, CacheClientConfigurationParser>();
	
	static {
		register(MemcachedClientImpl.class, new MemcachedClientConfigurationParser());
		register(KvdbClientImpl.class, new MemcachedClientConfigurationParser());
		register(EhcacheClientImpl.class, new EhcacheClientConfigurationParser());
	}

	public static void register(Class<? extends CacheClient> clientClazz, CacheClientConfigurationParser parser) {
		parserMap.put(clientClazz, parser);
	}

	/**
	 * @param detail
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static CacheClientConfiguration parse(CacheConfigDetailDTO detail) {
		try {
			Class clientClazz = Class.forName(detail.getClientClazz());
			return parserMap.get(clientClazz).parse(detail);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Parser not found with cache client[" + detail.getClientClazz() + "].");
		}
	}

}
