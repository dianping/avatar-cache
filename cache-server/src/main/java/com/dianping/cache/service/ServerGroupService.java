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
package com.dianping.cache.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.dianping.avatar.exception.DuplicatedIdentityException;
import com.dianping.cache.entity.ServerGroup;

/**
 * TODO Comment of ServerGroupService
 * @author danson.liu
 *
 */
@Transactional
public interface ServerGroupService {
	
	List<ServerGroup> findAll();
	
	ServerGroup find(String group);
	
	ServerGroup create(ServerGroup serverGroup) throws DuplicatedIdentityException;
	
	ServerGroup update(ServerGroup serverGroup);
	
	void delete(String group);

}
