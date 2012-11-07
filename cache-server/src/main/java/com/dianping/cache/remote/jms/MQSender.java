/**
 * Project: com.dianping.cache-server-2.0.1-old
 * 
 * File Created at 2011-10-17
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
package com.dianping.cache.remote.jms;

/**
 * MQSender
 * @author youngphy.yang
 *
 */
public interface MQSender {
	public void sendMessageToTopic(final Object msg);
}
