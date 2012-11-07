/**
 * Project: 3-com.dianping.cache-server-2.0.3
 * 
 * File Created at 2011-12-21
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
package com.dianping.cache.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author danson.liu
 *
 */
public class ServerGroup implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3041787596323303250L;
	
	private String group;
	
	private String servers;
	
	private Date createdTime;
	
	private Date updatedTime;

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getServers() {
		return servers;
	}

	public void setServers(String servers) {
		this.servers = servers;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

}
