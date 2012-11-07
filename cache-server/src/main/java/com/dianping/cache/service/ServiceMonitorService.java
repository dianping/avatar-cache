/**
 * Project: cache-server
 * 
 * File Created at 2012-3-26
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

/**
 * 监控服务调用客户端状态
 * @author danson.liu
 *
 */
public interface ServiceMonitorService {
	
	String getClientStats(String clientIp, int skip, int size) throws Exception;
	
}
