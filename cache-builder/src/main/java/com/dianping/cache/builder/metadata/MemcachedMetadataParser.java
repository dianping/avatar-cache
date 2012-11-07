/**
 * Project: avatar
 * 
 * File Created at 2010-7-13
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
package com.dianping.cache.builder.metadata;

import net.spy.memcached.transcoders.Transcoder;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.dianping.cache.core.CacheClientConfiguration;
import com.dianping.cache.memcached.MemcachedClientConfiguration;

/**
 * MemcachedCache metadata parser
 * 
 * @author guoqing.chen
 * 
 */
public class MemcachedMetadataParser implements CacheMetadataParser {

	@SuppressWarnings("unchecked")
	@Override
	public CacheClientConfiguration parse(Element e) {
		if (e == null) {
			throw new IllegalArgumentException("Element is null.");
		}

		MemcachedClientConfiguration config = new MemcachedClientConfiguration();

		String transcoderClass = e.getAttribute("transcoder");

		if (transcoderClass != null && !transcoderClass.trim().isEmpty()) {
			transcoderClass = transcoderClass.trim();

			Class<?> cz = forName(transcoderClass);

			try {
				Transcoder<Object> transcoder = (Transcoder<Object>) cz.newInstance();
				config.setTranscoder(transcoder);
			} catch (Exception ex) {
				if (ex instanceof RuntimeException) {
					throw (RuntimeException) e;
				}

				throw new RuntimeException("Failed to initialize factory.", ex);
			}
		}

		NodeList servers = e.getElementsByTagName("server");

		if (servers == null || servers.getLength() == 0) {
			return null;
		}

		for (int i = 0; i < servers.getLength(); i++) {
			Element server = (Element) servers.item(i);

			String address = server.getAttribute("address");
			String port = server.getAttribute("port");

			config.addServer(address, Integer.parseInt(port));
		}

		return config;
	}

	private Class<?> forName(String className) {
		try {
			Class<?> cz = Thread.currentThread().getContextClassLoader().loadClass(className);

			if (cz == null) {
				cz = this.getClass().getClassLoader().loadClass(className);
			}

			return cz;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
}
