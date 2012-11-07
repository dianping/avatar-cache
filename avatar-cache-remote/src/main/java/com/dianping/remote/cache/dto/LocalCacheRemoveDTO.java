/**
 * Project: avatar-cache-remote
 * 
 * File Created at 2010-10-19
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
package com.dianping.remote.cache.dto;

import com.dianping.remote.share.annotation.JmsMessageBody;

/**
 * Local cache removing message.
 * 
 * @author pengshan.zhang
 * 
 */
@JmsMessageBody(innerDestination="LOCAL_CACHE_REMOVE")
public class LocalCacheRemoveDTO extends CacheMessageDTO {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 6746249464661690655L;
    
    /**
     * Key type
     */
    private String msgValue;

    /**
     * @param msgValue the msgValue to set
     */
    public void setMsgValue(String msgValue) {
        this.msgValue = msgValue;
    }

    /**
     * @return the msgValue
     */
    public String getMsgValue() {
        return msgValue;
    }
    

}
