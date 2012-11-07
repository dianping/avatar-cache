/**
 * Project: cache-server
 * 
 * File Created at 2010-10-15
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
package com.dianping.cache.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.dianping.cache.entity.CacheKeyConfiguration;

/**
 * TODO Comment of CacheKeyConfigurationDaoTest
 * @author danson.liu
 *
 */
@ContextConfiguration(locations = {
		"classpath:/config/spring/applicationContext-context.xml",
		"classpath:/config/spring/applicationContext-ibatis.xml",
		"classpath:/config/spring/applicationContext-aop-core.xml",
		"classpath:/config/spring/applicationContext-aop-transaction.xml",
		"classpath:/config/spring/applicationContext-dao.xml"
})
@TransactionConfiguration(transactionManager = "platformTransactionManager")
public class CacheKeyConfigurationDaoTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	@Autowired
	private CacheKeyConfigurationDao configurationDao;

	/**
	 * Test method for {@link com.dianping.cache.dao.CacheKeyConfigurationDao#findAll()}.
	 */
	@Test
	public void testFindAll() {
		List<CacheKeyConfiguration> founds = configurationDao.findAll();
		assertEquals(1, founds.size());
	}

}
