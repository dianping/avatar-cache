/**
 * Project: com.dianping.cache-server-2.0.0-SNAPSHOT
 * 
 * File Created at 2011-2-27
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
package com.dianping.cache.service.condition;

import java.io.Serializable;

/**
 * TODO Comment of CacheKeyConfigSearchCondition
 * @author danson.liu
 *
 */
@SuppressWarnings("serial")
public class CacheKeyConfigSearchCondition implements Serializable {

	private String category;
	
	private String cacheType;

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category != null ? category.trim() : null;
	}

	/**
	 * @return the cacheType
	 */
	public String getCacheType() {
		return cacheType;
	}

	/**
	 * @param cacheType the cacheType to set
	 */
	public void setCacheType(String cacheType) {
		this.cacheType = cacheType;
	}
	
}
