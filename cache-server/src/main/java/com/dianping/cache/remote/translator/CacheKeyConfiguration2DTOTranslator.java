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
package com.dianping.cache.remote.translator;

import com.dianping.cache.entity.CacheKeyConfiguration;
import com.dianping.remote.cache.dto.CacheKeyConfigurationDTO;
import com.dianping.remote.share.Translator;
import com.dianping.remote.util.DTOUtils;

/**
 * Translator from {@link CacheKeyConfiguration} to {@link CacheKeyConfigurationDTO}
 * 
 * @author danson.liu
 * 
 */
public class CacheKeyConfiguration2DTOTranslator implements Translator<CacheKeyConfiguration, CacheKeyConfigurationDTO> {

	@Override
	public CacheKeyConfigurationDTO translate(CacheKeyConfiguration source) {
		assert source != null;
		CacheKeyConfigurationDTO cacheKeyType = new CacheKeyConfigurationDTO();
		DTOUtils.copyProperties(cacheKeyType, source);
		return cacheKeyType;
	}

}
