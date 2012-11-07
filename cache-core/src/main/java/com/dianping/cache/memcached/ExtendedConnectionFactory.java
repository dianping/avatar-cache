/**
 * Project: com.dianping.memcachedb-0.0.1-SNAPSHOT
 * 
 * File Created at 2011-4-19
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
package com.dianping.cache.memcached;

import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.transcoders.Transcoder;

/**
 * TODO Comment of ExtendedConnectionFactory
 * @author jian.liu
 *
 */
public interface ExtendedConnectionFactory extends ConnectionFactory {
	
	/**
	 * 
	 * @param transcoder
	 */
	void setTranscoder(Transcoder<Object> transcoder);

}
