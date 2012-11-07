/**
 * Project: avatar
 * 
 * File Created at 2010-7-14
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
package com.dianping.avatar.cache;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

/**
 * Cache key object
 * 
 * @author danson.liu
 * 
 */
public class CacheKey implements Serializable {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -1099870460150967658L;

	/**
	 * Item category
	 */
	private String category;

	/**
	 * Parameters
	 */
	private Object[] params;

	/**
	 * Constructor
	 */
	public CacheKey(String category, Object... params) {
		this.category = category;
		this.params = params;
	}

	public String getCategory() {
		return category;
	}

	/**
	 * @return the params
	 */
	public Object[] getParams() {
		return params;
	}

	public List<Object> getParamsAsList() {
		if (params == null) {
			return Collections.emptyList();
		}
		return Arrays.asList(params);
	}

	@Override
	public String toString() {
		return "CacheKey[category:" + category + ", indexParams:" + ArrayUtils.toString(params) + "]";
	}

}
