/**
 * Project: cache-builder
 * 
 * File Created at 2010-7-19
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

import org.w3c.dom.Element;

import com.dianping.cache.core.CacheClientConfiguration;
import com.dianping.cache.local.LocalCacheConfiguration;

/**
 * Local Cache metadata parser
 * @author danson.liu
 *
 */
public class LocalCacheMetadataParser implements CacheMetadataParser {

	@Override
	public CacheClientConfiguration parse(Element e) {
		LocalCacheConfiguration configuration = new LocalCacheConfiguration(); 
		String maxIdleTime = e.getAttribute("maxIdleTime");
		if (maxIdleTime != null && maxIdleTime.length() > 0) {
			configuration.setMaxIdleTime(Long.parseLong(maxIdleTime));
		}
		return configuration;
	}

}
