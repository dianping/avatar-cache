/**
 * Project: cache-core
 * 
 * File Created at 2010-7-26
 * $Id$
 * 
 * Copyright 2010 Dianping.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Dianping.com.
 */
package com.dianping.cache.memcached;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import net.spy.memcached.CachedData;
import net.spy.memcached.compat.SpyObject;
import net.spy.memcached.transcoders.Transcoder;

/**
 * Decode the {@link InputStream} to String
 * 
 * @author guoqing.chen
 * @author danson.liu
 * 
 */
public class KvdbTranscoder extends SpyObject implements Transcoder<Object> {

	/**
	 * Decode bytes with UTF-8, if error using default charset
	 */
	@Override
	public Object decode(CachedData data) {
		try {
			return new String(data.getData(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return new String(data.getData());
		}
	}

	@Override
	public CachedData encode(Object str) {
		if (str == null) {
			return null;
		}

		String strVal = (String) str;
		byte[] data;
		try {
			data = strVal.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			data = strVal.getBytes();
		}

		return new CachedData(0, data, getMaxSize());
	}

	@Override
	public boolean asyncDecode(CachedData d) {
		return false;
	}

	@Override
	public int getMaxSize() {
		//KVDB's value not exceed 1M generally
		return CachedData.MAX_SIZE;
	}
}
