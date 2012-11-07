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
 * Every item in the cache is associated with an element attributes object. It is used to track the life of
 * the object
 * @author danson.liu
 */
public class ElementAttributes {
	
	/**
	 * in milliseconds
	 */
	private long createTime;
	
	/**
	 * in milliseconds
	 */
	private long lastAccessTime;
	
	private long maxIdleTimeSeconds = -1;
	
	private long maxLifeSeconds = -1;

	public ElementAttributes() {
		createTime = System.currentTimeMillis();
		lastAccessTime = createTime;
	}
	
	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(long lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public long getMaxIdleTimeSeconds() {
		return maxIdleTimeSeconds;
	}

	public void setMaxIdleTimeSeconds(long maxIdleTimeSeconds) {
		this.maxIdleTimeSeconds = maxIdleTimeSeconds;
	}

	public long getMaxLifeSeconds() {
		return maxLifeSeconds;
	}

	public void setMaxLifeSeconds(long maxLifeSeconds) {
		this.maxLifeSeconds = maxLifeSeconds;
	}

	/**
	 * 
	 */
	public void setLastAccessTimeNow() {
		setLastAccessTime(System.currentTimeMillis());
	}

}
