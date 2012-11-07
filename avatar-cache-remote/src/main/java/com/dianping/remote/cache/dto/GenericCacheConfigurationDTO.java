/**
 * Project: com.dianping.avatar-cache-remote-2.0.0-SNAPSHOT
 * 
 * File Created at 2011-2-18
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
package com.dianping.remote.cache.dto;

import com.dianping.remote.share.dto.AbstractDTO;

/**
 * TODO Comment of GenericCacheConfigurationDTO
 * @author danson.liu
 *
 */
public class GenericCacheConfigurationDTO extends AbstractDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6009866894577345358L;
	
	private String cacheKey;
	
	private String clientClazz;
	
	private String servers;
	
	private String transcoderClazz;

	/**
	 * @return the cacheKey
	 */
	public String getCacheKey() {
		return cacheKey;
	}

	/**
	 * @param cacheKey the cacheKey to set
	 */
	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	/**
	 * @return the clientClazz
	 */
	public String getClientClazz() {
		return clientClazz;
	}

	/**
	 * @param clientClazz the clientClazz to set
	 */
	public void setClientClazz(String clientClazz) {
		this.clientClazz = clientClazz;
	}

	/**
	 * @return the servers
	 */
	public String getServers() {
		return servers;
	}

	/**
	 * @param servers the servers to set
	 */
	public void setServers(String servers) {
		this.servers = servers;
	}

	/**
	 * @return the transcoderClazz
	 */
	public String getTranscoderClazz() {
		return transcoderClazz;
	}

	/**
	 * @param transcoderClazz the transcoderClazz to set
	 */
	public void setTranscoderClazz(String transcoderClazz) {
		this.transcoderClazz = transcoderClazz;
	}

}
