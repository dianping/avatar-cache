/**
 * 
 */
package com.dianping.cache.remote;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dianping.remote.cache.CacheManageWebService;
import com.dianping.remote.cache.dto.CacheClearDTO;

/**
 * @author jian.liu
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/config/spring/appcontext-*.xml")
public class CacheCleanForDotNet {
	
	@Autowired
	private CacheManageWebService cacheManageWebService;
	
	@Test
	public void testClearByKey() throws InterruptedException {
//		CacheClearDTO cacheClear = new CacheClearDTO("memcached", "oCMS.c2s4m66_0", "oCMS", Arrays.asList((Object) new Integer(2), 4, 66));
//		cacheManageWebService.clearByKey(cacheClear);
		Thread.sleep(200000);
	}

}
