/**
 * Project: avatar-cache
 * 
 * File Created at 2010-7-12
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
package com.dianping.cache.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The configuration can be initialized by call {@link #init(String)} with
 * configuration file or {@link #addCache(String, String)} manually. The caller
 * should guaranteed the {@link #init(String)} only be invoked once.
 * 
 * @author guoqing.chen
 * 
 */
public class CacheConfiguration {

	/**
	 * Classpath resource prefix
	 */
	private static final String RESOURCE_PREFIX_CLASSPATH = "classpath:";

	/**
	 * File system resource prefix
	 */
	private static final String RESOURCE_PREFIX_FILE = "file:";

	/**
	 * Cache key and implementation of {@link CacheClient}
	 */
	private static Properties cacheProps = new Properties();

	/**
	 * Initialize the configuration by path. The path can startWiths
	 * "classpath:" or "file:" for representing classpath resource or file
	 * system resource. If the no any prefix it will be reated as file system
	 * resource.
	 */
	public synchronized static void init(String configurationLocation) throws IOException {

		if (configurationLocation == null) {
			throw new IllegalArgumentException("Configuration file location cann't be null.");
		}

		InputStream in = null;

		if (configurationLocation.startsWith(RESOURCE_PREFIX_CLASSPATH)) {
			String _configuration = configurationLocation.substring(RESOURCE_PREFIX_CLASSPATH.length());
			in = readClasspahtStream(_configuration);
		} else {
			String _configuration = configurationLocation;
			if (configurationLocation.startsWith(RESOURCE_PREFIX_FILE)) {
				_configuration = configurationLocation.substring(RESOURCE_PREFIX_FILE.length());
			}

			in = new FileInputStream(_configuration);
		}

		try {
			cacheProps.load(in);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public static void addCache(String key, String czName) {
		if (key == null || czName == null) {
			throw new IllegalArgumentException("key/czName is null.");
		}

		if (cacheProps.containsKey(key)) {
			throw new IllegalArgumentException("key " + key + " is existing.");
		}

		cacheProps.setProperty(key, czName);
	}

	public synchronized static String getCache(String key) {
		return cacheProps.getProperty(key);
	}

	public synchronized static void removeCache(String key) {
		if (key != null) {
			cacheProps.remove(key);
		}
	}

	/**
	 * Read classpath resource, it will read from current thread Classloader
	 * firstly,it not exists, read it from current Classloader again.
	 * 
	 * @param path
	 *            resource path
	 * @throws IOException
	 *             Any IO error.
	 */
	private static InputStream readClasspahtStream(String path) throws IOException {
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);

		if (in == null) {
			in = CacheConfiguration.class.getResourceAsStream(path);
		}

		if (in == null) {
			throw new FileNotFoundException("File " + path + " is not exists.");
		}

		return in;
	}
}
