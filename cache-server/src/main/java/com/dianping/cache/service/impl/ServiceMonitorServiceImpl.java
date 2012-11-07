/**
 * Project: cache-server
 * 
 * File Created at 2012-3-26
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
package com.dianping.cache.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.support.MBeanServerConnectionFactoryBean;

import com.dianping.cache.service.ServiceMonitorService;

/**
 * @author danson.liu
 *
 */
public class ServiceMonitorServiceImpl implements ServiceMonitorService {
	
	private static Logger logger = LoggerFactory.getLogger(ServiceMonitorServiceImpl.class);

	private static ConcurrentMap<String, MBeanServerConnFactoryExtend> mbeanServerConnections = new ConcurrentHashMap<String, MBeanServerConnFactoryExtend>();
	
	private String jmxServerUser = DEFAULT_JMX_USER;
	private String jmxServerPasswd = DEFAULT_JMX_PASSWD;
	private ObjectName statMbeanName;
	
	private static final String DEFAULT_JMX_USER = "hawk-jmx";
	private static final String DEFAULT_JMX_PASSWD = "52521070";
	
	static {
		new MBeanServerConnectMaintainer().start();
	}
	
	@Override
	public String getClientStats(String clientIp, int skip, int size) throws Exception {
		int retry = 1;
		while (true) {
			try {
				MBeanServerConnFactoryExtend serverConnFactory = getMBeanConnection(clientIp);
				MBeanServerConnection serverConn = (MBeanServerConnection) serverConnFactory.factory.getObject();
				return (String) serverConn.invoke(getStatMbeanName(), "getServiceStats", new Object[] {skip, size}, 
						new String[] {Integer.TYPE.getName(), Integer.TYPE.getName()});
			} catch (IOException e) {
				if (retry-- <= 0) {
					logger.warn("Get servicestats from mbean server[" + clientIp + "] failed, detail[" + e.getMessage() + "].");
					throw e;
				} else {
					MBeanServerConnFactoryExtend removed = mbeanServerConnections.remove(clientIp);
					try {
						if (removed != null) {
							removed.factory.destroy();
						}
					} catch (Throwable e1) {}
				}
			}
		}
	}

	private ObjectName getStatMbeanName() throws MalformedObjectNameException, NullPointerException {
		if (this.statMbeanName == null) {
			this.statMbeanName = new ObjectName("com.dianping.hawk:name=com.dianping.dpsf.jmx.DpsfRequestorMonitor");
		}
		return this.statMbeanName;
	}

	private MBeanServerConnFactoryExtend getMBeanConnection(String clientIp) throws IOException {
		MBeanServerConnFactoryExtend connectionFactory = mbeanServerConnections.get(clientIp);
		if (connectionFactory == null) {
			MBeanServerConnFactoryExtend newConnFactory = createMBeanConnFactory(clientIp);
			connectionFactory = mbeanServerConnections.putIfAbsent(clientIp, newConnFactory);
			if (connectionFactory == null) {
				connectionFactory = newConnFactory;
			} else {
				newConnFactory.factory.destroy();
			}
		}
		connectionFactory.lastAccessTime = System.currentTimeMillis();
		return connectionFactory;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private MBeanServerConnFactoryExtend createMBeanConnFactory(String clientIp) throws IOException {
		MBeanServerConnectionFactoryBean factory = new MBeanServerConnectionFactoryBean();
		String jmxHost = clientIp.contains(":") ? clientIp : clientIp + ":3397";
		factory.setServiceUrl("service:jmx:rmi:///jndi/rmi://" + jmxHost + "/HawkMBeanServer");
		factory.setConnectOnStartup(false);
		Map environment = new HashMap();
		environment.put("jmx.remote.credentials", new String[] {this.jmxServerUser, this.jmxServerPasswd});
		factory.setEnvironmentMap(environment);
		factory.afterPropertiesSet();
		return new MBeanServerConnFactoryExtend(factory);
	}

	public void setJmxServerUser(String jmxServerUser) {
		this.jmxServerUser = jmxServerUser;
	}

	public void setJmxServerPasswd(String jmxServerPasswd) {
		this.jmxServerPasswd = jmxServerPasswd;
	}
	
	static class MBeanServerConnectMaintainer extends Thread {
		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {Thread.sleep(10000);} catch (Throwable e) {}
				Iterator<Entry<String, MBeanServerConnFactoryExtend>> entryIter = mbeanServerConnections.entrySet().iterator();
				while (entryIter.hasNext()) {
					Entry<String, MBeanServerConnFactoryExtend> entry = entryIter.next();
					try {
						MBeanServerConnFactoryExtend connectFactory = entry.getValue();
						if (System.currentTimeMillis() - connectFactory.lastAccessTime >= 5 * 60 * 1000) {
							entryIter.remove();
							connectFactory.factory.destroy();
						}
					} catch (Throwable e) {
						logger.warn("Remove unused mbean server connect failed, detail[" + e.getMessage() + "].");
					}
				}
			}
		}
	}
	
	class MBeanServerConnFactoryExtend {
		MBeanServerConnectionFactoryBean factory;
		long lastAccessTime;
		public MBeanServerConnFactoryExtend(MBeanServerConnectionFactoryBean factory) {
			this.factory = factory;
			this.lastAccessTime = System.currentTimeMillis();
		}
	}

}
