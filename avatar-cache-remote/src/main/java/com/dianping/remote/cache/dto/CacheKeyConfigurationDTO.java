/**
 * Project: avatar-cache-remote
 * 
 * File Created at 2010-10-14
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
 * Configuration for the key of cached item
 * @author danson.liu
 *
 */
@JmsMessageBody(innerDestination = "CACHE_KEY_CONFIG_UPDATE")
public class CacheKeyConfigurationDTO extends AbstractDTO {

	private static final long serialVersionUID = -942096736262267919L;
	
	private String category;
	
	private String duration;
	
	private String indexTemplate;
	
	private String indexDesc;
	
	private String cacheType;
	
	private int version;
	
	private boolean sync2Dnet;
	
	private boolean hot;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getIndexTemplate() {
		return indexTemplate;
	}

	public void setIndexTemplate(String indexTemplate) {
		this.indexTemplate = indexTemplate;
	}

	public String getIndexDesc() {
		return indexDesc;
	}

	public void setIndexDesc(String indexDesc) {
		this.indexDesc = indexDesc;
	}

	public String getCacheType() {
		return cacheType;
	}

	public void setCacheType(String cacheType) {
		this.cacheType = cacheType;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public boolean isSync2Dnet() {
		return sync2Dnet;
	}

	public void setSync2Dnet(boolean sync2Dnet) {
		this.sync2Dnet = sync2Dnet;
	}

	public boolean isHot() {
		return hot;
	}

	public void setHot(boolean hot) {
		this.hot = hot;
	}

}
