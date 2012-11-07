/**
 * Project: cache-server
 * 
 * File Created at 2010-10-19
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
package com.dianping.cache.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Cache client configuration
 * @author danson.liu
 *
 */
public class CacheConfiguration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6623912955403363194L;
	
	private static final String LIST_SEPARATOR = ";~;";
	
	private String cacheKey;
	
	private String clientClazz;
	
	private String servers;
	
	private String transcoderClazz;

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public String getClientClazz() {
		return clientClazz;
	}

	public void setClientClazz(String clientClazz) {
		this.clientClazz = clientClazz;
	}

	public String getServers() {
		return servers;
	}
	
	public List<String> getServerList() {
		if (servers == null) {
			return null;
		}
		return Arrays.asList(servers.split(LIST_SEPARATOR));
	}
	
	public void setServers(String servers) {
		this.servers = servers;
	}
	
	public void setServerList(List<String> serverList) {
		String servers = null;
		if (serverList != null && !serverList.isEmpty()) {
			servers = StringUtils.join(serverList, LIST_SEPARATOR);
		}
		setServers(servers);
	}

	public String getTranscoderClazz() {
		return transcoderClazz;
	}

	public void setTranscoderClazz(String transcoderClazz) {
		this.transcoderClazz = transcoderClazz;
	}

}
