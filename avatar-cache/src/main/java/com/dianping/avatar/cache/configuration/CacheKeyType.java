/**
 * Project: avatar
 * 
 * File Created at 2010-7-15
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
package com.dianping.avatar.cache.configuration;


/**
 * The metadata class for representing business-common configuration files.
 * 
 * @author danson.liu
 * 
 */
public class CacheKeyType {

	/**
	 * Default cache type
	 */
	public final static String DEFAULT_CACHE_TYPE = "memcached";

	/**
	 * Item category
	 */
	private String category;

	/**
	 * Duration(default hour)
	 * support time unit: hour, minute
	 * as: 
	 * 		3(h)	3 hours
	 * 		4m		4 minutes
	 */
	private String duration;

	/**
	 * index template, such as c{0}st{1}rt{2}
	 */
	private String indexTemplate;

	/**
	 * Parameter descriptions
	 */
	private String indexDesc;

	/**
	 * Cache type
	 */
	private String cacheType;

	/**
	 * Version
	 */
	private int version;
	
	private boolean sync2Dnet;
	
	private boolean hot;
	
	private int durationInSeconds = -1;

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	/**
	 * @param indexTemplate
	 *            the indexTemplate to set
	 */
	public void setIndexTemplate(String indexTemplate) {
		this.indexTemplate = indexTemplate;
	}

	/**
	 * @param cacheType
	 *            the cacheType to set
	 */
	public void setCacheType(String cacheType) {
		this.cacheType = cacheType;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @return
	 */
	public String getCacheType() {
		return cacheType;
	}

	/**
	 * @return
	 */
	public String getCategory() {
		return category;
	}

	public String getDuration() {
		return duration;
	}

	public int getDurationSeconds() {
		if (durationInSeconds == -1) {
			synchronized (this) {
				if (durationInSeconds == -1) {
					if (duration.endsWith("m")) {
						int minutes = Integer.parseInt(duration.substring(0, duration.length() - 1));
						durationInSeconds = minutes * 60;
					} else {
						String hourString = duration;
						if (hourString.endsWith("h")) {
							hourString = hourString.substring(0, hourString.length() - 1);
						}
						int hours = Integer.parseInt(hourString);
						durationInSeconds = hours * 60 * 60;
					}
				}
			}
		}
		return durationInSeconds;
	}

	public String getIndexTemplate() {
		return indexTemplate;
	}

	/**
	 * @return the indexDesc
	 */
	public String getIndexDesc() {
		return indexDesc;
	}

	/**
	 * @param indexDesc
	 *            the indexDesc to set
	 */
	public void setIndexDesc(String indexDesc) {
		this.indexDesc = indexDesc;
	}

	public String[] getIndexParamDescs() {
		return indexDesc.split("\\|");
	}

	public int getVersion() {
		return version;
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

	/**
	 * Return the string key for cache store. Key
	 * Rule:{category}.{index}_{version}
	 */
	public String getKey(Object... params) {
		String accessKey = getCategory() + "." + getIndexTemplate() + "_" + getVersion();
		if (params == null) {
			params = new Object[] {null};
		}
		for (int i = 0; i < params.length; i++) {
			accessKey = accessKey.replace("{" + i + "}", params[i].toString());
		}
		return accessKey;
	}
}
