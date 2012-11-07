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
package com.dianping.cache.dao;

import java.util.List;

import com.dianping.avatar.dao.GenericDao;
import com.dianping.avatar.dao.annotation.DAOAction;
import com.dianping.avatar.dao.annotation.DAOActionType;
import com.dianping.avatar.dao.annotation.DAOParam;
import com.dianping.avatar.dao.annotation.DAOParamType;
import com.dianping.cache.entity.ServerGroup;

/**
 * TODO Comment of ServerGroupDao
 * @author danson.liu
 *
 */
public interface ServerGroupDao extends GenericDao {

	@DAOAction
	List<ServerGroup> findAll();

	@DAOAction(action = DAOActionType.LOAD)
	ServerGroup find(@DAOParam("group") String group);

	@DAOAction(action = DAOActionType.INSERT)
	void create(@DAOParam(type = DAOParamType.ENTITY) ServerGroup serverGroup);

	@DAOAction(action = DAOActionType.UPDATE)
	void update(@DAOParam(type = DAOParamType.ENTITY) ServerGroup serverGroup);

	@DAOAction(action = DAOActionType.DELETE)
	void delete(@DAOParam("group") String group);

}
