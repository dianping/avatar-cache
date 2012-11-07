/**
 * Project: cache-server
 * 
 * File Created at 2010-10-15
 * $Id$
 * 
 * Copyright 2010 dianping.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.cache.remote.jms;

import java.io.Serializable;
import java.util.List;

/**
 * A decorator for the AMQ and swallow producer client, on behalf of the compatibility with
 * the old systems using the AMQ.
 */
public class CacheMessageProducer implements Serializable {
	private static final long serialVersionUID = -5708176189195821560L;
	
	private List<MQSender> senders = null;
	
    public void sendMessageToTopic(final Object msg) {
    	for(MQSender mqSender : senders) {
    		mqSender.sendMessageToTopic(msg);
    	}
    }

	public List<MQSender> getSenders() {
		return senders;
	}

	public void setSenders(List<MQSender> senders) {
		this.senders = senders;
	}
    
}
