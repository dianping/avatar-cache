/**
 * Project: avatar-cache-remote
 * 
 * File Created at 2010-10-18
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
package com.dianping.remote.cache.dto;

/**
 * Ehcache Config Detail DTO
 * @author danson.liu
 *
 */
public class EhcacheConfigDetailDTO extends CacheConfigDetailDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6622073723502785607L;
	
	/**
	 * 
	 */
	public EhcacheConfigDetailDTO() {
		clientClazz = "com.dianping.cache.ehcache.EhcacheClientImpl";
	}

}
