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
package com.dianping.cache.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Supported Cache Specification
 * @author danson.liu
 *
 */
public class SupportedSpecification {

	/**
	 * Supported Cache Clients
	 * @author danson.liu
	 *
	 */
	public static class SupportedCacheClients {

		/**
		 * supported cache client classes
		 */
		private static List<String> supportedClazzes = new ArrayList<String>();
		
		public static final String EHCACHE_CLIENT_CLAZZ = "com.dianping.cache.ehcache.EhcacheClientImpl";
		public static final String MEMCACHED_CLIENT_CLAZZ = "com.dianping.cache.memcached.MemcachedClientImpl";
		
		static {
			supportedClazzes.add(MEMCACHED_CLIENT_CLAZZ);
			supportedClazzes.add(EHCACHE_CLIENT_CLAZZ);
		}

		public static List<String> getSupportedClazzes() {
			return supportedClazzes;
		}
		
	}
	
	/**
	 * Supported Memcached Transcoders
	 * @author danson.liu
	 *
	 */
	public static class SupportedMemcachedTranscoders {
		
		/**
		 * supported memcached transcoders
		 */
		private static List<String> supportedTranscoders = new ArrayList<String>();
		
		static {
			supportedTranscoders.add("com.dianping.cache.memcached.HessianTranscoder");
			supportedTranscoders.add("net.spy.memcached.transcoders.SerializingTranscoder");
		}

		public static List<String> getSupportedTranscoders() {
			return supportedTranscoders;
		}
		
	}
	
}
