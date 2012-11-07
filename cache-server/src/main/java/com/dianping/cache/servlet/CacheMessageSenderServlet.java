/**
 * Project: cache-server
 * 
 * File Created at 2010-10-18
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
package com.dianping.cache.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

import com.dianping.cache.support.spring.SpringLocator;
import com.dianping.remote.cache.CacheManageWebService;

/**
 * CacheMessageSenderServlet is used to receive commond to send jms message to
 * jms server.
 * 
 * @author pengshan.zhang
 * 
 */
public class CacheMessageSenderServlet extends HttpServlet {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 7602875515792719125L;

    /**
     * Message value
     */
    private static final String MESSAGE_VALUE = "value";

    /**
     * Message type
     */
    private static final String MESSAGE_TYPE = "type";

    private CacheManageWebService cacheManageService = SpringLocator.getBean(CacheManageWebService.class);

    /**
     * Get building message parameters, and then send this message to JMS
     * server.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        try {
        	String type = req.getParameter(MESSAGE_TYPE);
            String value = req.getParameter(MESSAGE_VALUE);

            if (!StringUtils.hasLength(type) || !StringUtils.hasLength(value)) {
            	out.println("Cache message format should like this: http://host:port/cache-message/send.action?type=1&value=test");
            }
            int messageType = parseInt(type);
            if (Constants.KEYTYPE_VERSION_UPDATE_MESSAGE == messageType) {
            	cacheManageService.clearByCategory(value);
            } else if (Constants.SINGLE_CACHE_REMOVE_MESSAGE == messageType) {
            	cacheManageService.clearByKey("web", value);
            	cacheManageService.clearByKey("memcached", value);
            }
            out.println("Send message successfully!");
        } finally {
            out.close();
        }
    }

    private int parseInt(String type) {
        int messageType = 0;
        try {
            messageType = Integer.parseInt(type);
        } catch (Exception e) {
        }
        return messageType;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
