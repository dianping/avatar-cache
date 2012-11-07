/**
 * Project: avatar
 * 
 * File Created at 2010-7-21
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
package com.dianping.avatar.cache.configuration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import com.dianping.avatar.exception.SystemException;

/**
 * The configuration class for cache items
 * 
 * @author guoqing.chen
 * 
 */
@Deprecated
public class CacheItemConfig {

	private static final String CACHE_CONFIG_PATH = "classpath:/config/cache/cache-item-config.xml";

	/**
	 * Singleton instance
	 */
	private static CacheItemConfig instance = new CacheItemConfig();

	/**
	 * Cache key map
	 */
	private final Map<String, CacheKeyType> cacheKeyTypes = new HashMap<String, CacheKeyType>();

	/**
     * @return the cacheKeyTypes
     */
    public Map<String, CacheKeyType> getCacheKeyTypes() {
        return cacheKeyTypes;
    }

    /**
	 * Default resource loader
	 */
	private static ResourceLoader resourceLoader = new DefaultResourceLoader();

	/**
	 * Imports
	 */
	private final List<String> importFiles = new ArrayList<String>();

	static {
		instance.init(CACHE_CONFIG_PATH);
	}

	/**
	 * Singleton method
	 */
	public static CacheItemConfig getInstance() {
		return instance;
	}

	/**
	 * Parse configuraiton file
	 */
	private void init(String file) {
		try {

			Digester digester = new Digester();
			
			digester.addCallMethod("dpcache/import", "addImport", 1, new Class[] { String.class });
			digester.addCallParam("dpcache/import", 0, "file");
			
			digester.addObjectCreate("dpcache/add", CacheKeyType.class);
			digester.addSetProperties("dpcache/add", "name", "category");
			digester.addSetProperties("dpcache/add", "index", "indexTemplate");
			digester.addSetProperties("dpcache/add", "duration", "duration");
			digester.addSetProperties("dpcache/add", "indexDesc", "indexParamDescs");
			digester.addSetProperties("dpcache/add", "type", "cacheType");

			digester.addSetNext("dpcache/add", "addCacheKeyType");

			InputStream in = resourceLoader.getResource(file).getInputStream();

			digester.push(this);
			digester.parse(in);

			in.close();

			for (String importFile : importFiles) {
				CacheItemConfig config = new CacheItemConfig();
				config.init(importFile);
				this.cacheKeyTypes.putAll(config.cacheKeyTypes);
			}
		} catch (Exception e) {
			throw new SystemException("Failed to initialize cache items config file", e);
		}
	}

	/**
	 * Add {@link CacheKeyType} for Digester
	 */
	public void addCacheKeyType(CacheKeyType cacheKeyType) {
		cacheKeyTypes.put(cacheKeyType.getCategory(), cacheKeyType);
	}

	/**
	 * Find {@link CacheKeyType} by category
	 */
	public CacheKeyType findCackeKeyType(String category) {
		return cacheKeyTypes.get(category);
	}

	/**
	 * Callback by digester
	 */
	public void addImport(String file) {
		this.importFiles.add(file);
	}
}
