/**
 * Project: com.dianping.cache-core-2.0.0
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

import net.spy.memcached.KetamaConnectionFactory;
import net.spy.memcached.transcoders.Transcoder;

/**
 * TODO Comment of CustomizedConnectionFactory
 * @author jian.liu
 *
 */
public class ExtendedKetamaConnectionFactory extends KetamaConnectionFactory implements ExtendedConnectionFactory {

	private Transcoder<Object> transcoder;
	private long opQueueMaxBlockTime;
	
	public ExtendedKetamaConnectionFactory(int qLen, int bufSize, long opQueueMaxBlockTime) {
		super(qLen, bufSize, opQueueMaxBlockTime);
		this.opQueueMaxBlockTime = opQueueMaxBlockTime;
	}
	
	@Override
	public long getOpQueueMaxBlockTime() {
	    return opQueueMaxBlockTime;
	}
	
	public ExtendedKetamaConnectionFactory() {
		this(DEFAULT_OP_QUEUE_LEN, DEFAULT_READ_BUFFER_SIZE, DEFAULT_OP_QUEUE_MAX_BLOCK_TIME);
	}
	
	@Override
	public Transcoder<Object> getDefaultTranscoder() {
		return transcoder;
	}

	public void setTranscoder(Transcoder<Object> transcoder) {
		this.transcoder = transcoder;
	}
	
}
