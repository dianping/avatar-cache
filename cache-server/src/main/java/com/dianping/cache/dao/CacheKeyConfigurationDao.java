/**
 * Project: cache-server
 * 
 * File Created at 2010-10-15
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
package com.dianping.cache.dao;

import java.util.List;

import com.dianping.avatar.dao.GenericDao;
import com.dianping.avatar.dao.annotation.DAOAction;
import com.dianping.avatar.dao.annotation.DAOActionType;
import com.dianping.avatar.dao.annotation.DAOParam;
import com.dianping.avatar.dao.annotation.DAOParamType;
import com.dianping.cache.entity.CacheKeyConfiguration;
import com.dianping.cache.service.condition.CacheKeyConfigSearchCondition;
import com.dianping.core.type.PageModel;

/**
 * CacheKeyConfiguration data access object
 * 
 * @author danson.liu
 * 
 */
public interface CacheKeyConfigurationDao extends GenericDao {

    /**
     * retrieve all configurations
     * 
     * @return
     */
	@DAOAction
    List<CacheKeyConfiguration> findAll();

    /**
     * inc version for specified category
     */
	@DAOAction(action = DAOActionType.UPDATE)
    void incVersion(@DAOParam("category")String category);

    /**
     * retrive version for specified category
     */
	@DAOAction(action = DAOActionType.LOAD)
    String loadVersion(@DAOParam("category")String category);

	/**
	 * @param category
	 * @return
	 */
	@DAOAction(action = DAOActionType.LOAD)
	CacheKeyConfiguration find(@DAOParam("category")String category);

	/**
	 * @param config
	 */
	@DAOAction(action = DAOActionType.INSERT)
	void create(@DAOParam(type=DAOParamType.ENTITY)CacheKeyConfiguration config);

	/**
	 * @param config
	 */
	@DAOAction(action = DAOActionType.UPDATE)
	void update(@DAOParam(type=DAOParamType.ENTITY)CacheKeyConfiguration config);

	/**
	 * @param paginater
	 * @param searchCondition
	 * @return
	 */
	@DAOAction(action = DAOActionType.PAGE)
	PageModel paginate(@DAOParam("paginater")PageModel paginater, @DAOParam(type=DAOParamType.ENTITY)CacheKeyConfigSearchCondition searchCondition);

	/**
	 * @param category
	 */
	@DAOAction(action = DAOActionType.DELETE)
	void delete(@DAOParam("category")String category);
}
