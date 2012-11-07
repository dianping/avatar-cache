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

import org.w3c.dom.Element;

import com.dianping.cache.core.CacheClientConfiguration;

/**
 * Parse the cache from XML
 * 
 * @author guoqing.chen
 * 
 */
public interface CacheMetadataParser {

	/**
	 * Create a {@link CacheClientConfiguration} instance by Element. The
	 * concrete implementation of {@link CacheClientConfiguration} will be found
	 * in Element.
	 */
	CacheClientConfiguration parse(Element e);
}
