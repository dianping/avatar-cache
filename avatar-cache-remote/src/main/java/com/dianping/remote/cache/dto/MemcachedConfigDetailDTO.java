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

import java.util.ArrayList;
import java.util.List;

/**
 * Memcached Config Detail DTO
 * @author danson.liu
 *
 */
public class MemcachedConfigDetailDTO extends CacheConfigDetailDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5119818771060964933L;
	
	/**
	 * 
	 */
	private static final String DEFAULT_TRANSCODER_CLASS = "com.dianping.cache.memcached.HessianTranscoder";
	
	/**
	 * e.g. 10.10.1.1:8081
	 */
	private List<String> serverList = new ArrayList<String>();
	
	private String transcoderClazz = DEFAULT_TRANSCODER_CLASS;
	
	/**
	 * 
	 */
	public MemcachedConfigDetailDTO() {
		this.clientClazz = "com.dianping.cache.memcached.MemcachedClientImpl";
	}

	public List<String> getServerList() {
		return serverList;
	}

	public void setServerList(List<String> serverList) {
		this.serverList = serverList;
	}

	public String getTranscoderClazz() {
		return transcoderClazz;
	}

	public void setTranscoderClazz(String transcoderClazz) {
		this.transcoderClazz = transcoderClazz;
	}

}
