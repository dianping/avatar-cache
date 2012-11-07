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

import com.dianping.remote.share.dto.AbstractDTO;

/**
 * CacheConfigDetailDTO
 * @author danson.liu
 *
 */
public abstract class CacheConfigDetailDTO extends AbstractDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 463815040207539685L;
	
	protected String clientClazz;

	public String getClientClazz() {
		return clientClazz;
	}

}
