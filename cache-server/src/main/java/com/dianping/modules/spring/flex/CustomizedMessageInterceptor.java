/**
 * Project: cache-server
 * 
 * File Created at 2011-9-21
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
package com.dianping.modules.spring.flex;

import java.util.Map;

import org.springframework.flex.core.MessageInterceptor;
import org.springframework.flex.core.MessageProcessingContext;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

import flex.messaging.messages.CommandMessage;
import flex.messaging.messages.Message;

/**
 * TODO Comment of CustomizedMessageInterceptor
 * @author danson.liu
 *
 */
public class CustomizedMessageInterceptor implements MessageInterceptor {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Message postProcess(MessageProcessingContext context, Message inputMessage, Message outputMessage) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (inputMessage instanceof CommandMessage && ((CommandMessage) inputMessage).getOperation() == CommandMessage.LOGIN_OPERATION) {
            Object body = outputMessage.getBody();
            if (body instanceof Map<?, ?>) {
            	((Map) body).put("details", authentication.getDetails());
            }
        }
        return outputMessage;
	}

	@Override
	public Message preProcess(MessageProcessingContext context, Message inputMessage) {
		return inputMessage;
	}

}
