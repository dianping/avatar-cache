/**
 * Project: avatar
 * 
 * File Created at 2010-7-13
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
package com.dianping.cache.builder.metadata;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.dianping.cache.builder.CacheClientFactory;
import com.dianping.cache.core.CacheClient;
import com.dianping.cache.core.CacheClientBuilder;
import com.dianping.cache.core.CacheClientConfiguration;
import com.dianping.cache.core.CacheConfiguration;

/**
 * The XML {@link CacheClientFactory} implementation, it will read cache
 * information from XML configuration file.
 * 
 * @author guoqing.chen
 * 
 */
public class XMLCacheClientFactory implements CacheClientFactory {

	/**
	 * Classpath resource prefix
	 */
	private static final String RESOURCE_PREFIX_CLASSPATH = "classpath:";

	/**
	 * File system resource prefix
	 */
	private static final String RESOURCE_PREFIX_FILE = "file:";

	/**
	 * All cache configuration
	 */
	private Map<String, CacheClientConfiguration> configMap = new LinkedHashMap<String, CacheClientConfiguration>();

	/**
	 * cache config file
	 */
	private final String cacheConfigFile;

	/**
	 * Constructor with configuration file location
	 */
	public XMLCacheClientFactory(String cacheConfigFile) {
		this.cacheConfigFile = cacheConfigFile;
		init();
	}

	private void init() {

		try {

			InputStream in = getResource(cacheConfigFile);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();

			Document doc = builder.parse(in);

			NodeList caches = doc.getElementsByTagName("cache");

			if (caches != null) {
				for (int i = 0; i < caches.getLength(); i++) {
					Element cache = (Element) caches.item(i);

					String key = cache.getAttribute("key");
					String metadataClass = cache.getAttribute("metadataClass");
					String clientClass = cache.getAttribute("clientClass");

					Class<?> cz = forName(metadataClass);

					CacheMetadataParser parser = (CacheMetadataParser) cz.newInstance();

					CacheClientConfiguration config = parser.parse(cache);

					CacheConfiguration.addCache(key, clientClass);

					configMap.put(key, config);
				}
			}
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}

			throw new RuntimeException("Failed to initialize factory.", e);
		}
	}

	@Override
	public synchronized List<String> getCacheKeys() {
		return new ArrayList<String>(configMap.keySet());
	}

	@Override
	public CacheClient findCacheClient(String cacheKey) {

		CacheClientConfiguration config = configMap.get(cacheKey);

		if (config == null) {
			throw new IllegalArgumentException("The configuraiton is not found for cachekey " + cacheKey);
		}

		CacheClient client = CacheClientBuilder.buildCacheClient(cacheKey, config);

		return client;
	}

	private Class<?> forName(String className) {
		try {
			Class<?> cz = Thread.currentThread().getContextClassLoader().loadClass(className);

			if (cz == null) {
				cz = this.getClass().getClassLoader().loadClass(className);
			}

			return cz;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	private InputStream getResource(String path) {
		InputStream in = null;

		try {
			if (path.startsWith(RESOURCE_PREFIX_CLASSPATH)) {
				String _configuration = path.substring(RESOURCE_PREFIX_CLASSPATH.length());
				in = readClasspahtStream(_configuration);
			} else {
				String _configuration = path;
				if (path.startsWith(RESOURCE_PREFIX_FILE)) {
					_configuration = path.substring(RESOURCE_PREFIX_FILE.length());
				}

				in = new FileInputStream(_configuration);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		return in;
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
			in = XMLCacheClientFactory.class.getResourceAsStream(path);
		}

		if (in == null) {
			throw new FileNotFoundException("File " + path + " is not exists.");
		}

		return in;
	}

}
