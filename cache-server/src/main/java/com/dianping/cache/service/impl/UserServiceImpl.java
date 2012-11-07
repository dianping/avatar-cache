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
package com.dianping.cache.service.impl;

import com.dianping.cache.dao.UserDao;
import com.dianping.cache.entity.User;
import com.dianping.cache.service.UserService;

/**
 * @author danson.liu
 *
 */
public class UserServiceImpl implements UserService {
	
	private UserDao userDao;

	@Override
	public User findUser(String nameOrEmail) {
		return userDao.getUser(nameOrEmail);
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

}
