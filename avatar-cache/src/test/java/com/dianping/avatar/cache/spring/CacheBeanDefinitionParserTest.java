/**
 * Project: avatar-cache
 * 
 * File Created at 2011-9-19
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
package com.dianping.avatar.cache.spring;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * CacheBeanDefinitionParserTest
 * @author youngphy.yang
 *
 */
@Ignore
public class CacheBeanDefinitionParserTest{	
	/**
	 * Test whether the interceptor takes effect or not
	 */
	@Test
	public void testRegisterStatisticsCacheInterceptor() {
		ClassPathResource resource = new ClassPathResource("AOPDemo.xml");
		XmlBeanFactory beanFactory = new XmlBeanFactory(resource);
		BeanDefinitionRegistry beanDefinitionRegistry = beanFactory;
		
		CacheBeanDefinitionParser cacheBeanDefinitionParser = new CacheBeanDefinitionParser() {
		    protected Class<?> getStatisticsCacheInterceptor() {
		    	return com.dianping.avatar.cache.spring.DemoInterceptor.class;
		    }
		};
		cacheBeanDefinitionParser.registerStatisticsCacheInterceptor(null, beanDefinitionRegistry);
		cacheBeanDefinitionParser.registerCacheProxyBean(null, beanDefinitionRegistry, null);
		BusinessInterface businessBean = (BusinessInterface)beanFactory.getBean("cacheService");
		assertEquals(200, businessBean.bye());
		
		beanFactory = new XmlBeanFactory(resource);
		beanDefinitionRegistry = beanFactory;
		cacheBeanDefinitionParser = new CacheBeanDefinitionParser() {
		    protected Class<?> getStatisticsCacheInterceptor() {
		    	return com.dianping.avatar.cache.spring.DemoInterceptor2.class;
		    }
		};
		cacheBeanDefinitionParser.registerStatisticsCacheInterceptor(null, beanDefinitionRegistry);
		cacheBeanDefinitionParser.registerCacheProxyBean(null, beanDefinitionRegistry, null);
		businessBean = (BusinessInterface)beanFactory.getBean("cacheService");
		assertEquals(100, businessBean.bye());
		
	}

}
