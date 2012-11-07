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
package com.dianping.cache.service;

import java.util.Date;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.dianping.cache.service.condition.OperationLogSearchCondition;
import com.dianping.core.type.PageModel;

/**
 * @author danson.liu
 *
 */
@Transactional
public interface OperationLogService {
	
	void create(boolean succeed, String content, boolean critical);
	
	void create(boolean succeed, String operation, Map<String, String> detail, boolean critical);
	
	PageModel paginate(PageModel paginater, OperationLogSearchCondition searchCondition);
	
	void delete(Date before);

}
