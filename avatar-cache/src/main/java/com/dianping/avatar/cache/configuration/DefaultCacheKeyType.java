/**
 * Project: com.dianping.avatar-cache-2.1.4
 * 
 * File Created at 2011-9-15
 * $Id$
 * 
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.avatar.cache.configuration;

/**
 * @author liujian
 *
 */
public class DefaultCacheKeyType extends CacheKeyType {

	/**
	 * @param category
	 */
	public DefaultCacheKeyType(String category) {
		setCategory(category);
		setDuration("2");
		setIndexTemplate("{0}");
		setIndexDesc("");
		setCacheType(DEFAULT_CACHE_TYPE);
		setSync2Dnet(false);
		setHot(false);
	}
	
	@Override
	public String getKey(Object... params) {
		String accessKey = getCategory() + "." + getIndexTemplate() + "_" + getVersion();
		String paramStr = "";
		if (params == null) {
			params = new Object[] {null};
		}
		for (int i = 0; i < params.length; i++) {
			paramStr += "(" + params[i] + ")";
		}
		return accessKey.replace("{0}", paramStr);
	}
	
}
