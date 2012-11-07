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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dianping.remote.share.dto.AbstractDTO;

/**
 * Cache Client Configurations
 * @author danson.liu
 *
 */
public class CacheConfigurationsDTO extends AbstractDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2977016286139912352L;
	
	private Map<String, CacheConfigurationDTO> configurations = new HashMap<String, CacheConfigurationDTO>();
	
	public CacheConfigurationsDTO() {}
	
	public CacheConfigurationsDTO(List<CacheConfigurationDTO> configurations) {
		for (CacheConfigurationDTO configuration : configurations) {
			this.configurations.put(configuration.getKey(), configuration);
		}
	}
	
	public void addConfiguration(CacheConfigurationDTO configuration) {
		this.configurations.put(configuration.getKey(), configuration);
	}
	
	public Set<String> keys() {
		return configurations.keySet();
	}

	public CacheConfigurationDTO getConfiguration(String cacheKey) {
		return this.configurations.get(cacheKey);
	}

}
