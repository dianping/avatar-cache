/**
 * Project: cache-server
 * 
 * File Created at 2010-10-15
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
package com.dianping.cache.remote;

import java.util.ArrayList;
import java.util.List;

import com.dianping.cache.entity.CacheConfiguration;
import com.dianping.cache.entity.CacheKeyConfiguration;
import com.dianping.cache.remote.translator.CacheConfiguration2DTOTranslator;
import com.dianping.cache.remote.translator.CacheKeyConfiguration2DTOTranslator;
import com.dianping.cache.service.CacheConfigurationService;
import com.dianping.cache.service.CacheKeyConfigurationService;
import com.dianping.remote.cache.CacheConfigurationWebService;
import com.dianping.remote.cache.dto.CacheConfigurationDTO;
import com.dianping.remote.cache.dto.CacheConfigurationsDTO;
import com.dianping.remote.cache.dto.CacheKeyConfigurationDTO;
import com.dianping.remote.share.Translator;

/**
 * CacheConfiguration Web Service
 * @author danson.liu
 *
 */
public class CacheConfigurationWebServiceImpl implements CacheConfigurationWebService {
	
	private CacheConfigurationService configurationService;
	
	private CacheKeyConfigurationService itemConfigurationService;

	@Override
	public List<CacheKeyConfigurationDTO> getKeyConfigurations() {
		List<CacheKeyConfiguration> configurations = itemConfigurationService.findAll();
		return translate2KeyTypes(configurations);
	}

	/**
	 * @param configurations
	 * @return
	 */
	private List<CacheKeyConfigurationDTO> translate2KeyTypes(List<CacheKeyConfiguration> configurations) {
		List<CacheKeyConfigurationDTO> cacheKeyTypes = new ArrayList<CacheKeyConfigurationDTO>();
		Translator<CacheKeyConfiguration, CacheKeyConfigurationDTO> translator = new CacheKeyConfiguration2DTOTranslator();
		for (CacheKeyConfiguration configuration : configurations) {
			cacheKeyTypes.add(translator.translate(configuration));
		}
		return cacheKeyTypes;
	}
	
	@Override
	public CacheConfigurationsDTO getCacheConfigurations() {
		List<CacheConfiguration> configurations = configurationService.findAll();
		CacheConfigurationsDTO configurationsDTO = new CacheConfigurationsDTO();
		Translator<CacheConfiguration, CacheConfigurationDTO> translator = new CacheConfiguration2DTOTranslator();
		for (CacheConfiguration configuration : configurations) {
			configurationsDTO.addConfiguration(translator.translate(configuration));
		}
		return configurationsDTO;
	}

	public void setItemConfigurationService(CacheKeyConfigurationService configurationService) {
		this.itemConfigurationService = configurationService;
	}

	public void setConfigurationService(CacheConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}
