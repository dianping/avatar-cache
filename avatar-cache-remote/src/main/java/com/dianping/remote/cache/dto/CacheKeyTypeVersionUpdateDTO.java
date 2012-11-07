/**
 * Project: avatar
 * 
 * File Created at 2010-10-12
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
 * Cache message recived from jms to update key type version.
 * 
 * @author pengshan.zhang
 * 
 */
@JmsMessageBody(innerDestination="KEYTYPE_VERSION_UPDATE")
public class CacheKeyTypeVersionUpdateDTO extends CacheMessageDTO {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 903598456694367471L;

    /**
     * Key type
     */
    private String msgValue;

    /**
     * Version is used to update
     */
    private String version;

    /**
     * @param version
     *            the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param msgValue
     *            the msgValue to set
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
