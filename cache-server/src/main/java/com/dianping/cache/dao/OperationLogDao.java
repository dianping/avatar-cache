/**
 * Project: cache-server
 * 
 * File Created at 2011-9-18
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

import java.util.Date;

import com.dianping.avatar.dao.GenericDao;
import com.dianping.avatar.dao.annotation.DAOAction;
import com.dianping.avatar.dao.annotation.DAOActionType;
import com.dianping.avatar.dao.annotation.DAOParam;
import com.dianping.avatar.dao.annotation.DAOParamType;
import com.dianping.cache.entity.OperationLog;
import com.dianping.cache.service.condition.OperationLogSearchCondition;
import com.dianping.core.type.PageModel;

/**
 * @author danson.liu
 *
 */
public interface OperationLogDao extends GenericDao {
	
	@DAOAction(action = DAOActionType.INSERT)
	void create(@DAOParam(type=DAOParamType.ENTITY)OperationLog log);
	
	@DAOAction(action = DAOActionType.PAGE)
	PageModel paginate(@DAOParam("paginater")PageModel paginater, @DAOParam(type=DAOParamType.ENTITY)OperationLogSearchCondition searchCondition);
	
	@DAOAction(action = DAOActionType.DELETE)
	void delete(@DAOParam("before")Date before);

}
