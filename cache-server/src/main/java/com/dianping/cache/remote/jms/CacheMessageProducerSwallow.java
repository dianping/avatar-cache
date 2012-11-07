/**
 * Project: com.dianping.cache-server-2.0.1-old
 * 
 * File Created at 2011-10-14
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

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.dianping.cache.remote.jms.convert.Object2BytesConverter;
import com.dianping.lion.client.ConfigCache;
import com.dianping.swallow.Destination;
import com.dianping.swallow.MQService;
import com.dianping.swallow.MessageProducer;
import com.dianping.swallow.impl.MongoMQService;

/**
 * CacheMessageProducerSwallow
 * @author youngphy.yang
 */
public class CacheMessageProducerSwallow implements Serializable, InitializingBean, MQSender{

	private static final long serialVersionUID = 6415095593730417120L;

	private Logger logger = LoggerFactory.getLogger(CacheMessageProducerSwallow.class);
	
	private static final String CONFIG_KEY_SWALLOW_ENABLED = "avatar-cache.swallow.enabled";
    
    private String mongoUri = null;
    private Object2BytesConverter object2BytesConverter = null;
    private String destination = null;
    private String type = null;
    private MessageProducer messageProducer = null;

    /**
     * Send message to queue.
     * 
     * @param msg
     *            message used to send to queue
     */
    public void sendMessageToTopic(final Object msg) {
    	if (isSendSwallowRequired()) {
	    	try {
	    		byte[] bytes = object2BytesConverter.convertObject2Bytes(msg);
	    		messageProducer.send(messageProducer.createBinaryMessage(bytes));
	    	} catch (Exception e) {
	    		logger.error("Error occurs when try to convert and send the message to the swallow MQ.",e);
	    		throw new RuntimeException(e);
	    	}
    	}
    }
    
    private boolean isSendSwallowRequired() {
    	Boolean amqEnabled = null;
    	try {
			amqEnabled = ConfigCache.getInstance().getBooleanProperty(CONFIG_KEY_SWALLOW_ENABLED);
		} catch (Throwable e) {
			logger.warn("Get config[" + CONFIG_KEY_SWALLOW_ENABLED + "] from lion failed.", e);
		}
		return amqEnabled != null ? amqEnabled : true;
	}

	public void setMongoUri(String mongoUri) {
		this.mongoUri = mongoUri;
	}

	public void setObject2BytesConverter(
			Object2BytesConverter object2BytesConverter) {
		this.object2BytesConverter = object2BytesConverter;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Destination dest = null;
		MQService sqs = new MongoMQService(mongoUri);
		if("topic".equals(type)) {
			dest = Destination.topic(destination);
		} else {
			dest = Destination.queue(destination);
		}
		messageProducer = sqs.createProducer(dest, null);
	}
    
}