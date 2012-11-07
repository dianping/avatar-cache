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

import com.dianping.remote.share.annotation.JmsMessageBody;
import com.dianping.remote.share.dto.AbstractDTO;


/**
 * Cache Client Configuration
 * @author danson.liu
 *
 */
@JmsMessageBody(innerDestination="CACHE_CONFIG_UPDATE")
public class CacheConfigurationDTO extends AbstractDTO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4167929878555896829L;

	/**
	 * key for specified cache
	 */
	private String key;
	
	private CacheConfigDetailDTO detail;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public CacheConfigDetailDTO getDetail() {
		return detail;
	}

	public void setDetail(CacheConfigDetailDTO detail) {
		this.detail = detail;
	}

}
