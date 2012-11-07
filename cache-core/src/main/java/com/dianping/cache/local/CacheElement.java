/**
 * Project: cache-core
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
package com.dianping.cache.local;

/**
 * Every item in the cache is wrapped in an CacheElement. This contains
 * information about the element: the key, the value and the its attributes
 *@author danson.liu
 */
public class CacheElement {
	
	private String key;
	
	private Object value;
	
	private ElementAttributes attributes;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public ElementAttributes getAttributes() {
		return attributes;
	}

	public void setAttributes(ElementAttributes attributes) {
		this.attributes = attributes;
	}

}
