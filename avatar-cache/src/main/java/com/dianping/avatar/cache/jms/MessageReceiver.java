/**
 * Project: avatar-cache
 * 
 * File Created at 2011-9-23
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
package com.dianping.avatar.cache.jms;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import com.dianping.remote.cache.dto.CacheConfigurationDTO;
import com.dianping.remote.cache.dto.CacheKeyConfigurationDTO;
import com.dianping.remote.cache.dto.CacheKeyTypeVersionUpdateDTO;
import com.dianping.remote.cache.dto.SingleCacheRemoveDTO;
import com.dianping.avatar.cache.listener.CacheConfigurationUpdateListener;
import com.dianping.avatar.cache.listener.CacheKeyConfigUpdateListener;
import com.dianping.avatar.cache.listener.CacheKeyTypeVersionUpdateListener;
import com.dianping.avatar.cache.listener.SingleCacheRemoveListener;
import com.dianping.avatar.cache.util.CacheMonitorUtil;
import com.dianping.swallow.BinaryMessage;
import com.dianping.swallow.Message;
import com.dianping.swallow.MessageListener;
import com.dianping.swallow.impl.MongoBinaryMessage;

/**
 * MessageReceiver
 * @author youngphy.yang
 *
 */
public class MessageReceiver implements MessageListener{
	private List<Object> mappingList = new ArrayList<Object>();
	
	@Override
	public void onMessage(Message msg) {
		try {
			msg = (MongoBinaryMessage)msg;
			Object object = convertBytes2Object(((BinaryMessage)msg).getContent());
			if(CacheKeyTypeVersionUpdateDTO.class.isAssignableFrom(object.getClass())) {
				CacheKeyTypeVersionUpdateListener listener = (CacheKeyTypeVersionUpdateListener)mappingList.get(0);
				listener.handleMessage((CacheKeyTypeVersionUpdateDTO)object);
			} else if(SingleCacheRemoveDTO.class.isAssignableFrom(object.getClass())) {
				SingleCacheRemoveListener listener = (SingleCacheRemoveListener)mappingList.get(1);
				listener.handleMessage((SingleCacheRemoveDTO)object);
			} else if(CacheConfigurationDTO.class.isAssignableFrom(object.getClass())) {
				CacheConfigurationUpdateListener listener = (CacheConfigurationUpdateListener)mappingList.get(2);
				listener.handleMessage((CacheConfigurationDTO)object);
			} else if(CacheKeyConfigurationDTO.class.isAssignableFrom(object.getClass())) {
				CacheKeyConfigUpdateListener listener = (CacheKeyConfigUpdateListener)mappingList.get(3);
				listener.handleMessage((CacheKeyConfigurationDTO)object);
			}
		} catch (Exception e) {
			CacheMonitorUtil.logCacheError("Error occured when try to dispatch the message to the listener.", e);
		}
	}

	public List<Object> getMappingList() {
		return mappingList;
	}

	public void setMappingList(List<Object> mappingList) {
		this.mappingList = mappingList;
	}
	
	protected Object convertBytes2Object(byte[] bytes) throws Exception{
		Object object = null;
		if (bytes != null && bytes.length > 0) {
			ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
			ObjectInputStream oi = null;
			try {
				oi = new ObjectInputStream(bi);
				object = oi.readObject();
			} finally {
				if (oi != null) {
					oi.close();
				}
			}
		}
		return object;
	}

}
