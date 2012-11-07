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

import java.util.List;

import com.dianping.remote.share.dto.AbstractDTO;

/**
 * Cache message recived from jms or remote service
 * 
 * @author pengshan.zhang
 * 
 */
public abstract class CacheMessageDTO extends AbstractDTO {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -8627060114802935333L;

    /**
     * Time when message is born.
     */
    private long addTime = System.currentTimeMillis();
    
    /**
     * which destinations resolve the message
     */
    protected List<String> destinations;

    /**
     * @param addTime
     *            the addTime to set
     */
    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    /**
     * @return the addTime
     */
    public long getAddTime() {
        return addTime;
    }

	public List<String> getDestinations() {
		return destinations;
	}

	public void setDestinations(List<String> destinations) {
		this.destinations = destinations;
	}

}
