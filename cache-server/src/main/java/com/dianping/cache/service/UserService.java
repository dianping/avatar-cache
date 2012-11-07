/**
 * Project: cache-server
 * 
 * File Created at 2011-9-22
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

import org.springframework.transaction.annotation.Transactional;

import com.dianping.cache.entity.User;

/**
 * TODO Comment of UserService
 * @author danson.liu
 *
 */
@Transactional
public interface UserService {
	
	User findUser(String name);

}
