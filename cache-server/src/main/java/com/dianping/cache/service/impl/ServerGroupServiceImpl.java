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
package com.dianping.cache.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.avatar.exception.DuplicatedIdentityException;
import com.dianping.cache.dao.ServerGroupDao;
import com.dianping.cache.entity.ServerGroup;
import com.dianping.cache.service.ServerGroupService;

/**
 * TODO Comment of ServerGroupServiceImpl
 * @author danson.liu
 *
 */
public class ServerGroupServiceImpl implements ServerGroupService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private ServerGroupDao serverGroupDao;

	@Override
	public List<ServerGroup> findAll() {
		return serverGroupDao.findAll();
	}

	@Override
	public ServerGroup find(String group) {
		return serverGroupDao.find(group);
	}

	@Override
	public ServerGroup create(ServerGroup serverGroup) throws DuplicatedIdentityException {
		try {
			String group = serverGroup.getGroup();
			ServerGroup found = find(group);
			if (found != null) {
				throw new DuplicatedIdentityException("server group[" + group + "] already exists.");
			}
			Date createdTime = Calendar.getInstance().getTime();
			serverGroup.setCreatedTime(createdTime);
			serverGroup.setUpdatedTime(createdTime);
			serverGroupDao.create(serverGroup);
			return serverGroup;
		} catch (RuntimeException e) {
			logger.error("Create server group failed.", e);
			throw e;
		}
	}

	@Override
	public ServerGroup update(ServerGroup serverGroup) {
		try {
			Date updatedTime = Calendar.getInstance().getTime();
			serverGroup.setUpdatedTime(updatedTime);
			serverGroupDao.update(serverGroup);
			return serverGroup;
		} catch (RuntimeException e) {
			logger.error("Update server group failed.", e);
			throw e;
		}
	}

	@Override
	public void delete(String group) {
		try {
			serverGroupDao.delete(group);
		} catch (RuntimeException e) {
			logger.error("Delete server group failed.", e);
			throw e;
		}
	}

	public void setServerGroupDao(ServerGroupDao serverGroupDao) {
		this.serverGroupDao = serverGroupDao;
	}

}
