/**
 * Project: avatar
 * 
 * File Created at 2010-8-9
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
package com.dianping.avatar.cache.spring;

import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * The namespace handler for avatar:cache
 * 
 * @author guoqing.chen
 * 
 */
public class AvatarNamespacheHandler extends NamespaceHandlerSupport {

	/**
	 * Register {@link BeanDefinitionParser} instance
	 */
	@Override
	public void init() {
		this.registerBeanDefinitionParser("cache", new CacheBeanDefinitionParser());
	}
}
