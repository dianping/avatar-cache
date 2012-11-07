/**
 * Project: avatar-cache
 * 
 * File Created at 2010-7-12
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

import java.util.ArrayList;
import java.util.List;

import net.spy.memcached.transcoders.Transcoder;

import com.dianping.cache.core.CacheClientConfiguration;

/**
 * The configuration for memcached client implementation(Spymemcached)
 * 
 * @author guoqing.chen
 * 
 */
public class MemcachedClientConfiguration implements CacheClientConfiguration {

	/**
	 * All servers
	 */
	private List<String> servers = new ArrayList<String>();

	/**
	 * Transcoder
	 */
	private Transcoder<Object> transcoder;

	/**
	 * Add memcached server and prot
	 */
	public void addServer(String server, int port) {
		addServer(server + ":" + port);
	}
	
	public void addServer(String address) {
		servers.add(address);
	}

	public void setServers(List<String> servers) {
		this.servers = servers;
	}

	public String getServers() {

		StringBuffer sb = new StringBuffer();

		for (String server : servers) {
			sb.append(server.trim());
			sb.append(" ");
		}

		return sb.toString().trim();
	}

	/**
	 * @return the transcoder
	 */
	public Transcoder<Object> getTranscoder() {
		return transcoder;
	}

	/**
	 * @param transcoder
	 *            the transcoder to set
	 */
	public void setTranscoder(Transcoder<Object> transcoder) {
		this.transcoder = transcoder;
	}

}
